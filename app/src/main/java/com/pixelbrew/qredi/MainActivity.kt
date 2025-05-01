package com.pixelbrew.qredi

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.pixelbrew.qredi.data.network.di.NetworkModule
import com.pixelbrew.qredi.ui.components.services.SessionManager
import com.pixelbrew.qredi.ui.components.services.invoice.BluetoothPrinter
import com.pixelbrew.qredi.ui.components.sidemenu.SideMenu
import com.pixelbrew.qredi.ui.theme.QrediTheme


class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val sessionManager = SessionManager(this)
        var networkModule = NetworkModule.createApiService(sessionManager)

        setContent {
            QrediTheme(
                darkTheme = false,
            ) {
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

        checkBluetoothSetup()
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private fun checkBluetoothSetup() {
        if (!BluetoothPrinter.hasBluetoothPermissions(this)) {
            // Pide permisos y espera al resultado antes de continuar
            BluetoothPrinter.requestBluetoothPermissions(this)
            return
        }

        if (!BluetoothPrinter.isBluetoothEnabled()) {
            // Pide que se active el Bluetooth y espera al resultado antes de continuar
            BluetoothPrinter.requestEnableBluetooth(this)
            return
        }
    }
}

