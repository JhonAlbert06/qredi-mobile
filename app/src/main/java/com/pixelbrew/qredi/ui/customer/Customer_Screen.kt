package com.pixelbrew.qredi.ui.customer

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import com.pixelbrew.qredi.MainActivity
import com.pixelbrew.qredi.R
import com.pixelbrew.qredi.ui.customer.components.CreateCustomerBottomSheet
import com.pixelbrew.qredi.ui.customer.components.CustomerDetailBottomSheet
import com.pixelbrew.qredi.ui.customer.components.CustomerItem
import com.pixelbrew.qredi.ui.customer.components.FilterCustomerBottomSheet


@Composable
fun CustomerScreen(
    modifier: Modifier = Modifier,
    context: MainActivity
) {
    val viewModel: CustomerViewModel = hiltViewModel()
    val toastEvent by viewModel.toastMessage.observeAsState()

    LaunchedEffect(toastEvent) {
        toastEvent?.getContentIfNotHandled()?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    CustomerContent(viewModel = viewModel, modifier = modifier)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerContent(
    viewModel: CustomerViewModel,
    modifier: Modifier = Modifier
) {
    val customerList by viewModel.customerList.observeAsState(emptyList())
    val isLoading by viewModel.isLoading.observeAsState(false)
    val showFilterDialog by viewModel.showFilterCustomerDialog.observeAsState(false)
    val fieldSelected by viewModel.fieldSelected.observeAsState(Field("NONE", "NONE"))
    val query by viewModel.query.observeAsState("")
    val showCreationDialog by viewModel.showCreationDialog.observeAsState(false)

    val showCustomerDetail by viewModel.showCustomerDetail.observeAsState(false)

    var isFabExpanded by remember { mutableStateOf(false) }

    val refreshState = rememberPullToRefreshState()

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        floatingActionButton = {
            Column(horizontalAlignment = Alignment.End) {
                if (isFabExpanded) {
                    ExtendedFloatingActionButton(
                        text = { Text("Filtrar") },
                        icon = {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.filter_solid),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        onClick = {
                            isFabExpanded = false
                            viewModel.showFilterCustomerDialog(true)
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    ExtendedFloatingActionButton(
                        text = { Text("Crear") },
                        icon = {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.square_plus_regular),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        onClick = {
                            isFabExpanded = false
                            viewModel.showCreationDialog()
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                FloatingActionButton(
                    onClick = { isFabExpanded = !isFabExpanded },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = if (isFabExpanded) Icons.Default.Close else Icons.Default.Menu,
                        contentDescription = "Expandir acciones"
                    )
                }
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        PullToRefreshBox(
            state = refreshState,
            isRefreshing = isLoading,
            onRefresh = { viewModel.refreshCustomers() },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                contentPadding = PaddingValues(vertical = 12.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(customerList.size) { index ->
                    val customer = customerList[index]
                    CustomerItem(
                        onSelect = {
                            viewModel.getCustomerById(customer.id)
                        },
                        customer = customer
                    )
                }
            }
        }
    }

    if (showCustomerDetail) {
        CustomerDetailBottomSheet(
            customer = viewModel.selectedCustomer.value!!,
            onDismiss = { viewModel.showCustomerDetail(false) }
        )
    }

    if (showFilterDialog) {
        FilterCustomerBottomSheet(
            onDismiss = { viewModel.showFilterCustomerDialog(false) },
            onApplyFilters = { field, query -> viewModel.onSearchButtonClicked(field, query) },
            fields = viewModel.fields,
            selectedField = fieldSelected,
            query = query
        )
    }

    if (showCreationDialog) {
        CreateCustomerBottomSheet(
            onDismiss = { viewModel.hideCreationDialog() },
            onSubmit = { cedula, names, lastNames, address, phone, reference ->
                viewModel.createCustomer(cedula, names, lastNames, address, phone, reference)
            }
        )
    }
}