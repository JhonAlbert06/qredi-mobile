package com.pixelbrew.qredi.reprint

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pixelbrew.qredi.data.entities.LoanWithFees
import com.pixelbrew.qredi.data.entities.NewFeeEntity
import com.pixelbrew.qredi.data.repository.LoanRepository
import com.pixelbrew.qredi.invoice.BluetoothPrinter
import com.pixelbrew.qredi.invoice.InvoiceGenerator
import com.pixelbrew.qredi.network.api.ApiService
import com.pixelbrew.qredi.network.model.UploadFee
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ReprintViewModel(
    private val loanRepository: LoanRepository,
    private val apiService: ApiService
) : ViewModel() {

    private val _Loan = MutableLiveData<List<LoanWithFees>>(emptyList())
    val loan: LiveData<List<LoanWithFees>> get() = _Loan

    private val _newFees = MutableLiveData<List<NewFeeEntity>>(emptyList())
    val newFees: MutableLiveData<List<NewFeeEntity>> get() = _newFees

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> get() = _toastMessage

    private val _showUploadDialog = MutableLiveData<Boolean>(false)
    val showUploadDialog: LiveData<Boolean> get() = _showUploadDialog

    private val _showReprintDialog = MutableLiveData<Boolean>(false)
    val showReprintDialog: LiveData<Boolean> get() = _showReprintDialog

    private val _feeSelected = MutableLiveData<NewFeeEntity>()

    init {
        getAllNewFees()
    }

    fun setFeeSelected(fee: NewFeeEntity) {
        _feeSelected.value = fee
    }

    fun setShowUploadDialog(show: Boolean) {
        _showUploadDialog.value = show
    }

    fun setShowReprintDialog(show: Boolean) {
        _showReprintDialog.value = show
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
        _newFees.postValue(emptyList())
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

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun printCollect(context: Context) {

        val fee = _feeSelected.value ?: return

        try {
            val paymentData = InvoiceGenerator.DocumentData(
                items = listOf(
                    InvoiceGenerator.DocumentItem(
                        description = "Cuota #${fee.number}",
                        quantity = 1,
                        price = fee.paymentAmount,
                        tax = 0.0
                    )
                ),
                total = fee.total,
                clientName = fee.clientName,
                cashierName = fee.cashierName
            )

            viewModelScope.launch(Dispatchers.IO) {
                var attempts = 0
                var success = false

                while (attempts < 3 && !success) {
                    success = BluetoothPrinter.printDocument(
                        context,
                        "2C-P58-C",
                        BluetoothPrinter.DocumentType.PAYMENT,
                        paymentData
                    )

                    if (!success) {
                        attempts++
                        delay(1000)
                    }
                }

                withContext(Dispatchers.Main) {
                    if (success) {
                        showToast("Recibo impreso correctamente")
                    } else {
                        showToast("No se pudo imprimir el recibo después de 3 intentos")
                    }
                }
            }
        } catch (e: NumberFormatException) {
            showToast("El monto ingresado no es válido: ${e.localizedMessage}")
        } catch (e: Exception) {
            showToast("Error al generar el recibo: ${e.localizedMessage}")
        }
    }

    @SuppressLint("DefaultLocale")
    fun formatDate(day: Int, month: Int, year: Int): String {
        return String.format("%02d/%02d/%04d", day, month, year)
    }

    @SuppressLint("DefaultLocale")
    fun formatTime(hour: Int, minute: Int, second: Int): String {
        return String.format("%02d:%02d:%02d", hour, minute, second)
    }


    private fun showToast(message: String) {
        _toastMessage.value = message
    }
}