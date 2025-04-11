package com.pixelbrew.qredi.collect.components

import android.Manifest
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.pixelbrew.qredi.MainActivity
import com.pixelbrew.qredi.collect.CollectViewModel
import com.pixelbrew.qredi.network.model.DownloadModel

@RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
@Composable
fun LoanDetailDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    loan: DownloadModel,
    viewModel: CollectViewModel,
    context: MainActivity,
    modifier: Modifier = Modifier
) {
    if (showDialog) {
        AlertDialog(
            properties = DialogProperties(usePlatformDefaultWidth = false),
            modifier = modifier,
            onDismissRequest = onDismiss,
            title = {
                Text(text = "Préstamo")
            },
            text = {
                Column {
                    LoanItem(
                        loan = loan,
                        viewModel = viewModel
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    FeeItems(
                        viewModel = viewModel,
                        loan = loan,
                        context = context
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