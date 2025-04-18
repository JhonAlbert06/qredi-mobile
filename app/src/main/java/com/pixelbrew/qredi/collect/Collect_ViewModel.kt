package com.pixelbrew.qredi.collect

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
import com.pixelbrew.qredi.data.local.converter.LoanMapper
import com.pixelbrew.qredi.data.local.entities.FeeEntity
import com.pixelbrew.qredi.data.local.entities.NewFeeEntity
import com.pixelbrew.qredi.data.local.repository.LoanRepository
import com.pixelbrew.qredi.data.network.api.ApiService
import com.pixelbrew.qredi.data.network.model.FeeDownloadModel
import com.pixelbrew.qredi.data.network.model.LoanDownloadModel
import com.pixelbrew.qredi.data.network.model.RouteModel
import com.pixelbrew.qredi.data.network.model.UserModel
import com.pixelbrew.qredi.ui.components.services.SessionManager
import com.pixelbrew.qredi.ui.components.services.invoice.BluetoothPrinter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.ZoneId

@SuppressLint("StaticFieldLeak")
class CollectViewModel(
    private val loanRepository: LoanRepository,
    private val apiService: ApiService,
    private val sessionManager: SessionManager,
    private val context: android.content.Context
) : ViewModel() {

    private val baseUrl = sessionManager.fetchApiUrl()

    private val _cuote = MutableLiveData<Double>(0.0)
    val cuote: LiveData<Double> get() = _cuote

    val userSession: UserModel?
        get() = sessionManager.fetchUser()

    private val _routes = MutableLiveData<List<RouteModel>>(emptyList())
    val routes: LiveData<List<RouteModel>> get() = _routes

    private val _downloadedLoans = MutableLiveData<List<LoanDownloadModel>>()
    val downloadedLoans: LiveData<List<LoanDownloadModel>> get() = _downloadedLoans

    private val _downloadLoanSelected = MutableLiveData<LoanDownloadModel>()
    val downloadLoanSelected: LiveData<LoanDownloadModel> = _downloadLoanSelected

    private val _selectedFeeDownloadModel = MutableLiveData<FeeDownloadModel>()
    val selectedFeeDownloadModel: LiveData<FeeDownloadModel> get() = _selectedFeeDownloadModel

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

    fun getCuote(amount: Double) {
        val loan = downloadLoanSelected.value ?: return
        var cuoteAUX = ((loan.interest / 100) * loan.amount) + (loan.amount / loan.feesQuantity)

        _cuote.postValue(cuoteAUX)

        cuoteAUX -= amount

        _amount.postValue(cuoteAUX.toString())

    }

    fun setDownloadRouteSelected(downloadRoute: LoanDownloadModel) {
        _downloadLoanSelected.postValue(downloadRoute)
    }

    fun onAmountChange(amount: String) {
        _amount.postValue(amount)
    }

    private fun showToast(message: String) {
        _toastMessage.postValue(message)
    }

    fun setFeeSelected(feeDownloadModel: FeeDownloadModel) {
        _selectedFeeDownloadModel.postValue(feeDownloadModel)
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
                    feeId = _selectedFeeDownloadModel.value?.id ?: "",
                    loanId = downloadLoanSelected.value?.id ?: "",
                    paymentAmount = amountValue,
                    number = _selectedFeeDownloadModel.value?.number ?: 0,
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

                val response = withContext(Dispatchers.Main) {
                    apiService.getRoutes(routesUrl)
                }

                if (response.isSuccessful) {
                    val routes = response.body() ?: emptyList()
                    Log.d("API_RESPONSE", "Rutas: $routes")
                    _routes.postValue(routes)
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.d("API_RESPONSE", "Error: $errorBody")
                    showToast("Error: $errorBody")
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

    fun saveLoansOnDatabase(loan: LoanDownloadModel) {
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
        _isLoading.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val downloadUrl = "$baseUrl/route/download/$id"
                val response = apiService.downloadRoute(downloadUrl)

                if (response.isSuccessful) {
                    val loans = response.body() ?: emptyList()
                    loans.forEach { saveLoansOnDatabase(it) }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.d("API_RESPONSE", "Error: $errorBody")
                    showToast("Error: $errorBody")
                }

                delay(1000)
                getLoansFromDatabase()

                withContext(Dispatchers.Main) {
                    _isLoading.postValue(false)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showToast("Error al descargar ruta: ${e.message}")
                    _isLoading.postValue(false)
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
    fun printCollect() {
        val loan = downloadLoanSelected.value ?: run {
            showToast("No se ha seleccionado ningún préstamo")
            return
        }


        val fee = selectedFeeDownloadModel.value ?: run {
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
                        context = context,
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
                            companyNumber = "${userSession?.company?.phone1}/${userSession?.company?.phone2}",
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