package com.pixelbrew.qredi.ui.statistics

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.text.NumberFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StatisticsScreen(
    modifier: Modifier = Modifier
) {
    val viewModel: StatisticsViewModel = hiltViewModel()
    val state by viewModel.statisticsState.collectAsState()

    var source by remember { mutableStateOf("Local") }
    var apiLoaded by remember { mutableStateOf(false) }

    // REFRESCA cada vez que abres la pantalla
    LaunchedEffect(Unit) {
        viewModel.loadLocalStatistics()
    }

    // Si selecciona API por primera vez, cargar
    LaunchedEffect(source) {
        if (source == "API" && !apiLoaded) {
            // viewModel.loadApiStatistics()
            apiLoaded = true
        }
    }

    val stats = when (source) {
        "API" -> state.apiStats ?: DashboardResponse()
        else -> state.localStats ?: DashboardResponse()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Selector de fuente
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            SourceSelector(selectedSource = source, onSourceChange = { source = it })
        }

        // Primer y último cobro
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

        // Indicador de progreso
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
                    "Cobrado: ${formatCurrency(stats.amountCollected)}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    "Faltante: ${formatCurrency(stats.missingPaymentsMoney)}",
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

        Text("Préstamos", style = MaterialTheme.typography.titleMedium)
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            StatisticCard(
                title = "Nuevos",
                content = "${stats.newLoansCount}",
                subconten = formatCurrency(stats.newLoansAmount),
                modifier = Modifier.weight(1f)
            )
            StatisticCard(
                title = "Faltantes",
                content = "${stats.missingPaymentsAmount}",
                subconten = formatCurrency(stats.missingPaymentsMoney),
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
                elevation = CardDefaults.cardElevation(2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(customer.customerName, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        formatCurrency(customer.amountPaid),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@Composable
fun SourceSelector(selectedSource: String, onSourceChange: (String) -> Unit) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(50))
            .padding(4.dp)
    ) {
        listOf("Local", "API").forEach { option ->
            val selected = option == selectedSource
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(
                        if (selected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceVariant
                    )
                    .clickable { onSourceChange(option) }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    option,
                    color = if (selected) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelLarge
                )
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
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(content, style = MaterialTheme.typography.titleLarge)
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

fun formatCurrency(amount: Double): String {
    val formatter = NumberFormat.getNumberInstance(Locale.US)
    formatter.minimumFractionDigits = 2
    formatter.maximumFractionDigits = 2
    return "$${formatter.format(amount)}"
}

fun formatCurrency(amount: String): String {
    val cleanedAmount = amount.replace("[^\\d.]".toRegex(), "")

    val value = cleanedAmount.toDoubleOrNull() ?: 0.0

    val formatter = NumberFormat.getNumberInstance(Locale.US)
    formatter.minimumFractionDigits = 2
    formatter.maximumFractionDigits = 2

    return "$${formatter.format(value)}"
}