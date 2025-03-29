package com.pixelbrew.qredi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.pixelbrew.qredi.network.di.NetworkModule
import com.pixelbrew.qredi.ui.components.services.SessionManager
import com.pixelbrew.qredi.ui.components.sidemenu.SideMenu
import com.pixelbrew.qredi.ui.theme.QrediTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val sessionManager = SessionManager(this)
        var networkModule = NetworkModule.createApiService(sessionManager)

        setContent {
            QrediTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SideMenu(
                        modifier = Modifier.padding(innerPadding),
                        sessionManager = sessionManager,
                        apiService = networkModule,
                        context = this
                    )
                }
            }
        }

    }
}

