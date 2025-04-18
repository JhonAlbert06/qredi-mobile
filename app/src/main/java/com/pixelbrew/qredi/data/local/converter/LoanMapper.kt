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
            loanDateDay = model.dateModel.day,
            loanDateMonth = model.dateModel.month,
            loanDateYear = model.dateModel.year,
            loanDateHour = model.dateModel.hour,
            loanDateMinute = model.dateModel.minute,
            loanDateSecond = model.dateModel.second,
            loanDateTimezone = model.dateModel.timezone,
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
            dateModel = DateModel(
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
            dateDay = model.dateModel.day,
            dateMonth = model.dateModel.month,
            dateYear = model.dateModel.year,
            dateHour = model.dateModel.hour,
            dateMinute = model.dateModel.minute,
            dateSecond = model.dateModel.second,
            dateTimezone = model.dateModel.timezone
        )
    }

    fun feeEntityToModel(entity: FeeEntity): FeeDownloadModel {
        return FeeDownloadModel(
            id = entity.id,
            paymentAmount = entity.paymentAmount,
            number = entity.number,
            dateModel = DateModel(
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