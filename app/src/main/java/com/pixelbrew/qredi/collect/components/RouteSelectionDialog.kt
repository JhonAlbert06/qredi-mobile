package com.pixelbrew.qredi.collect.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.pixelbrew.qredi.collect.CollectViewModel
import com.pixelbrew.qredi.network.model.RouteModel

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
            title = {
                Text(text = "Selecciona una ruta")
            },
            text = {
                RoutesList(
                    routes = routes,
                    viewModel = viewModel,
                    onRouteSelected = onRouteSelected
                )
            },
            confirmButton = {},
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