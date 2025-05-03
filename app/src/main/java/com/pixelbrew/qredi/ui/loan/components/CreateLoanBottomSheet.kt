package com.pixelbrew.qredi.ui.loan.components


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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.pixelbrew.qredi.data.network.model.CustomerModelRes
import com.pixelbrew.qredi.data.network.model.RouteModel
import com.pixelbrew.qredi.ui.components.dropdown.GenericDropdown
import com.pixelbrew.qredi.ui.loan.LoanViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateLoanBottomSheet(
    viewModel: LoanViewModel,
    onDismiss: () -> Unit = {},
    onSubmit: (
        customerId: String,
        routeId: String,
        amount: String,
        interest: String,
        feesQuantity: String
    ) -> Unit = { _, _, _, _, _ -> }
) {
    val customers by viewModel.customerList.observeAsState(emptyList())
    val routes by viewModel.routesList.observeAsState(emptyList())

    var customerId by remember { mutableStateOf("") }
    var routeId by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var interest by remember { mutableStateOf("") }
    var feesQuantity by remember { mutableStateOf("") }

    var customerSelected by remember { mutableStateOf(CustomerModelRes()) }
    var routeSelected by remember { mutableStateOf(RouteModel()) }

    val scrollState = rememberScrollState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            Text(
                text = "Crear Préstamo",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(12.dp))

            GenericDropdown(
                items = customers,
                selectedItem = customerSelected,
                onItemSelected = { customer ->
                    customerSelected = customer
                    customerId = customer.id
                },
                itemLabel = { "${it.firstName} ${it.lastName}" },
                label = "Cliente",
            )

            Spacer(modifier = Modifier.height(12.dp))

            GenericDropdown(
                items = routes,
                selectedItem = routeSelected,
                onItemSelected = { route ->
                    routeSelected = route
                    routeId = route.id
                },
                itemLabel = { it.name },
                label = "Ruta",
            )

            Spacer(modifier = Modifier.height(12.dp))

            AmountField(amount = amount, onValueChange = { amount = it })

            Spacer(modifier = Modifier.height(12.dp))

            InterestField(interest = interest, onValueChange = { interest = it })

            Spacer(modifier = Modifier.height(12.dp))

            FeesQuantityField(feesQuantity = feesQuantity, onValueChange = { feesQuantity = it })

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Cancelar")
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(
                    onClick = {
                        onSubmit(customerId, routeId, amount, interest, feesQuantity)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00BCD4),
                        contentColor = Color.Black,
                        disabledContainerColor = Color(0x2C00BCD4),
                        disabledContentColor = Color(0xFF0C0C0C)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Guardar")
                }
            }
        }
    }
}

@Composable
fun AmountField(amount: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = amount,
        onValueChange = onValueChange,
        label = { Text("Monto") },
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        maxLines = 1
    )
}

@Composable
fun InterestField(interest: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = interest,
        onValueChange = onValueChange,
        label = { Text("Interés") },
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        maxLines = 1
    )
}

@Composable
fun FeesQuantityField(feesQuantity: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = feesQuantity,
        onValueChange = onValueChange,
        label = { Text("Cantidad de Cuotas") },
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        maxLines = 1
    )
}
