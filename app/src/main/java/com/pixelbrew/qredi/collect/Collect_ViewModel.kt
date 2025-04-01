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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CollectViewModel(
    private val loanRepository: LoanRepository,
    private val apiService: ApiService,
) : ViewModel() {


    private val _routes = MutableLiveData<List<RouteModel>>(emptyList())
    val routes: LiveData<List<RouteModel>> get() = _routes

    private val _downloadedRoutes = MutableLiveData<List<DownloadModel>>()
    val downloadedRoutes: LiveData<List<DownloadModel>> get() = _downloadedRoutes

    private val _downloadRouteSelected = MutableLiveData<DownloadModel>()
    val downloadRouteSelected: LiveData<DownloadModel> = _downloadRouteSelected

    private val _selectedFee = MutableLiveData<Fee>()
    val selectedFee: LiveData<Fee> get() = _selectedFee

    private val _amount = MutableLiveData<String>()
    val amount: LiveData<String> get() = _amount

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> get() = _toastMessage

    init {
        getLoansFromDatabase()
    }

    fun setDownloadRouteSelected(downloadRoute: DownloadModel) {
        _downloadRouteSelected.value = downloadRoute
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

    fun resetAmount() {
        _amount.value = ""
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
                    loanId = downloadRouteSelected.value?.id ?: "",
                    paymentAmount = amountValue,
                    number = _selectedFee.value?.number ?: 0
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
                    _downloadedRoutes.value = newLoans

                    newLoans.forEach { loan ->
                        if (loan.id == downloadRouteSelected.value?.id) {
                            _downloadRouteSelected.value = loan
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

        val loan = downloadRouteSelected.value
        val fee = selectedFee.value

        val cuotaN = fee?.number

        val feeAmount: Double = stringToDouble(_amount.value.toString())
        var total: Double = 0.0

        loan?.fees?.forEach { fee ->
            total += fee.paymentAmount
        }

        total += feeAmount

        val clientName = loan?.customer?.name!!

        val paymentData = InvoiceGenerator.DocumentData(
            items = listOf(
                InvoiceGenerator.DocumentItem(
                    description = "Cuota #${cuotaN}",
                    quantity = 1,
                    price = feeAmount,
                    tax = 0.0,
                )
            ),
            total = total,
            clientName = clientName,
            cashierName = "Jhon A. Guzman G.",
        )

        viewModelScope.launch(Dispatchers.IO) {
            BluetoothPrinter.printDocument(
                context,
                "2C-P58-C",
                BluetoothPrinter.DocumentType.PAYMENT,
                paymentData
            )
        }
        resetAmount()
    }
}