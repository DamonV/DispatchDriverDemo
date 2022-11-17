package com.driver.reliantdispatch.data

import android.content.Context
import android.location.Location
import android.net.Uri
import android.util.Log
import com.driver.reliantdispatch.App
import com.driver.reliantdispatch.R
import com.driver.reliantdispatch.data.dto.*
import com.driver.reliantdispatch.domain.Global
import com.driver.reliantdispatch.domain.LOG_TAG
import com.driver.reliantdispatch.domain.boundaries.ApiGateway
import com.driver.reliantdispatch.domain.boundaries.FilesGateway
import com.driver.reliantdispatch.domain.dto.ApiResponseDTO
import com.driver.reliantdispatch.domain.dto.ZipEntryDTO
import com.driver.reliantdispatch.domain.entities.EbolStatus
import com.driver.reliantdispatch.domain.entities.joined.EbolJoined
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import javax.inject.Inject
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.lifecycle.MutableLiveData
import com.driver.reliantdispatch.domain.dto.LoginResponseDTO
import com.driver.reliantdispatch.data.dto.PasswordDTO
import com.driver.reliantdispatch.domain.entities.Profile
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.delay
import retrofit2.Response
import java.net.URLEncoder


const val FILE_LOAD_ERROR = 0
const val FILE_LOAD_SUCCESS = 1
const val FILE_LOAD_NOT_AUTH = 2


class ApiGatewayImpl: ApiGateway {
    @Inject
    lateinit var context: Context

    @Inject
    lateinit var retrofitService: ApiRetrofitService

    @Inject
    lateinit var connectivityService: ConnectivityService

    @Inject
    lateinit var global: Global

    @Inject
    lateinit var files: FilesGateway

    init {
        App.component.inject(this)
    }

    var isRefreshRunning = false
    var mRefreshJob: Deferred<Response<LoginResponseDTO>>? = null

    override suspend fun postEbol(ebol: EbolJoined): ApiResponseDTO =  withContext(Dispatchers.IO) {
        checkAllConditions()?.let{ return@withContext it }

        if (!ebol.posted) {
            val ebolDTO = ebol.toEbolDTO()
            //val a = Gson().toJson(ebolDTO).toString()       //todo remove
            if (ebolDTO != null) try {
                val response = retrofitService.postEbol.queryAsync(
                    "Bearer ${global.mToken}",
                    ebolDTO
                ).await()
                if (response.isSuccessful && response.body() != null) {
                    //spread the response to an existing data model
                    ebol.posted = true
                    val result = response.body() as List<ResponseInspectionDTO>
                    for ((i, vehicle) in ebol.vehiclesList.withIndex()) vehicle.pickupInspect?.let { inspection ->
                        if (i in 0 until result.size) {
                            inspection.apiId = result[i].inspect_id
                            result[i].items?.let { items ->
                                for (item in items)
                                    if (item.index in 0 until inspection.imagesList.size)
                                        inspection.imagesList[item.index!!].apiId = item.id
                            }
                        }
                    }
                    return@withContext when (postEbolFiles(ebol)) {
                        FILE_LOAD_NOT_AUTH -> ApiResponseDTO(notAuthorized = true)
                        FILE_LOAD_ERROR -> ApiResponseDTO(partialSuccess = true)
                        else -> ApiResponseDTO(null, true)
                    }
                } else {
                    Log.d(LOG_TAG, "server error ${response.code()} ${response.message()}")
                    if (response.code() == 401) return@withContext ApiResponseDTO(notAuthorized = true)
                    else return@withContext ApiResponseDTO(
                        when (response.code()) {
                            422 -> try {
                                Gson().fromJson(response.errorBody()?.string(), ErrorResponseDTO::class.java)
                            } catch (e: JsonSyntaxException) {
                                null
                            }
                            else -> null
                        },
                        false,
                        when (response.code()) {
                            403 -> context.resources.getString(R.string.error_login_inactive)
                            415 -> context.resources.getString(R.string.error_field)
                            else -> context.resources.getString(R.string.error_server_unknown)
                        }
                    )
                }
            } catch (e: HttpException) {
                Log.d(LOG_TAG, "http exception", e)
            } catch (e: Throwable) {
                Log.d(LOG_TAG, "exception", e)
            }
        } else return@withContext when (postEbolFiles(ebol)) {
                FILE_LOAD_NOT_AUTH -> ApiResponseDTO(notAuthorized = true)
                FILE_LOAD_ERROR -> ApiResponseDTO(partialSuccess = true)
                else -> ApiResponseDTO(null, true)
        }
        return@withContext ApiResponseDTO(null, false, context.resources.getString(R.string.error_connection))
    }

