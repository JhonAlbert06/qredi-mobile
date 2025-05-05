package com.pixelbrew.qredi.ui.customer.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pixelbrew.qredi.data.network.model.CustomerModelResWithDetail
import com.pixelbrew.qredi.data.network.model.FeeModelRes
import com.pixelbrew.qredi.data.network.model.LoanModelRes
import com.pixelbrew.qredi.data.network.model.Payments

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerDetailBottomSheet(
    customer: CustomerModelResWithDetail,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = null
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Título
            Text(
                text = "Detalles del Cliente",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Información personal
            InfoCard(title = "Información Personal") {
                InfoRow("Nombre", "${customer.firstName} ${customer.lastName}")
                InfoRow("Cédula", customer.cedula)
                InfoRow("Teléfono", customer.phone)
                InfoRow("Dirección", customer.address)
                InfoRow("Referencia", customer.reference)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Información de la empresa
            InfoCard(title = "Empresa") {
                InfoRow("Nombre", customer.company.name)
                InfoRow("Teléfono 1", customer.company.phone1)
                InfoRow("Teléfono 2", customer.company.phone2)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Lista de préstamos
            Text(
                text = "Préstamos",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (customer.loans.orEmpty().isEmpty()) {
                Text(
                    text = "No tiene préstamos registrados.",
                    style = MaterialTheme.typography.bodySmall
                )
            } else {
                customer.loans.forEach { loan ->
                    ExpandableLoanItemCard(loan)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botón de cerrar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismiss) {
                    Text("Cerrar")
                }
            }
        }
    }
}

@Composable
fun InfoCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun ExpandableLoanItemCard(loan: LoanModelRes) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Resumen principal
            InfoRow("Monto", "$${"%.2f".format(loan.amount)}")
            InfoRow("Interés", "${loan.interest}%")
            InfoRow("Cuotas", loan.feesQuantity.toString())
            InfoRow("Pagado", if (loan.loanIsPaid) "Sí" else "No")
            InfoRow("Préstamo actual", if (loan.isCurrentLoan) "Sí" else "No")
            InfoRow("Ruta", loan.route.name)
            InfoRow("Fecha", "${loan.date.day}/${loan.date.month}/${loan.date.year}")

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    Text(
                        text = "Cuotas",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    if (loan.fees.isEmpty()) {
                        Text(
                            "No hay cuotas registradas.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    } else {
                        loan.fees.forEach { fee ->
                            FeeItemWithPayments(fee)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FeeItemWithPayments(fee: FeeModelRes) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "Cuota #${fee.number}",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            InfoRow(
                "Fecha esperada",
                "${fee.expectedDate.day}/${fee.expectedDate.month}/${fee.expectedDate.year}"
            )

            if (fee.payments.orEmpty().isEmpty()) {
                Text("Sin pagos registrados.", style = MaterialTheme.typography.bodySmall)
            } else {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Pagos:", style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(4.dp))
                fee.payments.forEach { payment ->
                    PaymentRow(payment)
                    Divider(modifier = Modifier.padding(vertical = 4.dp))
                }
            }
        }
    }
}

@Composable
fun PaymentRow(payment: Payments) {
    Column(modifier = Modifier.padding(start = 8.dp)) {
        InfoRow("Monto pagado", "$${"%.2f".format(payment.paidAmount)}")
        InfoRow(
            "Fecha",
            "${payment.paidDate.day}/${payment.paidDate.month}/${payment.paidDate.year}"
        )
        InfoRow("Usuario", "${payment.user.firstName} ${payment.user.lastName}")
    }
}