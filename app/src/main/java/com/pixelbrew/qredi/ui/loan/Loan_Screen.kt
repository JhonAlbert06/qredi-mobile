package com.pixelbrew.qredi.ui.loan

import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.pixelbrew.qredi.MainActivity
import com.pixelbrew.qredi.R
import com.pixelbrew.qredi.data.network.model.CustomerModelRes
import com.pixelbrew.qredi.data.network.model.LoanModel
import com.pixelbrew.qredi.data.network.model.LoanModelRes
import com.pixelbrew.qredi.data.network.model.RouteModel
import com.pixelbrew.qredi.ui.collect.components.LoanLabel
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
            .fillMaxSize(),
    ) {
        Loan(viewModel, modifier, context)
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
    val loans by viewModel.loans.observeAsState(initial = emptyList())

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        HeaderLoan(viewModel)
        Spacer(modifier = Modifier.height(8.dp))

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(loans.size) { index ->
                    val loan = loans[index]
                    Card(
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .clickable {
                                //viewModel.setDownloadRouteSelected(loan)
                                //showDialogLoan = true
                            }
                    ) {
                        LoanItem(loan, viewModel)
                    }
                }
            }

        }
    }
}

@Composable
fun HeaderLoan(
    viewModel: LoanViewModel,
    modifier: Modifier = Modifier
) {

    var routeId by remember { mutableStateOf("") }
    var customerId by remember { mutableStateOf("") }
    var isPaid by remember { mutableStateOf(false) }

    val routesList by viewModel.routesList.observeAsState(initial = emptyList())

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "Prestamos",
            style = MaterialTheme.typography.headlineMedium
        )

        Icon(
            imageVector = ImageVector.vectorResource(id = R.drawable.square_plus_regular),
            contentDescription = "Crear",
            modifier = Modifier
                .size(40.dp)
                .clickable {
                    viewModel.setShowCreationDialog(true)
                }
                .padding(8.dp),
            tint = Color(0xFF71FF78)
        )

        Icon(
            imageVector = ImageVector.vectorResource(id = R.drawable.filter_solid),
            contentDescription = "Filtrar",
            modifier = Modifier
                .size(40.dp)
                .clickable {
                    viewModel.setShowFilterLoanDialog(true)
                }
                .padding(8.dp),
            tint = Color(0xFF6BEDFF)
        )

        Icon(
            imageVector = ImageVector.vectorResource(id = R.drawable.arrows_rotate_solid),
            contentDescription = "Actualizar",
            modifier = Modifier
                .size(40.dp)
                .clickable {
                    viewModel.refreshLoans()
                }
                .padding(8.dp),
            tint = Color(0xFF00BCD4)
        )
    }

    if (viewModel.showCreationDialog.observeAsState().value == true) {
        CreateLoanDialog(
            viewModel = viewModel,
            onDismiss = { viewModel.setShowCreationDialog(false) },
            onSubmit = { customerId, routeId, amount, interest, feesQuantity ->

                var loan = LoanModel()

                try {
                    loan = LoanModel(
                        customerId = customerId,
                        routeId = routeId,
                        amount = amount.toDouble(),
                        interest = interest.toDouble(),
                        feesQuantity = feesQuantity.toInt(),
                    )

                    viewModel.createNewLoan(loan)
                } catch (e: Exception) {
                    //viewModel("Error al crear el prestamo")
                }


            }
        )
    }

    if (viewModel.showFilterLoanDialog.observeAsState().value == true) {
        FilterLoanDialog(
            onDismiss = { viewModel.setShowFilterLoanDialog(false) },
            onApplyFilters = { userId, routeId, isPaid ->
                viewModel.fetchLoans(userId, routeId, isPaid)
            },
            routesList = routesList,
            userId = customerId,
            routeId = routeId,
            isPaid = isPaid
        )
    }

}

