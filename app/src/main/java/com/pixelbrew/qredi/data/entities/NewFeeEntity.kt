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
    val dateDay: Int,
    val dateMonth: Int,
    val dateYear: Int,
    val dateHour: Int,
    val dateMinute: Int,
    val dateSecond: Int,
    val dateTimezone: String,
    val clientName: String,
    val total: Double,
    val cashierName: String,
)