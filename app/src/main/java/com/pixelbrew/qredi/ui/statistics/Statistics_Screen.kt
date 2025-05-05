package com.pixelbrew.qredi.ui.statistics

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pixelbrew.qredi.data.network.model.DashboardResponse
import com.pixelbrew.qredi.data.network.model.TopCustomer
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StatisticsScreen(
    modifier: Modifier = Modifier
) {
    val stats = DashboardResponse(
        amountCollected = "1000.00",
        percentageCollected = "50%",
        newLoansCount = 5,
        newLoansAmount = "500.00",
        missingPaymentsAmount = 2,
        missingPaymentsMoney = "200.00",
        firstPaymentTime = LocalDateTime.now().minusDays(1),
        lastPaymentTime = LocalDateTime.now(),
        topCustomers = listOf(
            TopCustomer("Cliente 1", 100.0),
            TopCustomer("Cliente 2", 200.0)
        )
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            StatisticCard(
                title = "Primer cobro",
                content = stats.firstPaymentTime?.let { formatTime(it) } ?: "Sin cobros",
                modifier = Modifier.weight(1f)
            )

            StatisticCard(
                title = "Último cobro",
                content = stats.lastPaymentTime?.let { formatTime(it) } ?: "Sin cobros",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .background(
                    MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "Cobrado: ${stats.amountCollected}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    "Faltante: ${stats.missingPaymentsMoney}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = (stats.percentageCollected.removeSuffix("%").toFloatOrNull()
                        ?: 0f) / 100f,
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(8.dp),
                    color = Color(0xFF00BCD4),
                    trackColor = Color.LightGray
                )
                Text(
                    "${stats.percentageCollected} cobrado",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Prestamos", style = MaterialTheme.typography.titleMedium)
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            StatisticCard(
                title = "Nuevos",
                content = "${stats.newLoansCount}",
                subconten = stats.newLoansAmount,
                modifier = Modifier.weight(1f)
            )

            StatisticCard(
                title = "Faltantes",
                content = "${stats.missingPaymentsAmount}",
                subconten = stats.missingPaymentsMoney,
                modifier = Modifier.weight(1f)
            )

        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Top 5 Clientes del Día", style = MaterialTheme.typography.titleMedium)
        stats.topCustomers.forEach { customer ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(customer.customerName, style = MaterialTheme.typography.bodyLarge)
                    Text(
                        "$${"%.2f".format(customer.amountPaid)}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@Composable
fun StatisticCard(title: String, content: String, subconten: String = "", modifier: Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,

                )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                content,
                style = MaterialTheme.typography.titleLarge,
            )
            if (subconten.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    subconten,
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun formatTime(time: LocalDateTime): String {
    val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
    return time.format(formatter)
}