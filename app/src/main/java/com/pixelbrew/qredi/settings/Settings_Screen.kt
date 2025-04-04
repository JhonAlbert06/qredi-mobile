package com.pixelbrew.qredi.settings

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.pixelbrew.qredi.MainActivity

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier,
    context: MainActivity,
) {

    val printerName by viewModel.printerName.observeAsState("")
    val apiUrl by viewModel.apiUrl.observeAsState("")

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            "Configuración",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(24.dp))


        Spacer(modifier = Modifier.height(24.dp))
        Text("Impresora", style = MaterialTheme.typography.titleLarge)
        PrinterField(
            printerName,
            onValueChange = { viewModel.onPrinterNameChange(it) }
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
        )
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