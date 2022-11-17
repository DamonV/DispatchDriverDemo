package com.driver.reliantdispatch.data

import com.driver.reliantdispatch.App
import com.driver.reliantdispatch.data.dto.*
import com.driver.reliantdispatch.domain.boundaries.getSafe
import com.driver.reliantdispatch.domain.entities.*
import com.driver.reliantdispatch.domain.entities.joined.EbolJoined
import com.driver.reliantdispatch.domain.entities.joined.InspectImageJoined
import com.driver.reliantdispatch.domain.entities.joined.InspectionJoined
import com.driver.reliantdispatch.domain.entities.joined.VehicleJoined
import java.math.BigDecimal
import kotlin.reflect.full.memberProperties
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


fun EbolJoined.toEbolDTO(): EbolDTO? {
    if (this.ebol != null) {
        val ebolDTO = this.ebol.toEbolDTO()

        shipperComp?.let{ ebolDTO.shipperCompany = it.toCompanyDTO() }
        pickUpComp?.let{ ebolDTO.pickUpCompany = it.toCompanyDTO() }
        deliveryComp?.let{ ebolDTO.deliveryCompany = it.toCompanyDTO() }

        ebolDTO.vehiclesList = vehiclesList.map { it.toVehicleDTO() ?: VehicleDTO()}.toMutableList()

        return ebolDTO
    } else return null
}

fun Ebol.toEbolDTO(): EbolDTO {

    val ebolDTO = with(::EbolDTO) {
        val propertiesByName = Ebol::class.memberProperties.associateBy { it.name }
        callBy(args = parameters.associateWith { parameter ->
            val property = propertiesByName[parameter.name]
            if (property!=null && parameter.type == property.returnType)
                property.get(this@toEbolDTO)
            else null
        })
    }

    val res = App.component.getResourcesGateway()
    
    ebolDTO.shipDateStr = shipDate?.toAPIFormat()
    ebolDTO.pickUpDateStr = pickUpDate?.toAPIFormat()
    ebolDTO.deliveryDateStr = deliveryDate?.toAPIFormat()
    ebolDTO.delayedPickUpDateStr = delayedPickUpDate?.toAPIFormat()
    ebolDTO.delayedDeliveryDateStr = delayedDeliveryDate?.toAPIFormat()

    ebolDTO.trailerType = res.trailerTypeArray.getSafe(trailerType)
    ebolDTO.pickUpDateType = res.datesTypesArray.getSafe(pickUpDateType)
    ebolDTO.deliveryDateType = res.datesTypesArray.getSafe(deliveryDateType)
    ebolDTO.delayedPickUpDateType = res.datesTypesArray.getSafe(delayedPickUpDateType)
    ebolDTO.delayedDeliveryDateType = res.datesTypesArray.getSafe(delayedDeliveryDateType)
    ebolDTO.delayReasons = delayReasonsArr()

    ebolDTO.payment = PaymentDTO(
        paymentAmount.toBigDecimal(),
        res.paymentMethodArray.getSafe(paymentMethod),
        res.paymentTypeShortArray.getSafe(paymentType),
        paymentAmountReceived.toBigDecimal()
    )

    //val signatureBytes = customerSignatureFile?.let{ App.component.getFilesGateway().getFileAsBytes(it) }
    ebolDTO.customer = CustomerDTO(
        customerName,
        mutableListOf<String>().let{
            if(customerEmail!=null) it.add(customerEmail!!)
            if(customerEmail2!=null) it.add(customerEmail2!!)
            if(customerEmail3!=null) it.add(customerEmail3!!)
            it
        },
        customerSignature, //signatureBytes?.let{ Base64.encodeToString(it, Base64.DEFAULT) },
        customerAvailability,
        customerAvailability?.let{ (!it) }
    )
    return ebolDTO
}

fun EbolJoined.toEbolShortDTO(): EbolShortDTO? {
    if (this.ebol != null) {
        val ebolDTO = this.ebol.toEbolShortDTO()

        ebolDTO.vehiclesList = vehiclesList.map { it.toVehicleShortDTO(ebolDTO.status) ?: VehicleShortDTO()}.toMutableList()

        return ebolDTO
    } else return null
}

