package com.pixelbrew.qredi.ui.spent

import android.Manifest
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pixelbrew.qredi.MainActivity
import com.pixelbrew.qredi.data.network.model.SpentTypeModelRes
import com.pixelbrew.qredi.ui.components.dropdown.GenericDropdown
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
@Composable
fun SpentScreen(modifier: Modifier = Modifier, context: MainActivity) {
    val viewModel: SpentViewModel = hiltViewModel()
    val toastEvent by viewModel.toastMessage.observeAsState()
    val spents by viewModel.spentsList.observeAsState(emptyList())
    val spentTypes by viewModel.spentTypesList.observeAsState(emptyList())

    var showSheet by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.getSpents()
    }

    LaunchedEffect(toastEvent) {
        toastEvent?.getContentIfNotHandled()?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                scope.launch {
                    viewModel.getSpentTypes()
                    showSheet = true
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrar Gasto")
        }

        Spacer(Modifier.height(24.dp))

        if (spents.isNotEmpty()) {
            spents.forEach { spent ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            spent.type.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Text(
                            spent.note,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Text(
                            "${spent.cost}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .align(Alignment.End)
                                .padding(top = 4.dp)
                        )
                    }
                }
            }
        } else {
            Text("No hay gastos registrados.", style = MaterialTheme.typography.bodyMedium)
        }
    }

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = bottomSheetState
        ) {
            RegisterSpentBottomSheet(
                spentTypes = spentTypes,
                onSubmit = { amount, typeId, description ->

                    viewModel.createSpent(amount, typeId, description)
                    showSheet = false
                },
                onDismiss = { showSheet = false }
            )
        }
    }
}

@Composable
fun RegisterSpentBottomSheet(
    spentTypes: List<SpentTypeModelRes>,
    onSubmit: (Double, String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var amountInput by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf<SpentTypeModelRes?>(null) }
    var descriptionInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Nuevo Gasto", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = descriptionInput,
            onValueChange = { descriptionInput = it },
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
            value = amountInput,
            onValueChange = { amountInput = it },
            label = { Text("Monto") },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(Modifier.height(12.dp))

        // Selector de tipo de gasto
        GenericDropdown(
            items = spentTypes,
            selectedItem = selectedType,
            onItemSelected = { selectedType = it },
            modifier = Modifier.fillMaxWidth(),
            label = "Tipo de gasto",
            itemLabel = { it?.name ?: "" }
        )

        Spacer(Modifier.height(24.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
            Spacer(Modifier.width(8.dp))
            Button(
                onClick = {
                    val amount = amountInput.toDoubleOrNull() ?: 0.0
                    selectedType?.id?.let {
                        onSubmit(amount, it, descriptionInput)
                    }
                },
                enabled = amountInput.isNotBlank() && selectedType != null
            ) {
                Text("Guardar")
            }
        }
    }
}