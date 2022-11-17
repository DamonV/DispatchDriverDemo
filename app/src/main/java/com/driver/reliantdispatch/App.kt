package com.driver.reliantdispatch

import android.app.Application
import androidx.multidex.MultiDexApplication


class App: MultiDexApplication(){

    override fun onCreate() {
        super.onCreate()

        component = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()
    }

    companion object {
        @JvmStatic
        lateinit var component: AppComponent
    }
}