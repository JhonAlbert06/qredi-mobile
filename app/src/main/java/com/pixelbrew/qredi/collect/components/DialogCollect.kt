package com.pixelbrew.qredi.collect.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import com.pixelbrew.qredi.MainActivity
import com.pixelbrew.qredi.collect.CollectViewModel

@RequiresApi(Build.VERSION_CODES.O)
@androidx.annotation.RequiresPermission(android.Manifest.permission.BLUETOOTH_CONNECT)
@Composable
fun DialogCollect(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    viewModel: CollectViewModel,
    context: MainActivity,
) {

    val amount: String by viewModel.amount.observeAsState(initial = "")

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
                        viewModel.printCollect(context)
                    },
                    enabled = !amount.isEmpty()
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