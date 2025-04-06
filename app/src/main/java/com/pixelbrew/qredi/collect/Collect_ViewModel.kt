package com.pixelbrew.qredi.collect

import android.Manifest
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
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
import java.time.LocalDateTime
import java.time.ZoneId

class CollectViewModel(
    private val loanRepository: LoanRepository,
    private val apiService: ApiService,
    private val sessionManager: SessionManager,
) : ViewModel() {

    private val baseUrl = sessionManager.fetchApiUrl()

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

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

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
        _toastMessage.postValue(message)
    }

    fun setFeeSelected(fee: Fee) {
        _selectedFee.value = fee
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun collectFee() {
        val date = LocalDateTime.now(ZoneId.systemDefault())
        Log.d("DEBUG_AMOUNT", "Valor de _amount antes de conversión: ${_amount.value}")
        var amountValue: Double = _amount.value?.toDoubleOrNull() ?: 0.0
        Log.d("DEBUG_AMOUNT", "Valor de paymentAmount después de conversión: $amountValue")

        viewModelScope.launch(Dispatchers.IO) {
            try {
                var newFeeEntity = NewFeeEntity(
                    id = 0,
                    feeId = _selectedFee.value?.id ?: "",
                    loanId = downloadLoanSelected.value?.id ?: "",
                    paymentAmount = amountValue,
                    number = _selectedFee.value?.number ?: 0,
                    dateDay = date.dayOfMonth,
                    dateMonth = date.monthValue,
                    dateYear = date.year,
                    dateHour = date.hour,
                    dateMinute = date.minute,
                    dateSecond = date.second,
                    dateTimezone = ZoneId.systemDefault().id,
                    clientName = downloadLoanSelected.value?.customer?.name.toString(),
                    companyName = userSession?.company?.name ?: "J & J Prestamos",
                    numberTotal = downloadLoanSelected.value?.feesQuantity ?: 0,
                    companyNumber = "${userSession?.company?.phone1}/${userSession?.company?.phone2}"
                        ?: "809-123-4567 / 809-123-4567"
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
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val routesUrl = "$baseUrl/routes"
                val response = apiService.getRoutes(routesUrl)

                withContext(Dispatchers.Main) {
                    _routes.value = response
                    showToast("Routes loaded successfully")
                }
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
                    loanRepository.getAllLoans().first()

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
        _isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val downloadUrl = "$baseUrl/route/download/$id"
                val response = apiService.downloadRoute(downloadUrl)

                response.forEach { saveLoansOnDatabase(it) }

                delay(1000)
                getLoansFromDatabase()

                withContext(Dispatchers.Main) {
                    showToast("Ruta descargada correctamente")
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showToast("Error al descargar ruta: ${e.message}")
                    _isLoading.value = false
                }
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
            Log.e("DEBUG", "Error al convertir a Double: ${e.message}")
            0.0
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun printCollect(context: Context) {
        val loan = downloadLoanSelected.value ?: run {
            showToast("No se ha seleccionado ningún préstamo")
            return
        }


        val fee = selectedFee.value ?: run {
            showToast("No se ha seleccionado ninguna cuota")
            return
        }

        try {
            val feeAmount = stringToDouble(_amount.value.toString())

            viewModelScope.launch(Dispatchers.IO) {
                var attempts = 0
                var success = false

                while (attempts < 3 && !success) {
                    success = BluetoothPrinter.printDocument(

                        sessionManager.fetchPrinterName().toString(),
                        BluetoothPrinter.DocumentType.PAYMENT,
                        feeEntity = NewFeeEntity(
                            id = 0,
                            feeId = fee.id,
                            loanId = loan.id,
                            paymentAmount = feeAmount,
                            dateDay = LocalDateTime.now().dayOfMonth,
                            dateMonth = LocalDateTime.now().monthValue,
                            dateYear = LocalDateTime.now().year,
                            dateHour = LocalDateTime.now().hour,
                            dateMinute = LocalDateTime.now().minute,
                            dateSecond = LocalDateTime.now().second,
                            dateTimezone = ZoneId.systemDefault().id,
                            number = fee.number,
                            numberTotal = loan.feesQuantity,
                            companyName = "J & J Prestamos",
                            companyNumber = "809-123-4567 / 809-123-4567",
                            clientName = loan.customer.name
                        )
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

}