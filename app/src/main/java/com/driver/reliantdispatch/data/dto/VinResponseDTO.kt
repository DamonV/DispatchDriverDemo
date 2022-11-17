package com.driver.reliantdispatch.data.dto

import com.google.gson.annotations.SerializedName

data class VinResponseDTO(
    val vin: String?,
    val attributes: VinAttributesDTO?,
    val success: Boolean = false,
    val error: String?
) {
    data class VinAttributesDTO(
        @SerializedName("VIN")
        val vin: String?,

        @SerializedName("Year")
        val year: String?,

        @SerializedName("Make")
        val make: String?,

        @SerializedName("Model")
        val model: String?,

        @SerializedName("Trim")
        val trim_: String?,

        @SerializedName("Short Trim")
        val shortTrim: String?,

        @SerializedName("Trim Variations")
        val trimVariations: String?,

        @SerializedName("Made In")
        val madeIn: String?,

        @SerializedName("Vehicle Style")
        val vehicleStyle: String?,

        @SerializedName("Vehicle Type")
        val vehicleType: String?,

        @SerializedName("Vehicle Size")
        val vehicleSize: String?,

        @SerializedName("Vehicle Category")
        val vehicleCategory: String?,

        @SerializedName("Doors")
        val doors: String?,

        @SerializedName("Fuel Type")
        val fuelType: String?,

        @SerializedName("Fuel Capacity")
        val fuelCapacity: String?,

        @SerializedName("City Mileage")
        val cityMileage: String?,

        @SerializedName("Highway Mileage")
        val highwayMileage: String?,

        @SerializedName("Engine")
        val engine: String?,

        @SerializedName("Engine Size")
        val engineSize: String?,

        @SerializedName("Engine Cylinders")
        val engineCylinders: String?,

        @SerializedName("Transmission Type")
        val transmissionType: String?,

        @SerializedName("Transmission Gears")
        val transmissionGears: String?,

        @SerializedName("Driven Wheels")
        val drivenWheels: String?,

        @SerializedName("Anti-Brake System")
        val antiBrakeSystem: String?,

        @SerializedName("Steering Type")
        val steeringType: String?,

        @SerializedName("Curb Weight")
        val curbWeight: String?,

        @SerializedName("Gross Weight")
        val grossWeight: String?,

        @SerializedName("Overall Height")
        val overallHeight: String?,

        @SerializedName("Overall Length")
        val overallLength: String?,

        @SerializedName("Overall Width")
        val overallWidth: String?,

        @SerializedName("Standard Seating")
        val standardSeating: String?,

        @SerializedName("Optional Seating")
        val optionalSeating: String?,

        @SerializedName("Invoice Price")
        val invoicePrice: String?,

        @SerializedName("Delivery Charges")
        val deliveryCharges: String?,

        @SerializedName("MSRP")
        val mSRP: String
    )
}

/*"VIN": "JHLRD77874C026456",
"Year": "2004",
"Make": "Honda",
"Model": "CR-V",
"Trim": "EX",
"Short Trim": "EX",
"Trim Variations": "EX",
"Made In": "Japan",
"Made In City": "Takanezawa",
"Vehicle Style": "4WD",
"Vehicle Type": "Sport Utility Vehicle",
"Vehicle Size": "Midsize",
"Vehicle Category": "Standard Sport Utility Vehicle",
"Doors": "4",
"Fuel Type": "Regular Unleaded",
"Fuel Capacity": "15.30 gallons",
"City Mileage": "21 miles/gallon",
"Highway Mileage": "25 miles/gallon",
"Engine": "2.4-L L-4 DOHC 16V",
"Engine Size": "2.4",
"Engine Cylinders": "4",
"Transmission Type": "Manual",
"Transmission Gears": "5",
"Driven Wheels": "Four-Wheel Drive",
"Anti-Brake System": "4-Wheel ABS",
"Steering Type": "Rack & Pinion",
"Curb Weight": "3347",
"Gross Weight": "",
"Overall Height": "66.20 inches",
"Overall Length": "178.60 inches",
"Overall Width": "70.20 inches",
"Standard Seating": "5",
"Optional Seating": "",
"Invoice Price": "$20,092",
"Delivery Charges": "$490",
"MSRP": "$21,750"*/