package com.vango.di

import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.vango.data.dataSource.remote.places.PlacesDataSource
import com.vango.data.repository.SearchRepository
import com.vango.data.repository.SearchRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.util.Locale
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context {
        return context
    }

    @Provides
    @Singleton
    fun provideFusedLocationClient(
        @ApplicationContext context: Context
    ): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(context)
    }

    @Provides
    @Singleton
    fun providePlacesClient(@ApplicationContext context: Context): PlacesClient {
        val apiKey = getApiKeyFromManifest(context)
            ?: throw IllegalStateException("Google API Key no encontrada en el AndroidManifest.xml")
        Places.initialize(context, apiKey)
        return Places.createClient(context)
    }

    @Provides
    @Singleton
    fun provideSearchRepository(placesDataSource: PlacesDataSource): SearchRepository {
        return SearchRepositoryImpl(placesDataSource)
    }

    private fun getApiKeyFromManifest(context: Context): String? {
        return try {
            val appInfo = context.packageManager.getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
            val metaData = appInfo.metaData
            metaData.getString("com.google.android.geo.API_KEY")
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            null
        }
    }

    @Provides
    @Singleton
    fun provideGeocoder(@ApplicationContext context: Context): Geocoder {
        return Geocoder(context, Locale.getDefault())
    }



}