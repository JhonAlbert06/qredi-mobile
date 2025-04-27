package com.pixelbrew.qredi.ui.loan

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.unit.dp
import com.pixelbrew.qredi.MainActivity
import com.pixelbrew.qredi.R
import com.pixelbrew.qredi.data.network.model.LoanModel
import com.pixelbrew.qredi.ui.loan.components.CreateLoanBottomSheet
import com.pixelbrew.qredi.ui.loan.components.FilterLoanBottomSheet
import com.pixelbrew.qredi.ui.loan.components.LoanItem
import kotlinx.coroutines.delay

@Composable
fun LoanScreen(
    viewModel: LoanViewModel,
    modifier: Modifier = Modifier,
    context: MainActivity,
) {
    val toastMessage by viewModel.toastMessage.observeAsState()

    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            delay(200)
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    LoanContent(viewModel = viewModel, modifier = modifier)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoanContent(
    viewModel: LoanViewModel,
    modifier: Modifier = Modifier,
) {
    val isLoading by viewModel.isLoading.observeAsState(initial = false)
    val loans by viewModel.loans.observeAsState(initial = emptyList())

    val routesList by viewModel.routesList.observeAsState(initial = emptyList())

    var routeId by remember { mutableStateOf("") }
    var customerId by remember { mutableStateOf("") }
    var isPaid by remember { mutableStateOf(false) }

    // Estado para el botón flotante expandible
    var isFabExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Prestamos") },
                actions = {
                    IconButton(onClick = { viewModel.refreshLoans() }) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.arrows_rotate_solid),
                            contentDescription = "Actualizar",
                            tint = Color(0xFF00BCD4),
                            modifier = Modifier
                                .size(40.dp)
                                .padding(8.dp),
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            // Botón flotante expandible
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier
                    .padding(16.dp)
            ) {
                if (isFabExpanded) {
                    ExtendedFloatingActionButton(
                        text = { Text("Filtrar") },
                        icon = {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.filter_solid),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(40.dp)
                                    .padding(8.dp),
                            )
                        },
                        onClick = {
                            isFabExpanded = false
                            viewModel.setShowFilterLoanDialog(true)
                        },
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    ExtendedFloatingActionButton(
                        text = { Text("Crear") },
                        icon = {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.square_plus_regular),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(40.dp)
                                    .padding(8.dp),
                            )
                        },
                        onClick = {
                            isFabExpanded = false
                            viewModel.setShowCreationDialog(true)
                        },
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                FloatingActionButton(
                    onClick = { isFabExpanded = !isFabExpanded }
                ) {
                    Icon(
                        imageVector = if (isFabExpanded) Icons.Default.Close else Icons.Default.Menu,
                        contentDescription = "Expandir acciones"
                    )
                }
            }
        },
        modifier = modifier
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 12.dp, end = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(loans.size) { index ->
                        val loan = loans[index]
                        Card(
                            modifier = Modifier
                                .padding(bottom = 8.dp)
                                .clickable {
                                    // Acciones al hacer clic en un préstamo
                                }
                        ) {
                            LoanItem(loan, viewModel)
                        }
                    }
                }
            }
        }
    }

    if (viewModel.showCreationDialog.observeAsState().value == true) {
        CreateLoanBottomSheet(
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
                    Log.e("LoanScreen", "Error al crear el préstamo: ${e.message}")
                    //Toast.makeText(context, "Error al crear el préstamo", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    if (viewModel.showFilterLoanDialog.observeAsState().value == true) {
        FilterLoanBottomSheet(
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

