package com.pixelbrew.qredi.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "loans")
data class LoanEntity(
    @PrimaryKey val id: String,
    val amount: Double,
    val interest: Double,
    val feesQuantity: Int,
    val loanDateDay: Int,
    val loanDateMonth: Int,
    val loanDateYear: Int,
    val loanDateHour: Int,
    val loanDateMinute: Int,
    val loanDateSecond: Int,
    val loanDateTimezone: String,
    val customerId: String,
    val customerName: String,
    val customerCedula: String
)