    private suspend fun postEbolFiles(ebol: EbolJoined): Int{
        for (vehicle in ebol.vehiclesList) when (ebol.ebol?.status){
            EbolStatus.DELIVERED.ordinal -> vehicle.deliveryInspect
            else -> vehicle.pickupInspect
        }?.let { inspection ->
            for (image in inspection.imagesList){
                if (image.apiId != null && !image.posted) {
                    val res = postFileInspect(image.inspectImage?.fileUrl, image.apiId!!)
                    if (res == FILE_LOAD_SUCCESS){
                        image.posted = true
                    } else return res
                }
            }
            if (inspection.apiId != null)
                for (attach in inspection.attachmentList){
                    if (attach.posted == false) {
                        val res = postFileInspectAttach(attach.attachmentUrl, attach.attachmentName, inspection.apiId!!)
                        if (res == FILE_LOAD_SUCCESS){
                            attach.posted = true
                        } else return res
                    }
                }
        }
        return FILE_LOAD_SUCCESS
    }

    private suspend fun postFileInspect(fileUri: String?, id: Long): Int{
        val uri: Uri?
        try {
            uri = Uri.parse(fileUri)
        } catch (e: Exception){
            return FILE_LOAD_SUCCESS
        }
        Log.d(LOG_TAG, "$fileUri $id ${uri.scheme}")
        if (uri.scheme == "http" || uri.scheme == "https")
            return FILE_LOAD_SUCCESS
        else try {
            val filePart = when (uri.scheme){
                "content" -> {
                        val input = context.contentResolver.openInputStream(uri)
                        if (input != null)
                            MultipartBody.Part.createFormData(
                                "file",
                                URLEncoder.encode(getContentFilename(uri), "UTF-8"),
                                RequestBody.create(MediaType.parse("image/*"), readBytes(input))
                            )
                        else return FILE_LOAD_ERROR
                    }
                "file" -> {
                        val file = File(uri.path)
                        MultipartBody.Part.createFormData(
                            "file",
                            URLEncoder.encode(file.name, "UTF-8"),
                            RequestBody.create(MediaType.parse("image/*"), file)
                        )
                    }
                else -> return FILE_LOAD_SUCCESS
            }
            val response = retrofitService.fileInspect.queryAsync(
                "Bearer ${global.mToken}",
                id,
                filePart
            ).await()
            if (response.isSuccessful) return FILE_LOAD_SUCCESS
            else if (response.code() == 401) return FILE_LOAD_NOT_AUTH
        } catch (e: HttpException) {
            Log.d(LOG_TAG, "http exception", e)
            return FILE_LOAD_ERROR
        } catch (e: Throwable) {
            Log.d(LOG_TAG, "exception", e)
            return FILE_LOAD_ERROR
        }
        return FILE_LOAD_SUCCESS
    }

    private suspend fun postFileInspectAttach(fileUri: String?, filename: String?, id: Long): Int{
        val uri: Uri?
        try {
            uri = Uri.parse(fileUri)
        } catch (e: Exception){
            return FILE_LOAD_SUCCESS
        }
        Log.d(LOG_TAG, "attach $fileUri ${uri.scheme}  ${getMimeType(fileUri)}")
        if (uri.scheme == "http" || uri.scheme == "https")
            return FILE_LOAD_SUCCESS
        else try {
            val filePart = when (uri.scheme){
                "content" -> {
                    val input = context.contentResolver.openInputStream(uri)
                    if (input != null)
                        MultipartBody.Part.createFormData(
                            "file",
                            URLEncoder.encode(getContentFilename(uri), "UTF-8"),
                            RequestBody.create(MediaType.parse(getMimeType(fileUri)), readBytes(input))
                        )
                    else return FILE_LOAD_ERROR
                }
                "file" -> {
                    val file = File(uri.path)
                    MultipartBody.Part.createFormData(
                        "file",
                        URLEncoder.encode(file.name, "UTF-8"),
                        RequestBody.create(MediaType.parse(getMimeType(fileUri)), file)
                    )

                }
                else -> return FILE_LOAD_SUCCESS
            }
            val response = retrofitService.fileInspectAttach.queryAsync(
                "Bearer ${global.mToken}",
                id,
                filePart
            ).await()
            if (response.isSuccessful) return FILE_LOAD_SUCCESS
            else if (response.code() == 401) return FILE_LOAD_NOT_AUTH
        } catch (e: HttpException) {
            Log.d(LOG_TAG, "http exception", e)
            return FILE_LOAD_ERROR
        } catch (e: Throwable) {
            Log.d(LOG_TAG, "exception", e)
            return FILE_LOAD_ERROR
        }
        return FILE_LOAD_SUCCESS
    }

