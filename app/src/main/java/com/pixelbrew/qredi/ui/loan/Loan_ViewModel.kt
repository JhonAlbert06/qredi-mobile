package com.pixelbrew.qredi.ui.loan

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pixelbrew.qredi.data.local.repository.LoanRepository
import com.pixelbrew.qredi.data.network.api.ApiService
import com.pixelbrew.qredi.data.network.model.CustomerModelRes
import com.pixelbrew.qredi.data.network.model.RouteModel
import com.pixelbrew.qredi.ui.components.services.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class LoanViewModel(
    private val loanRepository: LoanRepository,
    private val apiService: ApiService,
    private val sessionManager: SessionManager,
) : ViewModel() {

    private val baseUrl = sessionManager.fetchApiUrl()

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _showCreationDialog = MutableLiveData<Boolean>(false)
    val showCreationDialog: LiveData<Boolean> get() = _showCreationDialog

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> get() = _toastMessage

    private val _customerList = MutableLiveData<List<CustomerModelRes>>()
    val customerList: LiveData<List<CustomerModelRes>> get() = _customerList

    private val _routes = MutableLiveData<List<RouteModel>>()
    val routesList: LiveData<List<RouteModel>> get() = _routes

    private fun showToast(message: String) {
        _toastMessage.postValue(message)
    }

    init {

    }

    fun setShowCreationDialog(show: Boolean) {
        _showCreationDialog.postValue(show)
        fetchCustomers()
        getRoutes()
    }

    private fun fetchCustomers(query: String = "", field: String = "") {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val url = "$baseUrl/customer?query=$query&field=$field"
                val response = apiService.getCustomers(url)
                var customers = emptyList<CustomerModelRes>()

                if (response.isSuccessful) {
                    customers = response.body() ?: emptyList()
                    Log.d("CustomerViewModel", "Fetched customers: $customers")
                } else {
                    Log.e("CustomerViewModel", "Error fetching customers: ${response.errorBody()}")
                    showToast("Error fetching customers: ${response.errorBody()}")
                }

                _customerList.postValue(customers)
                _isLoading.postValue(false)
            } catch (e: Exception) {
                Log.e("CustomerViewModel", "Error fetching customers: ${e.message}")
                showToast("Error fetching customers: ${e.message}")
                _isLoading.postValue(false)
            }
        }
    }

    fun getRoutes() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val routesUrl = "$baseUrl/routes"

                val response = withContext(Dispatchers.Main) {
                    apiService.getRoutes(routesUrl)
                }

                if (response.isSuccessful) {
                    val routes = response.body() ?: emptyList()
                    Log.d("API_RESPONSE", "Rutas: $routes")
                    _routes.postValue(routes)
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.d("API_RESPONSE", "Error: $errorBody")
                    showToast("Error: $errorBody")
                }

            } catch (e: Exception) {
                Log.e("API_ERROR", "Error al obtener datos: ${e.message}")
                showToast(e.message.toString())
            }
        }
    }
}