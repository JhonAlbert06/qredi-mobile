package com.pixelbrew.qredi.ui.loan

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.pixelbrew.qredi.data.network.api.ApiService
import com.pixelbrew.qredi.data.network.model.ApiError
import com.pixelbrew.qredi.data.network.model.CustomerModelRes
import com.pixelbrew.qredi.data.network.model.LoanModel
import com.pixelbrew.qredi.data.network.model.LoanModelRes
import com.pixelbrew.qredi.data.network.model.RouteModel
import com.pixelbrew.qredi.ui.components.services.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class LoanViewModel(
    private val apiService: ApiService,
    private val sessionManager: SessionManager,
) : ViewModel() {

    private val baseUrl = sessionManager.fetchApiUrl()

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _showCreationDialog = MutableLiveData<Boolean>(false)
    val showCreationDialog: LiveData<Boolean> get() = _showCreationDialog

    private val _showFilterLoanDialog = MutableLiveData<Boolean>(false)
    val showFilterLoanDialog: LiveData<Boolean> get() = _showFilterLoanDialog

    private val _showLoanDetailsDialog = MutableLiveData<Boolean>(false)
    val showLoanDetailsDialog: LiveData<Boolean> get() = _showLoanDetailsDialog

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> get() = _toastMessage

    private val _customerList = MutableLiveData<List<CustomerModelRes>>()
    val customerList: LiveData<List<CustomerModelRes>> get() = _customerList

    private val _routes = MutableLiveData<List<RouteModel>>()
    val routesList: LiveData<List<RouteModel>> get() = _routes

    private val _loans = MutableLiveData<List<LoanModelRes>>()
    val loans: LiveData<List<LoanModelRes>> get() = _loans

    private val _loanSelected = MutableLiveData<LoanModelRes>()
    val loanSelected: LiveData<LoanModelRes> get() = _loanSelected

    init {
        _isLoading.postValue(true)
        fetchLoans()

        fetchCustomers()
        getRoutes()
    }

    fun setLoanDetailsDialog(show: Boolean) {
        _showLoanDetailsDialog.postValue(show)
    }

    fun setLoanSelected(loan: LoanModelRes) {
        _loanSelected.postValue(loan)
    }

    fun createNewLoan(loan: LoanModel) {
        _isLoading.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val url = "$baseUrl/loan"
                val response = apiService.createLoan(url, loan)

                if (response.isSuccessful) {
                    Log.d("LoanViewModel", "Loan created successfully: ${response.body()}")
                    showToast("Loan created successfully")
                    fetchLoans()
                    _showCreationDialog.postValue(false)
                } else {
                    val errorBody = response.errorBody()?.string()
                    val error = Gson().fromJson(errorBody, ApiError::class.java)
                    Log.e("API_RESPONSE", "Error: ${error.message}")
                    showToast("Error: ${error.message}")
                }
            } catch (e: Exception) {
                Log.e("LoanViewModel", "Error creating loan: ${e.message}")
                showToast("Error creating loan: ${e.message}")
            }
        }
    }

    fun setShowCreationDialog(show: Boolean) {
        _showCreationDialog.postValue(show)
    }

    fun setShowFilterLoanDialog(show: Boolean) {
        _showFilterLoanDialog.postValue(show)
    }

    fun refreshLoans() {
        _isLoading.postValue(true)
        fetchLoans()
    }

    fun fetchLoans(
        field: String? = null,
        query: String? = null,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Construir la URL base
                val urlBuilder = StringBuilder("$baseUrl/loan?")

                if (!field.isNullOrEmpty() && !query.isNullOrEmpty()) {
                    urlBuilder.append("$field=$query")
                }

                val url = urlBuilder.toString()

                // Realizar la solicitud a la API
                val response = apiService.getLoans(url)

                if (response.isSuccessful) {
                    _loans.postValue(response.body() ?: emptyList())
                    Log.d("LoanViewModel", "Fetched loans: ${_loans.value}")
                } else {
                    val errorBody = response.errorBody()?.string()
                    val error = Gson().fromJson(errorBody, ApiError::class.java)
                    Log.e("API_RESPONSE", "Error: ${error.message}")
                    showToast("Error: ${error.message}")
                }

                _isLoading.postValue(false)
            } catch (e: Exception) {
                Log.e("LoanViewModel", "Error fetching loans: ${e.message}")
                showToast("Error fetching loans: ${e.message}")
                _isLoading.postValue(false)
            }
        }
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
                    val errorBody = response.errorBody()?.string()
                    val error = Gson().fromJson(errorBody, ApiError::class.java)
                    Log.e("API_RESPONSE", "Error: ${error.message}")
                    showToast("Error: ${error.message}")
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
                    val error = Gson().fromJson(errorBody, ApiError::class.java)
                    Log.e("API_RESPONSE", "Error: ${error.message}")
                    showToast("Error: ${error.message}")
                }

            } catch (e: Exception) {
                Log.e("API_ERROR", "Error al obtener datos: ${e.message}")
                showToast(e.message.toString())
            }
        }
    }

    private fun showToast(message: String) {
        _toastMessage.postValue(message)
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