    override suspend fun performInspection(ebol: EbolJoined): ApiResponseDTO =  withContext(Dispatchers.IO) {
        checkAllConditions()?.let{ return@withContext it }

        if (!ebol.posted) {
            val ebolDTO = ebol.toEbolShortDTO()
            //val a = Gson().toJson(ebolDTO).toString()       //todo remove
            if (ebolDTO != null) try {
                val response = retrofitService.performInspection.queryAsync(
                    "Bearer ${global.mToken}",
                    ebolDTO
                ).await()
                if (response.isSuccessful && response.body() != null) {
                    //spread the response to an existing data model
                    ebol.posted = true
                    val result = response.body() as List<ResponseInspectionDTO>
                    for ((i, vehicle) in ebol.vehiclesList.withIndex())
                        when (ebol.ebol?.status){
                            EbolStatus.DELIVERED.ordinal -> vehicle.deliveryInspect
                            else -> vehicle.pickupInspect
                        }?.let { inspection ->
                        if (i in 0 until result.size) {
                            inspection.apiId = result[i].inspect_id
                            result[i].items?.let { items ->
                                for (item in items)
                                    if (item.index in 0 until inspection.imagesList.size)
                                        inspection.imagesList[item.index!!].apiId = item.id
                            }
                        }
                    }
                    return@withContext when (postEbolFiles(ebol)) {
                        FILE_LOAD_NOT_AUTH -> ApiResponseDTO(notAuthorized = true)
                        FILE_LOAD_ERROR -> ApiResponseDTO(partialSuccess = true)
                        else -> ApiResponseDTO(null, true)
                    }
                } else {
                    Log.d(LOG_TAG, "server error ${response.code()} ${response.message()}")
                    if (response.code() == 401) return@withContext ApiResponseDTO(notAuthorized = true)
                    else return@withContext ApiResponseDTO(
                        when (response.code()) {
                            422 -> try {
                                Gson().fromJson(response.errorBody()?.string(), ErrorResponseDTO::class.java)
                            } catch (e: JsonSyntaxException) {
                                null
                            }
                            else -> null
                        },
                        false,
                        when (response.code()) {
                            403 -> context.resources.getString(R.string.error_login_inactive)
                            415 -> context.resources.getString(R.string.error_field)
                            else -> context.resources.getString(R.string.error_server_unknown)
                        }
                    )

                }
            } catch (e: HttpException) {
                Log.d(LOG_TAG, "http exception", e)
            } catch (e: Throwable) {
                Log.d(LOG_TAG, "exception", e)
            }
        } else return@withContext when (postEbolFiles(ebol)) {
            FILE_LOAD_NOT_AUTH -> ApiResponseDTO(notAuthorized = true)
            FILE_LOAD_ERROR -> ApiResponseDTO(partialSuccess = true)
            else -> ApiResponseDTO(null, true)
        }
        return@withContext ApiResponseDTO(null, false, context.resources.getString(R.string.error_connection))
    }

    override fun getEbolsPaged(status: EbolStatus, addResult: MutableLiveData<ApiResponseDTO>)
            = EbolsDataSourceFactory(addResult, status)


    override suspend fun getEbols(status: EbolStatus,
                                  page: Int,
                                  perPage: Int,
                                  sortField: String,
                                  sortType: String): ApiResponseDTO = withContext(Dispatchers.IO) {
        checkAllConditions("getEbols")?.let{ return@withContext it }
        try {
            val response = retrofitService.getEbols.queryAsync(
                "Bearer ${global.mToken}",
                page,
                perPage,
                sortField,
                sortType,
                status.ordinal
            ).await()
            Log.d(LOG_TAG, "get ebols $page $perPage")
            if (response.isSuccessful && response.body()!= null) {
                val result = (response.body() as List<EbolDTO>).map{ it.toEbolJoined()}
                return@withContext ApiResponseDTO(result, true, null)
            } else {
                Log.d(LOG_TAG, "server error ${response.code()} ${response.message()}")
                if (response.code() == 401) return@withContext ApiResponseDTO(notAuthorized = true)
                return@withContext ApiResponseDTO(
                    null,
                    false,
                    when (response.code()){
                        //401 -> context.resources.getString(R.string.error_token)
                        403 -> context.resources.getString(R.string.error_login_inactive)
                        415 -> context.resources.getString(R.string.error_field)
                        else -> context.resources.getString(R.string.error_server_unknown)
                    })
            }
        } catch (e: HttpException) {
            Log.d(LOG_TAG, "http exception", e)
        } catch (e: Throwable) {
            Log.d(LOG_TAG, "exception", e)
        }
        return@withContext ApiResponseDTO(null, false, context.resources.getString(R.string.error_connection))
    }

    override suspend fun vehicleType(): ApiResponseDTO =  withContext(Dispatchers.IO) {
        checkAllConditions("vehicleType")?.let{ return@withContext it }

        try {
            val response = retrofitService.vehicleType.queryAsync("Bearer ${global.mToken}").await()
            if (response.isSuccessful && response.body()!= null) {
                return@withContext ApiResponseDTO(response.body(), true, null)
            } else {
                Log.d(LOG_TAG, "server error ${response.code()} ${response.message()}")
                if (response.code() == 401) return@withContext ApiResponseDTO(notAuthorized = true)
                return@withContext ApiResponseDTO(
                    null,
                    false,
                    when (response.code()){
                        403 -> context.resources.getString(R.string.error_login_inactive)
                        else -> context.resources.getString(R.string.error_server_unknown)
                    })
            }
        } catch (e: HttpException) {
            Log.d(LOG_TAG, "http exception", e)
        } catch (e: Throwable) {
            Log.d(LOG_TAG, "exception", e)
        }
        return@withContext ApiResponseDTO(null, false, context.resources.getString(R.string.error_connection))
    }

