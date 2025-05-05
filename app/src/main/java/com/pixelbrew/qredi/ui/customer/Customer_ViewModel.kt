package com.pixelbrew.qredi.ui.customer

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.pixelbrew.qredi.data.network.api.ApiService
import com.pixelbrew.qredi.data.network.model.ApiError
import com.pixelbrew.qredi.data.network.model.CustomerModel
import com.pixelbrew.qredi.data.network.model.CustomerModelRes
import com.pixelbrew.qredi.data.network.model.CustomerModelResWithDetail
import com.pixelbrew.qredi.data.network.model.UserModel
import com.pixelbrew.qredi.ui.components.services.SessionManager
import com.pixelbrew.qredi.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class Field(
    val name: String,
    val value: String
)

@HiltViewModel
class CustomerViewModel @Inject constructor(
    private val apiService: ApiService,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _showCreationDialog = MutableLiveData<Boolean>(false)
    val showCreationDialog: LiveData<Boolean> get() = _showCreationDialog

    private val _customerList = MutableLiveData<List<CustomerModelRes>>()
    val customerList: LiveData<List<CustomerModelRes>> get() = _customerList

    private val _newCustomer = MutableLiveData<CustomerModel>()

    val userSession: UserModel?
        get() = sessionManager.fetchUser()

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _showFilterCustomerDialog = MutableLiveData<Boolean>(false)
    val showFilterCustomerDialog: LiveData<Boolean> get() = _showFilterCustomerDialog

    private val _fieldSelected = MutableLiveData<Field>()
    val fieldSelected: LiveData<Field> get() = _fieldSelected

    private val _query = MutableLiveData<String>()
    val query: LiveData<String> get() = _query

    private val _toastMessage = MutableLiveData<Event<String>>()
    val toastMessage: LiveData<Event<String>> get() = _toastMessage

    private val _selectedCustomer = MutableLiveData<CustomerModelResWithDetail?>()
    val selectedCustomer: LiveData<CustomerModelResWithDetail?> get() = _selectedCustomer

    private val _showCustomerDetail = MutableLiveData<Boolean>(false)
    val showCustomerDetail: LiveData<Boolean> get() = _showCustomerDetail

    val fields = listOf(
        Field("Nombre", "names"),
        Field("Cedula", "cedula"),
        Field("Celular", "phone")
    )

    init {
        _isLoading.value = true
        fetchCustomers()
    }

    fun showCustomerDetail(value: Boolean) {
        _showCustomerDetail.value = value
    }

    private fun showToast(message: String) {
        _toastMessage.postValue(Event(message))
    }

    fun refreshCustomers() {
        _isLoading.value = true
        fetchCustomers()
    }

    fun showFilterCustomerDialog(value: Boolean) {
        _showFilterCustomerDialog.value = value
    }

    fun onSearchButtonClicked(field: Field, query: String) {
        _isLoading.value = true
        Log.d("CustomerViewModel", "Search initiated with query: $query, field: ${field.value}")
        fetchCustomers(query, field.value)
    }

    private fun fetchCustomers(query: String = "", field: String = "") {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val url = "${sessionManager.fetchApiUrl()}/customer?query=$query&field=$field"
                val response = apiService.getCustomers(url)
                val customers = if (response.isSuccessful) {
                    response.body() ?: emptyList()
                } else {
                    val errorBody = response.errorBody()?.string()
                    val error = Gson().fromJson(errorBody, ApiError::class.java)
                    Log.e("API_RESPONSE", "Fetch error: ${error.message}")
                    showToast("Error: ${error.message}")
                    emptyList()
                }
                withContext(Dispatchers.Main) {
                    _customerList.value = customers
                    _isLoading.value = false
                    Log.d("CustomerViewModel", "Fetched ${customers.size} customers")
                }
            } catch (e: Exception) {
                Log.e("CustomerViewModel", "Exception fetching customers: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    showToast("Error al obtener clientes: ${e.message}")
                    _isLoading.value = false
                }
            }
        }
    }

    fun showCreationDialog() {
        _showCreationDialog.value = true
    }

    fun hideCreationDialog() {
        _showCreationDialog.value = false
    }

    fun createCustomer(
        cedula: String,
        names: String,
        lastNames: String,
        address: String,
        phone: String,
        reference: String
    ) {
        val customer = CustomerModel(
            companyId = userSession?.company?.id ?: "",
            cedula = cedula,
            names = names,
            lastNames = lastNames,
            address = address,
            phone = phone,
            reference = reference
        )
        _newCustomer.postValue(customer)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val url = "${sessionManager.fetchApiUrl()}/customer"
                val response = apiService.createCustomer(url, customer)

                if (response.isSuccessful) {
                    val createdCustomer = response.body()
                    Log.d(
                        "CustomerViewModel",
                        "Customer created: ${createdCustomer?.firstName} ${createdCustomer?.lastName}"
                    )
                    withContext(Dispatchers.Main) {
                        hideCreationDialog()
                        fetchCustomers()
                        showToast("Cliente creado correctamente")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val error = Gson().fromJson(errorBody, ApiError::class.java)
                    Log.e("API_RESPONSE", "Create error: ${error.message}")
                    withContext(Dispatchers.Main) {
                        showToast("Error: ${error.message}")
                    }
                }
            } catch (e: Exception) {
                Log.e("CustomerViewModel", "Error creating customer: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    showToast("Error al crear cliente: ${e.message}")
                }
            }
        }
    }

    fun getCustomerById(customerId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val url = "${sessionManager.fetchApiUrl()}/customer/$customerId"
                val response = apiService.getCustomerWithDetail(url)

                if (response.isSuccessful) {
                    val customer = response.body()
                    _selectedCustomer.postValue(customer)
                    _showCustomerDetail.postValue(true)
                    Log.d("CustomerViewModel", "Customer loaded: $customer")
                    withContext(Dispatchers.Main) {
                        showToast("Cliente cargado correctamente")
                    }
                }

            } catch (e: Exception) {
                Log.e("CustomerViewModel", "Error fetching customer: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    showToast("Error al obtener cliente: ${e.message}")
                }
            }
        }
    }
}
