package com.pixelbrew.qredi.customer

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pixelbrew.qredi.data.repository.LoanRepository
import com.pixelbrew.qredi.network.api.ApiService
import com.pixelbrew.qredi.network.model.CustomerModel
import com.pixelbrew.qredi.network.model.CustomerModelRes
import com.pixelbrew.qredi.network.model.UserModel
import com.pixelbrew.qredi.ui.components.services.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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

    init {
        fetchCustomers()
    }

    private fun fetchCustomers(query: String = "", field: String = "names") {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val url = "$baseUrl/customer?query=$query&field=$field"
                val customers = apiService.getCustomers(url)
                _customerList.postValue(customers)
            } catch (e: Exception) {
                Log.e("CustomerViewModel", "Error fetching customers: ${e.message}")
                showToast("Error fetching customers: ${e.message}")
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
        _newCustomer.value = customer

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val url = "$baseUrl/customer"
                val res = apiService.createCustomer(
                    url,
                    customer
                )

                Log.d("CustomerViewModel", "Creating customer: ${res.firstName} ${res.lastName}")
                hideCreationDialog()
            } catch (e: Exception) {
                Log.e("CustomerViewModel", "Error creating customer: ${e.message}")
                showToast("Error creating customer: ${e.message}")
            }
        }
    }
}