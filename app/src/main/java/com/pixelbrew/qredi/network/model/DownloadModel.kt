package com.pixelbrew.qredi.network.model

class DownloadModel(
    var id: String = "",
    var amount: Double = 0.0,
    var interest: Double = 0.0,
    var feesQuantity: Int = 0,
    var fees: List<Fee> = emptyList(),
    var date: Date = Date(0, 0, 0, 0, 0, 0, ""),
    var customer: Customer = Customer("", "", ""),
)

class Customer(
    var id: String = "",
    var name: String = "",
    var cedula: String = "",
)

class Date(
    var day: Int = 0,
    var month: Int = 0,
    var year: Int = 0,
    var hour: Int = 0,
    var minute: Int = 0,
    var second: Int = 0,
    var timezone: String = "",
)

class Fee(
    var id: String = "",
    var paymentAmount: Double = 0.0,
    var number: Int = 0,
    var date: Date = Date(0, 0, 0, 0, 0, 0, "")
)
