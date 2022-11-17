package com.driver.reliantdispatch.domain.entities

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.driver.reliantdispatch.App
import com.driver.reliantdispatch.R
import com.driver.reliantdispatch.data.toBitmap
import java.math.BigDecimal
import java.text.DecimalFormat
import java.util.Locale


@Entity(tableName = "ebols", foreignKeys = [
    ForeignKey(entity = Company::class, parentColumns = arrayOf("id"), childColumns = arrayOf("shipperCompanyId"), onDelete = ForeignKey.SET_DEFAULT),
    ForeignKey(entity = Company::class, parentColumns = arrayOf("id"), childColumns = arrayOf("pickUpCompanyId"), onDelete = ForeignKey.SET_DEFAULT),
    ForeignKey(entity = Company::class, parentColumns = arrayOf("id"), childColumns = arrayOf("deliveryCompanyId"), onDelete = ForeignKey.SET_DEFAULT)
])
data class Ebol (
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,

    var status: Int? = 0,
    var loadId: String? = null,
    var trailerType: Int? = 0,
    var paymentAmount: String? = null,
    var paymentMethod: Int? = null,
    var paymentType: Int? = null,
    var paymentAmountReceived: String? = null,
    var paymentDeliveryAmountReceived: String? = null,
    var comment: String? = null,

    var shipDate: String? = null,
    var pickUpDate: String? = null,
    var pickUpDateType: Int? = null,
    var deliveryDate: String? = null,
    var deliveryDateType: Int? = null,

    var delayedPickUpDate: String? = null,
    var delayedPickUpDateType: Int? = null,
    var delayedDeliveryDate: String? = null,
    var delayedDeliveryDateType: Int? = null,
    var delayReasons: String? = null,                  //array serialized to string with separator "|"  For instance - "weather related|mechanical issue(s)"

    var shipperCompanyId: Long? = null,             // foreign-key in "Company" table (1:1 relationship)
    var pickUpCompanyId: Long? = null,              // foreign-key in "Company" table (1:1 relationship)
    var deliveryCompanyId: Long? = null,            // foreign-key in "Company" table (1:1 relationship)

    var customerName: String? = null,
    var customerEmail: String? = null,
    var customerEmail2: String? = null,
    var customerEmail3: String? = null,
    //var customerSignatureFile: String? = null,     //local file URL
    var customerSignature: String? = null,          //base64 encoded string - decoded when need showing
    var customerAvailability: Boolean? = true,

    var driverSignatureDesc: String? = null,    //todo delete
    var additionalInfo: String? = null,
    var sendInvoice: Boolean? = false,

    var user: String? = App.component.getGlobal().mUser
){
    val statusE: EbolStatus?
        get() = status?.let{
            try {
                EbolStatus.values()[it]
            } catch(e: IndexOutOfBoundsException) {null}
        }

    fun delayReasonsArr() =
        delayReasons?.split("|")?.map{
            try {
                it.toInt()
            }catch (e:NumberFormatException){
                0
            }}?.toTypedArray()

    fun setDelayReasonsFromArr(arr: Array<Int>?) = arr?.let{
        var result = ""
        for (index in it) result += "$index|"
        if (result.isNotEmpty()) result = result.substring(0, result.length - 1)
        delayReasons = result
    }

    companion object {
        @JvmStatic
        fun delayReasons(item: Ebol?): String {
            val context = App.component.getContext()
            val delayReasonsArray = context.resources.getStringArray(R.array.delay_reasons)
            var result = ""
            item?.let {
                it.delayReasonsArr()?.let {
                    for (reason in it) result += "${delayReasonsArray[reason]}; "
                    if (result.isNotEmpty()) result = result.substring(0, result.length - 2)
                }
            }
            return result
        }

        @JvmStatic
        fun getCustomerTextView(item: Ebol?): String {
            var result: String = ""
            item?.let {
                if (it.customerAvailability == true) {
                    if (!it.customerName.isNullOrEmpty()) result = " - ${it.customerName}"
                    if (!it.customerEmail.isNullOrEmpty()) result += " - ${it.customerEmail}"
                    if (!it.customerEmail2.isNullOrEmpty()) result += " - ${it.customerEmail2}"
                    if (!it.customerEmail3.isNullOrEmpty()) result += " - ${it.customerEmail3}"
                    if (!result.isEmpty()) result = result.substring(3)
                } //else result = App.component.getContext().resources.getString(R.string.label_not_available)
            }
            return result
        }

        @JvmStatic
        fun getSignatureBitmap(item: Ebol?): Drawable? {
            item?.customerSignature?.let {
                return BitmapDrawable(App.component.getContext().resources, it.toBitmap())
            }
            return null
        }

        @JvmStatic
        fun getBalanceRemaining(item: Ebol?): String = item?.let{
            getBalanceRemaining(it.paymentAmount, it.paymentAmountReceived)
        } ?: ""

        @JvmStatic
        fun getBalanceRemaining(paymentAmount: String?, paymentAmountReceived: String?): String = try {
                val dot = (DecimalFormat.getInstance(Locale.US) as DecimalFormat).decimalFormatSymbols.decimalSeparator
                val a = BigDecimal(paymentAmount?.replace("[^0-9^$dot]".toRegex(), "")?.replace("\\$dot".toRegex(), ".") ?: "0")
                val b = BigDecimal(paymentAmountReceived?.replace("[^0-9^$dot]".toRegex(), "")?.replace("\\$dot".toRegex(), ".") ?: "0")
                "$" + DecimalFormat("#,###.00").format(a - b)
            } catch (e: NumberFormatException) {
                "-"
            }
    }
}