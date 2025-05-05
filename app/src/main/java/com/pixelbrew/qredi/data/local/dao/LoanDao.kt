package com.pixelbrew.qredi.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.pixelbrew.qredi.data.local.entities.FeeEntity
import com.pixelbrew.qredi.data.local.entities.LoanEntity
import com.pixelbrew.qredi.data.local.entities.LoanWithFeesAndNewFees
import com.pixelbrew.qredi.data.local.entities.LoanWithNewFees
import com.pixelbrew.qredi.data.local.entities.NewFeeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LoanDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLoan(loan: LoanEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFee(fee: FeeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNewFee(fee: NewFeeEntity)

    @Query("SELECT * FROM loans")
    fun getAllLoans(): Flow<List<LoanEntity>>

    @Query("SELECT * FROM fees WHERE loanId = :loanId")
    fun getFeesByLoanId(loanId: String): Flow<List<FeeEntity>>

    @Query("SELECT * FROM new_fees WHERE loanId = :loanId")
    fun getNewFeesByLoanId(loanId: String): Flow<List<NewFeeEntity>>

    @Query("SELECT * FROM new_fees ")
    fun getAllNewFees(): Flow<List<NewFeeEntity>>

    @Query("DELETE FROM loans")
    fun deleteAllLoans()

    @Query("DELETE FROM fees")
    fun deleteAllFees()

    @Query("DELETE FROM new_fees")
    fun deleteAllNewFees()

    @Transaction
    @Query("SELECT * FROM loans WHERE id = :loanId")
    fun getLoanById(loanId: String): Flow<LoanWithNewFees>

    @Transaction
    @Query("SELECT * FROM loans")
    fun getLoansWithFeesAndNewFees(): Flow<List<LoanWithFeesAndNewFees>>


}