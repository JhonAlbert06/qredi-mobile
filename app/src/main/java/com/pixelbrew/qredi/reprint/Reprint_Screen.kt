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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pixelbrew.qredi.MainActivity

@Composable
fun ReprintScreen(
    viewModel: ReprintViewModel,
    modifier: Modifier = Modifier,
    context: MainActivity
) {
    val newFees by viewModel.newFees.observeAsState(emptyList())

    Column(modifier = modifier.fillMaxSize()) {
        Button(
            onClick = {
                viewModel.uploadFees()
            },
            modifier = Modifier.padding(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(
                text = "Subir Data",
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
        Text(
            "Reimprimir Comprobantes",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )

        LazyColumn {
            items(newFees) { fee ->
                Card(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                        .clickable { /* Reimprimir esta transacción */ }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "${fee.dateDay}/${fee.dateMonth}/${fee.dateYear}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Monto: \$ ${fee.paymentAmount}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}