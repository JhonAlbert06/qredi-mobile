package com.pixelbrew.qredi.reprint

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ReprintScreen(modifier: Modifier = Modifier) {
    val transactions = listOf(
        "Transacción #1234 - 12/05/2023",
        "Transacción #1235 - 12/05/2023",
        "Transacción #1236 - 13/05/2023",
        "Transacción #1237 - 14/05/2023",
        "Transacción #1238 - 15/05/2023"
    )

    Column(modifier = modifier.fillMaxSize()) {
        Text(
            "Reimprimir Comprobantes",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )
        LazyColumn {
            items(transactions) { transaction ->
                Card(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                        .clickable { /* Reimprimir esta transacción */ }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(transaction)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Monto: \$125.00", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}