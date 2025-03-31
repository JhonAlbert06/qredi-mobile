package com.pixelbrew.qredi.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fees")
data class FeeEntity(
    @PrimaryKey val id: String,
    val loanId: String,  // Relación con LoanEntity
    val paymentAmount: Double,
    val number: Int,
    val dateDay: Int,
    val dateMonth: Int,
    val dateYear: Int,
    val dateHour: Int,
    val dateMinute: Int,
    val dateSecond: Int,
    val dateTimezone: String
)