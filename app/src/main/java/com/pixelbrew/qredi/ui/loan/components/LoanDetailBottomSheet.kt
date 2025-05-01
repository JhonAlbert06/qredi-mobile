package com.pixelbrew.qredi.ui.loan.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pixelbrew.qredi.data.network.model.FeeModelRes
import com.pixelbrew.qredi.data.network.model.LoanModelRes
import com.pixelbrew.qredi.data.network.model.Payments

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoanDetailBottomSheet(
    loan: LoanModelRes,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
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
            // Título
            Text(
                text = "Detalles del Préstamo",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Información del préstamo
            LoanSummary(loan)

            Spacer(modifier = Modifier.height(16.dp))

            // Información del cliente
            CustomerInfo(loan)

            Spacer(modifier = Modifier.height(16.dp))

            // Cuotas
            Text(
                text = "Cuotas",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(loan.fees) { fee ->
                    FeeItem(fee)
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
fun LoanSummary(loan: LoanModelRes) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Monto: \$${loan.amount}")
            Text("Interés: ${loan.interest}%")
            Text("Cantidad de cuotas: ${loan.feesQuantity}")
            Text("Pagado: ${if (loan.loanIsPaid) "Sí" else "No"}")
            Text("Préstamo actual: ${if (loan.isCurrentLoan) "Sí" else "No"}")
        }
    }
}

@Composable
fun CustomerInfo(loan: LoanModelRes) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = "Cliente",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("Nombre: ${loan.customer.firstName} ${loan.customer.lastName}")
            Text("Cédula: ${loan.customer.cedula}")
            Text("Teléfono: ${loan.customer.phone}")
            Text("Dirección: ${loan.customer.address}")
            Text("Referencia: ${loan.customer.reference}")
        }
    }
}

@Composable
fun FeeItem(fee: FeeModelRes) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = "Cuota #${fee.number}",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text("Fecha esperada: ${fee.expectedDate.day}/${fee.expectedDate.month}/${fee.expectedDate.year}")

            Spacer(modifier = Modifier.height(8.dp))
            Divider()

            if (!fee.payments.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Pagos:",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                fee.payments.forEach { payment ->
                    PaymentItem(payment)
                    Divider(Modifier.padding(vertical = 8.dp))
                }
            } else {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Sin pagos registrados.", style = MaterialTheme.typography.bodySmall)
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
        Text("Monto pagado: \$${payment.paidAmount}", style = MaterialTheme.typography.bodySmall)
        Text(
            "Fecha: ${payment.paidDate.day}/${payment.paidDate.month}/${payment.paidDate.year}",
            style = MaterialTheme.typography.bodySmall
        )
    }
}