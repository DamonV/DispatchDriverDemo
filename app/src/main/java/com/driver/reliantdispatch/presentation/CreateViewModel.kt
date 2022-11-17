package com.driver.reliantdispatch.presentation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.driver.reliantdispatch.App
import com.driver.reliantdispatch.R
import com.driver.reliantdispatch.data.dto.ErrorResponseDTO
import com.driver.reliantdispatch.domain.*
import com.driver.reliantdispatch.domain.entities.*
import com.driver.reliantdispatch.domain.entities.joined.EbolJoined
import com.driver.reliantdispatch.domain.entities.joined.InspectImageJoined
import com.driver.reliantdispatch.domain.entities.joined.InspectionJoined
import com.driver.reliantdispatch.domain.entities.joined.VehicleJoined
import com.driver.reliantdispatch.presentation.dto.EventType
import com.driver.reliantdispatch.presentation.dto.PresentationEvent
import com.driver.reliantdispatch.presentation.dto.RequiredField
import com.driver.reliantdispatch.presentation.secondary.CompanyEditViewModel
import com.driver.reliantdispatch.presentation.secondary.ScopedViewModel
import kotlinx.coroutines.launch
import java.lang.Exception


class CreateViewModel: ScopedViewModel(), CompanyEditViewModel {

    val mItem = CustomObservableField<EbolJoined>()
    val mFragmentType = ObservableField(EbolFragmentType.CREATE)
    val mTrailerType = ObservableField<Int>()
    val mSendInvoice = ObservableBoolean()
    val mVehiclesList = ObservableField<MutableList<VehicleJoined>>()
    var mScrollPosition = 0
    val mDriverSignVisibility = ObservableBoolean(false)
    val mProfile = ObservableField<Profile?>()
    val mCreateEbolUseCase = CreateEbolUseCase()


    inner class CustomObservableField<T>: ObservableField<T>(){
        override fun set(value: T){
            super.set(value)
            if (value!=null) {
                val ebolJoined = (value as EbolJoined)
                with(ebolJoined.ebol) {
                    this?.let {
                        trailerType?.let { mTrailerType.set(it) }
                        sendInvoice?.let { mSendInvoice.set(it) }
                    }
                }
                mVehiclesList.set(ebolJoined.vehiclesList)
            }
        }

        override fun get(): T? {
            val value = super.get()
            if (value!=null) {
                val ebolJoined = (value as EbolJoined)
                with(ebolJoined.ebol) {
                    this?.let {
                        trailerType = mTrailerType.get()
                        sendInvoice = mSendInvoice.get()
                    }
                }
                mVehiclesList.get()?.let{ ebolJoined.vehiclesList = it}
            }
            return value
        }
    }

