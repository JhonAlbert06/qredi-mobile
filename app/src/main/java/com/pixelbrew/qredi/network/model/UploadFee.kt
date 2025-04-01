package com.pixelbrew.qredi.network.model

class UploadFee {
    var feeId: String = ""
    var amount: Double = 0.0

    constructor(feeId: String, amount: Double) {
        this.feeId = feeId
        this.amount = amount
    }
}