    override suspend fun login(login: String, password: String): ApiResponseDTO =  withContext(Dispatchers.IO) {
        if (!connectivityService.getNetworkAvailability()) return@withContext ApiResponseDTO(noInternet = true)
        try {
            val response = retrofitService.login.queryAsync(LoginDTO(login, password)).await()
            if (response.isSuccessful && response.body()!= null) {
                Log.d(LOG_TAG, "login token ${(response.body() as LoginResponseDTO).expires_in} ${(response.body() as LoginResponseDTO).token}")
                return@withContext ApiResponseDTO(response.body(), true, null)
            } else {
                Log.d(LOG_TAG, "server error ${response.code()}")
                return@withContext ApiResponseDTO(
                    null,
                    false,
                    when (response.code()){
                    403 -> context.resources.getString(R.string.error_login_inactive)
                    404 -> context.resources.getString(R.string.error_login_notfound)
                    else -> context.resources.getString(R.string.error_server_unknown)
                    })
            }
        } catch (e: HttpException) {
            Log.d(LOG_TAG, "http exception", e)
        } catch (e: Throwable) {
            Log.d(LOG_TAG, "exception", e)
        }
        return@withContext ApiResponseDTO(null, false, context.resources.getString(R.string.error_connection))
    }

    override suspend fun forgot(email: String): ApiResponseDTO =  withContext(Dispatchers.IO) {
        if (!connectivityService.getNetworkAvailability()) return@withContext ApiResponseDTO(noInternet = true)
        try {
            val response = retrofitService.forgot.queryAsync(email).await()
            if (response.isSuccessful && response.body()!= null) {
                return@withContext ApiResponseDTO(null, true, null)
            } else {
                Log.d(LOG_TAG, "server error ${response.code()}")
                return@withContext ApiResponseDTO(
                    null,
                    false,
                    when (response.code()){
                        403 -> context.resources.getString(R.string.error_login_inactive)
                        404 -> context.resources.getString(R.string.error_login_notfound)
                        else -> context.resources.getString(R.string.error_server_unknown)
                    })
            }
        } catch (e: HttpException) {
            Log.d(LOG_TAG, "http exception", e)
        } catch (e: Throwable) {
            Log.d(LOG_TAG, "exception", e)
        }
        return@withContext ApiResponseDTO(null, false, context.resources.getString(R.string.error_connection))
    }

    override suspend fun refresh(): ApiResponseDTO =  withContext(Dispatchers.IO) {
        if (isRefreshRunning) {
            Log.d(LOG_TAG, "waiting refresh ${mRefreshJob}")
            var i = 0
            while (mRefreshJob == null && i < 20){
                delay(250)
                i++
            }
            mRefreshJob?.join()
            i = 0
            while (isRefreshRunning && i < 20){
                delay(250)
                i++
            }
            Log.d(LOG_TAG, "join refresh")
            return@withContext ApiResponseDTO(success = true)
        }
        isRefreshRunning = true
        if (!connectivityService.getNetworkAvailability()) { isRefreshRunning = false; return@withContext ApiResponseDTO(noInternet = true)}
        if (global.mToken.isNullOrEmpty()) { isRefreshRunning = false; return@withContext ApiResponseDTO(notAuthorized = true)}
        try {
            Log.d(LOG_TAG, "start refresh")
            mRefreshJob = retrofitService.refresh.queryAsync("Bearer ${global.mToken}")
            mRefreshJob?.let {
                val response = it.await()
                mRefreshJob = null
                if (response.isSuccessful && response.body() != null) {
                    with(response.body() as LoginResponseDTO) {
                        global.mToken = token
                        global.setExpiresAt(expires_in)
                    }
                    Log.d(LOG_TAG, "refresh token ${(response.body() as LoginResponseDTO).expires_in} ${(response.body() as LoginResponseDTO).token}")
                    isRefreshRunning = false
                    return@withContext ApiResponseDTO(success = true)
                } else {
                    Log.d(LOG_TAG, "server error ${response.code()} ${response.message()}")
                    isRefreshRunning = false
                    if (response.code() == 401) return@withContext ApiResponseDTO(notAuthorized = true)
                    return@withContext ApiResponseDTO(
                        null,
                        false,
                        when (response.code()) {
                            403 -> context.resources.getString(R.string.error_login_inactive)
                            404 -> context.resources.getString(R.string.error_login_notfound)
                            else -> context.resources.getString(R.string.error_server_unknown)
                        }
                    )
                }
            }
        } catch (e: HttpException) {
            Log.d(LOG_TAG, "http exception", e)
        } catch (e: Throwable) {
            Log.d(LOG_TAG, "exception", e)
        }
        isRefreshRunning = false
        mRefreshJob = null
        return@withContext ApiResponseDTO(null, false, context.resources.getString(R.string.error_connection))
    }

