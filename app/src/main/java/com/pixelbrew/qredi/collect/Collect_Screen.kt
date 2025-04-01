package com.pixelbrew.qredi.collect

import android.widget.Toast
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.pixelbrew.qredi.MainActivity
import com.pixelbrew.qredi.R
import com.pixelbrew.qredi.network.model.DownloadModel
import com.pixelbrew.qredi.network.model.RouteModel
import kotlinx.coroutines.launch

@androidx.annotation.RequiresPermission(android.Manifest.permission.BLUETOOTH_CONNECT)
@Composable
fun CollectScreen(
    viewModel: CollectViewModel,
    modifier: Modifier = Modifier,
    context: MainActivity,
) {

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
    ) {

        Collect(viewModel, modifier, context)
        Spacer(modifier = Modifier.height(8.dp))
    }

    val viewModel: CollectViewModel = viewModel
    val toastMessage by viewModel.toastMessage.observeAsState()

    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }
}

@androidx.annotation.RequiresPermission(android.Manifest.permission.BLUETOOTH_CONNECT)
@Composable
fun Collect(
    viewModel: CollectViewModel,
    modifier: Modifier,
    context: MainActivity,
) {
    val loans by viewModel.downloadedRoutes.observeAsState(emptyList())

    Column {
        DownloadRoute(viewModel, modifier)
        LoansList(
            loans = loans,
            viewModel = viewModel,
            context = context
        )
    }
}

@androidx.annotation.RequiresPermission(android.Manifest.permission.BLUETOOTH_CONNECT)
@Composable
fun FeeItems(
    viewModel: CollectViewModel,
    loan: DownloadModel,
    context: MainActivity,
) {
    val amount: String by viewModel.amount.observeAsState(initial = "")

    var showDialogCollect by remember { mutableStateOf(false) }
    var totalLoanPaid = 0.0

    loan.fees.forEach { fee ->
        totalLoanPaid += fee.paymentAmount
    }

    FeeLabel(
        "Cobrado:",
        "${viewModel.formatNumber(totalLoanPaid)}$ / ${viewModel.formatNumber(loan.amount)}$"
    )

    LazyColumn {
        items(loan.fees) { fee ->

            var cuote by remember { mutableDoubleStateOf(0.0) }
            cuote = ((loan.interest / 100) * loan.amount) + (loan.amount / loan.feesQuantity)

            Card(
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    Spacer(modifier = Modifier.height(8.dp))

                    FeeLabel(
                        "${fee.number}/${loan.feesQuantity}",
                        "${fee.date.day}/${fee.date.month}/${fee.date.year}"
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    FeeLabel(
                        "${
                            viewModel.formatNumber(fee.paymentAmount)
                        }$ / ${
                            viewModel.formatNumber(
                                cuote
                            )
                        }$",
                        ""
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            showDialogCollect = true
                            viewModel.setFeeSelected(fee)
                        },
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00BCD4),
                            contentColor = Color.Black,
                            disabledContainerColor = Color(0x2C00BCD4),
                            disabledContentColor = Color(0xFF0C0C0C)
                        ),
                        enabled = fee.paymentAmount < cuote
                    ) {
                        Text("Cobrar")
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.coins_solid),
                            contentDescription = "",
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .size(20.dp)
                        )
                    }

                }
            }
        }
    }


    if (showDialogCollect) {
        AlertDialog(
            title = {
                Text(text = "Cobrar")
            },
            text = {
                AmountField(amount) {
                    viewModel.onAmountChange(it)
                }
            },
            onDismissRequest = {
                showDialogCollect = false
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.collectFee()
                        showDialogCollect = false
                        viewModel.printCollect(context)

                        viewModel.resetAmount()
                    },
                    enabled = amount.isNotEmpty()
                ) {
                    Text("Cobrar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDialogCollect = false
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }


}


@Composable
fun AmountField(amount: String, onValueChange: (String) -> Unit) {
    TextField(
        value = amount,
        onValueChange = { onValueChange(it) },
        modifier = Modifier.fillMaxWidth(),
        placeholder = {
            Text(
                text = "Monto",
                style = MaterialTheme.typography.bodyLarge
            )
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
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
fun FeeLabel(x0: String, x1: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(x0)
        Text(x1)
    }
}

@Composable
fun RoutesList(
    routes: List<RouteModel>,
    viewModel: CollectViewModel,
    onRouteSelected: () -> Unit
) {

    if (routes.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(Modifier.align(Alignment.Center))
        }
    }

    val coroutineScope = rememberCoroutineScope()

    LazyColumn {
        items(routes) { route ->
            Card(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .clickable {
                        coroutineScope.launch {
                            viewModel.downloadRoute(route.id)
                            onRouteSelected()
                        }
                    }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(route.name)
                }
            }
        }
    }
}

@Composable
fun LoanLabel(
    icon: ImageVector,
    text: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically

    ) {
        Icon(
            imageVector = icon,
            contentDescription = "Loan Icon",
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 8.dp),
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun LoanItem(
    loan: DownloadModel,
    viewModel: CollectViewModel
) {

    Column(modifier = Modifier.padding(16.dp)) {
        LoanLabel(
            icon = ImageVector.vectorResource(id = R.drawable.user_solid),
            text = loan.customer.name
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

    }

}

@androidx.annotation.RequiresPermission(android.Manifest.permission.BLUETOOTH_CONNECT)
@Composable
fun LoansList(
    loans: List<DownloadModel>,
    viewModel: CollectViewModel,
    context: MainActivity
) {
    var showDialogLoan by remember { mutableStateOf(false) }
    val loanSelectedState =
        viewModel.downloadRouteSelected.observeAsState(initial = DownloadModel())
    val loanSelected: DownloadModel = loanSelectedState.value

    LazyColumn {
        items(loans) { loan ->
            Card(
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .clickable {
                        viewModel.setDownloadRouteSelected(loan)
                        showDialogLoan = true
                    }
            ) {
                LoanItem(loan, viewModel)
            }
        }
    }

    if (showDialogLoan) {
        AlertDialog(

            title = {
                Text(text = "Prestamo")
            },
            text = {
                Column {
                    LoanItem(
                        loan = loanSelected,
                        viewModel = viewModel
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    FeeItems(
                        viewModel = viewModel,
                        loan = loanSelected,
                        context = context
                    )
                }
            },
            onDismissRequest = {
                showDialogLoan = false
            },
            confirmButton = {},
            dismissButton = {
                TextButton(
                    onClick = {
                        showDialogLoan = false
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

}

@Composable
fun DownloadRoute(
    viewModel: CollectViewModel,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var showDialogRoute by remember { mutableStateOf(false) }

    val routes by viewModel.routes.observeAsState(emptyList())

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "Rutas",
            style = MaterialTheme.typography.headlineMedium
        )

        Button(
            onClick = {
                showDialogRoute = true
                coroutineScope.launch {
                    viewModel.getRoutes()
                }
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
            enabled = viewModel.downloadedRoutes.value?.isEmpty() == true
        ) {
            Text("Descargar")
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.download_solid),
                contentDescription = "Download Route",
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(20.dp)
            )
        }

    }

    if (showDialogRoute) {
        AlertDialog(
            title = {
                Text(text = "Selecciona una ruta")
            },
            text = {
                RoutesList(
                    routes = routes,
                    viewModel = viewModel,
                    onRouteSelected = { showDialogRoute = false }
                )
            },
            onDismissRequest = {
                showDialogRoute = false
            },
            confirmButton = {},
            dismissButton = {
                TextButton(
                    onClick = {
                        showDialogRoute = false
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}




