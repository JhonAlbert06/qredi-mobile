package com.pixelbrew.qredi.customer

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pixelbrew.qredi.data.local.repository.LoanRepository
import com.pixelbrew.qredi.data.network.api.ApiService
import com.pixelbrew.qredi.data.network.model.CustomerModel
import com.pixelbrew.qredi.data.network.model.CustomerModelRes
import com.pixelbrew.qredi.data.network.model.UserModel
import com.pixelbrew.qredi.ui.components.services.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

data class Field(
    val name: String,
    val value: String
)

class CustomerViewModel(
    private val loanRepository: LoanRepository,
    private val apiService: ApiService,
    private val sessionManager: SessionManager,
) : ViewModel() {

    private val _showCreationDialog = MutableLiveData<Boolean>(false)
    val showCreationDialog: LiveData<Boolean> get() = _showCreationDialog

    private val _customerList = MutableLiveData<List<CustomerModelRes>>()
    val customerList: LiveData<List<CustomerModelRes>> get() = _customerList

    private val _newCustomer = MutableLiveData<CustomerModel>()

    private val baseUrl = sessionManager.fetchApiUrl()

    val userSession: UserModel?
        get() = sessionManager.fetchUser()

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> get() = _toastMessage

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _showFilterCustomerDialog = MutableLiveData<Boolean>(false)
    val showFilterCustomerDialog: LiveData<Boolean> get() = _showFilterCustomerDialog

    private val _fieldSelected = MutableLiveData<Field>()
    val fieldSelected: LiveData<Field> get() = _fieldSelected

    private val _query = MutableLiveData<String>()
    val query: LiveData<String> get() = _query

    val fields = listOf(
        Field("Nombre", "names"),
        Field("Apellido", "lastNames"),
        Field("Cedula", "cedula"),
        Field("Celular", "phone")
    )

    init {
        _isLoading.postValue(true)
        fetchCustomers()
    }

    fun refreshCustomers() {
        _isLoading.postValue(true)
        fetchCustomers()
    }

    fun showFilterCustomerDialog(value: Boolean) {
        _showFilterCustomerDialog.postValue(value)
    }

    fun onSearchButtonClicked(field: Field, query: String) {
        _isLoading.postValue(true)

        Log.d("CustomerViewModel", "Search button clicked. Query: $query, Field: $field")
        fetchCustomers(query, field.value)
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

    private fun showToast(message: String) {
        _toastMessage.postValue(message)
    }

    fun showCreationDialog() {
        _showCreationDialog.postValue(true)
    }

    fun hideCreationDialog() {
        _showCreationDialog.postValue(false)
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
                val url = "$baseUrl/customer"
                val res = apiService.createCustomer(
                    url,
                    customer
                )
                val createdCustomer = res.body()!!
                if (res.isSuccessful) {
                    Log.d(
                        "CustomerViewModel",
                        "Creating customer: ${createdCustomer.firstName} ${createdCustomer.lastName}"
                    )
                    hideCreationDialog()
                    fetchCustomers()
                } else {
                    Log.e("CustomerViewModel", "Error creating customer: ${res.errorBody()}")
                    showToast("Error creating customer: ${res.errorBody()}")
                }
            } catch (e: Exception) {
                Log.e("CustomerViewModel", "Error creating customer: ${e.message}")
                showToast("Error creating customer: ${e.message}")
            }
        }
    }
}