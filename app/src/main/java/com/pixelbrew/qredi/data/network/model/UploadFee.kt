package com.pixelbrew.qredi.data.network.model

class UploadFee {
    var feeId: String = ""
    var amount: Double = 0.0
    var dateDay: Int = 0
    var dateMonth: Int = 0
    var dateYear: Int = 0
    var dateHour: Int = 0
    var dateMinute: Int = 0
    var dateSecond: Int = 0
    var dateTimezone: String = ""

    constructor(
        feeId: String,
        amount: Double,
        dateDay: Int,
        dateMonth: Int,
        dateYear: Int,
        dateHour: Int,
        dateMinute: Int,
        dateSecond: Int,
        dateTimezone: String
    ) {
        this.feeId = feeId
        this.amount = amount
        this.dateDay = dateDay
        this.dateMonth = dateMonth
        this.dateYear = dateYear
        this.dateHour = dateHour
        this.dateMinute = dateMinute
        this.dateSecond = dateSecond
        this.dateTimezone = dateTimezone
    }
}