package com.pixelbrew.qredi.ui.collect.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.pixelbrew.qredi.data.network.model.RouteModel
import com.pixelbrew.qredi.ui.collect.CollectViewModel

@Composable
fun RouteSelectionDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    routes: List<RouteModel>,
    viewModel: CollectViewModel,
    onRouteSelected: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Selecciona una ruta") },
            text = {
                RoutesList(routes, viewModel, onRouteSelected)
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = onDismiss) { Text("Cancelar") }
            }
        )
    }
}