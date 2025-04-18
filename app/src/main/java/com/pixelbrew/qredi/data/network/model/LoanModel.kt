package com.pixelbrew.qredi.data.network.model

class LoanModel {
    val customerId = ""
    val routeId = ""
    val amount = 0.0
    val interest = 0.0
    val feesQuantity = 0
}

class LoanModelRes {
    val id = ""
    val amount = 0.0
    val interest = 0.0
    val feesQuantity = 0
    val loanIsPaid = false
    val isCurrentLoan = false
    val DateModel = DateModel()
    val customer = CustomerModelRes()
    val route = RouteModel()
    val feeDownloadModels = listOf<FeeDownloadModel>()
}