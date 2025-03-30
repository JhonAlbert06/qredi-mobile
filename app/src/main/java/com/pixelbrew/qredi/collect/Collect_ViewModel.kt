package com.pixelbrew.qredi.collect

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pixelbrew.qredi.network.api.ApiService
import com.pixelbrew.qredi.network.model.DownloadModel
import com.pixelbrew.qredi.network.model.RouteModel
import kotlinx.coroutines.launch

class CollectViewModel(
    private val apiService: ApiService
) : ViewModel() {

    private val _routes = MutableLiveData<List<RouteModel>>(emptyList())
    val routes: LiveData<List<RouteModel>> get() = _routes

    private val _downloadedRoutes = MutableLiveData<List<DownloadModel>>()
    val downloadedRoutes: LiveData<List<DownloadModel>> get() = _downloadedRoutes

    private val _downloadRouteSelected = MutableLiveData<DownloadModel>()
    val downloadRouteSelected: LiveData<DownloadModel> = _downloadRouteSelected

    private val _amount = MutableLiveData<String>()
    val amount: LiveData<String> get() = _amount

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> get() = _toastMessage

    fun setDownloadRouteSelected(downloadRoute: DownloadModel) {
        _downloadRouteSelected.value = downloadRoute
    }

    fun onAmountChange(amount: String) {
        _amount.value = amount
    }

    private fun showToast(message: String) {
        _toastMessage.value = message
    }

    fun getRoutes() {
        viewModelScope.launch {
            try {
                val response = apiService.getRoutes()

                _routes.value = response

                showToast("Routes loaded successfully")
            } catch (e: Exception) {
                Log.e("API_ERROR", "Error al obtener datos: ${e.message}")
                showToast(e.message.toString())
            }
        }
    }

    fun downloadRoute(id: String) {
        viewModelScope.launch {
            try {
                val response = apiService.downloadRoute(id)
                _downloadedRoutes.postValue(response)
                showToast("Route downloaded successfully")
            } catch (e: Exception) {
                Log.e("API_ERROR", "Error al obtener datos: ${e.message}")
                showToast(e.message.toString())
            }
        }
    }

    fun formatNumber(number: Double): String {
        return "%,.2f".format(number)
    }

    fun formatCedula(cedula: String): String {
        return if (cedula.length == 11) {
            "${cedula.substring(0, 3)}-${cedula.substring(3, 10)}-${cedula.substring(10)}"
        } else {
            cedula
        }
    }
}