    override suspend fun logout(): ApiResponseDTO =  withContext(Dispatchers.IO) {
        if (!connectivityService.getNetworkAvailability()) return@withContext ApiResponseDTO(noInternet = true)
        try {
            val response = retrofitService.logout.queryAsync("Bearer ${global.mToken}").await()
            global.mToken = null
            global.mExpiresAt = null
            global.mUser = null
            global.mProfile.postValue(null)
            if (response.isSuccessful) {
                return@withContext ApiResponseDTO(response.body(), true, null)
            } else {
                Log.d(LOG_TAG, "server error ${response.code()} ${response.message()}")
                return@withContext ApiResponseDTO(
                    null,
                    false,
                    when (response.code()){
                        403 -> context.resources.getString(R.string.error_login_inactive)
                        else -> context.resources.getString(R.string.error_server_unknown)
                    })
            }
        } catch (e: HttpException) {
            Log.d(LOG_TAG, "http exception", e)
        } catch (e: Throwable) {
            Log.d(LOG_TAG, "exception", e)
        }
        return@withContext ApiResponseDTO(null, false, context.resources.getString(R.string.error_connection))
    }

    override suspend fun autocompleteZip(value: String, byZip: Boolean): ApiResponseDTO =  withContext(Dispatchers.IO) {
        checkAllConditions()?.let{ return@withContext it }
        try {
            val response = retrofitService.cities.queryAsync("Bearer ${global.mToken}", value).await()
            if (response.isSuccessful && response.body()!= null) {
                Log.d(LOG_TAG, response.body().toString())
                val stateCodesArr = context.resources.getStringArray(R.array.state_codes)
                val stateNamesArr = context.resources.getStringArray(R.array.state_names)
                val result = (response.body() as MutableList<CityApiDTO>).map{
                        var stateIndex = if (it.state != null) stateCodesArr.indexOf(it.state) else 0
                        stateIndex = if (stateIndex==-1) 0 else stateIndex
                        ZipEntryDTO(
                            it.city,
                            stateIndex,
                            stateNamesArr[stateIndex],
                            it.postal
                        )
                }
                return@withContext ApiResponseDTO(result, true, null)
            } else {
                Log.d(LOG_TAG, "server error ${response.code()} ${response.message()}")
                if (response.code() == 401) return@withContext ApiResponseDTO(notAuthorized = true)
                return@withContext ApiResponseDTO(
                    null,
                    false,
                    when (response.code()){
                        403 -> context.resources.getString(R.string.error_login_inactive)
                        415 -> context.resources.getString(R.string.error_field)
                        else -> context.resources.getString(R.string.error_server_unknown)
                    })
            }
        } catch (e: HttpException) {
            Log.d(LOG_TAG, "http exception", e)
        } catch (e: Throwable) {
            Log.d(LOG_TAG, "exception", e)
        }
        return@withContext ApiResponseDTO(null, false, context.resources.getString(R.string.error_connection))
    }

    override suspend fun getCounters(): ApiResponseDTO =  withContext(Dispatchers.IO) {
        checkAllConditions("getCounters")?.let{ return@withContext it }
        try {
            val response = retrofitService.counts.queryAsync(
                "Bearer ${global.mToken}"
            ).await()
            if (response.isSuccessful && response.body()!= null) {
                //global.mCounters.postValue(response.body())
                return@withContext ApiResponseDTO(response.body(), true, null)
            } else {
                Log.d(LOG_TAG, "server error ${response.code()} ${response.message()}")
                if (response.code() == 401) return@withContext ApiResponseDTO(notAuthorized = true)
                return@withContext ApiResponseDTO(
                    null,
                    false,
                    when (response.code()){
                        //401 -> context.resources.getString(R.string.error_token)
                        403 -> context.resources.getString(R.string.error_login_inactive)
                        415 -> context.resources.getString(R.string.error_field)
                        else -> context.resources.getString(R.string.error_server_unknown)
                    })
            }
        } catch (e: HttpException) {
            Log.d(LOG_TAG, "http exception", e)
        } catch (e: Throwable) {
            Log.d(LOG_TAG, "exception", e)
        }
        return@withContext ApiResponseDTO(null, false, context.resources.getString(R.string.error_connection))
    }

    override suspend fun ebolPaid(id: Long?): ApiResponseDTO =  withContext(Dispatchers.IO) {
        checkAllConditions()?.let{ return@withContext it }
        try {
            val response = retrofitService.ebolPaid.queryAsync(
                "Bearer ${global.mToken}",
                IdDTO(id)
            ).await()
            if (response.isSuccessful) {
                return@withContext ApiResponseDTO(null, true, null)
            } else {
                Log.d(LOG_TAG, "server error ${response.code()} ${response.message()}")
                if (response.code() == 401) return@withContext ApiResponseDTO(notAuthorized = true)
                return@withContext ApiResponseDTO(
                    null,
                    false,
                    when (response.code()){
                        //401 -> context.resources.getString(R.string.error_token)
                        403 -> context.resources.getString(R.string.error_login_inactive)
                        415 -> context.resources.getString(R.string.error_field)
                        else -> context.resources.getString(R.string.error_server_unknown)
                    })
            }
        } catch (e: HttpException) {
            Log.d(LOG_TAG, "http exception", e)
        } catch (e: Throwable) {
            Log.d(LOG_TAG, "exception", e)
        }
        return@withContext ApiResponseDTO(null, false, context.resources.getString(R.string.error_connection))
    }

