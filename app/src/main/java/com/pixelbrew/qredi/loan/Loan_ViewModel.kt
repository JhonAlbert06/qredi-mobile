package com.pixelbrew.qredi.loan

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pixelbrew.qredi.data.local.repository.LoanRepository
import com.pixelbrew.qredi.data.network.api.ApiService
import com.pixelbrew.qredi.ui.components.services.SessionManager

class LoanViewModel(
    private val loanRepository: LoanRepository,
    private val apiService: ApiService,
    private val sessionManager: SessionManager,
) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> get() = _toastMessage

    private fun showToast(message: String) {
        _toastMessage.postValue(message)
    }
}