package com.pixelbrew.qredi.data.local.repository

import android.content.Context
import com.pixelbrew.qredi.data.dtos.TopCustomerDto
import com.pixelbrew.qredi.data.local.AppDatabase
import com.pixelbrew.qredi.data.local.entities.FeeEntity
import com.pixelbrew.qredi.data.local.entities.LoanEntity
import com.pixelbrew.qredi.data.local.entities.LoanWithFeesAndNewFees
import com.pixelbrew.qredi.data.local.entities.LoanWithNewFees
import com.pixelbrew.qredi.data.local.entities.NewFeeEntity
import kotlinx.coroutines.flow.Flow

class LoanRepository(context: Context) {

    private val loanDao = AppDatabase.getDatabase(context).loanDao()

    fun insertLoan(loan: LoanEntity) {
        loanDao.insertLoan(loan)
    }

    fun insertFee(fee: FeeEntity) {
        loanDao.insertFee(fee)
    }

    fun insertNewFee(fee: NewFeeEntity) {
        loanDao.insertNewFee(fee)
    }

    fun getAllNewFees(): Flow<List<NewFeeEntity>> {
        return loanDao.getAllNewFees()
    }

    fun deleteAllLoans() {
        loanDao.deleteAllLoans()
    }

    fun deleteAllFees() {
        loanDao.deleteAllFees()
    }

    fun deleteAllNewFees() {
        loanDao.deleteAllNewFees()
    }

    fun getLoanById(loanId: String): Flow<LoanWithNewFees> {
        return loanDao.getLoanById(loanId)
    }

    fun getLoansWithFeesAndNewFees(): Flow<List<LoanWithFeesAndNewFees>> {
        return loanDao.getLoansWithFeesAndNewFees()
    }


    suspend fun getAmountCollectedToday(day: Int, month: Int, year: Int): Double {
        return loanDao.getAmountCollectedToday(day, month, year)
    }

    suspend fun getTotalFeesToday(day: Int, month: Int, year: Int): Int {
        return loanDao.getTotalFeesToday(day, month, year)
    }

    suspend fun getPaidFeesToday(day: Int, month: Int, year: Int): Int {
        return loanDao.getPaidFeesToday(day, month, year)
    }

    suspend fun getNewLoansAmountToday(day: Int, month: Int, year: Int): Double {
        return loanDao.getNewLoansAmountToday(day, month, year)
    }

    suspend fun getMissingFeesCount(day: Int, month: Int, year: Int): Int {
        return loanDao.getMissingFeesCount(day, month, year)
    }

    suspend fun getMissingFeesAmount(day: Int, month: Int, year: Int): Double {
        return loanDao.getMissingFeesAmount(day, month, year)
    }

    suspend fun getTopCustomers(day: Int, month: Int, year: Int): List<TopCustomerDto> {
        return loanDao.getTopCustomers(day, month, year)
    }

    suspend fun getLargestLoan(): LoanEntity? {
        return loanDao.getLargestLoan()
    }

    suspend fun getFirstPaymentTime(day: Int, month: Int, year: Int): String? {
        return loanDao.getFirstPaymentTimestamp(day, month, year)
    }

    suspend fun getLastPaymentTime(day: Int, month: Int, year: Int): String? {
        return loanDao.getLastPaymentTimestamp(day, month, year)
    }

}