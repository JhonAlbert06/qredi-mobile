package com.pixelbrew.qredi.customer.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pixelbrew.qredi.customer.Field

@Composable
fun FilterCustomerDialog(
    onDismiss: () -> Unit,
    onApplyFilters: (Field, String) -> Unit,
    fields: List<Field>,
    selectedField: Field,
    query: String
) {
    var fieldSelected by remember { mutableStateOf(selectedField) }
    var queryAux by remember { mutableStateOf(query) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Filtrar Clientes") },
        text = {
            Column {
                FieldDropdown(
                    items = fields,
                    selectedField = fieldSelected,
                    onFieldSelected = { fieldSelected = it },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    value = queryAux,
                    onValueChange = { queryAux = it },
                    placeholder = { Text("Escribe tu búsqueda") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onApplyFilters(fieldSelected, queryAux)
                    onDismiss()
                }
            ) {
                Text("Aplicar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}