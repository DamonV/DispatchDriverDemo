package com.driver.reliantdispatch.data

import android.content.Context
import android.content.SharedPreferences
import com.driver.reliantdispatch.App
import javax.inject.Inject

const val PREF_NAME = "reliant-pref"

class PreferencesService{
    @Inject
    lateinit var context: Context

    val sharedPref: SharedPreferences

    init {
        App.component.inject(this)
        sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun putPref(key: String, value: Any?) {
        val editor = sharedPref.edit()
        if (value != null)
            when(value){
                is Int -> editor.putInt(key, value)
                is String -> editor.putString(key, value)
                is Boolean -> editor.putBoolean(key, value)
                is Float -> editor.putFloat(key, value)
                is Long -> editor.putLong(key, value)
            }
        else editor.remove(key)
        editor.apply()
    }
}