@Composable
fun FilterLoanDialog(
    onDismiss: () -> Unit,
    onApplyFilters: (String, String, Boolean) -> Unit,
    usersList: List<CustomerModelRes> = emptyList(),
    routesList: List<RouteModel> = emptyList(),
    userId: String,
    routeId: String,
    isPaid: Boolean,
) {

    var userSelected by remember { mutableStateOf(CustomerModelRes()) }
    var routeSelected by remember { mutableStateOf(RouteModel()) }
    var isPaidAux by remember { mutableStateOf(isPaid) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Filtrar Prestamos") },
        text = {

            Column {
                GenericDropdown(
                    items = usersList,
                    selectedItem = userSelected,
                    onItemSelected = { userSelected = it },
                    itemLabel = { it.firstName + " " + it.lastName },
                    label = "Cliente",
                )

                Spacer(modifier = Modifier.height(8.dp))

                GenericDropdown(
                    items = routesList,
                    selectedItem = routeSelected,
                    onItemSelected = { routeSelected = it },
                    itemLabel = { it.name },
                    label = "Ruta",
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isPaidAux,
                        onCheckedChange = { isPaidAux = it }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Pagado")
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {

                    val userIdAux = if (userSelected.id.isNotEmpty()) userSelected.id else userId
                    val routeIdAux =
                        if (routeSelected.id.isNotEmpty()) routeSelected.id else routeId
                    onApplyFilters(userIdAux, routeIdAux, isPaidAux)
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


@Composable
fun LoanItem(
    loan: LoanModelRes,
    viewModel: LoanViewModel
) {

    Column(modifier = Modifier.padding(16.dp)) {

        LoanLabel(
            icon = ImageVector.vectorResource(id = R.drawable.calendar_solid),
            text = "${loan.date.day}/${loan.date.month}/${loan.date.year}"
        )
        Spacer(modifier = Modifier.height(8.dp))

        LoanLabel(
            icon = ImageVector.vectorResource(id = R.drawable.user_solid),
            text = loan.customer.firstName + " " + loan.customer.lastName
        )
        Spacer(modifier = Modifier.height(8.dp))

        LoanLabel(
            icon = ImageVector.vectorResource(id = R.drawable.address_card_solid),
            text = viewModel.formatCedula(loan.customer.cedula)
        )
        Spacer(modifier = Modifier.height(8.dp))

        LoanLabel(
            icon = ImageVector.vectorResource(id = R.drawable.coins_solid),
            text = "${viewModel.formatNumber(loan.amount)} $"
        )
        Spacer(modifier = Modifier.height(8.dp))

        LoanLabel(
            icon = ImageVector.vectorResource(id = R.drawable.hashtag_solid),
            text = "${loan.feesQuantity} cuotas"
        )
        Spacer(modifier = Modifier.height(8.dp))

        LoanLabel(
            icon = ImageVector.vectorResource(id = R.drawable.percent_solid),
            text = "${viewModel.formatNumber(loan.interest)} interes"
        )
        Spacer(modifier = Modifier.height(8.dp))

        LoanLabel(
            icon = ImageVector.vectorResource(id = R.drawable.wallet_solid),
            text = "${viewModel.formatNumber(loan.amount + ((loan.interest / 100) * loan.amount) * loan.feesQuantity)} $"
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (loan.loanIsPaid) {
                Tag(
                    text = "Pagado",
                    icon = R.drawable.check_solid,
                    backgroundColor = Color(0xFFEEFFFA),
                    contentColor = Color(0xFF00D455)
                )
            } else {
                Tag(
                    text = "No Pagado",
                    icon = R.drawable.xmark_solid,
                    backgroundColor = Color(0xFFFFEDED),
                    contentColor = Color(0xFFEF4444)
                )
            }

            Spacer(modifier = Modifier.width(3.dp))

            if (loan.isCurrentLoan) {
                Tag(
                    text = "Actual",
                    icon = R.drawable.check_to_slot_solid,
                    backgroundColor = Color(0xFFEFFBF5),
                    contentColor = Color(0xFF00D455)
                )
            }

            Spacer(modifier = Modifier.width(3.dp))

            Tag(
                text = loan.route.name,
                icon = R.drawable.location_dot_solid,
                backgroundColor = Color(0xFF000000),
                contentColor = Color(0xFFFFFFFF)
            )
        }


    }

}

@Composable
fun Tag(
    text: String,
    @DrawableRes icon: Int,
    backgroundColor: Color,
    contentColor: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(backgroundColor, shape = RoundedCornerShape(50))
            .padding(horizontal = 8.dp, vertical = 8.dp)
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(id = icon),
            contentDescription = text,
            tint = contentColor,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            color = contentColor,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
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
