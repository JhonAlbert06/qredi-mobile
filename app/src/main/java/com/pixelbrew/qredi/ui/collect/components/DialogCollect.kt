package com.pixelbrew.qredi.ui.collect.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.unit.dp
import com.pixelbrew.qredi.ui.collect.CollectViewModel

@androidx.annotation.RequiresPermission(android.Manifest.permission.BLUETOOTH_CONNECT)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DialogCollect(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    viewModel: CollectViewModel,
    amount: String,
) {
    val cuote by viewModel.cuote.observeAsState(0.0)

    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Cobrar") },
            text = {
                AmountField(amount) { viewModel.onAmountChange(it) }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.collectFee()
                        onDismiss()
                        viewModel.printCollect()
                    },
                    enabled = amount.isNotEmpty() && amount.toDouble() in 0.0..cuote,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Cobrar")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancelar")
                }
            }
        )
    }
}