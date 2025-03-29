package com.pixelbrew.qredi.collect

import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.pixelbrew.qredi.MainActivity
import com.pixelbrew.qredi.network.api.ApiService
import com.pixelbrew.qredi.network.model.DownloadModel
import com.pixelbrew.qredi.network.model.RouteModel
import com.pixelbrew.qredi.ui.components.services.SessionManager

class CollectViewModel(
    private val apiService: ApiService,
    private val sessionManager: SessionManager,
    context: MainActivity
) : ViewModel() {

    var text = "Hello toast!"
    val duration = Toast.LENGTH_SHORT

    var toast: Toast = Toast.makeText(context, text, duration)

    private val _routes = mutableStateOf<List<RouteModel>>(emptyList())
    val routes: List<RouteModel> get() = _routes.value

    private val _downloadedRoutes = mutableStateOf<List<DownloadModel>>(emptyList())
    val downloadedRoutes: List<DownloadModel> get() = _downloadedRoutes.value

    init {
        _downloadedRoutes.value = sessionManager.fetchLoanData()!!

    }

    fun setToastText(text: String) {
        this.text = text
        toast.setText(text)
        toast.show()
    }

    suspend fun getRoutes() {
        try {
            val response = apiService.getRoutes()
            _routes.value = response

            setToastText("Routes loaded successfully")
        } catch (e: Exception) {
            Log.e("API_ERROR", "Error al obtener datos: ${e.message}")
            setToastText(e.message.toString())
        }
    }

    suspend fun downloadRoute(id: String) {
        try {
            val response = apiService.downloadRoute(id)
            sessionManager.saveLoanData(response)

            _downloadedRoutes.value = sessionManager.fetchLoanData()!!
            setToastText("Route downloaded successfully")
        } catch (e: Exception) {
            Log.e("API_ERROR", "Error al obtener datos: ${e.message}")
            setToastText(e.message.toString())
        }
    }

    fun formatNumber(number: Double): String {
        return "%,.2f".format(number)
    }

    fun formatCedula(cedula: String): String {
        return if (cedula.length == 11) {
            "${cedula.substring(0, 3)}-${cedula.substring(3, 10)}-${cedula.substring(10)}"
        } else {
            cedula // Return original if not 11 digits
        }
    }
}