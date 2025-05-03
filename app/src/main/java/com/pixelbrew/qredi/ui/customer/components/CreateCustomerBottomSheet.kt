package com.pixelbrew.qredi.ui.customer.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCustomerBottomSheet(
    onDismiss: () -> Unit = {},
    onSubmit: (String, String, String, String, String, String) -> Unit = { _, _, _, _, _, _ -> }
) {
    var cedula by remember { mutableStateOf("") }
    var names by remember { mutableStateOf("") }
    var lastNames by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var reference by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var sector by remember { mutableStateOf("") }
    var street by remember { mutableStateOf("") }
    var houseNumber by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = null
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            Text("Crear Cliente", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
            CedulaField(cedula) { cedula = it }
            NamesField(names) { names = it }
            LastNamesField(lastNames) { lastNames = it }
            PhoneField(phone) { phone = it }
            ReferenceField(reference) { reference = it }
            Spacer(Modifier.height(16.dp))
            Text("Dirección", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))
            CityField(city) { city = it }
            SectorField(sector) { sector = it }
            Row(Modifier.fillMaxWidth()) {
                StreetField(
                    street, { street = it }, Modifier
                        .weight(1f)
                        .padding(end = 4.dp)
                )
                HouseNumberField(
                    houseNumber,
                    { houseNumber = it },
                    Modifier
                        .weight(1f)
                        .padding(start = 4.dp)
                )
            }
            Spacer(Modifier.height(24.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onDismiss) { Text("Cancelar") }
                Spacer(Modifier.width(16.dp))
                Button(
                    onClick = {
                        val fullAddress =
                            "${city.trim()}, ${sector.trim()}, Calle ${street.trim()}, Casa #${houseNumber.trim()}"
                        onSubmit(cedula, names, lastNames, fullAddress, phone, reference)
                        onDismiss()
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.height(50.dp)
                ) { Text("Guardar") }
            }
        }
    }
}

// ✅ Los TextFields mantienen diseño limpio y coherente
@Composable
fun CedulaField(value: String, onChange: (String) -> Unit) =
    OutlinedField("Cédula", value, onChange, KeyboardType.Number)

@Composable
fun NamesField(value: String, onChange: (String) -> Unit) =
    OutlinedField("Nombres", value, onChange, KeyboardType.Text)

@Composable
fun LastNamesField(value: String, onChange: (String) -> Unit) =
    OutlinedField("Apellidos", value, onChange, KeyboardType.Text)

@Composable
fun PhoneField(value: String, onChange: (String) -> Unit) =
    OutlinedField("Teléfono", value, onChange, KeyboardType.Phone)

@Composable
fun ReferenceField(value: String, onChange: (String) -> Unit) =
    OutlinedField("Referencia", value, onChange, KeyboardType.Text)

@Composable
fun CityField(value: String, onChange: (String) -> Unit) =
    OutlinedField("Ciudad", value, onChange, KeyboardType.Text)

@Composable
fun SectorField(value: String, onChange: (String) -> Unit) =
    OutlinedField("Sector", value, onChange, KeyboardType.Text)

@Composable
fun StreetField(value: String, onChange: (String) -> Unit, modifier: Modifier) =
    OutlinedField("Calle", value, onChange, KeyboardType.Text, modifier)

@Composable
fun HouseNumberField(value: String, onChange: (String) -> Unit, modifier: Modifier) =
    OutlinedField(
        "Número de casa", value, onChange, KeyboardType.Text, modifier,
        KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        )
    )

@Composable
fun OutlinedField(
    label: String,
    value: String,
    onChange: (String) -> Unit,
    keyboardType: KeyboardType,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default.copy(
        keyboardType = keyboardType,
        imeAction = ImeAction.Next
    ),
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        keyboardOptions = keyboardOptions,
        singleLine = true,
        maxLines = 1,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.fillMaxWidth()
    )
}
