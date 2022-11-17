package com.driver.reliantdispatch.data

import android.content.Context
import android.content.res.Resources
import android.util.Log
import com.driver.reliantdispatch.App
import com.driver.reliantdispatch.R
import com.driver.reliantdispatch.domain.LOG_TAG
import com.driver.reliantdispatch.domain.boundaries.ApiGateway
import com.driver.reliantdispatch.domain.boundaries.ResourcesGateway
import com.driver.reliantdispatch.domain.dto.VehicleTypeDTO
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class ResourcesGatewayImpl: ResourcesGateway {
    @Inject
    lateinit var context: Context

    @Inject
    lateinit var api: ApiGateway

    init {
        App.component.inject(this)
    }

    override val colorArray: Array<String> by lazy {
        try {
            context.resources.getStringArray(R.array.exterior_colors_names)
        } catch (e: Resources.NotFoundException) {
            arrayOf<String>()
        }
    }

    override val trailerTypeArray: Array<String> by lazy {
        try {
            context.resources.getStringArray(R.array.trailer_type)
        } catch (e: Resources.NotFoundException) {
            arrayOf<String>()
        }
    }

    override val vehicleDriveTypeArray: Array<String> by lazy {
        try {
            context.resources.getStringArray(R.array.vehicle_drive_type)
        } catch (e: Resources.NotFoundException) {
            arrayOf<String>()
        }
    }

    override val paymentTypeShortArray: Array<String> by lazy {
        try {
            context.resources.getStringArray(R.array.payment_type_short)
        } catch (e: Resources.NotFoundException) {
            arrayOf<String>()
        }
    }

    override val paymentMethodArray: Array<String> by lazy {
        try {
            context.resources.getStringArray(R.array.payment_method)
        } catch (e: Resources.NotFoundException) {
            arrayOf<String>()
        }
    }

    override val datesTypesArray: Array<String> by lazy {
        try {
            context.resources.getStringArray(R.array.dates_types)
        } catch (e: Resources.NotFoundException) {
            arrayOf<String>()
        }
    }

    override val stateCodesArray: Array<String> by lazy {
        try {
            context.resources.getStringArray(R.array.state_codes)
        } catch (e: Resources.NotFoundException) {
            arrayOf<String>()
        }
    }

    override val stateNamesArray: Array<String> by lazy {
        try {
            context.resources.getStringArray(R.array.state_names)
        } catch (e: Resources.NotFoundException) {
            arrayOf<String>()
        }
    }

    override val vehicleTypesArray: Array<String> by lazy {
        try {
            context.resources.getStringArray(R.array.vehicle_types)
        } catch (e: Resources.NotFoundException) {
            arrayOf<String>()
        }
    }

    override val driveTypesVinauditArray: Array<String> by lazy {
        try {
            context.resources.getStringArray(R.array.vehicle_drive_type_vinaudit)
        } catch (e: Resources.NotFoundException) {
            arrayOf<String>()
        }
    }

    override val damageKindsArray: Array<String> by lazy {
        try {
            context.resources.getStringArray(R.array.damage_kinds)
        } catch (e: Resources.NotFoundException) {
            arrayOf<String>()
        }
    }

    override val damageKindsShortArray: Array<String> by lazy {
        try {
            context.resources.getStringArray(R.array.damage_kinds_short)
        } catch (e: Resources.NotFoundException) {
            arrayOf<String>()
        }
    }

    override val additionalInspectionShortArray: Array<String> by lazy {
        try {
            context.resources.getStringArray(R.array.additional_inspection_short)
        } catch (e: Resources.NotFoundException) {
            arrayOf<String>()
        }
    }

    override val yesNoArray: Array<String> by lazy {
        try {
            context.resources.getStringArray(R.array.yes_no)
        } catch (e: Resources.NotFoundException) {
            arrayOf<String>()
        }
    }

    override var vehicleTypesApiList: List<VehicleTypeDTO>? = null
        get() {
            if (field == null) runBlocking {
                val response = api.vehicleType()
                if (response.success && response.body != null) {
                    field = response.body as List<VehicleTypeDTO>
                    Log.d(LOG_TAG, "get vehicleTypesApiList $field")
                }
            }
            return field
        }

}