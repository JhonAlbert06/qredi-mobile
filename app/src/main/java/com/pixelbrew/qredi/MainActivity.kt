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
import com.pixelbrew.qredi.ui.components.services.invoice.BluetoothPrinter
import com.pixelbrew.qredi.ui.components.sidemenu.SideMenu
import com.pixelbrew.qredi.ui.theme.QrediTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        setContent {
            QrediTheme(
                darkTheme = false,
            ) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SideMenu(
                        modifier = Modifier.padding(innerPadding),
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
            BluetoothPrinter.requestBluetoothPermissions(this)
            return
        }

        if (!BluetoothPrinter.isBluetoothEnabled()) {
            BluetoothPrinter.requestEnableBluetooth(this)
            return
        }
    }
}

