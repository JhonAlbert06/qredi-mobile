package com.pixelbrew.qredi.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "new_fees")
data class NewFeeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val feeId: String,
    val loanId: String,
    val paymentAmount: Double,
    val dateDay: Int,
    val dateMonth: Int,
    val dateYear: Int,
    val dateHour: Int,
    val dateMinute: Int,
    val dateSecond: Int,
    val dateTimezone: String,
    val number: Int,
    val numberTotal: Int,
    val companyName: String,
    val companyNumber: String,
    val clientName: String,
)