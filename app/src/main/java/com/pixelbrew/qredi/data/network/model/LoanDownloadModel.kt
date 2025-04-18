package com.pixelbrew.qredi.data.network.model

class LoanDownloadModel(
    var id: String = "",
    var amount: Double = 0.0,
    var interest: Double = 0.0,
    var feesQuantity: Int = 0,
    var feeDownloadModels: List<FeeDownloadModel> = emptyList(),
    var dateModel: DateModel = DateModel(0, 0, 0, 0, 0, 0, ""),
    var customerDownLoadModel: CustomerDownLoadModel = CustomerDownLoadModel("", "", ""),
)

class CustomerDownLoadModel(
    var id: String = "",
    var name: String = "",
    var cedula: String = "",
)

class DateModel(
    var day: Int = 0,
    var month: Int = 0,
    var year: Int = 0,
    var hour: Int = 0,
    var minute: Int = 0,
    var second: Int = 0,
    var timezone: String = ""
)

class FeeDownloadModel(
    var id: String = "",
    var paymentAmount: Double = 0.0,
    var number: Int = 0,
    var dateModel: DateModel = DateModel(0, 0, 0, 0, 0, 0, "")
)
