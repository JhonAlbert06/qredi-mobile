package com.pixelbrew.qredi.invoice

import com.pixelbrew.qredi.data.entities.NewFeeEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object InvoiceGenerator {

    data class DocumentData(
        val items: List<DocumentItem> = emptyList(),
        val total: Double = 0.0,
        val clientName: String? = null,
        val loanDetails: LoanDetails? = null,
        val date: String = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date()),
        val cashierName: String = "Cajero 1",
        val dailySummary: DailySummary? = null
    )

    data class DocumentItem(
        val description: String,
        val quantity: Int = 1,
        val price: Double,
        val tax: Double
    )

    data class LoanDetails(
        val loanNumber: String,
        val dueDate: String,
        val interestRate: Double
    )

    data class DailySummary(
        val totalSales: Double,
        val totalPayments: Double,
        val totalLoans: Double,
        val initialBalance: Double,
        val finalBalance: Double,
        val transactionsCount: Int
    )

    fun generatePaymentContent(fee: NewFeeEntity): String {
        val builder = StringBuilder()
        val fecha = "%02d/%02d/%04d".format(fee.dateDay, fee.dateMonth, fee.dateYear)

        builder.appendLine("=".repeat(32))
        builder.appendLine("       * RECIBO DE PAGO *")
        builder.appendLine("=".repeat(32))
        builder.appendLine(centerText(fee.companyName, 32))
        builder.appendLine(centerText(fee.companyNumber, 32))
        builder.appendLine("-".repeat(32))

        builder.appendLine("NOMBRE: ${fee.clientName.uppercase()}")
        builder.appendLine("CONCEPTO   : Pago de cuota")
        builder.appendLine("CUOTA      : ${fee.number} de ${fee.numberTotal}")
        builder.appendLine("FECHA      : $fecha")
        builder.appendLine("-".repeat(32))

        builder.appendLine(centerText("* MONTO PAGADO *", 32))
        builder.appendLine(centerText("RD$ ${"%,.2f".format(fee.paymentAmount)}", 32, '*'))
        builder.appendLine("-".repeat(32))

        builder.appendLine("=".repeat(32))
        builder.appendLine(centerText("NO NOS HACEMOS RESPONSABLES", 32))
        builder.appendLine(centerText("DEL DINERO ENTREGADO SIN", 32))
        builder.appendLine(centerText("RECIBO", 32))
        builder.appendLine("=".repeat(32))
        builder.appendLine("\n\n")

        return builder.toString()
    }

    fun centerText(text: String, width: Int, padChar: Char = ' '): String {
        val padding = width - text.length
        val padStart = padding / 2
        val padEnd = padding - padStart
        return padChar.toString().repeat(padStart) + text + padChar.toString().repeat(padEnd)
    }

    fun generateDayCloseContent(data: DocumentData): String {
        val builder = StringBuilder()
        val summary = data.dailySummary ?: DailySummary(0.0, 0.0, 0.0, 0.0, 0.0, 0)

        builder.appendLine("--------------------------------")
        builder.appendLine("CIERRE DE CAJA")
        builder.appendLine("--------------------------------")
        builder.appendLine("Fecha: ${data.date}")
        builder.appendLine("Cajero: ${data.cashierName}")
        builder.appendLine("--------------------------------")
        builder.appendLine("RESUMEN DEL DÍA")
        builder.appendLine("--------------------------------")
        builder.appendLine("Ventas totales: ${"%.2f".format(summary.totalSales)}")
        builder.appendLine("Pagos recibidos: ${"%.2f".format(summary.totalPayments)}")
        builder.appendLine("Préstamos otorgados: ${"%.2f".format(summary.totalLoans)}")
        builder.appendLine("Transacciones: ${summary.transactionsCount}")
        builder.appendLine("--------------------------------")
        builder.appendLine("Saldo inicial: ${"%.2f".format(summary.initialBalance)}")
        builder.appendLine("Saldo final: ${"%.2f".format(summary.finalBalance)}")
        builder.appendLine("--------------------------------")
        builder.appendLine("FIRMA: _________________________")
        builder.appendLine("\n\n\n")
        return builder.toString()
    }
}