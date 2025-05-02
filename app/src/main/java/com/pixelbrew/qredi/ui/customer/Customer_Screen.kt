package com.pixelbrew.qredi.ui.customer

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.pixelbrew.qredi.MainActivity
import com.pixelbrew.qredi.R
import com.pixelbrew.qredi.ui.customer.components.CreateCustomerBottomSheet
import com.pixelbrew.qredi.ui.customer.components.CustomerItem
import com.pixelbrew.qredi.ui.customer.components.FilterCustomerBottomSheet

@Composable
fun CustomerScreen(
    modifier: Modifier = Modifier,
    context: MainActivity,
    navController: NavHostController
) {
    val viewModel: CustomerViewModel = hiltViewModel()
    val toastEvent by viewModel.toastMessage.observeAsState()

    LaunchedEffect(toastEvent) {
        toastEvent?.getContentIfNotHandled()?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    CustomerContent(viewModel = viewModel, modifier = modifier, navController = navController)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerContent(
    viewModel: CustomerViewModel,
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    val customerList by viewModel.customerList.observeAsState(initial = emptyList())
    val isLoading by viewModel.isLoading.observeAsState(initial = false)
    val showFilterDialog by viewModel.showFilterCustomerDialog.observeAsState(initial = false)
    val fieldSelected by viewModel.fieldSelected.observeAsState(initial = Field("NONE", "NONE"))
    val query by viewModel.query.observeAsState(initial = "")
    val showCreationDialog by viewModel.showCreationDialog.observeAsState(initial = false)

    var isFabExpanded by remember { mutableStateOf(false) }

    Scaffold(

        topBar = {
            TopAppBar(
                modifier = Modifier.padding(top = 12.dp),
                title = { Text("") },
                actions = {
                    Button(
                        onClick = {
                            viewModel.refreshCustomers()
                        },
                        modifier = Modifier
                            .padding(8.dp)
                            .align(Alignment.CenterVertically),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xA413E2FD),
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
            Column(
                horizontalAlignment = Alignment.End
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
                            viewModel.showFilterCustomerDialog(true)
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
                            viewModel.showCreationDialog()
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
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(customerList.size) { index ->
                        val customer = customerList[index]
                        CustomerItem(
                            customer = customer,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }

    if (showFilterDialog) {
        FilterCustomerBottomSheet(
            onDismiss = { viewModel.showFilterCustomerDialog(false) },
            onApplyFilters = { field, query ->
                viewModel.onSearchButtonClicked(field, query)
            },
            fields = viewModel.fields,
            selectedField = fieldSelected,
            query = query
        )
    }

    if (showCreationDialog) {
        CreateCustomerBottomSheet(
            onDismiss = { viewModel.hideCreationDialog() },
            onSubmit = { cedula, names, lastNames, address, phone, reference ->
                viewModel.createCustomer(
                    cedula = cedula,
                    names = names,
                    lastNames = lastNames,
                    address = address,
                    phone = phone,
                    reference = reference,
                )

                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.set("refreshCustomers", true)
            }
        )
    }
}