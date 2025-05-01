package com.pixelbrew.qredi.module

import android.content.Context
import com.pixelbrew.qredi.data.local.repository.LoanRepository
import com.pixelbrew.qredi.data.network.api.ApiService
import com.pixelbrew.qredi.data.network.di.NetworkModule
import com.pixelbrew.qredi.ui.components.services.SessionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSessionManager(@ApplicationContext context: Context): SessionManager {
        return SessionManager(context)
    }

    @Provides
    @Singleton
    fun provideApiService(sessionManager: SessionManager): ApiService {
        return NetworkModule.createApiService(sessionManager)
    }

    @Provides
    fun provideLoanRepository(@ApplicationContext context: Context): LoanRepository {
        return LoanRepository(context)
    }
}