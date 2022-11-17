package com.driver.reliantdispatch.presentation.sections

import android.util.Log
import android.view.View
import android.widget.Filter
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.driver.reliantdispatch.domain.LOG_TAG
import com.driver.reliantdispatch.R
import com.driver.reliantdispatch.domain.AutocompleteUseCase
import com.driver.reliantdispatch.domain.CompanyUseCase
import com.driver.reliantdispatch.domain.dto.ZipEntryDTO
import com.driver.reliantdispatch.domain.entities.Company
import com.driver.reliantdispatch.domain.entities.Ebol
import com.driver.reliantdispatch.domain.entities.joined.EbolJoined
import com.driver.reliantdispatch.domain.getActivity
import com.driver.reliantdispatch.presentation.dto.EventType
import com.driver.reliantdispatch.presentation.dto.PresentationEvent
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class ShipperConsignorViewModel : SectionViewModel(){

    override var mItem = CustomObservableField<EbolJoined?>()
    var mInitial: Company? = null

    inner class CustomObservableField<T>: ObservableField<T>(){
        override fun set(value: T){
            super.set(value)
            (value as EbolJoined?)?.shipperComp?.run{
                mInitial = this
                companyName?.let { mShipperCompany.set(it) }
                contact?.let { mShipperContact.set(it) }
                address?.let { mAddress.set(it) }
                address2?.let { mAddress2.set(it) }
                zip?.let { mZip.set(it) }
                city?.let { mCity.set(it) }
                state?.let { mState.set(it) }

                phone?.let { mPhone.set(it) }
                phone2?.let { mPhone2.set(it) }
                cellPhone?.let { mCellPhone.set(it) }
                email?.let { mEmail.set(it) }
            }
        }

        override fun get(): T? {
            val value = super.get()
            if (value!=null) {
                if ((value as EbolJoined).shipperComp == null) (value as EbolJoined).shipperCompany.add(Company())
                with((value as EbolJoined).shipperComp) {
                    this?.let {
                        companyName = mShipperCompany.get()
                        contact = mShipperContact.get()
                        address = mAddress.get()
                        address2 = mAddress2.get()
                        zip = mZip.get()
                        city = mCity.get()
                        state = mState.get()

                        phone = mPhone.get()
                        phone2 = mPhone2.get()
                        cellPhone = mCellPhone.get()
                        email = mEmail.get()
                    }
                }
            }
            return value
        }
    }

    val mShipperCompany = ObservableField<String>()
    val mShipperContact = ObservableField<String>()
    val mAddress = ObservableField<String>()
    val mAddress2 = ObservableField<String>()
    val mZip = ObservableField<String>()
    val mCity = ObservableField<String>()
    val mState = ObservableInt()

    val mPhone = ObservableField<String>()
    val mPhone2 = ObservableField<String>()
    val mCellPhone = ObservableField<String>()
    val mEmail = ObservableField<String>()


    val mZipDataset = ObservableField<List<ZipEntryDTO>>()
    val mFilterZip = object : Filter(){
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            Log.d(LOG_TAG, "autocompl: $constraint")

            val result = FilterResults()
            if (constraint!=null) {
                runBlocking {
                    val response = AutocompleteUseCase().autocompleteZip(constraint.toString())
                    if (response.success){
                        val list = response.body as List<ZipEntryDTO>
                        result.values = list
                        result.count = list.size
                    }
                    else if (response.noInternet) viewModelEvent.postValue(PresentationEvent(
                        EventType.SHOW_TOAST,
                        dialogMsg = context.resources.getString(R.string.title_no_internet)
                    ))
                    else if (response.notAuthorized) viewModelEvent.postValue(PresentationEvent(EventType.NAVIGATE, R.id.nav_login))
                    else viewModelEvent.postValue(PresentationEvent(
                        EventType.SHOW_TOAST,
                        dialogMsg = response.errorMsg
                    ))
                }
            }
            return result
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            results?.let {
                mZipDataset.set(it.values as? List<ZipEntryDTO>)
            }
        }

        override fun convertResultToString(resultValue: Any?): CharSequence {
            return (resultValue as? ZipEntryDTO)?.postal ?: ""
        }
    }
    fun onZipItemClick(pos: Int){
        mZipDataset.get()?.let{
            val entry = it[pos]
            mCity.set(entry.city)
            mState.set(entry.stateIndex)
        }
    }


    val mCityDataset = ObservableField<List<ZipEntryDTO>>()
    val mFilterCity = object : Filter(){
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            Log.d(LOG_TAG, "autocompl: $constraint")

            val result = FilterResults()
            if (constraint!=null) {
                runBlocking {
                    val response = AutocompleteUseCase().autocompleteCity(constraint.toString())
                    if (response.success){
                        val list = response.body as List<ZipEntryDTO>
                        result.values = list
                        result.count = list.size
                    }
                    else if (response.noInternet) viewModelEvent.postValue(PresentationEvent(
                        EventType.SHOW_TOAST,
                        dialogMsg = context.resources.getString(R.string.title_no_internet)
                    ))
                    else if (response.notAuthorized) viewModelEvent.postValue(PresentationEvent(EventType.NAVIGATE, R.id.nav_login))
                    else viewModelEvent.postValue(PresentationEvent(
                        EventType.SHOW_TOAST,
                        dialogMsg = response.errorMsg
                    ))
                }
            }
            return result
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            results?.let {
                mCityDataset.set(it.values as? List<ZipEntryDTO>)
            }
        }

        override fun convertResultToString(resultValue: Any?): CharSequence {
            return (resultValue as? ZipEntryDTO)?.city ?: ""
        }
    }
    fun onCityItemClick(pos: Int){
        mCityDataset.get()?.let{
            val entry = it[pos]
            mZip.set(entry.postal)
            mState.set(entry.stateIndex)
        }
    }

    override fun isFormHasChanged(): Boolean{
        (mInitial ?: Company(state = 0)).run {
            return companyName != mShipperCompany.get()
                    || contact != mShipperContact.get()
                    || address != mAddress.get()
                    || address2 != mAddress2.get()
                    || zip != mZip.get()
                    || city != mCity.get()
                    || state != mState.get()
                    || phone != mPhone.get()
                    || phone2 != mPhone2.get()
                    || cellPhone != mCellPhone.get()
                    || email != mEmail.get()
        }
        return false
    }

    fun onClickLocateHome(v: View){
        uiScope.launch {
            val addr = CompanyUseCase().getCurrentLocationAddress(v.context.getActivity())
            if (addr!=null) {
                with(addr) {
                    mAddress.set(address)
                    mZip.set(zip)
                    mCity.set(city)
                    mState.set(state)
                }
            } else {
                viewModelEvent.value = PresentationEvent(
                    EventType.SHOW_DIALOG,
                    -1,
                    R.string.dialog_title_error,
                    context.getString(R.string.error_unavailable_location)
                )
            }
        }
    }

}