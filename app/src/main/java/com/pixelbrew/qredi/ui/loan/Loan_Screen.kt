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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import com.pixelbrew.qredi.ui.loan.components.LoanDetailBottomSheet
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
    val customers by viewModel.customerList.observeAsState(initial = emptyList())

    val loanSelected by viewModel.loanSelected.observeAsState()

    // Estado para el botón flotante expandible
    var isFabExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {

            TopAppBar(
                modifier = Modifier.padding(top = 12.dp),
                title = { Text("") },
                actions = {

                    Button(
                        onClick = {
                            viewModel.refreshLoans()
                        },
                        modifier = Modifier
                            .padding(8.dp)
                            .align(Alignment.CenterVertically),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00BCD4),
                            contentColor = Color.Black,
                            disabledContainerColor = Color(0x2C00BCD4),
                            disabledContentColor = Color(0xFF0C0C0C)
                        )
                    ) {
                        Text("Actualizar")
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.arrows_rotate_solid),
                            contentDescription = "Actualizar",
                            modifier = Modifier
                                .size(20.dp)
                                .padding(start = 8.dp)
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
                                    viewModel.setLoanSelected(loan)
                                    viewModel.setLoanDetailsDialog(true)
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
            onApplyFilters = { field, query ->
                viewModel.fetchLoans(field, query)
            },
            routesList = routesList,
            usersList = customers,
        )
    }

    if (viewModel.showLoanDetailsDialog.observeAsState().value == true) {
        LoanDetailBottomSheet(
            loan = loanSelected!!,
            onDismiss = { viewModel.setLoanDetailsDialog(false) },
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        )
    }
}



