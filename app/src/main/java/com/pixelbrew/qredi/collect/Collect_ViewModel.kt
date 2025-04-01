package com.pixelbrew.qredi.collect

import android.Manifest
import android.content.Context
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pixelbrew.qredi.data.converter.LoanMapper
import com.pixelbrew.qredi.data.entities.FeeEntity
import com.pixelbrew.qredi.data.entities.NewFeeEntity
import com.pixelbrew.qredi.data.repository.LoanRepository
import com.pixelbrew.qredi.invoice.BluetoothPrinter
import com.pixelbrew.qredi.invoice.InvoiceGenerator
import com.pixelbrew.qredi.network.api.ApiService
import com.pixelbrew.qredi.network.model.DownloadModel
import com.pixelbrew.qredi.network.model.Fee
import com.pixelbrew.qredi.network.model.RouteModel
import com.pixelbrew.qredi.network.model.UserModel
import com.pixelbrew.qredi.ui.components.services.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CollectViewModel(
    private val loanRepository: LoanRepository,
    private val apiService: ApiService,
    private val sessionManager: SessionManager,
) : ViewModel() {

    val userSession: UserModel?
        get() = sessionManager.fetchUser()

    private val _routes = MutableLiveData<List<RouteModel>>(emptyList())
    val routes: LiveData<List<RouteModel>> get() = _routes

    private val _downloadedLoans = MutableLiveData<List<DownloadModel>>()
    val downloadedLoans: LiveData<List<DownloadModel>> get() = _downloadedLoans

    private val _downloadLoanSelected = MutableLiveData<DownloadModel>()
    val downloadLoanSelected: LiveData<DownloadModel> = _downloadLoanSelected

    private val _selectedFee = MutableLiveData<Fee>()
    val selectedFee: LiveData<Fee> get() = _selectedFee

    private val _amount = MutableLiveData<String>()
    val amount: LiveData<String> get() = _amount

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> get() = _toastMessage

    init {
        getLoansFromDatabase()
        Log.d("DEBUG", "CollectViewModel initialized")
        Log.d("DEBUG", "${userSession?.firstName}")
    }

    fun resetAmount() {
        _amount.value = ""
    }

    fun setDownloadRouteSelected(downloadRoute: DownloadModel) {
        _downloadLoanSelected.value = downloadRoute
    }

    fun onAmountChange(amount: String) {
        _amount.value = amount
    }

    private fun showToast(message: String) {
        _toastMessage.value = message
    }

    fun setFeeSelected(fee: Fee) {
        _selectedFee.value = fee
    }

    fun collectFee() {
        Log.d("DEBUG_AMOUNT", "Valor de _amount antes de conversión: ${_amount.value}")
        val amountValue = _amount.value?.toDoubleOrNull() ?: 0.0
        Log.d("DEBUG_AMOUNT", "Valor de paymentAmount después de conversión: $amountValue")
        viewModelScope.launch(Dispatchers.IO) {
            try {
                var newFeeEntity = NewFeeEntity(
                    id = 0,
                    feeId = _selectedFee.value?.id ?: "",
                    loanId = downloadLoanSelected.value?.id ?: "",
                    paymentAmount = amountValue,
                    number = _selectedFee.value?.number ?: 0,
                    dateDay = _selectedFee.value?.date?.day ?: 0,
                    dateMonth = _selectedFee.value?.date?.month ?: 0,
                    dateYear = _selectedFee.value?.date?.year ?: 0,
                    dateHour = _selectedFee.value?.date?.hour ?: 0,
                    dateMinute = _selectedFee.value?.date?.minute ?: 0,
                    dateSecond = _selectedFee.value?.date?.second ?: 0,
                    dateTimezone = _selectedFee.value?.date?.timezone ?: ""
                )
                loanRepository.insertNewFee(newFeeEntity)
                getLoansFromDatabase()
            } catch (e: Exception) {
                Log.e("API_ERROR", "Error al obtener datos: ${e.message}")
                showToast(e.message.toString())
            }
        }
    }

    fun getRoutes() {
        viewModelScope.launch {
            try {
                val response = apiService.getRoutes()
                _routes.value = response
                showToast("Routes loaded successfully")
            } catch (e: Exception) {
                Log.e("API_ERROR", "Error al obtener datos: ${e.message}")
                showToast(e.message.toString())
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            loanRepository.deleteAllLoans()
            loanRepository.deleteAllFees()
        }
    }

    fun saveLoansOnDatabase(loan: DownloadModel) {
        viewModelScope.launch(Dispatchers.IO) { // ← Mover a IO
            try {
                val newLoan = LoanMapper.loanModelToEntity(loan)
                loanRepository.insertLoan(newLoan)

                loan.fees.forEach { fee ->
                    val newFee = LoanMapper.feeModelToEntity(fee, newLoan.id)
                    loanRepository.insertFee(newFee)
                }
            } catch (e: Exception) {
                Log.e("DB_ERROR", "Error al guardar datos: ${e.message}")
                showToast(e.message.toString())
            }
        }
    }

    fun getLoansFromDatabase() {
        Log.d("ROOM_DB", "Ejecutando getLoansFromDatabase()...")

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val downloadedRoutesAux =
                    loanRepository.getAllLoans().first() // Usamos first() en lugar de collect()

                Log.d("ROOM_DB", "Cantidad de préstamos recuperados: ${downloadedRoutesAux.size}")

                val newLoans = downloadedRoutesAux.map { loan ->

                    val feesDB = loanRepository.getFeesByLoanId(loan.id)
                        .first()

                    val feesNewDB = loanRepository.getNewFeesByLoanId(loan.id)
                        .first()

                    val feesRes = feesFusionAmount(feesDB, feesNewDB)

                    val newLoan = LoanMapper.loanEntityToModel(loan, feesRes)
                    newLoan
                }

                withContext(Dispatchers.Main) {
                    Log.d("ROOM_DB", "Actualizando LiveData con ${newLoans.size} préstamos")
                    _downloadedLoans.value = newLoans

                    newLoans.forEach { loan ->
                        if (loan.id == downloadLoanSelected.value?.id) {
                            _downloadLoanSelected.value = loan
                        }
                    }
                }

            } catch (e: Exception) {
                Log.e("DB_ERROR", "Error al obtener datos: ${e.message}")
            }
        }
    }

    fun feesFusionAmount(feesDB: List<FeeEntity>, feesNewDB: List<NewFeeEntity>): List<FeeEntity> {
        val newFeesMap = feesNewDB.groupBy { it.loanId to it.number }

        return feesDB.map { fee ->
            val extraAmount =
                newFeesMap[fee.loanId to fee.number]?.sumOf { it.paymentAmount } ?: 0.0

            fee.copy(paymentAmount = fee.paymentAmount + extraAmount)
        }
    }

    fun downloadRoute(id: String) {
        viewModelScope.launch {
            try {
                val response = apiService.downloadRoute(id)

                response.forEach { loan ->
                    saveLoansOnDatabase(loan)
                }

                // Esperar 2 segundos
                delay(2000)
                getLoansFromDatabase()
                showToast("Route downloaded successfully")
            } catch (e: Exception) {
                Log.e("API_ERROR", "Error al obtener datos: ${e.message}")
                showToast(e.message.toString())
            }
        }
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

    fun stringToDouble(value: String): Double {
        return try {
            value.toDouble()
        } catch (e: NumberFormatException) {
            0.0
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun printCollect(context: Context) {
        val loan = downloadLoanSelected.value ?: run {
            showError("No se ha seleccionado ningún préstamo")
            return
        }

        val fee = selectedFee.value ?: run {
            showError("No se ha seleccionado ninguna cuota")
            return
        }

        try {
            val feeAmount = stringToDouble(_amount.value.toString())
            var total = loan.fees.sumOf { it.paymentAmount } + feeAmount

            val paymentData = InvoiceGenerator.DocumentData(
                items = listOf(
                    InvoiceGenerator.DocumentItem(
                        description = "Cuota #${fee.number}",
                        quantity = 1,
                        price = feeAmount,
                        tax = 0.0
                    )
                ),
                total = total,
                clientName = loan.customer.name,
                cashierName = "${userSession?.firstName} ${userSession?.lastName}".trim()
            )

            viewModelScope.launch(Dispatchers.IO) {
                var attempts = 0
                var success = false

                while (attempts < 3 && !success) {
                    success = BluetoothPrinter.printDocument(
                        context,
                        "2C-P58-C", // Nombre de tu impresora
                        BluetoothPrinter.DocumentType.PAYMENT,
                        paymentData
                    )

                    if (!success) {
                        attempts++
                        delay(1000) // Esperar 1 segundo antes de reintentar
                    }
                }

                withContext(Dispatchers.Main) {
                    if (success) {
                        //resetAmount()
                        showSuccess("Recibo impreso correctamente")
                    } else {
                        showError("No se pudo imprimir el recibo después de 3 intentos")
                    }
                }
            }
        } catch (e: NumberFormatException) {
            showError("El monto ingresado no es válido")
        } catch (e: Exception) {
            showError("Error al generar el recibo: ${e.localizedMessage}")
        }
    }

    private fun showError(message: String) {
        // Implementa tu lógica para mostrar errores al usuario
        // _errorMessage.value = message
    }

    private fun showSuccess(message: String) {
        // Implementa tu lógica para mostrar mensajes de éxito
        //_successMessage.value = message
    }
}