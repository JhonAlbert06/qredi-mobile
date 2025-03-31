package com.pixelbrew.qredi.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "new_fees")
data class NewFeeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val feeId: String,
    val loanId: String,
    val paymentAmount: Double,
    val number: Int,
)