    override suspend fun ebolCancel(id: Long, restore: Boolean): ApiResponseDTO =  withContext(Dispatchers.IO) {
        checkAllConditions()?.let{ return@withContext it }
        try {
            val response = retrofitService.ebolCancel.queryAsync(
                "Bearer ${global.mToken}",
                CancelDTO(
                    id,
                    if (restore) 0 else 1
                )
            ).await()
            if (response.isSuccessful) {
                return@withContext ApiResponseDTO(response.body(), true, null)
            } else {
                Log.d(LOG_TAG, "server error ${response.code()} ${response.message()}")
                if (response.code() == 401) return@withContext ApiResponseDTO(notAuthorized = true)
                return@withContext ApiResponseDTO(
                    null,
                    false,
                    when (response.code()){
                        //401 -> context.resources.getString(R.string.error_token)
                        403 -> context.resources.getString(R.string.error_login_inactive)
                        415 -> context.resources.getString(R.string.error_field)
                        else -> context.resources.getString(R.string.error_server_unknown)
                    })
            }
        } catch (e: HttpException) {
            Log.d(LOG_TAG, "http exception", e)
        } catch (e: Throwable) {
            Log.d(LOG_TAG, "exception", e)
        }
        return@withContext ApiResponseDTO(null, false, context.resources.getString(R.string.error_connection))
    }

    override suspend fun ebolArchive(id: Long, restore: Boolean): ApiResponseDTO =  withContext(Dispatchers.IO) {
        checkAllConditions()?.let{ return@withContext it }
        try {
            val response = retrofitService.ebolArchive.queryAsync(
                "Bearer ${global.mToken}",
                ArchivedDTO(
                    id,
                    if (restore) 0 else 1
                )
            ).await()
            if (response.isSuccessful) {
                return@withContext ApiResponseDTO(null, true, null)
            } else {
                Log.d(LOG_TAG, "server error ${response.code()} ${response.message()}")
                if (response.code() == 401) return@withContext ApiResponseDTO(notAuthorized = true)
                return@withContext ApiResponseDTO(
                    null,
                    false,
                    when (response.code()){
                        //401 -> context.resources.getString(R.string.error_token)
                        403 -> context.resources.getString(R.string.error_login_inactive)
                        415 -> context.resources.getString(R.string.error_field)
                        else -> context.resources.getString(R.string.error_server_unknown)
                    })
            }
        } catch (e: HttpException) {
            Log.d(LOG_TAG, "http exception", e)
        } catch (e: Throwable) {
            Log.d(LOG_TAG, "exception", e)
        }
        return@withContext ApiResponseDTO(null, false, context.resources.getString(R.string.error_connection))
    }

    override suspend fun ebolDelete(id: Long): ApiResponseDTO =  withContext(Dispatchers.IO) {
        checkAllConditions()?.let{ return@withContext it }
        try {
            val response = retrofitService.ebolDelete.queryAsync(
                "Bearer ${global.mToken}",
                id
            ).await()
            if (response.isSuccessful) {
                return@withContext ApiResponseDTO(null, true, null)
            } else {
                Log.d(LOG_TAG, "server error ${response.code()} ${response.message()}")
                if (response.code() == 401) return@withContext ApiResponseDTO(notAuthorized = true)
                return@withContext ApiResponseDTO(
                    null,
                    false,
                    when (response.code()){
                        //401 -> context.resources.getString(R.string.error_token)
                        403 -> context.resources.getString(R.string.error_login_inactive)
                        415 -> context.resources.getString(R.string.error_field)
                        else -> context.resources.getString(R.string.error_server_unknown)
                    })
            }
        } catch (e: HttpException) {
            Log.d(LOG_TAG, "http exception", e)
        } catch (e: Throwable) {
            Log.d(LOG_TAG, "exception", e)
        }
        return@withContext ApiResponseDTO(null, false, context.resources.getString(R.string.error_connection))
    }

