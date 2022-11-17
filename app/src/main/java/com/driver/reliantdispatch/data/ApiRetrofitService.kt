package com.driver.reliantdispatch.data

import android.content.Context
import com.driver.reliantdispatch.App
import com.driver.reliantdispatch.data.dto.*
import com.driver.reliantdispatch.data.secondary.BooleanTypeAdapter
import com.driver.reliantdispatch.data.secondary.trustReliantCert
import com.driver.reliantdispatch.domain.URL_API
import com.driver.reliantdispatch.data.dto.ProfileDTO
import com.driver.reliantdispatch.domain.dto.*
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import javax.inject.Inject


const val URL_YA = "https://s325sas.storage.yandex.net/"

class ApiRetrofitService {
    @Inject
    lateinit var context: Context
    init {
        App.component.inject(this)
    }

    private val yandex by lazy {
        Retrofit.Builder()
            .baseUrl(URL_YA)
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val reliantApi by lazy {
        Retrofit.Builder()
            .baseUrl(URL_API)
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create(
                GsonBuilder().registerTypeAdapter(Boolean::class.javaObjectType, BooleanTypeAdapter()).create()
            ))
            .client(
                OkHttpClient
                    .Builder()
                    .trustReliantCert(context)
                    .build())
            .build()
    }

    val files by lazy {
        yandex.create(File::class.java)
    }
    interface File {
        @Streaming
        @GET("rdisk/8606c8c3d93a5d60dc6b7552b8734f7f29c7dcb975f699933571fef76f6f6e0f/5cfa8f8a/fKqInKw3d7bLFOeFnMGnhD5fSJrYuXi8iDHhoHQqxseV9ik-d0H0798QMo9yQ2ov6KAurzhQFE6n4a2LXQ_utyYS9MOFm8lzo6hlwMMC41ar8npumZHI4midPdWhecNq?uid=1130000031087468&disposition=attachment&hash=&limit=0&content_type=application%2Fpdf&fsize=33976&hid=b708c93605dc000a67987b71bc0c8e9a&media_type=document&tknv=v2&etag=6740e91f4e1a4509d1f9541421d3d5ca&rtoken=OkriVfflQjcx&force_default=yes&ycrid=na-8402a7b6b6d85580e23c8e06e9deaebe-downloader4h&ts=58abe40bb7680&s=e885b116a06fbd2d3e371329222675a252aaa7dbf0fe0e04a769850708d7e7f3&pb=U2FsdGVkX19X87niJJUaBP_QLkU0hD63pZxp771t-K33O5Jna1cvjywS6fMTKG_dDqKXztSB9kwf7XyFg-wHUT-v-w6rVNbnAmPVxfX4yYI9CLPOB-rJ1wK3XeRa5Q2W")
        fun queryAsync(
            @Query("filename") filename: String
        ): Deferred<Response<ResponseBody>>
    }


    val cities by lazy {
        reliantApi.create(Cities::class.java)
    }
    interface Cities {
        @GET("list/city")
        fun queryAsync(
            @Header("Authorization") token: String,
            @Query("param") param: String
        ): Deferred<Response<MutableList<CityApiDTO>>>
    }

    val login by lazy {
        reliantApi.create(Login::class.java)
    }
    interface Login {
        @POST("login")
        fun queryAsync(@Body login: LoginDTO): Deferred<Response<LoginResponseDTO>>
    }

    val forgot by lazy {
        reliantApi.create(Forgot::class.java)
    }
    interface Forgot {
        @GET("user/password")
        fun queryAsync(@Query("driver_email") email: String): Deferred<Response<Any>>
    }

    val refresh by lazy {
        reliantApi.create(Refresh::class.java)
    }
    interface Refresh {
        @GET("refresh")
        fun queryAsync(@Header("Authorization") token: String): Deferred<Response<LoginResponseDTO>>
    }

    val logout by lazy {
        reliantApi.create(Logout::class.java)
    }
    interface Logout {
        @GET("logout")
        fun queryAsync(@Header("Authorization") token: String): Deferred<Response<Any>>
    }

    val vehicleType by lazy {
        reliantApi.create(VehicleType::class.java)
    }
    interface VehicleType {
        @GET("list/vehicle-type")
        fun queryAsync(@Header("Authorization") token: String): Deferred<Response<List<VehicleTypeDTO>>>
    }

    val getEbols by lazy {
        reliantApi.create(GetEbols::class.java)
    }
    interface GetEbols {
        @GET("ebol")
        fun queryAsync(
            @Header("Authorization") token: String,
            @Query("page") page: Int,
            @Query("per_page") perPage: Int,
            @Query("sort_field") sortField: String,
            @Query("sort_type") sortType: String,
            @Query("status") status: Int
        ): Deferred<Response<List<EbolDTO>>>
    }

