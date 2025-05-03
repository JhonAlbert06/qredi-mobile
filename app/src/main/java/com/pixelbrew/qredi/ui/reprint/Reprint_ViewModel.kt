package com.pixelbrew.qredi.ui.reprint

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
import com.pixelbrew.qredi.data.local.entities.LoanWithNewFees
import com.pixelbrew.qredi.data.local.entities.NewFeeEntity
import com.pixelbrew.qredi.data.local.repository.LoanRepository
import com.pixelbrew.qredi.data.network.api.ApiService
import com.pixelbrew.qredi.data.network.model.DateModel
import com.pixelbrew.qredi.data.network.model.UploadFee
import com.pixelbrew.qredi.ui.components.services.SessionManager
import com.pixelbrew.qredi.ui.components.services.invoice.BluetoothPrinter
import com.pixelbrew.qredi.ui.components.services.invoice.InvoiceGenerator.DayCloseData
import com.pixelbrew.qredi.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
@SuppressLint("StaticFieldLeak")
class ReprintViewModel @Inject constructor(
    private val loanRepository: LoanRepository,
    private val apiService: ApiService,
    private val sessionManager: SessionManager
) : ViewModel() {


    private val _newFees = MutableLiveData<List<NewFeeEntity>>(emptyList())
    val newFees: LiveData<List<NewFeeEntity>> get() = _newFees


    private val _showUploadDialog = MutableLiveData<Boolean>(false)
    val showUploadDialog: LiveData<Boolean> get() = _showUploadDialog

    private val _showReprintDialog = MutableLiveData<Boolean>(false)
    val showReprintDialog: LiveData<Boolean> get() = _showReprintDialog

    private val _feeSelected = MutableLiveData<NewFeeEntity>()
    private val _selectedLoan = MutableLiveData<LoanWithNewFees>()

    private val _toastMessage = MutableLiveData<Event<String>>()
    val toastMessage: LiveData<Event<String>> get() = _toastMessage


    init {
        getAllNewFees()
    }

    private fun showToast(message: String) {
        _toastMessage.postValue(Event(message))
    }


    fun getLoanById(loanId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                loanRepository.getLoanById(loanId).collect { loan ->
                    withContext(Dispatchers.Main) {
                        _selectedLoan.value = loan
                        Log.d("ReprintViewModel", "Loan loaded: $loan")
                    }
                }
            } catch (e: Exception) {
                Log.e("ReprintViewModel", "Error fetching loan: ${e.message}", e)
                showToast("Error al cargar el préstamo: ${e.message}")
            }
        }
    }

    fun setFeeSelected(fee: NewFeeEntity) {
        _feeSelected.postValue(fee)
        getLoanById(fee.loanId)
    }

    fun setShowUploadDialog(show: Boolean) {
        _showUploadDialog.postValue(show)
    }

    fun setShowReprintDialog(show: Boolean) {
        _showReprintDialog.postValue(show)
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
                        amount = fee.paymentAmount,
                        dateDay = fee.dateDay,
                        dateMonth = fee.dateMonth,
                        dateYear = fee.dateYear,
                        dateHour = fee.dateHour,
                        dateMinute = fee.dateMinute,
                        dateSecond = fee.dateSecond,
                        dateTimezone = fee.dateTimezone
                    )
                } ?: emptyList()

                val uploadUrl = "${sessionManager.fetchApiUrl()}/fee/uploadFees"

                if (uploadFeeModel.isNotEmpty()) {
                    val response = apiService.uploadFees(uploadUrl, uploadFeeModel)
                    if (response.isSuccessful) {
                        resetDatabase()
                        showToast("Recibos subidos correctamente")
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.d("API_RESPONSE", "Error: $errorBody")
                        showToast("Error: $errorBody")
                    }
                } else {
                    showToast("No hay recibos para subir")
                }
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
            withContext(Dispatchers.Main) {
                _newFees.value = emptyList()
            }
        }
    }

    fun getAllNewFees() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                loanRepository.getAllNewFees().collect { fees ->
                    withContext(Dispatchers.Main) {
                        _newFees.value = fees
                        Log.d("ReprintViewModel", "Recibos obtenidos correctamente: ${fees.size}")
                    }
                }
            } catch (e: Exception) {
                Log.e("ReprintViewModel", "Error al obtener recibos: ${e.message}", e)
                showToast("Error al obtener recibos: ${e.message}")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun printDayCloset() {
        val payments = newFees.value ?: emptyList()
        val date = LocalDateTime.now()
        val dateModelAux = DateModel(
            day = date.dayOfMonth,
            month = date.monthValue,
            year = date.year,
            hour = date.hour,
            minute = date.minute,
            second = date.second
        )

        val cierre = DayCloseData(
            date = "${dateModelAux.day}/${dateModelAux.month}/${dateModelAux.year}  ${dateModelAux.hour}:${dateModelAux.minute}",
            cashierName = "${sessionManager.fetchUser()?.firstName} ${sessionManager.fetchUser()?.lastName}",
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
            Log.d("ReprintViewModel", "Impresión cierre: $recibo")
        }
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
                    feeEntity = fee
                )

                if (!success) {
                    attempts++
                    delay(800)
                }
            }

            withContext(Dispatchers.Main) {
                val message = if (success) {
                    "Recibo impreso correctamente"
                } else {
                    "No se pudo imprimir el recibo después de 3 intentos"
                }
                showToast(message)
            }
        }
    }

    @SuppressLint("DefaultLocale")
    fun formatDate(day: Int, month: Int, year: Int): String =
        String.format("%02d/%02d/%04d", day, month, year)

    @SuppressLint("DefaultLocale")
    fun formatTime(hour: Int, minute: Int, second: Int): String =
        String.format("%02d:%02d:%02d", hour, minute, second)

}
