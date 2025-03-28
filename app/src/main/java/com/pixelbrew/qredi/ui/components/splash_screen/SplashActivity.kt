package com.pixelbrew.qredi.ui.components.splash_screen

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.pixelbrew.qredi.MainActivity

class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Instalar el splash screen antes de mostrar la interfaz
        installSplashScreen()

        // Redirigir a MainActivity
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}