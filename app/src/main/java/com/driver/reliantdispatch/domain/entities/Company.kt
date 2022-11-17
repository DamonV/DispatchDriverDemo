package com.driver.reliantdispatch.domain.entities

import android.telephony.PhoneNumberFormattingTextWatcher
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.driver.reliantdispatch.App
import com.driver.reliantdispatch.domain.boundaries.getSafe
import com.driver.reliantdispatch.presentation.secondary.PhoneTextWatcher


@Entity(tableName = "companies")
data class Company (
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    var companyName: String? = null,
    var contact: String? = null,
    var address: String? = null,
    var address2: String? = null,
    var zip: String? = null,
    var city: String? = null,
    var state: Int? = null,
    var phone: String? = null,
    var phone2: String? = null,
    var cellPhone: String? = null,
    var email: String? = null,
    var addInfo: String? = null
){
    companion object {
        @JvmStatic
        fun addressShort(item: Company?): String {
            var result = ""
            item?.let {
                val stateArr = App.component.getResourcesGateway().stateCodesArray
                result = (if (it.state!=null) stateArr.getSafe(it.state) else "") +
                        (if (it.city.isNullOrEmpty()) "" else " - ${it.city}") +
                        (if (it.zip.isNullOrEmpty()) "" else " - ${it.zip}")
            }
            return result
        }

        @JvmStatic
        fun addressFull(item: Company?): String {
            var result = ""
            item?.let {
                val stateArr = App.component.getResourcesGateway().stateNamesArray
                result = (if (it.address.isNullOrEmpty()) "" else "${it.address}, ") +
                        (if (it.address2.isNullOrEmpty()) "" else "${it.address2}, ") +
                        (if (it.city.isNullOrEmpty()) "" else "${it.city}, ") +
                        (if (it.state == null) "" else "${stateArr.getSafe(it.state)}, ")
                if (it.zip.isNullOrEmpty() && result.endsWith(", ")) {
                    result = result.substring(0, result.length - 2)
                } else result += "${it.zip}"
            }
            return result
        }

        @JvmStatic
        fun addressTextView(item: Company?): String {
            var result = ""
            item?.let {
                val stateArr = App.component.getResourcesGateway().stateCodesArray
                result = (if (it.address.isNullOrEmpty()) "" else "${it.address} - ") +
                        (if (it.city.isNullOrEmpty()) "" else "${it.city} - ") +
                        (if (it.state == null) "" else "${stateArr.getSafe(it.state)} - ")
                if (it.zip.isNullOrEmpty() && result.endsWith(" - ")) {
                    result = result.substring(0, result.length - 3)
                } else result += "${it.zip}"
            }
            return result
        }

        @JvmStatic
        fun getPhoneNumberWatcher() = PhoneNumberFormattingTextWatcher()

        @JvmStatic
        fun getPhoneWatcher(format: String) = PhoneTextWatcher.getInstance(format) //PhoneNumberFormattingTextWatcher()
    }
}