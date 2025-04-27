package com.pixelbrew.qredi.ui.collect.components

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.pixelbrew.qredi.data.network.model.LoanDownloadModel
import com.pixelbrew.qredi.ui.collect.CollectViewModel

@RequiresApi(Build.VERSION_CODES.O)
@RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
@Composable
fun LoanDetailDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    loan: LoanDownloadModel,
    viewModel: CollectViewModel,
    modifier: Modifier = Modifier
) {
    if (showDialog) {
        AlertDialog(
            properties = DialogProperties(usePlatformDefaultWidth = false),
            modifier = modifier.padding(16.dp),
            onDismissRequest = onDismiss,
            title = {
                Text(text = "Préstamo")
            },
            text = {
                Column {
                    LoanItemCollect(
                        loan = loan,
                        viewModel = viewModel
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    FeeItems(
                        viewModel = viewModel,
                        loan = loan
                    )
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancelar")
                }
            }
        )
    }
}