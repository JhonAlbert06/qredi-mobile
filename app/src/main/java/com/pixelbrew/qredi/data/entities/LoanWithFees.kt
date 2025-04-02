package com.pixelbrew.qredi.data.entities

import androidx.room.Embedded
import androidx.room.Relation

data class LoanWithFees(
    @Embedded val loan: LoanEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "loanId"
    )
    val fees: List<FeeEntity>
)

data class LoanWithNewFees(
    @Embedded val loan: LoanEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "loanId"
    )
    val newFees: List<NewFeeEntity>
)