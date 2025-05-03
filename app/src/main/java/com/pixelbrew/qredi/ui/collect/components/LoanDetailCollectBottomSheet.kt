package com.pixelbrew.qredi.ui.collect.components

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pixelbrew.qredi.data.network.model.LoanDownloadModel
import com.pixelbrew.qredi.ui.collect.CollectViewModel

@RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoanDetailCollectBottomSheet(
    showBottomSheet: Boolean,
    onDismiss: () -> Unit,
    loan: LoanDownloadModel,
    viewModel: CollectViewModel
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState
        ) {
            Column(Modifier.padding(16.dp)) {
                Text("Préstamo", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(8.dp))
                LoanItemCollect(loan, viewModel)
                Spacer(Modifier.height(8.dp))
                FeeItems(viewModel, loan)
                Spacer(Modifier.height(16.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancelar") }
                }
            }
        }
    }
}