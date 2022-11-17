package com.driver.reliantdispatch

import android.content.Context
import com.driver.reliantdispatch.data.*
import com.driver.reliantdispatch.domain.EbolsCountersUseCase
import com.driver.reliantdispatch.domain.GPSServiceUseCase
import com.driver.reliantdispatch.domain.Global
import com.driver.reliantdispatch.domain.InspectionUseCase
import com.driver.reliantdispatch.domain.boundaries.*
import com.driver.reliantdispatch.presentation.secondary.ScopedViewModel
import com.driver.reliantdispatch.presentation.sections.MarkingImageView
import com.driver.reliantdispatch.presentation.sections.SectionViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
    fun inject(instance: ScopedViewModel)
    fun inject(instance: SectionViewModel)
    fun inject(instance: ApiGatewayImpl)
    fun inject(instance: LocationGatewayImpl)
    fun inject(instance: ApiRetrofitService)
    fun inject(instance: VinGatewayImpl)
    fun inject(instance: FilesGatewayImpl)
    fun inject(instance: ConnectivityService)
    fun inject(instance: InspectionUseCase)
    fun inject(instance: ResourcesGatewayImpl)
    fun inject(instance: PreferencesService)
    fun inject(instance: MarkingImageView)
    fun inject(instance: Global)
    fun inject(instance: MainService)
    fun inject(instance: GPSServiceUseCase)
    fun inject(instance: BootBroadcastReceiver)


    fun getContext(): Context
    fun getApiGateway(): ApiGateway
    fun getGlobal(): Global
    fun getLocationGateway(): LocationGateway
    fun getVinGateway(): VinGateway
    fun getFilesGateway(): FilesGateway
    fun getResourcesGateway(): ResourcesGateway
    fun getPreferencesService(): PreferencesService
    fun getPreferencesGateway(): PreferencesGateway
    fun getAppDatabase(): AppDatabase
    fun getDbGateway(): DbGateway
    fun getEbolsCountersUseCase(): EbolsCountersUseCase
    fun getGPSServiceUseCase(): GPSServiceUseCase
}