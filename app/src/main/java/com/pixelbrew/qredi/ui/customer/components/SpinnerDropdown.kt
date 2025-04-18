package com.pixelbrew.qredi.ui.customer.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.pixelbrew.qredi.ui.customer.Field

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FieldDropdown(
    items: List<Field>,
    selectedField: Field,
    onFieldSelected: (Field) -> Unit,
    modifier: Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedField.name,
            onValueChange = {},
            readOnly = true,
            label = { Text("Campo") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()

        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach { field ->
                DropdownMenuItem(
                    text = {
                        Text(
                            field.name,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    },
                    onClick = {
                        onFieldSelected(field)
                        expanded = false
                    }
                )
            }
        }
    }
}