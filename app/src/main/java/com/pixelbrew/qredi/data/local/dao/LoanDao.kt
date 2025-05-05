package com.pixelbrew.qredi.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.pixelbrew.qredi.data.dtos.TopCustomerDto
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


    // Estadisticas


    // Monto total cobrado hoy
    @Query(
        """
    SELECT COALESCE(SUM(paymentAmount), 0) 
    FROM new_fees 
    WHERE dateDay = :day AND dateMonth = :month AND dateYear = :year
"""
    )
    suspend fun getAmountCollectedToday(day: Int, month: Int, year: Int): Double

    //Total de cuotas programadas hoy
    @Query(
        """
    SELECT COUNT(*) 
    FROM fees 
    WHERE dateDay = :day AND dateMonth = :month AND dateYear = :year
"""
    )
    suspend fun getTotalFeesToday(day: Int, month: Int, year: Int): Int

    //Cuotas pagadas hoy
    @Query(
        """
    SELECT COUNT(*) 
    FROM new_fees 
    WHERE dateDay = :day AND dateMonth = :month AND dateYear = :year
"""
    )
    suspend fun getPaidFeesToday(day: Int, month: Int, year: Int): Int

    //
    @Query(
        """
    SELECT COALESCE(SUM(amount), 0) 
    FROM loans 
    WHERE loanDateDay = :day AND loanDateMonth = :month AND loanDateYear = :year
"""
    )
    suspend fun getNewLoansAmountToday(day: Int, month: Int, year: Int): Double


    @Query(
        """
    SELECT COUNT(*) 
    FROM fees 
    WHERE dateDay = :day AND dateMonth = :month AND dateYear = :year
      AND id NOT IN (SELECT feeId FROM new_fees)
"""
    )
    suspend fun getMissingFeesCount(day: Int, month: Int, year: Int): Int

    @Query(
        """
    SELECT COALESCE(SUM(paymentAmount), 0) 
    FROM fees 
    WHERE dateDay = :day AND dateMonth = :month AND dateYear = :year
      AND id NOT IN (SELECT feeId FROM new_fees)
"""
    )
    suspend fun getMissingFeesAmount(day: Int, month: Int, year: Int): Double


    @Query(
        """
    SELECT clientName, SUM(paymentAmount) AS amountPaid 
    FROM new_fees 
    WHERE dateDay = :day AND dateMonth = :month AND dateYear = :year
    GROUP BY clientName 
    ORDER BY amountPaid DESC 
    LIMIT 5
"""
    )
    suspend fun getTopCustomers(day: Int, month: Int, year: Int): List<TopCustomerDto>


    @Query(
        """
    SELECT * 
    FROM loans 
    ORDER BY amount DESC 
    LIMIT 1
"""
    )
    suspend fun getLargestLoan(): LoanEntity?

    @Query(
        """
    SELECT MIN(printf('%04d-%02d-%02d %02d:%02d:%02d', dateYear, dateMonth, dateDay, dateHour, dateMinute, dateSecond))
    FROM new_fees
    WHERE dateDay = :day AND dateMonth = :month AND dateYear = :year
    """
    )
    suspend fun getFirstPaymentTimestamp(day: Int, month: Int, year: Int): String?

    @Query(
        """
    SELECT MAX(printf('%04d-%02d-%02d %02d:%02d:%02d', dateYear, dateMonth, dateDay, dateHour, dateMinute, dateSecond))
    FROM new_fees
    WHERE dateDay = :day AND dateMonth = :month AND dateYear = :year
    """
    )
    suspend fun getLastPaymentTimestamp(day: Int, month: Int, year: Int): String?
}