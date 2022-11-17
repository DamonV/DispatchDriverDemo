package com.driver.reliantdispatch.data

import com.driver.reliantdispatch.App
import com.driver.reliantdispatch.domain.boundaries.PreferencesGateway

const val PREF_1 = "atkn"
const val PREF_2 = "exp"
const val PREF_3 = "remember"
const val PREF_4 = "usr"
const val PREF_5 = "interval"

class PreferencesGatewayImpl: PreferencesGateway {

    val prefService = App.component.getPreferencesService()

    override fun getAccessToken(): String? = prefService.sharedPref.getString(PREF_1, "")
    override fun setAccessToken(value: String?) = prefService.putPref(PREF_1, value)

    override fun getExpiresIn(): Long? = prefService.sharedPref.getLong(PREF_2, 0)
    override fun setExpiresIn(value: Long?) = prefService.putPref(PREF_2, value)

    override fun getRememberMe(): Boolean? = prefService.sharedPref.getBoolean(PREF_3, false)
    override fun setRememberMe(value: Boolean?) = prefService.putPref(PREF_3, value)

    override fun getUser(): String? = prefService.sharedPref.getString(PREF_4, "")
    override fun setUser(value: String?) = prefService.putPref(PREF_4, value)

    override fun getInterval(): Int? = prefService.sharedPref.getInt(PREF_5, 0)
    override fun setInterval(value: Int?) = prefService.putPref(PREF_5, value)
}

