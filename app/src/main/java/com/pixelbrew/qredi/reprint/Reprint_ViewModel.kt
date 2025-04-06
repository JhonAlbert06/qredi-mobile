package com.pixelbrew.qredi.reprint

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pixelbrew.qredi.data.entities.LoanWithNewFees
import com.pixelbrew.qredi.data.entities.NewFeeEntity
import com.pixelbrew.qredi.data.repository.LoanRepository
import com.pixelbrew.qredi.invoice.BluetoothPrinter
import com.pixelbrew.qredi.invoice.InvoiceGenerator.DayCloseData
import com.pixelbrew.qredi.network.api.ApiService
import com.pixelbrew.qredi.network.model.Date
import com.pixelbrew.qredi.network.model.UploadFee
import com.pixelbrew.qredi.ui.components.services.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

class ReprintViewModel(
    private val loanRepository: LoanRepository,
    private val apiService: ApiService,
    private val sessionManager: SessionManager,
) : ViewModel() {
    private val baseUrl = sessionManager.fetchApiUrl()

    private val user = sessionManager.fetchUser()

    private val _newFees = MutableLiveData<List<NewFeeEntity>>(emptyList())
    val newFees: MutableLiveData<List<NewFeeEntity>> get() = _newFees

    private val _toastMessage = MutableLiveData<String?>()
    val toastMessage: LiveData<String?> get() = _toastMessage

    private val _showUploadDialog = MutableLiveData<Boolean>(false)
    val showUploadDialog: LiveData<Boolean> get() = _showUploadDialog

    private val _showReprintDialog = MutableLiveData<Boolean>(false)
    val showReprintDialog: LiveData<Boolean> get() = _showReprintDialog

    private val _feeSelected = MutableLiveData<NewFeeEntity>()

    private val _selectedLoan = MutableLiveData<LoanWithNewFees>()

    init {
        getAllNewFees()
    }

    fun getLoanById(loanId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val loan = loanRepository.getLoanById(loanId).first()
                _selectedLoan.postValue(loan)
            } catch (e: Exception) {
                Log.e("ReprintViewModel", "Error fetching loan: ${e.message}", e)
            }
        }
    }

    fun setFeeSelected(fee: NewFeeEntity) {
        _feeSelected.value = fee
        getLoanById(fee.loanId)
    }

    fun setShowUploadDialog(show: Boolean) {
        _showUploadDialog.value = show
    }

    fun setShowReprintDialog(show: Boolean) {
        _showReprintDialog.value = show
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    @RequiresApi(Build.VERSION_CODES.O)
    fun uploadFees() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                printDayCloset()

                val uploadFeeModel = _newFees.value?.map { fee ->
                    UploadFee(
                        feeId = fee.feeId,
                        amount = fee.paymentAmount
                    )
                } ?: emptyList()

                if (uploadFeeModel.isNotEmpty()) {
                    val uploadUrl = "$baseUrl/fee/uploadFees"
                    apiService.uploadFees(uploadUrl, uploadFeeModel)
                    resetDatabase()  // Se llama solo si la subida es exitosa
                }

                showToast("Recibos subidos correctamente")
            } catch (e: Exception) {
                Log.e("ReprintViewModel", "Error uploading fees: ${e.message}", e)
                showToast("Error al subir los recibos: ${e.message}")
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

    @RequiresApi(Build.VERSION_CODES.O)
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun printDayCloset() {
        val payments = newFees.value ?: emptyList()
        val date = LocalDateTime.now()
        var dateAux = Date(
            day = date.dayOfMonth,
            month = date.monthValue,
            year = date.year,
            hour = date.hour,
            minute = date.minute,
            second = date.second
        )

        val cierre = DayCloseData(
            date = "${dateAux.day}/${dateAux.month}/${dateAux.year}  ${dateAux.hour}:${dateAux.minute}",
            cashierName = user?.firstName + " " + user?.lastName,
            initialBalance = 0.0,
            totalLoans = 0.0,
            payments = payments
        )

        viewModelScope.launch(Dispatchers.IO) {
            val recibo = BluetoothPrinter.printDocument(
                sessionManager.fetchPrinterName().toString(),
                BluetoothPrinter.DocumentType.DAY_CLOSE,
                data = cierre
            )
            println(recibo)
        }


    }

    fun clearToast() {
        _toastMessage.value = null
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun printCollect() {
        val fee = _feeSelected.value ?: return

        viewModelScope.launch(Dispatchers.IO) {
            var attempts = 0
            var success = false

            while (attempts < 3 && !success) {
                success = BluetoothPrinter.printDocument(
                    sessionManager.fetchPrinterName().toString(),
                    BluetoothPrinter.DocumentType.PAYMENT,
                    feeEntity = fee,
                )

                if (!success) {
                    attempts++
                    delay(800) // Menor delay para no congelar UI por mucho tiempo
                }
            }

            val message = if (success) {
                "Recibo impreso correctamente"
            } else {
                "No se pudo imprimir el recibo después de 3 intentos"
            }

            // Enviar una sola vez al hilo principal
            withContext(Dispatchers.Main) {
                showToast(message)
            }
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
        _toastMessage.postValue(message)
    }
}