fun Ebol.toEbolShortDTO(): EbolShortDTO {

    val ebolDTO = EbolShortDTO(
        id,
        status,
        /*when (status){
            EbolStatus.PICKED_UP.ordinal -> EbolStatus.DELIVERED.ordinal
            else -> EbolStatus.PICKED_UP.ordinal
        },*/
        sendInvoice ?: false,
        additionalInfo
    )

    val res = App.component.getResourcesGateway()

    ebolDTO.delayedPickUpDateStr = delayedPickUpDate?.toAPIFormat()
    ebolDTO.delayedDeliveryDateStr = delayedDeliveryDate?.toAPIFormat()
    ebolDTO.delayedPickUpDateType = res.datesTypesArray.getSafe(delayedPickUpDateType)
    ebolDTO.delayedDeliveryDateType = res.datesTypesArray.getSafe(delayedDeliveryDateType)
    ebolDTO.delayReasons = delayReasonsArr()

    ebolDTO.payment = PaymentDTO(
        //paymentAmount.toBigDecimal(),
        null,
        res.paymentMethodArray.getSafe(paymentMethod),
        res.paymentTypeShortArray.getSafe(paymentType),
        paymentAmountReceived.toBigDecimal(),
        paymentDeliveryAmountReceived.toBigDecimal(),
        comment
    )

    /*val signatureBytes = if (customerSignature != null) customerSignature?.toByteArray()
    else customerSignatureFile?.let{ App.component.getFilesGateway().getFileAsBytes(it) }*/
    ebolDTO.customer = CustomerDTO(
        customerName,
        mutableListOf<String>().let{
            if(customerEmail!=null) it.add(customerEmail!!)
            if(customerEmail2!=null) it.add(customerEmail2!!)
            if(customerEmail3!=null) it.add(customerEmail3!!)
            it
        },
        customerSignature, //signatureBytes?.let{ Base64.encodeToString(it, Base64.DEFAULT) },
        customerAvailability,
        customerAvailability?.let{ (!it) }
    )
    return ebolDTO
}


fun Company.toCompanyDTO(): CompanyDTO {
    val companyDTO = with(::CompanyDTO) {
        val propertiesByName = Company::class.memberProperties.associateBy { it.name }
        callBy(args = parameters.associateWith { parameter ->
            val property = propertiesByName[parameter.name]
            if (property != null && parameter.type == property.returnType)
                property.get(this@toCompanyDTO)
            else null
        })
    }
    companyDTO.state = App.component.getResourcesGateway().stateCodesArray.getSafe(state)
    return companyDTO
}

fun VehicleJoined.toVehicleDTO(): VehicleDTO? {
    if (this.vehicle != null) {
        val dto = this.vehicle!!.toVehicleDTO()

        pickupInspect?.let{ dto.pickupInspection = it.toPickupInspectionDTO() }
        deliveryInspect?.let{ dto.deliveryInspection = it.toDeliveryInspectionDTO() }

        return dto
    } else return null
}

fun Vehicle.toVehicleDTO(): VehicleDTO {
    val dto = with(::VehicleDTO) {
        val propertiesByName = Vehicle::class.memberProperties.associateBy { it.name }
        callBy(args = parameters.associateWith { parameter ->
            val property = propertiesByName[parameter.name]
            if (property != null && parameter.type == property.returnType)
                property.get(this@toVehicleDTO)
            else null
        })
    }
    val res = App.component.getResourcesGateway()

    try {
        dto.year = year?.toInt()
    } catch (e: NumberFormatException) {e.printStackTrace()}
    dto.type = res.vehicleTypesApiList.getSafe(type)
    dto.driveType = res.vehicleDriveTypeArray.getSafe(driveType)
    dto.running = res.yesNoArray.getSafe(running)
    dto.state = res.stateCodesArray.getSafe(state)
    dto.exteriorColor = res.colorArray.getSafe(state)
    dto.length = length.toBigDecimal()
    dto.width = width.toBigDecimal()
    dto.height = height.toBigDecimal()
    dto.curbWeight = curbWeight.toBigDecimal()
    dto.quantity = 1
    return dto
}

