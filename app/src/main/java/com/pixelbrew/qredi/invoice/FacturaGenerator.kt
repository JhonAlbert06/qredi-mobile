package com.pixelbrew.qredi.invoice

import com.pixelbrew.qredi.data.entities.NewFeeEntity

object InvoiceGenerator {

    data class DayCloseData(
        val date: String,
        val cashierName: String,
        val initialBalance: Double,
        val totalLoans: Double,
        val payments: List<NewFeeEntity>
    ) {
        val totalPayments: Double get() = payments.sumOf { it.paymentAmount }
        val finalBalance: Double get() = initialBalance + totalPayments
        val transactionsCount: Int get() = payments.size
        val uniqueClients: Int get() = payments.map { it.clientName }.distinct().count()
    }

    fun generatePaymentContent(fee: NewFeeEntity): String {
        val builder = StringBuilder()
        val fecha = "%02d/%02d/%04d".format(fee.dateDay, fee.dateMonth, fee.dateYear)
        //val hashCode = (fee.feeId + fee.paymentAmount.toString()).hashCode()

        builder.appendLine("=".repeat(32))
        builder.appendLine("       * RECIBO DE PAGO *")
        builder.appendLine("=".repeat(32))
        builder.appendLine(centerText(fee.companyName.take(32), 32))
        builder.appendLine(centerText(fee.companyNumber.take(32), 32))
        builder.appendLine("-".repeat(32))

        builder.appendLine("NOMBRE     : ${fee.clientName.take(26).uppercase()}")
        builder.appendLine("CONCEPTO   : Pago de cuota")
        builder.appendLine("CUOTA      : ${fee.number} de ${fee.numberTotal}")
        builder.appendLine("FECHA      : $fecha")
        builder.appendLine("-".repeat(32))

        builder.appendLine(centerText("* MONTO PAGADO *", 32))
        builder.appendLine(centerText("RD$ ${"%,.2f".format(fee.paymentAmount)}", 32, '*'))
        builder.appendLine("-".repeat(32))

        //builder.appendLine("Codigo: $hashCode")
        builder.appendLine("=".repeat(32))
        builder.appendLine(centerText("NO NOS HACEMOS RESPONSABLES", 32))
        builder.appendLine(centerText("DEL DINERO ENTREGADO SIN", 32))
        builder.appendLine(centerText("RECIBO", 32))
        builder.appendLine("=".repeat(32))
        builder.appendLine("\n\n")

        return builder.toString()
    }

    fun generateDayCloseContent(data: DayCloseData): String {
        val builder = StringBuilder()
        builder.appendLine("=".repeat(32))
        builder.appendLine(centerText("* CIERRE DE CAJA *", 32))
        builder.appendLine("=".repeat(32))
        builder.appendLine("Fecha   : ${data.date}")
        builder.appendLine("Usuario : ${data.cashierName}")
        builder.appendLine("-".repeat(32))
        builder.appendLine(centerText("RESUMEN DEL DIA", 32))
        builder.appendLine("-".repeat(32))
        builder.appendLine("Clientes atendidos : ${data.uniqueClients}")
        builder.appendLine("Pagos recibidos    : ${data.transactionsCount}")
        builder.appendLine("Monto total        : ${"%,.2f".format(data.totalPayments)}")
        builder.appendLine("Prestamos otorgados: ${"%,.2f".format(data.totalLoans)}")
        builder.appendLine("Saldo inicial      : ${"%,.2f".format(data.initialBalance)}")
        builder.appendLine("Saldo final        : ${"%,.2f".format(data.finalBalance)}")
        builder.appendLine("-".repeat(32))

        if (data.payments.isNotEmpty()) {
            builder.appendLine(centerText("DETALLE DE PAGOS", 32))
            builder.appendLine("-".repeat(32))
            data.payments.forEachIndexed { index, fee ->
                val hora = "%02d:%02d".format(fee.dateHour, fee.dateMinute)
                builder.appendLine("(${index + 1}) ${fee.clientName.take(20)}")
                builder.appendLine("   Cuota: ${fee.number}/${fee.numberTotal}")
                builder.appendLine("   Monto: RD$ ${"%,.2f".format(fee.paymentAmount)}")
                builder.appendLine("   Hora : $hora")
                builder.appendLine("-".repeat(32))
            }
        }

        builder.appendLine(centerText("FIRMA", 32))
        builder.appendLine("_".repeat(32))
        builder.appendLine("\n\n")
        return builder.toString()
    }

    fun centerText(text: String, width: Int, padChar: Char = ' '): String {
        val safeText = text.take(width)
        val padding = width - safeText.length
        val padStart = padding / 2
        val padEnd = padding - padStart
        return padChar.toString().repeat(padStart) + safeText + padChar.toString().repeat(padEnd)
    }
}