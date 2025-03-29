package com.pixelbrew.qredi.collect

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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.pixelbrew.qredi.R
import com.pixelbrew.qredi.network.model.DownloadModel
import com.pixelbrew.qredi.network.model.RouteModel
import kotlinx.coroutines.launch

@Composable
fun CollectScreen(
    viewModel: CollectViewModel,
    modifier: Modifier = Modifier
) {
    val routes = viewModel.routes

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
    ) {
        Collect(viewModel, modifier)
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun Collect(
    viewModel: CollectViewModel,
    modifier: Modifier
) {
    Column {
        DownloadRoute(viewModel, modifier)

        LoansList(
            loans = viewModel.downloadedRoutes,
            viewModel = viewModel,
            modifier = modifier
        )

    }

}

@Composable
fun LoansList(
    loans: List<DownloadModel>,
    viewModel: CollectViewModel,
    modifier: Modifier
) {
    LazyColumn {
        items(loans) { loan ->
            Card(
                modifier = Modifier
                    .clickable {
                        // Handle click event
                    }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    LoanLabel(
                        icon = ImageVector.vectorResource(id = R.drawable.user_solid),
                        text = "${loan.customer.firstName} ${loan.customer.lastName}"
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
            modifier = Modifier.padding(start = 8.dp)
        )
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
fun DownloadRoute(
    viewModel: CollectViewModel,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var showDialog by remember { mutableStateOf(false) }

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
                showDialog = true
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

    if (showDialog) {
        AlertDialog(
            title = {
                Text(text = "Selecciona una ruta")
            },
            text = {
                RoutesList(
                    routes = viewModel.routes,
                    viewModel = viewModel,
                    onRouteSelected = { showDialog = false }
                )
            },
            onDismissRequest = {
                showDialog = false
            },
            confirmButton = {},
            dismissButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}