    override suspend fun getProfile(): ApiResponseDTO =  withContext(Dispatchers.IO) {
        checkAllConditions("getProfile")?.let{ return@withContext it }
        try {
            val response = retrofitService.getUser.queryAsync(
                "Bearer ${global.mToken}"
            ).await()
            if (response.isSuccessful && response.body()!= null) {
                return@withContext ApiResponseDTO(
                    (response.body() as ProfileDTO).toProfile(),
                    true
                )
            } else {
                Log.d(LOG_TAG, "server error ${response.code()} ${response.message()}")
                if (response.code() == 401) return@withContext ApiResponseDTO(notAuthorized = true)
                return@withContext ApiResponseDTO(
                    null,
                    false,
                    when (response.code()){
                        //401 -> context.resources.getString(R.string.error_token)
                        403 -> context.resources.getString(R.string.error_login_inactive)
                        415 -> context.resources.getString(R.string.error_field)
                        else -> context.resources.getString(R.string.error_server_unknown)
                    })
            }
        } catch (e: HttpException) {
            Log.d(LOG_TAG, "http exception", e)
        } catch (e: Throwable) {
            Log.d(LOG_TAG, "exception", e)
        }
        return@withContext ApiResponseDTO(null, false, context.resources.getString(R.string.error_connection))
    }

    override suspend fun putProfile(profile: Profile): ApiResponseDTO =  withContext(Dispatchers.IO) {
        checkAllConditions()?.let{ return@withContext it }
        try {
            val response = retrofitService.putUser.queryAsync(
                "Bearer ${global.mToken}",
                profile.toProfileDTO()
            ).await()
            if (response.isSuccessful) {
                return@withContext ApiResponseDTO(null, true)
            } else {
                Log.d(LOG_TAG, "server error ${response.code()} ${response.message()}")
                if (response.code() == 401) return@withContext ApiResponseDTO(notAuthorized = true)
                return@withContext ApiResponseDTO(
                    when (response.code()) {
                        422 -> try {
                            Gson().fromJson(response.errorBody()?.string(), ErrorResponseDTO::class.java)
                        } catch (e: JsonSyntaxException) {
                            null
                        }
                        else -> null
                    },
                    false,
                    when (response.code()){
                        //401 -> context.resources.getString(R.string.error_token)
                        403 -> context.resources.getString(R.string.error_login_inactive)
                        415 -> context.resources.getString(R.string.error_field)
                        else -> context.resources.getString(R.string.error_server_unknown)
                    })
            }
        } catch (e: HttpException) {
            Log.d(LOG_TAG, "http exception", e)
        } catch (e: Throwable) {
            Log.d(LOG_TAG, "exception", e)
        }
        return@withContext ApiResponseDTO(null, false, context.resources.getString(R.string.error_connection))
    }

    override suspend fun changePassword(password: String, newPassword: String): ApiResponseDTO =  withContext(Dispatchers.IO) {
        checkAllConditions()?.let{ return@withContext it }
        try {
            val response = retrofitService.putUserPassword.queryAsync(
                "Bearer ${global.mToken}",
                PasswordDTO(
                    password,
                    newPassword
                )
            ).await()
            if (response.isSuccessful) {
                return@withContext ApiResponseDTO(null, true)
            } else {
                Log.d(LOG_TAG, "server error ${response.code()} ${response.message()}")
                if (response.code() == 401) return@withContext ApiResponseDTO(notAuthorized = true)
                return@withContext ApiResponseDTO(
                    when (response.code()) {
                        422 -> try {
                            Gson().fromJson(response.errorBody()?.string(), ErrorResponseDTO::class.java)
                        } catch (e: JsonSyntaxException) {
                            null
                        }
                        else -> null
                    },
                    false,
                    when (response.code()){
                        //401 -> context.resources.getString(R.string.error_token)
                        403 -> context.resources.getString(R.string.error_login_inactive)
                        415 -> context.resources.getString(R.string.error_field)
                        else -> context.resources.getString(R.string.error_server_unknown)
                    })
            }
        } catch (e: HttpException) {
            Log.d(LOG_TAG, "http exception", e)
        } catch (e: Throwable) {
            Log.d(LOG_TAG, "exception", e)
        }
        return@withContext ApiResponseDTO(null, false, context.resources.getString(R.string.error_connection))
    }

    override suspend fun getFile(filename: String, extension: String): Uri? =  withContext(Dispatchers.IO) {
        try {
            val response = retrofitService.files.queryAsync("$filename.$extension").await()
            Log.d(LOG_TAG, "code ${response.code()}")
            if (response.isSuccessful && response.body()!= null) {
                return@withContext files.writeBodyToDisk(response.body()!!, filename, extension)
            } else {
                Log.d(LOG_TAG, "server error ${response.code()}")
            }
        } catch (e: HttpException) {
            Log.d(LOG_TAG, "http exception", e)
        } catch (e: Throwable) {
            Log.d(LOG_TAG, "exception", e)
        }
        throw Exception("The file is not available")
    }

