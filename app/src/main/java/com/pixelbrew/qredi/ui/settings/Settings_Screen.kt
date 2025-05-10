package com.pixelbrew.qredi.ui.settings

import android.Manifest
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.pixelbrew.qredi.MainActivity
import com.pixelbrew.qredi.R
import com.pixelbrew.qredi.data.network.model.RouteModelRes
import com.pixelbrew.qredi.data.network.model.SpentTypeModelRes
import com.pixelbrew.qredi.ui.components.dropdown.GenericDropdown
import com.pixelbrew.qredi.ui.customer.components.OutlinedField
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier,
    context: MainActivity,
) {
    val printerName by viewModel.printerName.observeAsState("")
    val apiUrl by viewModel.apiUrl.observeAsState("")
    val pairedDevices by viewModel.pairedDevices.observeAsState(emptyList())
    val selectedDevice by viewModel.selectedDevice.observeAsState()
    val toastEvent by viewModel.toastMessage.observeAsState()

    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()
    var showSheet by remember { mutableStateOf(false) }

    var showSpentTypeSheet by remember { mutableStateOf(false) }

    val spentTypesList by viewModel.spentTypesList.observeAsState(emptyList())

    val routesList by viewModel.routesList.observeAsState(emptyList())

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
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Impresora",
            style = MaterialTheme.typography.titleLarge
        )

        PrinterField(
            name = printerName ?: "",
            onValueChange = { viewModel.onPrinterNameChange(it) },
            onRefresh = { viewModel.refreshPairedDevices() }
        )

        GenericDropdown(
            items = pairedDevices,
            selectedItem = selectedDevice,
            onItemSelected = { device ->
                viewModel.onDeviceSelected(device)
            },
            modifier = Modifier.fillMaxWidth(),
            label = "Seleccionar impresora",
            itemLabel = { it?.name ?: "" }
        )

        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Seguridad",
            style = MaterialTheme.typography.titleLarge
        )

        ApiUrlField(
            url = apiUrl ?: "",
            onValueChange = { viewModel.onApiUrlChange(it) }
        )

        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                coroutineScope.launch {
                    viewModel.getRoutes()
                    showSheet = true
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            )
        ) {
            Text(text = "Crear Nueva Ruta")
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                coroutineScope.launch {
                    viewModel.getSpentTypes()
                    showSpentTypeSheet = true
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            )
        ) {
            Text(text = "Crear Tipo de Gasto")
        }

        Spacer(modifier = Modifier.height(24.dp))
        SaveButton(
            modifier = Modifier.padding(top = 16.dp),
            onLoginSelected = { viewModel.saveSettings() }

        )
    }


    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = bottomSheetState
        ) {
            CreateRouteBottomSheet(
                onDismiss = { showSheet = false },
                onSubmit = { routeName ->
                    viewModel.createRoute(routeName)
                    showSheet = false
                },
                routeList = routesList
            )
        }
    }

    if (showSpentTypeSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSpentTypeSheet = false },
            sheetState = bottomSheetState
        ) {
            SpentTypeBottomSheet(
                onDismiss = { showSpentTypeSheet = false },
                onSubmit = { spentTypeName ->
                    viewModel.createSpentType(spentTypeName)
                    showSpentTypeSheet = false
                },
                spentTypes = spentTypesList
            )
        }
    }
}

@Composable
fun SpentTypeBottomSheet(
    onDismiss: () -> Unit,
    onSubmit: (String) -> Unit,
    spentTypes: List<SpentTypeModelRes>
) {
    var spentTypeName by remember { mutableStateOf("") }

    Column(
        Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("Crear Tipo de Gasto", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))

        OutlinedField(
            "Nombre del tipo de gasto",
            spentTypeName,
            { spentTypeName = it },
            KeyboardType.Text
        )

        Spacer(Modifier.height(24.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
            Spacer(Modifier.width(16.dp))
            Button(
                onClick = { onSubmit(spentTypeName) },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.height(50.dp)
            ) {
                Text("Guardar")
            }
        }

        Spacer(Modifier.height(16.dp))

        if (spentTypes.isNotEmpty()) {
            Text("Tipos de Gasto Existentes", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))

            spentTypes.forEach { spentType ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            spentType.name,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        } else {
            Text("No hay tipos de gasto existentes", style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
fun CreateRouteBottomSheet(
    onDismiss: () -> Unit,
    onSubmit: (String) -> Unit,
    routeList: List<RouteModelRes>
) {
    var routeName by remember { mutableStateOf("") }

    Column(
        Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("Crear Nueva Ruta", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))

        OutlinedField("Nombre de la ruta", routeName, { routeName = it }, KeyboardType.Text)

        Spacer(Modifier.height(24.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
            Spacer(Modifier.width(16.dp))
            Button(
                onClick = { onSubmit(routeName) },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.height(50.dp)
            ) {
                Text("Guardar")
            }
        }

        Spacer(Modifier.height(16.dp))

        if (routeList.isNotEmpty()) {
            Text("Rutas Existentes", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))

            routeList.forEach { route ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            route.name,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        } else {
            Text("No hay rutas existentes", style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
fun PrinterField(name: String, onValueChange: (String) -> Unit, onRefresh: () -> Unit) {
    TextField(
        value = name,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("Impresora", style = MaterialTheme.typography.bodyLarge) },
        trailingIcon = {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.arrows_rotate_solid),
                contentDescription = "Actualizar dispositivos",
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onRefresh() }
                    .padding(4.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next
        ),
        singleLine = true,
        maxLines = 1,
        readOnly = true,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
fun ApiUrlField(url: String, onValueChange: (String) -> Unit) {
    TextField(
        value = url,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("URL de la API", style = MaterialTheme.typography.bodyLarge) },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Uri,
            imeAction = ImeAction.Done
        ),
        singleLine = true,
        maxLines = 1,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
fun SaveButton(modifier: Modifier, onLoginSelected: () -> Unit) {
    Button(
        onClick = onLoginSelected,
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
            disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f)
        )
    ) {
        Text(
            text = "Guardar Configuración",
            style = MaterialTheme.typography.titleMedium
        )
    }
}