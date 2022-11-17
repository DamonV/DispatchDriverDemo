package com.driver.reliantdispatch.domain.boundaries


interface PreferencesGateway{
    fun getAccessToken(): String?
    fun setAccessToken(value: String?)

    fun getExpiresIn(): Long?
    fun setExpiresIn(value: Long?)

    fun getRememberMe(): Boolean?
    fun setRememberMe(value: Boolean?)

    fun getUser(): String?
    fun setUser(value: String?)

    fun getInterval(): Int?
    fun setInterval(value: Int?)
}