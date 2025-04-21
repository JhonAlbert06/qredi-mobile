package com.pixelbrew.qredi.data.local.converter

import com.pixelbrew.qredi.data.local.entities.FeeEntity
import com.pixelbrew.qredi.data.local.entities.LoanEntity
import com.pixelbrew.qredi.data.network.model.CustomerDownLoadModel
import com.pixelbrew.qredi.data.network.model.DateModel
import com.pixelbrew.qredi.data.network.model.FeeDownloadModel
import com.pixelbrew.qredi.data.network.model.LoanDownloadModel


object LoanMapper {

    fun loanModelToEntity(model: LoanDownloadModel): LoanEntity {
        return LoanEntity(
            id = model.id,
            amount = model.amount,
            interest = model.interest,
            feesQuantity = model.feesQuantity,
            loanDateDay = model.date.day,
            loanDateMonth = model.date.month,
            loanDateYear = model.date.year,
            loanDateHour = model.date.hour,
            loanDateMinute = model.date.minute,
            loanDateSecond = model.date.second,
            loanDateTimezone = model.date.timezone,
            customerId = model.customer.id,
            customerName = model.customer.name,
            customerCedula = model.customer.cedula
        )
    }

    fun loanEntityToModel(entity: LoanEntity, fees: List<FeeEntity>): LoanDownloadModel {
        return LoanDownloadModel(
            id = entity.id,
            amount = entity.amount,
            interest = entity.interest,
            feesQuantity = entity.feesQuantity,
            date = DateModel(
                day = entity.loanDateDay,
                month = entity.loanDateMonth,
                year = entity.loanDateYear,
                hour = entity.loanDateHour,
                minute = entity.loanDateMinute,
                second = entity.loanDateSecond,
                timezone = entity.loanDateTimezone
            ),
            customer = CustomerDownLoadModel(
                id = entity.customerId,
                name = entity.customerName,
                cedula = entity.customerCedula
            ),
            fees = fees.map { feeEntityToModel(it) }
        )
    }


    fun feeModelToEntity(model: FeeDownloadModel, loanId: String): FeeEntity {
        return FeeEntity(
            id = model.id,
            loanId = loanId,
            paymentAmount = model.paymentAmount,
            number = model.number,
            dateDay = model.date.day,
            dateMonth = model.date.month,
            dateYear = model.date.year,
            dateHour = model.date.hour,
            dateMinute = model.date.minute,
            dateSecond = model.date.second,
            dateTimezone = model.date.timezone
        )
    }

    fun feeEntityToModel(entity: FeeEntity): FeeDownloadModel {
        return FeeDownloadModel(
            id = entity.id,
            paymentAmount = entity.paymentAmount,
            number = entity.number,
            date = DateModel(
                day = entity.dateDay,
                month = entity.dateMonth,
                year = entity.dateYear,
                hour = entity.dateHour,
                minute = entity.dateMinute,
                second = entity.dateSecond,
                timezone = entity.dateTimezone
            )
        )
    }
}