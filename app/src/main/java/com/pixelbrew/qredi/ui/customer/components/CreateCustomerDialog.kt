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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun CreateCustomerDialog(
    onDismiss: () -> Unit = {},
    onSubmit: (
        cedula: String,
        names: String,
        lastNames: String,
        address: String,
        phone: String,
        reference: String
    ) -> Unit = { _, _, _, _, _, _ -> }
) {
    var cedula by remember { mutableStateOf("") }
    var names by remember { mutableStateOf("") }
    var lastNames by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var reference by remember { mutableStateOf("") }

    // Dirección desglosada
    var city by remember { mutableStateOf("") }
    var sector by remember { mutableStateOf("") }
    var street by remember { mutableStateOf("") }
    var houseNumber by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = MaterialTheme.shapes.large,
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .padding(16.dp)
            ) {
                Text("Crear Cliente", style = MaterialTheme.typography.titleLarge)

                Spacer(modifier = Modifier.height(8.dp))
                CedulaField(
                    cedula = cedula,
                    onValueChange = { cedula = it }
                )

                NamesField(
                    names = names,
                    onValueChange = { names = it }
                )

                LastNamesField(
                    lastNames = lastNames,
                    onValueChange = { lastNames = it }
                )

                PhoneField(
                    phone = phone,
                    onValueChange = { phone = it }
                )

                ReferenceField(
                    reference = reference,
                    onValueChange = { reference = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Dirección", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))

                CityField(
                    city = city,
                    onValueChange = { city = it }
                )

                SectorField(
                    sector = sector,
                    onValueChange = { sector = it }
                )

                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    StreetField(
                        street = street,
                        onValueChange = { street = it },
                        modifier = Modifier.weight(1f)
                    )
                    HouseNumberField(
                        houseNumber = houseNumber,
                        onValueChange = { houseNumber = it },
                        modifier = Modifier.weight(1f)
                    )
                }


                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onDismiss
                    ) {
                        Text("Cancelar")
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00BCD4),
                            contentColor = Color.Black,
                            disabledContainerColor = Color(0x2C00BCD4),
                            disabledContentColor = Color(0xFF0C0C0C)
                        ),
                        onClick = {
                            val fullAddress =
                                "${city.trim()}, ${sector.trim()}, Calle ${street.trim()}, Casa #${houseNumber.trim()}"
                            onSubmit(
                                cedula,
                                names,
                                lastNames,
                                fullAddress,
                                phone,
                                reference
                            )
                        }
                    ) {
                        Text("Guardar")
                    }
                }
            }
        }
    }
}

@Composable
fun CedulaField(
    cedula: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = cedula,
        onValueChange = { onValueChange(it) },
        label = { Text("Cédula") },
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Next
        ),
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        maxLines = 1,
    )
}

@Composable
fun NamesField(
    names: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = names,
        onValueChange = { onValueChange(it) },
        label = { Text("Nombres") },
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next
        ),
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        maxLines = 1,
    )
}

@Composable
fun LastNamesField(
    lastNames: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = lastNames,
        onValueChange = { onValueChange(it) },
        label = { Text("Apellidos") },
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next
        ),
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        maxLines = 1,
    )
}

@Composable
fun PhoneField(
    phone: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = phone,
        onValueChange = { onValueChange(it) },
        label = { Text("Teléfono") },
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Phone,
            imeAction = ImeAction.Next
        ),
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        maxLines = 1,
    )
}

@Composable
fun ReferenceField(
    reference: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = reference,
        onValueChange = { onValueChange(it) },
        label = { Text("Referencia") },
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next
        ),
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        maxLines = 1,
    )
}

@Composable
fun CityField(
    city: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = city,
        onValueChange = { onValueChange(it) },
        label = { Text("Ciudad") },
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next
        ),
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        maxLines = 1,
    )
}

@Composable
fun SectorField(
    sector: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = sector,
        onValueChange = { onValueChange(it) },
        label = { Text("Sector") },
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next
        ),
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        maxLines = 1,
    )
}

@Composable
fun StreetField(
    street: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier
) {
    OutlinedTextField(
        value = street,
        onValueChange = { onValueChange(it) },
        label = { Text("Calle") },
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next
        ),
        modifier = modifier
            .padding(end = 4.dp),
        singleLine = true,
        maxLines = 1,
    )
}

@Composable
fun HouseNumberField(
    houseNumber: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier
) {
    OutlinedTextField(
        value = houseNumber,
        onValueChange = { onValueChange(it) },
        label = { Text("Número de casa") },
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done
        ),
        modifier = modifier
            .padding(start = 4.dp),
        singleLine = true,
        maxLines = 1,
    )
}

