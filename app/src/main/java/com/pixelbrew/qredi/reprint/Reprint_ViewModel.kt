package com.pixelbrew.qredi.reprint

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pixelbrew.qredi.data.entities.NewFeeEntity
import com.pixelbrew.qredi.data.repository.LoanRepository
import com.pixelbrew.qredi.network.api.ApiService
import com.pixelbrew.qredi.network.model.UploadFee
import com.pixelbrew.qredi.ui.components.services.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ReprintViewModel(
    private val loanRepository: LoanRepository,
    private val apiService: ApiService,
    private val sessionManager: SessionManager,
) : ViewModel() {

    private val _newFees = MutableLiveData<List<NewFeeEntity>>(emptyList())
    val newFees: MutableLiveData<List<NewFeeEntity>> get() = _newFees

    init {
        getAllNewFees()
    }

    fun uploadFees() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val uploadFeeModel = _newFees.value?.map { fee ->
                    UploadFee(
                        feeId = fee.feeId,
                        amount = fee.paymentAmount
                    )
                } ?: emptyList()

                if (uploadFeeModel.isNotEmpty()) {
                    apiService.uploadFees(uploadFeeModel)
                    resetDatabase()  // Se llama solo si la subida es exitosa
                }
            } catch (e: Exception) {
                Log.e("ReprintViewModel", "Error uploading fees: ${e.message}", e)
            }
        }
    }

    fun resetDatabase() {
        viewModelScope.launch(Dispatchers.IO) {
            loanRepository.deleteAllLoans()
            loanRepository.deleteAllFees()
            loanRepository.deleteAllNewFees()
            Log.d("ReprintViewModel", "Base de datos limpiada correctamente")
        
        }
    }

    fun getAllNewFees() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val responseDB = loanRepository.getAllNewFees().first()
                newFees.postValue(responseDB)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


}