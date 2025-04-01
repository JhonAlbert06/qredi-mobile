package com.pixelbrew.qredi.invoice

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

    fun generateLoanContent(data: DocumentData): String {
        val builder = StringBuilder()

        builder.appendLine("--------------------------------")
        builder.appendLine("COMPROBANTE DE PRÉSTAMO")
        builder.appendLine("--------------------------------")
        builder.appendLine("Fecha: ${data.date}")
        builder.appendLine("Cajero: ${data.cashierName}")
        data.clientName?.let { builder.appendLine("Cliente: $it") }
        builder.appendLine("No. Préstamo: ${data.loanDetails?.loanNumber ?: "N/A"}")
        builder.appendLine("Vence: ${data.loanDetails?.dueDate ?: "N/A"}")
        builder.appendLine("Tasa: ${data.loanDetails?.interestRate ?: 0.0}%")
        builder.appendLine("--------------------------------")
        builder.appendLine("Descripción       Cant  Monto")
        builder.appendLine("--------------------------------")

        data.items.forEach { item ->
            builder.appendLine(
                "${item.description.take(16).padEnd(16)} ${
                    item.quantity.toString().padStart(4)
                }  ${"%.2f".format(item.price)}"
            )
        }

        builder.appendLine("--------------------------------")
        builder.appendLine("TOTAL PRÉSTAMO: ${"%.2f".format(data.total)}")
        builder.appendLine("\n\n\n")
        return builder.toString()
    }

    fun generatePaymentContent(data: DocumentData): String {
        val builder = StringBuilder()

        builder.appendLine("--------------------------------")
        builder.appendLine("COMPROBANTE DE PAGO")
        builder.appendLine("--------------------------------")
        builder.appendLine("Fecha: ${data.date}")
        builder.appendLine("Cajero: ${data.cashierName}")
        data.clientName?.let { builder.appendLine("Cliente: $it") }
        builder.appendLine("--------------------------------")
        builder.appendLine("Descripcion       Cant  Monto")
        builder.appendLine("--------------------------------")

        data.items.forEach { item ->
            builder.appendLine(
                "${item.description.take(16).padEnd(16)} ${
                    item.quantity.toString().padStart(4)
                }  ${"%.2f".format(item.price)}"
            )
        }

        builder.appendLine("--------------------------------")
        builder.appendLine("TOTAL PAGADO: ${"%.2f".format(data.total)}")
        builder.appendLine("\n\n\n")
        return builder.toString()
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