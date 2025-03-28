package com.pixelbrew.qredi.collect

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.pixelbrew.qredi.network.api.ApiService
import com.pixelbrew.qredi.network.model.DownloadModel
import com.pixelbrew.qredi.network.model.RouteModel

class CollectViewModel(
    private val apiService: ApiService,
) : ViewModel() {

    private val _routes = mutableStateOf<List<RouteModel>>(emptyList())
    val routes: List<RouteModel> get() = _routes.value

    private val _downloadedRoutes = mutableStateOf<List<DownloadModel>>(emptyList())
    val downloadedRoutes: List<DownloadModel> get() = _downloadedRoutes.value

    suspend fun loaduser() {
        try {
            val response = apiService.loadUser()
            Log.d("API_RESPONSE", response.toString())
        } catch (e: Exception) {
            Log.e("API_ERROR", "Error al obtener datos: ${e.message}")
        }
    }

    suspend fun getRoutes() {
        try {
            val response = apiService.getRoutes()
            _routes.value = response
        } catch (e: Exception) {
            Log.e("API_ERROR", "Error al obtener datos: ${e.message}")
        }
    }

    suspend fun downloadRoute(id: String) {
        try {
            val response = apiService.downloadRoute(id)
            _downloadedRoutes.value = response
        } catch (e: Exception) {
            Log.e("API_ERROR", "Error al obtener datos: ${e.message}")
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