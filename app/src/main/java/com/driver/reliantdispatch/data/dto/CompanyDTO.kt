package com.driver.reliantdispatch.data.dto

import com.google.gson.annotations.SerializedName

data class CompanyDTO (
    @SerializedName("company_name")
    var companyName: String? = null,

    @SerializedName("contact_name")
    var contact: String? = null,

    @SerializedName("address1")
    var address: String? = null,

    var address2: String? = null,

    @SerializedName("zip_code")
    var zip: String? = null,

    var city: String? = null,

    var state: String? = null,             //Int

    @SerializedName("phone1")
    var phone: String? = null,

    @SerializedName("phone2")
    var phone2: String? = null,

    @SerializedName("cell_phone")
    var cellPhone: String? = null,

    var email: String? = null,

    @SerializedName("additional_info")
    var addInfo: String? = null
)