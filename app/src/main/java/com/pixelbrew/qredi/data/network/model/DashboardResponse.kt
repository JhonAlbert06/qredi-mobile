package com.pixelbrew.qredi.data.network.model

import java.time.LocalDateTime

data class DashboardResponse(
    val amountCollected: String,
    val percentageCollected: String,
    val newLoansCount: Int,
    val newLoansAmount: String,
    val missingPaymentsAmount: Int,
    val missingPaymentsMoney: String,
    val firstPaymentTime: LocalDateTime?,
    val lastPaymentTime: LocalDateTime?,
    val topCustomers: List<TopCustomer>
)

data class TopCustomer(
    val customerName: String,
    val amountPaid: Double
)