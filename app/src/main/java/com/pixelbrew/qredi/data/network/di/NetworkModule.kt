package com.pixelbrew.qredi.data.network.di

import com.pixelbrew.qredi.data.network.api.ApiService
import com.pixelbrew.qredi.data.network.utils.URL_BASE
import com.pixelbrew.qredi.ui.components.services.SessionManager
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkModule {
    fun createApiService(sessionManager: SessionManager): ApiService {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(sessionManager))
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(URL_BASE)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(ApiService::class.java)
    }
}