    fun initNewEbol(){
        mItem.set(EbolJoined(
            Ebol()
        ))
            /*EbolJoined(
                Ebol(
                    0,
                    0,
                    "load id 123",
                    0,
                    "\$26,000.00",
                    3,
                    1,
                    "\$10,000.00",
                    null,
                    "dfgfdg",
                    "05-30-2019",
                    "05-31-2019",
                    0,
                    "06-05-2019",
                    3,
                    "06-01-2019",
                    1,
                    "06-06-2019",
                    2,
                    "1|0",
                    0,
                    1,
                    2,
                    null,
                    null,
                    null,
                    null,
                    null,
                    false,
                    "Joe Papa Test\n" +
                            "(949) 422-0070",
                    "Not filled"
                ),
                mutableListOf(
                    Company(
                        0,
                        "Test Shipping Co.",
                        "Radovan",
                        "3570 Las Vegas Boulevard South",
                        "",
                        "87701",
                        "Las Vegas",
                        28,
                        "(562) 523-9852",
                        "",
                        "",
                        "info@wdf.com"
                    )
                ),
                mutableListOf(
                    Company(
                        0,
                        "Test Pickup Co.",
                        "Radovan",
                        "3570 Las Vegas Boulevard South",
                        "",
                        "87701",
                        "Las Vegas",
                        28,
                        "(562) 523-9852",
                        "",
                        "",
                        "info@wdf.com"
                    )
                ),
                mutableListOf(
                    Company(
                        0,
                        "Test Delivery Co.",
                        "Radovan",
                        "3570 Las Vegas Boulevard South",
                        "",
                        "87701",
                        "Las Vegas",
                        29,
                        "5345",
                        "",
                        "(562) 523-9852",
                        "info@wdf.com"
                    )
                ),
                mutableListOf(
                    /*VehicleJoined(
                        Vehicle(0, 0, "2015", "Honda", "Jazz", "1NXBR32E85Z505904"),
                        mutableListOf(InspectionJoined(
                            Inspection(0),
                            mutableListOf(
                                InspectImageJoined(
                                InspectImage(
                                    0,
                                    0,
                                    "04/05/2019 at 09:54",
                                    "1304 Colorado St",
                                    "http://reliant-dispatch.ustage.sharp-dev.net/images/vehicle_type_silhouette/1.jpg"
                                ),
                                mutableListOf()),
                                InspectImageJoined(
                                    InspectImage(
                                        0,
                                        0,
                                        "05/05/2019 at 19:54",
                                        "4465 Colorado St",
                                        "https://upload.wikimedia.org/wikipedia/commons/thumb/3/37/Chrysler-300C.jpg/300px-Chrysler-300C.jpg"
                                    ),
                                    mutableListOf())
                            ),
                            mutableListOf(
                                Attachment(
                                    0,
                                    0,
                                    "PPT",
                                    null,
                                    "test doc file"
                                ),
                                Attachment(
                                    0,
                                    0,
                                    "xls",
                                    null,
                                    "table 2"
                                ))
                        )),
                        mutableListOf())*//*,
                    VehicleJoined(
                        Vehicle(1, 0, "2017", "Mercedes-Benz", "C-klasse IV (W205)", "JHLRD77874C026456", type = 8),
                        mutableListOf(
                            InspectionJoined(
                            Inspection(-1),
                            mutableListOf(/*InspectImageJoined(
                                InspectImage(
                                    -1,
                                    -1,
                                    "04/05/2019 at 09:54",
                                    "1304 Colorado St",
                                    "https://reliantdispatch.com/images/vehicle_type_silhouette/1.jpg"
                                ),
                                mutableListOf()
                            )*/),
                            mutableListOf(Attachment(
                                -1,
                                -1,
                                "DOC",
                                null,
                                "test doc file"
                            ))
                        )
                        ),
                        mutableListOf())*/
                )
            )
        )*/
    }

    fun onStart(){
        mProfile.set(App.component.getGlobal().mProfile.value)
        mDriverSignVisibility.set(false)
    }

    fun onClickScan(v: View){
        viewModelEvent.value = PresentationEvent(
            EventType.NAVIGATE,
            R.id.nav_vin_scanner
        )
    }

    fun onClickEnter(v: View){
        viewModelEvent.value = PresentationEvent(
            EventType.NAVIGATE,
            R.id.nav_vehicle_info
        )
    }

    override fun onEditSection(navId: Int, field: Int) {
        onEditSection(navId, field, -1)
    }

    override fun onEditSection(navId: Int, field: Int, fragmentType: Int) {
        val args = Bundle()
        args.putInt(ARG_FIELD, field)
        if (fragmentType > -1) args.putInt(ARG_2, fragmentType)
        viewModelEvent.value = PresentationEvent(
            EventType.NAVIGATE,
            navId,
            -1,
            null,
            null,
            args
        )
    }

    fun onEditSection(field: RequiredField) {
        viewModelEvent.value = PresentationEvent(
            EventType.SHOW_DIALOG,
            -1,
            R.string.dialog_title_warning,
            context.resources.getString(R.string.error_check_drafted_ebol_required)
        )
        onEditSection(field.fragmentId, field.fieldNumber, field.fragmentType)
    }

    private fun onEditVehicle(index: Int, field: Int) {
        val args = Bundle()
        args.putInt(ARG_FIELD, field)
        args.putInt(ARG_1, index)
        viewModelEvent.value = PresentationEvent(
            EventType.NAVIGATE,
            R.id.nav_vehicle_info,
            args = args
        )
    }

    fun onClickDriverSign(){
        mDriverSignVisibility.set(
            !mDriverSignVisibility.get()
        )
    }

    fun onSaveClick(){
        mItem.get()?.let {
            if (it.ebol?.status == EbolStatus.DRAFTED.ordinal)
                uiScope.launch {
                    val res = mCreateEbolUseCase.saveEbol(it)
                    if (res >= 0) viewModelEvent.value = PresentationEvent(EventType.NAVIGATE_UP)
                    else viewModelEvent.value = PresentationEvent(
                        EventType.SHOW_DIALOG,
                        -1,
                        R.string.dialog_title_error,
                        context.resources.getString(R.string.error_db_writing)
                    )
                }
            else {
                uiScope.launch {
                    mProgressVisibility.set(true)
                    val response = EbolsBackendUseCase().saveEbol(it)
                    mProgressVisibility.set(false)
                    if (response.success) {
                    } else if (response.noInternet) viewModelEvent.value = PresentationEvent(EventType.NO_INTERNET)
                    else if (response.partialSuccess)
                        viewModelEvent.value = PresentationEvent(
                            EventType.REPEAT_ACTION,
                            -1,
                            R.string.dialog_title_error,
                            context.resources.getString(R.string.error_submit_pickup)
                        )
                    else if (response.notAuthorized) viewModelEvent.value = PresentationEvent(EventType.NAVIGATE, R.id.nav_login)
                    else if (response.body is ErrorResponseDTO) {
                        viewModelEvent.value = PresentationEvent(
                            EventType.SHOW_DIALOG,
                            -1,
                            R.string.dialog_title_error,
                            context.resources.getString(R.string.error_submit_pickup_required) + "\n" + response.body.errors.toList()[0].first ?: ""
                        )
                    }
                    else viewModelEvent.value = PresentationEvent(
                        EventType.SHOW_DIALOG,
                        -1,
                        R.string.dialog_title_error,
                        response.errorMsg
                    )
                }
            }
        }
    }

    fun checkEbol(): Boolean{
        mItem.get()?.let { ebolJoined ->
            mCreateEbolUseCase.checkEbol(ebolJoined)?.let{
                onEditSection(it)
                return false
            }

            for ((i, vehicle) in ebolJoined.vehiclesList.withIndex()) {
                var vehicleStr = ""
                vehicle.vehicle?.let { vehicle ->
                    vehicleStr = "at position ${i+1}: ${vehicle.year ?: ""} ${vehicle.make ?: ""} ${vehicle.model ?: ""}"
                    mCreateEbolUseCase.checkVehicle(vehicle)?.let {
                        viewModelEvent.value = PresentationEvent(
                            EventType.SHOW_DIALOG,
                            -1,
                            R.string.dialog_title_warning,
                            context.resources.getString(R.string.error_check_ebol1, vehicleStr)
                        )
                        onEditVehicle(i, it.fieldNumber)
                        return false
                    }
                }

                if (vehicle.pickupInspect == null
                    && (ebolJoined.ebol?.status == EbolStatus.DRAFTED.ordinal || ebolJoined.ebol?.status == EbolStatus.ASSIGNED.ordinal)) {
                    viewModelEvent.value = PresentationEvent(
                        EventType.SHOW_DIALOG,
                        -1,
                        R.string.dialog_title_warning,
                        context.resources.getString(R.string.error_check_ebol2, vehicleStr)
                    )
                    return false
                } else if (vehicle.deliveryInspect == null
                    && ebolJoined.ebol?.status == EbolStatus.PICKED_UP.ordinal) {
                    viewModelEvent.value = PresentationEvent(
                        EventType.SHOW_DIALOG,
                        -1,
                        R.string.dialog_title_warning,
                        context.resources.getString(R.string.error_check_ebol3, vehicleStr)
                    )
                    return false
                }
            }
            return true
        }
        return false
    }

    fun onClickSubmitPickupDelivery(){
        mItem.get()?.let {
            if (it.ebol?.status == EbolStatus.DRAFTED.ordinal)
                uiScope.launch {
                    it.ebol.id = mCreateEbolUseCase.saveEbol(it)
                    perfomSubmitPickupDelivery()
                }
            else perfomSubmitPickupDelivery()
        }
    }

    fun perfomSubmitPickupDelivery(){
        mItem.get()?.let {
            uiScope.launch {
                mProgressVisibility.set(true)
                val status = it.ebol?.status
                val response = EbolsBackendUseCase().postEbol(it)
                mProgressVisibility.set(false)
                if (response.success) {
                    if (status == EbolStatus.DRAFTED.ordinal) {
                        Log.d(LOG_TAG, "id = ${it.ebol.id} delete draft ebol ${mCreateEbolUseCase.deleteEbol(it)}")
                        //App.component.getGlobal().listNeedRefreshByStatus == EbolStatus.DRAFTED
                    }

                    if (status == EbolStatus.PICKED_UP.ordinal)
                        viewModelEvent.value = PresentationEvent(EventType.NAVIGATE_BOTTOM, R.id.nav_bottom_delivered)
                    else viewModelEvent.value = PresentationEvent(EventType.NAVIGATE_BOTTOM, R.id.nav_bottom_picked_up)
                } else if (response.noInternet) viewModelEvent.value = PresentationEvent(EventType.NO_INTERNET)
                else if (response.partialSuccess)
                    viewModelEvent.value = PresentationEvent(
                        EventType.REPEAT_ACTION,
                        -1,
                        R.string.dialog_title_error,
                        context.resources.getString(R.string.error_submit_pickup)
                        )
                else if (response.notAuthorized) viewModelEvent.value = PresentationEvent(EventType.NAVIGATE, R.id.nav_login)
                else if (response.body is ErrorResponseDTO) {
                    viewModelEvent.value = PresentationEvent(
                        EventType.SHOW_DIALOG,
                        -1,
                        R.string.dialog_title_error,
                        context.resources.getString(R.string.error_submit_pickup_required) + "\n" + response.body.errors.toList()[0].first ?: ""
                    )
                }
                else viewModelEvent.value = PresentationEvent(
                    EventType.SHOW_DIALOG,
                    -1,
                    R.string.dialog_title_error,
                    response.errorMsg
                )
            }
        }
    }

    fun removeVehicle(pos: Int){
        mVehiclesList.set(mVehiclesList.get()?.let {
            val newList = mutableListOf<VehicleJoined>()
            newList.addAll(it)
            newList.removeAt(pos)
            newList
        })
    }

    fun onOpenFile(){
        uiScope.launch {
            try {
                val uri = DetailsUseCase().openFile("DispatchSheet", "pdf")
                Log.d(LOG_TAG, "file ready: $uri")

                if (uri!=null) {
                    val newIntent = Intent(Intent.ACTION_VIEW)
                    newIntent.setDataAndType(uri, MimeTypeMap.getSingleton().getMimeTypeFromExtension("pdf"))
                    newIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
                    viewModelEvent.value = PresentationEvent(
                        EventType.INTENT,
                        -1,
                        -1,
                        null,
                        newIntent
                    )
                } else {
                    viewModelEvent.value = PresentationEvent(
                        EventType.SHOW_DIALOG,
                        -1,
                        R.string.dialog_title_error,
                        context.getString(R.string.error_cant_download)
                    )
                }
            } catch (e: Exception){
                viewModelEvent.value = PresentationEvent(
                    EventType.SHOW_DIALOG,
                    -1,
                    R.string.dialog_title_error,
                    e.message
                )
            }
        }
    }
}