    override suspend fun getCarrierList(): ApiResponseDTO =  withContext(Dispatchers.IO) {
        checkAllConditions()?.let{ return@withContext it }
        try {
            val response = retrofitService.listCarrier.queryAsync(
                "Bearer ${global.mToken}"
            ).await()
            if (response.isSuccessful && response.body()!= null) {
                return@withContext ApiResponseDTO(response.body(), true, null)
            } else {
                Log.d(LOG_TAG, "server error ${response.code()} ${response.message()}")
                if (response.code() == 401) return@withContext ApiResponseDTO(notAuthorized = true)
                return@withContext ApiResponseDTO(
                    null,
                    false,
                    when (response.code()){
                        //401 -> context.resources.getString(R.string.error_token)
                        403 -> context.resources.getString(R.string.error_login_inactive)
                        415 -> context.resources.getString(R.string.error_field)
                        else -> context.resources.getString(R.string.error_server_unknown)
                    })
            }
        } catch (e: HttpException) {
            Log.d(LOG_TAG, "http exception", e)
        } catch (e: Throwable) {
            Log.d(LOG_TAG, "exception", e)
        }
        return@withContext ApiResponseDTO(null, false, context.resources.getString(R.string.error_connection))
    }

    override suspend fun testSMS(carrierId: Int, phone: String): ApiResponseDTO =  withContext(Dispatchers.IO) {
        checkAllConditions()?.let{ return@withContext it }
        try {
            val response = retrofitService.putTestMMS.queryAsync(
                "Bearer ${global.mToken}",
                TestMmsDTO(
                    phone,
                    carrierId
                )
            ).await()
            if (response.isSuccessful) {
                return@withContext ApiResponseDTO(null, true, null)
            } else {
                Log.d(LOG_TAG, "server error ${response.code()} ${response.message()}")
                if (response.code() == 401) return@withContext ApiResponseDTO(notAuthorized = true)
                return@withContext ApiResponseDTO(
                    null,
                    false,
                    when (response.code()){
                        //401 -> context.resources.getString(R.string.error_token)
                        403 -> context.resources.getString(R.string.error_login_inactive)
                        415 -> context.resources.getString(R.string.error_field)
                        else -> context.resources.getString(R.string.error_server_unknown)
                    })
            }
        } catch (e: HttpException) {
            Log.d(LOG_TAG, "http exception", e)
        } catch (e: Throwable) {
            Log.d(LOG_TAG, "exception", e)
        }
        return@withContext ApiResponseDTO(null, false, context.resources.getString(R.string.error_connection))
    }

    override suspend fun putGPS(location: Location): ApiResponseDTO =  withContext(Dispatchers.IO) {
        checkAllConditions()?.let{ return@withContext it }
        try {
            val response = retrofitService.putGPS.queryAsync(
                "Bearer ${global.mToken}",
                GpsDTO(
                    location.longitude,
                    location.latitude
                )
            ).await()
            if (response.isSuccessful) {
                return@withContext ApiResponseDTO(null, true, null)
            } else {
                Log.d(LOG_TAG, "server error ${response.code()} ${response.message()}")
                if (response.code() == 401) return@withContext ApiResponseDTO(notAuthorized = true)
                return@withContext ApiResponseDTO(
                    null,
                    false,
                    when (response.code()){
                        403 -> context.resources.getString(R.string.error_login_inactive)
                        415 -> context.resources.getString(R.string.error_field)
                        else -> context.resources.getString(R.string.error_server_unknown)
                    })
            }
        } catch (e: HttpException) {
            Log.d(LOG_TAG, "http exception", e)
        } catch (e: Throwable) {
            Log.d(LOG_TAG, "exception", e)
        }
        return@withContext ApiResponseDTO(null, false, context.resources.getString(R.string.error_connection))
    }

    private suspend fun checkAllConditions(callFrom: String? = null): ApiResponseDTO? {
        if (!connectivityService.getNetworkAvailability()) return ApiResponseDTO(noInternet = true)
        if (global.mToken.isNullOrEmpty()) return ApiResponseDTO(notAuthorized = true)
        if (global.isTokenExpired())
            if (global.mRememberMe == true) {
                Log.d(LOG_TAG, "refresh in data layer from ${callFrom}")
                val apiResponse = refresh()
                if (!apiResponse.success) return apiResponse
            } else return ApiResponseDTO(notAuthorized = true)
        return null
    }

    @Throws(IOException::class)
    private fun readBytes(inputStream: InputStream): ByteArray {
        val byteBuffer = ByteArrayOutputStream()
        val buffer = ByteArray(1024)
        var len = 0
        while (true) {
            len = inputStream.read(buffer)
            if (len == -1) break
            byteBuffer.write(buffer, 0, len)
        }
        return byteBuffer.toByteArray()
    }

    private fun getContentFilename(uri: Uri): String?{
        if (uri.scheme == "content") {
            var filename: String? = null
            val proj = arrayOf(MediaStore.Images.Media.DISPLAY_NAME)
            try {
                val cursor = context.contentResolver.query(uri, proj, null, null, null)
                if (cursor != null && cursor.count != 0) {
                    val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                    cursor.moveToFirst()
                    filename = cursor.getString(columnIndex)
                }
                cursor?.close()
                return filename
            } catch (e: java.lang.Exception){
                e.printStackTrace()
            }
        }
        return null
    }

    private fun getMimeType(uri: String?): String {
        if (uri != null) {
            val extension = MimeTypeMap.getFileExtensionFromUrl(uri)
            if (extension != null)
                MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)?.let { return it }
        }
        return "text/plain"
    }
}