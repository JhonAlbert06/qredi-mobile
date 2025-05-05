package com.pixelbrew.qredi.ui.statistics

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pixelbrew.qredi.data.local.repository.LoanRepository
import com.pixelbrew.qredi.data.network.api.ApiService
import com.pixelbrew.qredi.data.network.model.TopCustomer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class DashboardResponse(
    val amountCollected: String = "0.00",
    val percentageCollected: String = "0%",
    val newLoansCount: Int = 0,
    val newLoansAmount: String = "0.00",
    val missingPaymentsAmount: Int = 0,
    val missingPaymentsMoney: String = "0.00",
    val firstPaymentTime: LocalDateTime? = null,
    val lastPaymentTime: LocalDateTime? = null,
    val topCustomers: List<TopCustomer> = emptyList()
)

data class StatisticsState(
    val localStats: DashboardResponse? = null,
    val apiStats: DashboardResponse? = null
)

@HiltViewModel
@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("StaticFieldLeak")
class StatisticsViewModel @Inject constructor(
    private val apiService: ApiService,
    private val loanRepository: LoanRepository
) : ViewModel() {

    private val _statisticsState = MutableStateFlow(StatisticsState())
    val statisticsState: StateFlow<StatisticsState> = _statisticsState

    init {
        loadLocalStatistics()
    }

    fun loadLocalStatistics() {
        viewModelScope.launch {
            val today = java.time.LocalDate.now()
            val day = today.dayOfMonth
            val month = today.monthValue
            val year = today.year

            val amountCollectedToday = loanRepository.getAmountCollectedToday(day, month, year)
            val totalFeesToday = loanRepository.getTotalFeesToday(day, month, year)
            val paidFeesToday = loanRepository.getPaidFeesToday(day, month, year)
            val newLoansAmountToday = loanRepository.getNewLoansAmountToday(day, month, year)
            val missingFeesCount = loanRepository.getMissingFeesCount(day, month, year)
            val missingFeesAmount = loanRepository.getMissingFeesAmount(day, month, year)
            val topCustomers = loanRepository.getTopCustomers(day, month, year)

            val firstPaymentString = loanRepository.getFirstPaymentTime(day, month, year)
            val lastPaymentString = loanRepository.getLastPaymentTime(day, month, year)

            val firstPaymentTime = firstPaymentString?.let {
                LocalDateTime.parse(
                    it.replace("-", "/"),
                    DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")
                )
            }
            val lastPaymentTime = lastPaymentString?.let {
                LocalDateTime.parse(
                    it.replace("-", "/"),
                    DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")
                )
            }

            val percentageCollected = if (totalFeesToday > 0) {
                "%.2f%%".format((paidFeesToday.toDouble() / totalFeesToday) * 100)
            } else {
                "0%"
            }

            val localDashboard = DashboardResponse(
                amountCollected = "$${"%.2f".format(amountCollectedToday)}",
                percentageCollected = percentageCollected,
                newLoansCount = 0, // si tienes newLoansCount, reemplaza aquí
                newLoansAmount = "$${"%.2f".format(newLoansAmountToday)}",
                missingPaymentsAmount = missingFeesCount,
                missingPaymentsMoney = "$${"%.2f".format(missingFeesAmount)}",
                firstPaymentTime = firstPaymentTime,
                lastPaymentTime = lastPaymentTime,
                topCustomers = topCustomers.map { TopCustomer(it.clientName, it.amountPaid) }
            )

            _statisticsState.value = _statisticsState.value.copy(localStats = localDashboard)
        }
    }

    /*
    fun loadApiStatistics() {
        viewModelScope.launch {
            try {
                val response = apiService.getDashboard() // Llama tu endpoint real aquí
                val apiDashboard = DashboardResponse(
                    amountCollected = response.amountCollected,
                    percentageCollected = response.percentageCollected,
                    newLoansCount = response.newLoansCount,
                    newLoansAmount = response.newLoansAmount,
                    missingPaymentsAmount = response.missingPaymentsAmount,
                    missingPaymentsMoney = response.missingPaymentsMoney,
                    firstPaymentTime = response.firstPaymentTime,
                    lastPaymentTime = response.lastPaymentTime,
                    topCustomers = response.topCustomers
                )

                _statisticsState.value = _statisticsState.value.copy(apiStats = apiDashboard)

            } catch (e: Exception) {
                // Aquí puedes manejar errores o logs si lo deseas
            }
        }
    }

     */
}