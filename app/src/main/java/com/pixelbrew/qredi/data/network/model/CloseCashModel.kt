package com.pixelbrew.qredi.data.network.model

class CloseCashModel {
    var date: String = ""
    var userId: String = ""
    var customerAttended: Int = 0
    var paymentsReceived: Int = 0
    var totalAmount: Float = 0.0f
    var loansGranted: Int = 0
    var finalBalance: Float = 0.0f
    var cashRegisterDetail: List<UploadFee> = emptyList()

    constructor() {}

    constructor(
        date: String,
        userId: String,
        customerAttended: Int,
        paymentsReceived: Int,
        totalAmount: Float,
        loansGranted: Int,
        finalBalance: Float,
        cashRegisterDetail: List<UploadFee>
    ) {
        this.date = date
        this.userId = userId
        this.customerAttended = customerAttended
        this.paymentsReceived = paymentsReceived
        this.totalAmount = totalAmount
        this.loansGranted = loansGranted
        this.finalBalance = finalBalance
        this.cashRegisterDetail = cashRegisterDetail
    }
}

