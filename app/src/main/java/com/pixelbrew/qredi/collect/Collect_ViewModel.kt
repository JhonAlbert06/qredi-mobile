package com.pixelbrew.qredi.collect

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.pixelbrew.qredi.network.api.ApiService
import com.pixelbrew.qredi.network.model.RouteModel

class CollectViewModel(
    private val apiService: ApiService,
) : ViewModel() {

    private val _routes = mutableStateOf<List<RouteModel>>(emptyList())
    val routes: List<RouteModel> get() = _routes.value

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
            Log.d("API_RESPONSE", response.toString())
        } catch (e: Exception) {
            Log.e("API_ERROR", "Error al obtener datos: ${e.message}")
        }
    }
}