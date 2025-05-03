package com.pixelbrew.qredi.ui.loan

import android.Manifest
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.pixelbrew.qredi.data.local.entities.LoanEntity
import com.pixelbrew.qredi.data.network.api.ApiService
import com.pixelbrew.qredi.data.network.model.ApiError
import com.pixelbrew.qredi.data.network.model.CustomerModelRes
import com.pixelbrew.qredi.data.network.model.LoanModel
import com.pixelbrew.qredi.data.network.model.LoanModelRes
import com.pixelbrew.qredi.data.network.model.RouteModel
import com.pixelbrew.qredi.ui.components.services.SessionManager
import com.pixelbrew.qredi.ui.components.services.invoice.BluetoothPrinter
import com.pixelbrew.qredi.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class LoanViewModel @Inject constructor(
    private val apiService: ApiService,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _showCreationDialog = MutableLiveData(false)
    val showCreationDialog: LiveData<Boolean> get() = _showCreationDialog

    private val _showFilterLoanDialog = MutableLiveData(false)
    val showFilterLoanDialog: LiveData<Boolean> get() = _showFilterLoanDialog

    private val _showLoanDetailsDialog = MutableLiveData(false)
    val showLoanDetailsDialog: LiveData<Boolean> get() = _showLoanDetailsDialog

    private val _customerList = MutableLiveData<List<CustomerModelRes>>()
    val customerList: LiveData<List<CustomerModelRes>> get() = _customerList

    private val _routes = MutableLiveData<List<RouteModel>>()
    val routesList: LiveData<List<RouteModel>> get() = _routes

    private val _loans = MutableLiveData<List<LoanModelRes>>()
    val loans: LiveData<List<LoanModelRes>> get() = _loans

    private val _loanSelected = MutableLiveData<LoanModelRes>()
    val loanSelected: LiveData<LoanModelRes> get() = _loanSelected

    private val _toastMessage = MutableLiveData<Event<String>>()
    val toastMessage: LiveData<Event<String>> get() = _toastMessage

    init {
        _isLoading.value = true
        fetchLoans()
        fetchCustomers()
        getRoutes()
    }

    private fun showToast(message: String) {
        _toastMessage.postValue(Event(message))
    }

    fun setLoanDetailsDialog(show: Boolean) {
        _showLoanDetailsDialog.value = show
    }

    fun setLoanSelected(loan: LoanModelRes) {
        _loanSelected.value = loan
    }

    fun setShowCreationDialog(show: Boolean) {
        _showCreationDialog.value = show
    }

    fun setShowFilterLoanDialog(show: Boolean) {
        _showFilterLoanDialog.value = show
    }

    fun refreshLoans() {
        _isLoading.value = true
        fetchLoans()
    }

    fun fetchLoans(field: String? = null, query: String? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val url = buildString {
                    append("${sessionManager.fetchApiUrl()}/loan?")
                    if (!field.isNullOrEmpty() && !query.isNullOrEmpty()) {
                        append("$field=$query")
                    }
                }
                val response = apiService.getLoans(url)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        _loans.value = response.body() ?: emptyList()
                        Log.d("LoanViewModel", "Fetched loans: ${_loans.value?.size}")
                    } else {
                        val errorBody = response.errorBody()?.string()
                        val error = Gson().fromJson(errorBody, ApiError::class.java)
                        Log.e("API_RESPONSE", "Fetch loans error: ${error.message}")
                        showToast("Error: ${error.message}")
                    }
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                Log.e("LoanViewModel", "Exception fetching loans: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    showToast("Error al obtener préstamos: ${e.message}")
                    _isLoading.value = false
                }
            }
        }
    }

    fun fetchCustomers(query: String = "", field: String = "") {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val url = "${sessionManager.fetchApiUrl()}/customer?query=$query&field=$field"
                val response = apiService.getCustomers(url)

                val customers = if (response.isSuccessful) {
                    response.body() ?: emptyList()
                } else {
                    val errorBody = response.errorBody()?.string()
                    val error = Gson().fromJson(errorBody, ApiError::class.java)
                    Log.e("API_RESPONSE", "Fetch customers error: ${error.message}")
                    showToast("Error: ${error.message}")
                    emptyList()
                }

                withContext(Dispatchers.Main) {
                    _customerList.value = customers
                    _isLoading.value = false
                    Log.d("LoanViewModel", "Fetched ${customers.size} customers")
                }
            } catch (e: Exception) {
                Log.e("LoanViewModel", "Exception fetching customers: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    showToast("Error al obtener clientes: ${e.message}")
                    _isLoading.value = false
                }
            }
        }
    }

    fun getRoutes() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val routesUrl = "${sessionManager.fetchApiUrl()}/routes"
                val response = apiService.getRoutes(routesUrl)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        _routes.value = response.body() ?: emptyList()
                        Log.d("API_RESPONSE", "Fetched routes: ${_routes.value?.size}")
                    } else {
                        val errorBody = response.errorBody()?.string()
                        val error = Gson().fromJson(errorBody, ApiError::class.java)
                        Log.e("API_RESPONSE", "Fetch routes error: ${error.message}")
                        showToast("Error: ${error.message}")
                    }
                }
            } catch (e: Exception) {
                Log.e("LoanViewModel", "Exception fetching routes: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    showToast("Error al obtener rutas: ${e.message}")
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun createNewLoan(loan: LoanModel) {
        _isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val url = "${sessionManager.fetchApiUrl()}/loan"
                val response = apiService.createLoan(url, loan)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Log.d("LoanViewModel", "Loan created successfully: ${response.body()}")
                        showToast("Loan created successfully")
                        fetchLoans()
                        _showCreationDialog.value = false
                        printLoan(loan)
                    } else {
                        val errorBody = response.errorBody()?.string()
                        val error = Gson().fromJson(errorBody, ApiError::class.java)
                        Log.e("API_RESPONSE", "Create loan error: ${error.message}")
                        showToast("Error: ${error.message}")
                    }
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                Log.e("LoanViewModel", "Exception creating loan: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    showToast("Error al crear préstamo: ${e.message}")
                    _isLoading.value = false
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun printLoan(loan: LoanModel) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val customer = customerList.value?.find { it.id == loan.customerId }
                if (customer == null) {
                    Log.e("LoanViewModel", "No customer found for loan ${loan.customerId}")
                    return@launch
                }

                var attempts = 0
                var success = false
                while (attempts < 3 && !success) {
                    success = BluetoothPrinter.printDocument(
                        sessionManager.fetchPrinterName().orEmpty(),
                        BluetoothPrinter.DocumentType.LOAN,
                        loanEntity = LoanEntity(
                            id = "",
                            amount = loan.amount,
                            interest = loan.interest,
                            feesQuantity = loan.feesQuantity,
                            loanDateDay = LocalDateTime.now().dayOfMonth,
                            loanDateMonth = LocalDateTime.now().monthValue,
                            loanDateYear = LocalDateTime.now().year,
                            loanDateHour = LocalDateTime.now().hour,
                            loanDateMinute = LocalDateTime.now().minute,
                            loanDateSecond = LocalDateTime.now().second,
                            loanDateTimezone = ZoneId.systemDefault().id,
                            customerId = customer.id,
                            customerName = "${customer.firstName} ${customer.lastName}",
                            customerCedula = customer.cedula
                        )
                    )
                    if (!success) {
                        attempts++
                        delay(1000)
                    }
                }
                Log.d("LoanViewModel", "Print loan success: $success after $attempts attempts")
            } catch (e: Exception) {
                Log.e("LoanViewModel", "Exception printing loan: ${e.message}", e)
            }
        }
    }

    fun formatNumber(number: Double): String = "%,.2f".format(number)

    fun formatCedula(cedula: String): String = if (cedula.length == 11) {
        "${cedula.substring(0, 3)}-${cedula.substring(3, 10)}-${cedula.substring(10)}"
    } else cedula
}