fun VehicleJoined.toVehicleShortDTO(status: Int?): VehicleShortDTO? {
    if (this.vehicle != null) {
        val dto = VehicleShortDTO(this.vehicle!!.id)
        when (status){
            EbolStatus.PICKED_UP.ordinal -> pickupInspect?.let{ dto.inspection = it.toPickupInspectionDTO() }
            EbolStatus.DELIVERED.ordinal -> deliveryInspect?.let{ dto.inspection = it.toDeliveryInspectionDTO() }
        }
        return dto
    } else return null
}

fun Inspection.toPickupInspectionDTO() = InspectionDTO(
    odometerPickup = odometer.toBigDecimal(),
    odometerInopPickup = odometerInop,
    additionalInspectNotesPickup = additionalInspectNotes,

    additionalInspection = additionalInspection,
    condition = ConditionDTO(
        isDark,
        isSnow,
        isRain,
        isDirty,
        isUninspect
    )
)

fun InspectionJoined.toPickupInspectionDTO(): InspectionDTO?{
    if (this.inspection != null) {
        val dto = this.inspection!!.toPickupInspectionDTO()
        dto.imagesList = imagesList.mapIndexed { i, it ->  it.toInspectImageDTO(i)}.toMutableList()
        return dto
    } else return null
}

fun Inspection.toDeliveryInspectionDTO() = InspectionDTO(
    odometerDelivery = odometer.toBigDecimal(),
    odometerInopDelivery = odometerInop,
    additionalInspectNotesDelivery = additionalInspectNotes,

    additionalInspection = additionalInspection,
    condition = ConditionDTO(
        isDark,
        isSnow,
        isRain,
        isDirty,
        isUninspect
    )
)

fun InspectionJoined.toDeliveryInspectionDTO(): InspectionDTO?{
    if (this.inspection != null) {
        val dto = this.inspection!!.toDeliveryInspectionDTO()
        dto.imagesList = imagesList.mapIndexed { i, it ->  it.toInspectImageDTO(i)}.toMutableList()
        return dto
    } else return null
}

fun InspectImageJoined.toInspectImageDTO(index: Int): InspectImageDTO{
    if (this.inspectImage != null) {
        val dto = this.inspectImage.toInspectImageDTO(index)
        dto.damagesList = damagesList.map { it.toDamageMarkDTO()}.toMutableList()
        return dto
    } else return InspectImageDTO(index = index)
}

fun InspectImage.toInspectImageDTO(index: Int) = InspectImageDTO(
    dateTaken,
    locationAddress,
    index = index
)

fun DamageMark.toDamageMarkDTO() = DamageMarkDTO(
    damageKind,//App.component.getResourcesGateway().damageKindsShortArray.getSafe(damageKind),
    x,
    y
)

fun Profile.toProfileDTO(): ProfileDTO {
    val dto = with(::ProfileDTO) {
        val propertiesByName = Profile::class.memberProperties.associateBy { it.name }
        callBy(args = parameters.associateWith { parameter ->
            val property = propertiesByName[parameter.name]
            if (property != null && parameter.type == property.returnType)
                property.get(this@toProfileDTO)
            else null
        })
    }
    dto.driverSignature = driverSignature.toBase64String()
    return dto
}


fun String?.toBigDecimal(): BigDecimal{
    val dot = (DecimalFormat.getInstance(Locale.US) as DecimalFormat).decimalFormatSymbols.decimalSeparator
    val value = if (!this.isNullOrEmpty()) this.replace("[^0-9^$dot]".toRegex(), "").replace("\\$dot".toRegex(), ".")
                else "0"
    return BigDecimal(value)
}

fun String?.toAPIFormat(): String?  = this?.let{
    try {
        val dateFormat = SimpleDateFormat("MM-dd-yyyy")
        val date = dateFormat.parse(it)
        dateFormat.applyPattern("yyyy-MM-dd")
        return@let dateFormat.format(date)
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    null
}

//fun Int?.toBoolean() = this?.let{ it == 1}

