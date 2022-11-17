package com.driver.reliantdispatch.domain

class ValidationRules{

    fun isValidEmail(email: String?): Boolean{
        return email?.contains(
            Regex("^([a-z0-9_-]+\\.)*[a-z0-9_-]+@[a-z0-9_-]+(\\.[a-z0-9_-]+)*\\.[a-z]{2,6}\$")
        ) ?: false
    }

    fun isValidPassword(password: String?): Boolean{
        return password?.isNotEmpty() ?: false
    }

    fun isValidNewPassword(password: String?, newPassword: String?): Boolean{
        return password?.isNotEmpty() ?: false
                && newPassword?.isNotEmpty() ?: false
                && password == newPassword
    }

}