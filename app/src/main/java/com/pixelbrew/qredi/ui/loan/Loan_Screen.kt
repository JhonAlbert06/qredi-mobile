package com.pixelbrew.qredi.ui.loan

import android.Manifest
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import com.pixelbrew.qredi.MainActivity
import com.pixelbrew.qredi.R
import com.pixelbrew.qredi.data.network.model.LoanModel
import com.pixelbrew.qredi.ui.loan.components.CreateLoanBottomSheet
import com.pixelbrew.qredi.ui.loan.components.FilterLoanBottomSheet
import com.pixelbrew.qredi.ui.loan.components.LoanDetailBottomSheet
import com.pixelbrew.qredi.ui.loan.components.LoanItem

@RequiresApi(Build.VERSION_CODES.O)
@RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
@Composable
fun LoanScreen(
    modifier: Modifier = Modifier,
    context: MainActivity,
    navController: NavHostController
) {
    val viewModel: LoanViewModel = hiltViewModel()
    val toastEvent by viewModel.toastMessage.observeAsState()

    val lifecycleOwner = LocalLifecycleOwner.current

    val refreshCustomers = navController
        .currentBackStackEntry
        ?.savedStateHandle
        ?.getLiveData<Boolean>("refreshCustomers")

    LaunchedEffect(refreshCustomers) {
        refreshCustomers?.observe(lifecycleOwner) { shouldRefresh ->
            if (shouldRefresh == true) {
                viewModel.fetchCustomers()
                navController.currentBackStackEntry?.savedStateHandle?.set(
                    "refreshCustomers",
                    false
                )
            }
        }
    }

    LaunchedEffect(toastEvent) {
        toastEvent?.getContentIfNotHandled()?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    LoanContent(viewModel = viewModel, modifier = modifier)
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
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

    var isFabExpanded by remember { mutableStateOf(false) }

    val refreshState = rememberPullToRefreshState()

    // Scope for the pull to refresh


    Scaffold(
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.padding(16.dp)
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
        PullToRefreshBox(
            state = refreshState,
            isRefreshing = isLoading,
            onRefresh = {
                viewModel.refreshLoans()
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    LazyColumn(
                        contentPadding = innerPadding,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(loans.size) { index ->
                            val loan = loans[index]
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                                modifier = Modifier
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