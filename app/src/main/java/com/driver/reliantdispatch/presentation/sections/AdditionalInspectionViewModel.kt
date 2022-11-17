package com.driver.reliantdispatch.presentation.sections

import android.view.View
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.driver.reliantdispatch.domain.AppLogic
import com.driver.reliantdispatch.domain.entities.AdditionalInspection
import com.driver.reliantdispatch.domain.entities.Ebol


class AdditionalInspectionViewModel: SectionViewModel(){

    val mAdditionalInspection = CustomObservableField<AdditionalInspection>()
    var mInitial: AdditionalInspection? = null

    inner class CustomObservableField<T>: ObservableField<T>(){
        override fun set(value: T){
            super.set(value)
            (value as AdditionalInspection?)?.run{
                mInitial = this
                mWindshield.set(windshield)
                mMissingParts.set(missingParts)     //todo !
                //windshield?.let { mWindshield.set(it) }
                //missingParts?.let { mMissingParts.set(it) }
                wheels?.let { mWheels.set(it) }
                wheelsAmount?.let { mWheelsAmount.set(it) }
                mVehicleStarts.set(vehicleStarts)
                engineVibration?.let { mEngineVibration.set(it) }
                engineNoise?.let { mEngineNoise.set(it) }
                engineSmoke?.let { mEngineSmoke.set(it) }
                clutchIssues?.let { mClutchIssues.set(it) }
                brakesIssues?.let { mBrakesIssues.set(it) }
                leaking?.let { mLeaking.set(it) }
                electricalIssues?.let { mElectricalIssues.set(it) }
                checkEngineLight?.let { mCheckEngineLight.set(it) }
                brakeABSLight?.let { mBrakeABSLight.set(it) }
                airbagLight?.let { mAirbagLight.set(it) }
                tractionControlLight?.let { mTractionControlLight.set(it) }
                smartKey?.let { mSmartKey.set(it) }
                otherKeys?.let { mOtherKeys.set(it) }
                manuals?.let { mManuals.set(it) }
            }
        }

        override fun get(): T? {
            val value = super.get()
            (value as AdditionalInspection?)?.run{
                windshield = mWindshield.get()
                missingParts = mMissingParts.get()
                wheels = mWheels.get()
                wheelsAmount = mWheelsAmount.get()
                vehicleStarts = mVehicleStarts.get()
                engineVibration = mEngineVibration.get()
                engineNoise = mEngineNoise.get()
                engineSmoke = mEngineSmoke.get()
                clutchIssues = mClutchIssues.get()
                brakesIssues = mBrakesIssues.get()
                leaking = mLeaking.get()
                electricalIssues = mElectricalIssues.get()
                checkEngineLight = mCheckEngineLight.get()
                brakeABSLight = mBrakeABSLight.get()
                airbagLight = mAirbagLight.get()
                tractionControlLight = mTractionControlLight.get()
                smartKey = mSmartKey.get()
                otherKeys = mOtherKeys.get()
                manuals = mManuals.get()
            }
            return value
        }
    }

    val mWindshield = ObservableField<Int?>()
    val mMissingParts = ObservableField<Boolean?>()
    val mWheels = ObservableField<Boolean?>()
    val mWheelsAmount = ObservableField<String>()
    val mVehicleStarts = ObservableField<Int?>()
    val mEngineVibration = ObservableField<Boolean?>()
    val mEngineNoise = ObservableField<Boolean?>()
    val mEngineSmoke = ObservableField<Boolean?>()
    val mClutchIssues = ObservableField<Boolean?>()
    val mBrakesIssues = ObservableField<Boolean?>()
    val mLeaking = ObservableField<Boolean?>()
    val mElectricalIssues = ObservableField<Boolean?>()
    val mCheckEngineLight = ObservableField<Boolean?>()
    val mBrakeABSLight = ObservableField<Boolean?>()
    val mAirbagLight = ObservableField<Boolean?>()
    val mTractionControlLight = ObservableField<Boolean?>()
    val mSmartKey = ObservableField<Int?>()
    val mOtherKeys = ObservableField<Int?>()
    val mManuals = ObservableField<Boolean?>()


    fun setBooleanToggle(v: View, isChecked: Boolean, value: Boolean, observableField: ObservableField<Boolean?>){
        if (isChecked) observableField.set(value)
        else if (observableField.get() == value) observableField.set(null)
    }

    fun setIntToggle(v: View, isChecked: Boolean, value: Int, observableField: ObservableField<Int?>){
        if (isChecked) observableField.set(value)
        else if (observableField.get() == value) observableField.set(null)
    }

    override fun onSave(): Boolean{
        mRequiredField =
            if (mWheels.get() == true && mWheelsAmount.get().isNullOrEmpty()) AppLogic.requiredAdditionalInspectionFields["wheelsAmount"]
            else null
        return super.onSave()
    }

    override fun isFormHasChanged(): Boolean{
        mInitial?.run {
            return windshield != mWindshield.get()
                    || missingParts != mMissingParts.get()
                    || wheels != mWheels.get()
                    || wheelsAmount != mWheelsAmount.get()
                    || vehicleStarts != mVehicleStarts.get()
                    || engineVibration != mEngineVibration.get()
                    || engineNoise != mEngineNoise.get()
                    || engineSmoke != mEngineSmoke.get()
                    || clutchIssues != mClutchIssues.get()
                    || brakesIssues != mBrakesIssues.get()
                    || leaking != mLeaking.get()
                    || electricalIssues != mElectricalIssues.get()
                    || checkEngineLight != mCheckEngineLight.get()
                    || brakeABSLight != mBrakeABSLight.get()
                    || airbagLight != mAirbagLight.get()
                    || tractionControlLight != mTractionControlLight.get()
                    || smartKey != mSmartKey.get()
                    || otherKeys != mOtherKeys.get()
                    || manuals != mManuals.get()
        }
        return false
    }
}