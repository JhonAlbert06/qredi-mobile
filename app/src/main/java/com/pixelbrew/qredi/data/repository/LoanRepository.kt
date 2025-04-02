package com.pixelbrew.qredi.data.repository

import android.content.Context
import com.pixelbrew.qredi.data.AppDatabase
import com.pixelbrew.qredi.data.entities.FeeEntity
import com.pixelbrew.qredi.data.entities.LoanEntity
import com.pixelbrew.qredi.data.entities.NewFeeEntity
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

    fun getAllLoans(): Flow<List<LoanEntity>> {
        return loanDao.getAllLoans()
    }

    fun getFeesByLoanId(loanId: String): Flow<List<FeeEntity>> {
        return loanDao.getFeesByLoanId(loanId)
    }

    fun getNewFeesByLoanId(loanId: String): Flow<List<NewFeeEntity>> {
        return loanDao.getNewFeesByLoanId(loanId)
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

}