    val postEbol by lazy {
        reliantApi.create(PostEbol::class.java)
    }
    interface PostEbol {
        @POST("ebol")
        @Headers("Accept: application/json")
        fun queryAsync(
            @Header("Authorization") token: String,
            @Body ebol: EbolDTO
        ): Deferred<Response<List<ResponseInspectionDTO>>>
    }

    val fileInspect by lazy {
        reliantApi.create(FileInspect::class.java)
    }
    interface FileInspect {
        @Multipart
        @POST("file/inspect")
        fun queryAsync(
            @Header("Authorization") token: String,
            @Query("id") id: Long,
            @Part filePart: MultipartBody.Part
        ): Deferred<Response<Any>>
    }

    val fileInspectAttach by lazy {
        reliantApi.create(FileInspectAttach::class.java)
    }
    interface FileInspectAttach {
        @Multipart
        @POST("file/inspect-attach")
        fun queryAsync(
            @Header("Authorization") token: String,
            @Query("id") id: Long,
            @Part filePart: MultipartBody.Part
        ): Deferred<Response<Any>>
    }

    val performInspection by lazy {
        reliantApi.create(PerformInspection::class.java)
    }
    interface PerformInspection {
        @PUT("ebol/perform-inspection")
        @Headers("Accept: application/json")
        fun queryAsync(
            @Header("Authorization") token: String,
            @Body ebol: EbolShortDTO
        ): Deferred<Response<List<ResponseInspectionDTO>>>
    }

    val counts by lazy {
        reliantApi.create(Counts::class.java)
    }
    interface Counts {
        @GET("ebol/counts")
        fun queryAsync(
            @Header("Authorization") token: String
        ): Deferred<Response<CountersDTO>>
    }

    val ebolPaid by lazy {
        reliantApi.create(EbolPaid::class.java)
    }
    interface EbolPaid {
        @PUT("ebol/paid")
        @Headers("Accept: application/json")
        fun queryAsync(
            @Header("Authorization") token: String,
            @Body ebol: IdDTO
        ): Deferred<Response<Any>>
    }

    val ebolCancel by lazy {
        reliantApi.create(EbolCancel::class.java)
    }
    interface EbolCancel {
        @PUT("ebol/cancel")
        @Headers("Accept: application/json")
        fun queryAsync(
            @Header("Authorization") token: String,
            @Body dto: CancelDTO
        ): Deferred<Response<EbolShortestDTO>>
    }

    val ebolArchive by lazy {
        reliantApi.create(EbolArchive::class.java)
    }
    interface EbolArchive {
        @PUT("ebol/archive")
        @Headers("Accept: application/json")
        fun queryAsync(
            @Header("Authorization") token: String,
            @Body dto: ArchivedDTO
        ): Deferred<Response<Any>>
    }

    val ebolDelete by lazy {
        reliantApi.create(EbolDelete::class.java)
    }
    interface EbolDelete {
        @DELETE("ebol")
        @Headers("Accept: application/json")
        fun queryAsync(
            @Header("Authorization") token: String,
            @Query("id") id: Long
        ): Deferred<Response<Any>>
    }

    val getUser by lazy {
        reliantApi.create(GetUser::class.java)
    }
    interface GetUser {
        @GET("user")
        fun queryAsync(
            @Header("Authorization") token: String
        ): Deferred<Response<ProfileDTO>>
    }

    val putUser by lazy {
        reliantApi.create(PutUser::class.java)
    }
    interface PutUser {
        @PUT("user")
        @Headers("Accept: application/json")
        fun queryAsync(
            @Header("Authorization") token: String,
            @Body dto: ProfileDTO
        ): Deferred<Response<Any>>
    }

    val putUserPassword by lazy {
        reliantApi.create(PutUserPassword::class.java)
    }
    interface PutUserPassword {
        @PUT("user/password")
        @Headers("Accept: application/json")
        fun queryAsync(
            @Header("Authorization") token: String,
            @Body dto: PasswordDTO
        ): Deferred<Response<Any>>
    }

    val listCarrier by lazy {
        reliantApi.create(ListCarrier::class.java)
    }
    interface ListCarrier {
        @GET("list/carrier")
        fun queryAsync(
            @Header("Authorization") token: String
        ): Deferred<Response<List<CarrierDTO>>>
    }

    val putTestMMS by lazy {
        reliantApi.create(PutTestMMS::class.java)
    }
    interface PutTestMMS {
        @PUT("user/test-mms")
        @Headers("Accept: application/json")
        fun queryAsync(
            @Header("Authorization") token: String,
            @Body dto: TestMmsDTO
        ): Deferred<Response<Any>>
    }

    val putGPS by lazy {
        reliantApi.create(PutGPS::class.java)
    }
    interface PutGPS {
        @PUT("user/gps")
        @Headers("Accept: application/json")
        fun queryAsync(
            @Header("Authorization") token: String,
            @Body dto: GpsDTO
        ): Deferred<Response<Any>>
    }
}