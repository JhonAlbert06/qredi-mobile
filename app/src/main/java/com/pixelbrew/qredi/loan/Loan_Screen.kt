package com.pixelbrew.qredi.loan

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.pixelbrew.qredi.MainActivity
import com.pixelbrew.qredi.R
import com.pixelbrew.qredi.data.network.model.CustomerModelRes
import com.pixelbrew.qredi.data.network.model.RouteModel
import com.pixelbrew.qredi.ui.components.dropdown.GenericDropdown
import kotlinx.coroutines.delay

@Composable
fun LoanScreen(
    viewModel: LoanViewModel,
    modifier: Modifier = Modifier,
    context: MainActivity,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
    ) {
        Loan(viewModel, modifier, context)
        Spacer(modifier = Modifier.height(8.dp))
    }

    val viewModel: LoanViewModel = viewModel
    val toastMessage by viewModel.toastMessage.observeAsState()

    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            delay(200)
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
fun Loan(
    viewModel: LoanViewModel,
    modifier: Modifier = Modifier,
    context: MainActivity,
) {
    val isLoading by viewModel.isLoading.observeAsState(initial = false)

    Column {
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            HeaderLoan(viewModel, modifier)
            Spacer(modifier = Modifier.height(1.dp))
        }
    }
}

@Composable
fun HeaderLoan(
    viewModel: LoanViewModel,
    modifier: Modifier = Modifier
) {

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "Prestamos",
            style = MaterialTheme.typography.headlineMedium
        )

        Button(
            onClick = {
                viewModel.setShowCreationDialog(true)
            },
            modifier = Modifier
                .padding(start = 8.dp)
                .align(Alignment.CenterVertically),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF00BCD4),
                contentColor = Color.Black,
                disabledContainerColor = Color(0x2C00BCD4),
                disabledContentColor = Color(0xFF0C0C0C)
            ),
        ) {
            Text("Nuevo Prestamo")
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.user_plus_solid),
                contentDescription = "Crear Prestamo",
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(20.dp)
            )
        }
    }

    if (viewModel.showCreationDialog.observeAsState().value == true) {
        CreateLoanDialog(
            viewModel = viewModel,
            onDismiss = { viewModel.setShowCreationDialog(false) },
            onSubmit = { customerId, routeId, amount, interest, feesQuantity ->
                // Handle the submit action here
                Log.d("CreateLoanDialog", "Customer ID: $customerId")
                Log.d("CreateLoanDialog", "Route ID: $routeId")
                Log.d("CreateLoanDialog", "Amount: $amount")
                Log.d("CreateLoanDialog", "Interest: $interest")
                Log.d("CreateLoanDialog", "Fees Quantity: $feesQuantity")
            }
        )
    }

}

@Composable
fun CreateLoanDialog(
    viewModel: LoanViewModel,
    onDismiss: () -> Unit = {},
    onSubmit: (
        customerId: String,
        routeId: String,
        amount: String,
        interest: String,
        feesQuantity: String
    ) -> Unit = { customerId, routeId, amount, interest, feesQuantity -> }
) {

    val customers by viewModel.customerList.observeAsState(initial = emptyList())
    val routes by viewModel.routesList.observeAsState(initial = emptyList())

    var customerId by remember { mutableStateOf("") }
    var routeId by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var interest by remember { mutableStateOf("") }
    var feesQuantity by remember { mutableStateOf("") }

    var customerSelected by remember { mutableStateOf(CustomerModelRes()) }
    var routeSelected by remember { mutableStateOf(RouteModel()) }

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
                Text("Crear Prestamo", style = MaterialTheme.typography.titleLarge)

                Spacer(modifier = Modifier.height(8.dp))
                GenericDropdown(
                    items = customers,
                    selectedItem = customerSelected,
                    onItemSelected = { customer ->
                        customerSelected = customer
                        customerId = customer.id
                    },
                    itemLabel = { it.firstName + " " + it.lastName },
                    label = "Cliente",
                )

                Spacer(modifier = Modifier.height(8.dp))
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

                Spacer(modifier = Modifier.height(8.dp))
                AmountField(
                    amount = amount,
                    onValueChange = { amount = it }
                )

                Spacer(modifier = Modifier.height(8.dp))
                InterestField(
                    interest = interest,
                    onValueChange = { interest = it }
                )

                Spacer(modifier = Modifier.height(8.dp))
                FeesQuantityField(
                    feesQuantity = feesQuantity,
                    onValueChange = { feesQuantity = it }
                )

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
                            onSubmit(
                                customerId,
                                routeId,
                                amount,
                                interest,
                                feesQuantity
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
fun FeesQuantityField(
    feesQuantity: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = feesQuantity,
        onValueChange = onValueChange,
        label = { Text("Cantidad de Cuotas") },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Number
        ),
        singleLine = true,
        maxLines = 1
    )
}

@Composable
fun InterestField(
    interest: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = interest,
        onValueChange = onValueChange,
        label = { Text("Interes") },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Number
        ),
        singleLine = true,
        maxLines = 1
    )
}

@Composable
fun AmountField(
    amount: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = amount,
        onValueChange = onValueChange,
        label = { Text("Monto") },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Number
        ),
        singleLine = true,
        maxLines = 1
    )
}
