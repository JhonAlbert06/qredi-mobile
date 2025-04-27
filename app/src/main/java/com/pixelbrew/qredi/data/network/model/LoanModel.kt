package com.pixelbrew.qredi.data.network.model

class LoanModel {
    var customerId = ""
    var routeId = ""
    var amount = 0.0
    var interest = 0.0
    var feesQuantity = 0

    constructor() {}

    constructor(
        customerId: String,
        routeId: String,
        amount: Double,
        interest: Double,
        feesQuantity: Int
    ) {
        this.customerId = customerId
        this.routeId = routeId
        this.amount = amount
        this.interest = interest
        this.feesQuantity = feesQuantity
    }
}

class LoanModelRes {
    val id = ""
    val amount = 0.0
    val interest = 0.0
    val feesQuantity = 0
    val loanIsPaid = false
    val isCurrentLoan = false
    val date = DateModel()
    val customer = CustomerModelRes()
    val route = RouteModel()
    val fees = listOf<FeeModelRes>()
}

class FeeModelRes {
    val id = ""
    val number = 0
    val expectedDate = DateModel()
    val payments = listOf<Payments>()
}

class Payments {
    val id = ""
    val paidAmount = 0.0
    val paidDate = DateModel()
    val user = UserModel()
}