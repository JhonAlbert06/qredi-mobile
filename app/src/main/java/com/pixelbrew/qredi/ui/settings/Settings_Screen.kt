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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.pixelbrew.qredi.MainActivity
import com.pixelbrew.qredi.R
import com.pixelbrew.qredi.ui.components.dropdown.GenericDropdown

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

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "",
                style = MaterialTheme.typography.headlineMedium
            )
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.arrows_rotate_solid),
                contentDescription = "Actualizar",
                modifier = Modifier
                    .size(40.dp)
                    .clickable { viewModel.refreshPairedDevices() }
                    .padding(8.dp),
                tint = Color(0xFF00BCD4)
            )

        }


        Spacer(modifier = Modifier.height(24.dp))

        Spacer(modifier = Modifier.height(24.dp))
        Text("Impresora", style = MaterialTheme.typography.titleLarge)
        PrinterField(
            printerName,
            onValueChange = { viewModel.onPrinterNameChange(it) }
        )

        GenericDropdown(
            items = pairedDevices,
            selectedItem = selectedDevice,
            onItemSelected = { device ->
                viewModel.onDeviceSelected(device)
            },
            modifier = Modifier.fillMaxWidth(),
            label = "Seleccionar impresora",
            itemLabel = {
                it?.name ?: ""
            },
        )


        Spacer(modifier = Modifier.height(24.dp))
        Text("Seguridad", style = MaterialTheme.typography.titleLarge)
        ApiUrlField(
            apiUrl,
            onValueChange = { viewModel.onApiUrlChange(it) }
        )

        Spacer(modifier = Modifier.height(24.dp))
        SaveButton(
            modifier = Modifier.padding(top = 16.dp),
            onLoginSelected = {
                viewModel.saveSettings()
            }
        )
    }

    val viewModel: SettingsViewModel = viewModel
    val toastMessage by viewModel.toastMessage.observeAsState()

    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
fun SwitchTheme(x0: Boolean, onCheckedChange: () -> Unit) {
    Switch(
        checked = x0,
        onCheckedChange = { onCheckedChange() },
        colors = androidx.compose.material3.SwitchDefaults.colors(
            checkedThumbColor = Color(0xFF00BCD4),
            uncheckedThumbColor = Color(0xFF00BCD4),
            checkedTrackColor = Color(0xFF00BCD4),
            uncheckedTrackColor = Color(0xFF00BCD4)
        )
    )
}

@Composable
fun PrinterField(name: String, onValueChange: (String) -> Unit) {
    TextField(
        value = name,
        onValueChange = { onValueChange(it) },
        modifier = Modifier.fillMaxWidth(),
        placeholder = {
            Text(
                text = "Impresora",
                style = MaterialTheme.typography.bodyLarge
            )
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next
        ),
        singleLine = true,
        maxLines = 1,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        ),
        readOnly = true
    )
}

@Composable
fun ApiUrlField(url: String, onValueChange: (String) -> Unit) {
    TextField(
        value = url,
        onValueChange = { onValueChange(it) },
        modifier = Modifier.fillMaxWidth(),
        placeholder = {
            Text(
                text = "URL de la API",
                style = MaterialTheme.typography.bodyLarge
            )
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Uri,
            imeAction = ImeAction.Done
        ),
        singleLine = true,
        maxLines = 1,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        )
    )
}

@Composable
fun SaveButton(modifier: Modifier, onLoginSelected: () -> Unit) {
    Button(
        onClick = { onLoginSelected() },
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = MaterialTheme.shapes.large,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF00BCD4),
            contentColor = Color.Black,
            disabledContainerColor = Color(0x2C00BCD4),
            disabledContentColor = Color(0xFF0C0C0C)
        )
    ) {
        Text(
            text = "Guardar Configuración",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White
        )
    }
}