package com.pixelbrew.qredi.ui.collect.components

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import com.pixelbrew.qredi.ui.collect.CollectViewModel

@RequiresApi(Build.VERSION_CODES.O)
@RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
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
            title = {
                Text(text = "Cobrar")
            },
            text = {
                AmountField(amount) {
                    viewModel.onAmountChange(it)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.collectFee()
                        onDismiss()
                        viewModel.printCollect()
                    },
                    enabled = !amount.isEmpty() && amount.toDouble() > 0 && amount.toDouble() <= cuote,
                ) {
                    Text("Cobrar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onDismiss
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}