package com.pixelbrew.qredi.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.pixelbrew.qredi.data.entities.FeeEntity
import com.pixelbrew.qredi.data.entities.LoanEntity
import com.pixelbrew.qredi.data.entities.LoanWithFees
import kotlinx.coroutines.flow.Flow

@Dao
interface LoanDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLoan(loan: LoanEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFee(fee: FeeEntity)

    @Query("SELECT * FROM loans")
    fun getAllLoans(): Flow<List<LoanEntity>>

    @Query("SELECT * FROM loans WHERE id = :loanId")
    fun getLoanById(loanId: String): LoanEntity?

    @Query("SELECT * FROM fees WHERE loanId = :loanId")
    fun getFeesByLoanId(loanId: String): Flow<List<FeeEntity>>

    @Transaction
    @Query("SELECT * FROM loans WHERE id = :loanId")
    fun getLoanWithFees(loanId: String): Flow<List<LoanWithFees>>
}