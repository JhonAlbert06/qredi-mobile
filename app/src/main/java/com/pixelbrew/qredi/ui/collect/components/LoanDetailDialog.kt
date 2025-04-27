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
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pixelbrew.qredi.data.network.model.LoanDownloadModel
import com.pixelbrew.qredi.ui.collect.CollectViewModel

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
@Composable
fun LoanDetailBottomSheet(
    showBottomSheet: Boolean,
    onDismiss: () -> Unit,
    loan: LoanDownloadModel,
    viewModel: CollectViewModel,
    modifier: Modifier = Modifier
) {
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            dragHandle = { BottomSheetDefaults.DragHandle() } // Agregar controlador de arrastre
        ) {
            Column(
                modifier = modifier
                    .padding(16.dp)
            ) {
                // Título
                Text(
                    text = "Préstamo",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Mostrar detalles del préstamo
                LoanItemCollect(
                    loan = loan,
                    viewModel = viewModel
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Mostrar cuotas
                FeeItems(
                    viewModel = viewModel,
                    loan = loan
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Botones de acción
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    // Botón de cancelar
                    TextButton(onClick = onDismiss) {
                        Text("Cancelar")
                    }
                }
            }
        }
    }
}