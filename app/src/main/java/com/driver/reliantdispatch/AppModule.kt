package com.driver.reliantdispatch

import android.content.Context
import android.location.Geocoder
import androidx.room.Room
import androidx.room.RoomDatabase
import com.driver.reliantdispatch.data.*
import com.driver.reliantdispatch.domain.EbolsCountersUseCase
import com.driver.reliantdispatch.domain.GPSServiceUseCase
import com.driver.reliantdispatch.domain.Global
import com.driver.reliantdispatch.domain.boundaries.*
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule (val context: Context){
    @Singleton
    @Provides
    fun provideContext() = context

    @Singleton
    @Provides
    fun provideAppDatabase(): AppDatabase = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "app-db.db"
    )
    //.addMigrations(MIGRATION_1_2)
    .setJournalMode(RoomDatabase.JournalMode.TRUNCATE)
    .build()

    @Singleton
    @Provides
    fun provideApiGateway(): ApiGateway = ApiGatewayImpl()

    @Singleton
    @Provides
    fun provideApiRetrofitService() = ApiRetrofitService()

    @Singleton
    @Provides
    fun provideVinGateway(): VinGateway = VinGatewayImpl()

    @Singleton
    @Provides
    fun provideVinRetrofitService() = VinRetrofitService()

    @Singleton
    @Provides
    fun provideFusedLocationProviderClient() = LocationServices.getFusedLocationProviderClient(context)

    @Singleton
    @Provides
    fun provideGeocoder() = Geocoder(context)

    @Singleton
    @Provides
    fun provideLocationGateway(): LocationGateway = LocationGatewayImpl()

    @Singleton
    @Provides
    fun provideGlobal(): Global = Global()

    @Singleton
    @Provides
    fun provideFilesGateway(): FilesGateway = FilesGatewayImpl()

    @Singleton
    @Provides
    fun provideConnectivityService() = ConnectivityService()

    @Singleton
    @Provides
    fun provideResourcesArrays(): ResourcesGateway = ResourcesGatewayImpl()

    @Singleton
    @Provides
    fun providePreferencesGateway(): PreferencesGateway = PreferencesGatewayImpl()

    @Singleton
    @Provides
    fun providePreferencesService() = PreferencesService()

    @Singleton
    @Provides
    fun provideDbGateway(): DbGateway = DBGatewayImpl()

    @Singleton
    @Provides
    fun provideEbolsCountersUseCase() = EbolsCountersUseCase()

    @Singleton
    @Provides
    fun provideGPSServiceUseCase() = GPSServiceUseCase()

    @Singleton
    @Provides
    fun provideGPSServiceGateway(): GPSServiceGateway = GPSServiceGatewayImpl()

}

