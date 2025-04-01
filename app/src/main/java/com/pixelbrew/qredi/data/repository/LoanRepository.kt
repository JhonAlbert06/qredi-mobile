package com.pixelbrew.qredi.data.repository

import android.content.Context
import com.pixelbrew.qredi.data.AppDatabase
import com.pixelbrew.qredi.data.entities.FeeEntity
import com.pixelbrew.qredi.data.entities.LoanEntity
import com.pixelbrew.qredi.data.entities.LoanWithFees
import com.pixelbrew.qredi.data.entities.NewFeeEntity
import kotlinx.coroutines.flow.Flow

class LoanRepository(context: Context) {

    private val loanDao = AppDatabase.getDatabase(context).loanDao()

    // Insertar un préstamo
    fun insertLoan(loan: LoanEntity) {
        loanDao.insertLoan(loan)
    }

    // Obtener todos los préstamos
    fun getAllLoans(): Flow<List<LoanEntity>> {
        return loanDao.getAllLoans()
    }

    // Obtener un préstamo por ID
    fun getLoanById(loanId: String): LoanEntity? {
        return loanDao.getLoanById(loanId)
    }

    // Insertar una cuota
    fun insertFee(fee: FeeEntity) {
        loanDao.insertFee(fee)
    }

    // Obtener todas las cuotas de un préstamo
    fun getFeesByLoanId(loanId: String): Flow<List<FeeEntity>> {
        return loanDao.getFeesByLoanId(loanId)
    }

    // Obtener un préstamo con sus cuotas
    fun getLoanWithFees(loanId: String): Flow<List<LoanWithFees>> {
        return loanDao.getLoanWithFees(loanId)
    }

    fun deleteAllLoans() {
        loanDao.deleteAllLoans()
    }

    fun deleteAllFees() {
        loanDao.deleteAllFees()
    }

    fun insertNewFee(fee: NewFeeEntity) {
        loanDao.insertNewFee(fee)
    }

    fun getNewFeesByLoanId(loanId: String): Flow<List<NewFeeEntity>> {
        return loanDao.getNewFeesByLoanId(loanId)
    }

    fun getAllNewFees(): Flow<List<NewFeeEntity>> {
        return loanDao.getAllNewFees()
    }

    fun deleteAllNewFees() {
        loanDao.deleteAllNewFees()
    }

}