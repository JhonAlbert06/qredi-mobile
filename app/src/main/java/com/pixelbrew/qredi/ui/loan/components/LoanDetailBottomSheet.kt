package com.pixelbrew.qredi.ui.loan.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.pixelbrew.qredi.data.network.model.FeeModelRes
import com.pixelbrew.qredi.data.network.model.LoanModelRes
import com.pixelbrew.qredi.data.network.model.Payments
import com.pixelbrew.qredi.ui.customer.components.InfoRow


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoanDetailBottomSheet(
    loan: LoanModelRes,
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
        ) {
            Text(
                text = "Detalles del Préstamo",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            LoanSummary(loan)

            Spacer(modifier = Modifier.height(16.dp))

            CustomerInfo(loan)

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Cuotas",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (loan.fees.isEmpty()) {
                Text("No hay cuotas registradas.", style = MaterialTheme.typography.bodySmall)
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(loan.fees) { fee ->
                        ExpandableFeeItem(fee)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Cerrar")
                }
            }
        }
    }
}

@Composable
fun LoanSummary(loan: LoanModelRes) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Monto: \$${loan.amount}", style = MaterialTheme.typography.bodyLarge)
            Text("Interés: ${loan.interest}%", style = MaterialTheme.typography.bodyLarge)
            Text(
                "Cantidad de cuotas: ${loan.feesQuantity}",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                "Pagado: ${if (loan.loanIsPaid) "Sí" else "No"}",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                "Préstamo actual: ${if (loan.isCurrentLoan) "Sí" else "No"}",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun CustomerInfo(loan: LoanModelRes) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = "Cliente",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Nombre: ${loan.customer.firstName} ${loan.customer.lastName}",
                style = MaterialTheme.typography.bodyLarge
            )
            Text("Cédula: ${loan.customer.cedula}", style = MaterialTheme.typography.bodyLarge)
            Text("Teléfono: ${loan.customer.phone}", style = MaterialTheme.typography.bodyLarge)
            Text("Dirección: ${loan.customer.address}", style = MaterialTheme.typography.bodyLarge)
            Text(
                "Referencia: ${loan.customer.reference}",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun ExpandableFeeItem(fee: FeeModelRes) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Info principal
            Text(
                text = "Cuota #${fee.number}",
                style = MaterialTheme.typography.titleMedium
            )
            InfoRow(
                "Fecha esperada",
                "${fee.expectedDate.day}/${fee.expectedDate.month}/${fee.expectedDate.year}"
            )

            AnimatedVisibility(visible = expanded) {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    Divider(
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    if (fee.payments.orEmpty().isEmpty()) {
                        Text("Sin pagos registrados.", style = MaterialTheme.typography.bodySmall)
                    } else {
                        Text("Pagos:", style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(4.dp))

                        fee.payments.forEach { payment ->
                            PaymentItem(payment)
                            Divider(
                                Modifier.padding(vertical = 4.dp),
                                thickness = 1.dp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentItem(payment: Payments) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp)
    ) {
        Text(
            text = "Monto pagado: \$${payment.paidAmount}",
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            text = "Fecha: ${payment.paidDate.day}/${payment.paidDate.month}/${payment.paidDate.year}",
            style = MaterialTheme.typography.bodySmall
        )
    }
}