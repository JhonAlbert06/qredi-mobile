package com.pixelbrew.qredi.ui.loan.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pixelbrew.qredi.data.network.model.CustomerModelRes
import com.pixelbrew.qredi.data.network.model.RouteModel
import com.pixelbrew.qredi.ui.components.dropdown.GenericDropdown

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterLoanBottomSheet(
    onDismiss: () -> Unit,
    onApplyFilters: (String, String) -> Unit,
    usersList: List<CustomerModelRes> = emptyList(),
    routesList: List<RouteModel> = emptyList(),
) {
    var selectedFilter by remember { mutableStateOf<String?>(null) }
    var userSelected by remember { mutableStateOf(CustomerModelRes()) }
    var routeSelected by remember { mutableStateOf(RouteModel()) }
    var isPaidAux by remember { mutableStateOf<Boolean?>(null) }
    var isCurrentLoanAux by remember { mutableStateOf<Boolean?>(null) }
    var selectedField by remember { mutableStateOf<String?>(null) }
    var selectedQuery by remember { mutableStateOf<String?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Filtrar Préstamos", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))

            GenericDropdown(
                items = listOf("Cliente", "Ruta", "Pagado", "Préstamo Actual"),
                selectedItem = selectedFilter ?: "Seleccione uno",
                onItemSelected = { filter ->
                    selectedFilter = filter
                    userSelected = CustomerModelRes()
                    routeSelected = RouteModel()
                    isPaidAux = null
                    isCurrentLoanAux = null
                    selectedField = null
                    selectedQuery = null
                },
                itemLabel = { it },
                label = "Filtrar por",
            )

            Spacer(modifier = Modifier.height(8.dp))

            when (selectedFilter) {
                "Cliente" -> {
                    GenericDropdown(
                        items = usersList,
                        selectedItem = userSelected,
                        onItemSelected = {
                            userSelected = it
                            selectedField = "customerId"
                            selectedQuery = it.id
                        },
                        itemLabel = { it.firstName + " " + it.lastName },
                        label = "Cliente",
                    )
                }

                "Ruta" -> {
                    GenericDropdown(
                        items = routesList,
                        selectedItem = routeSelected,
                        onItemSelected = {
                            routeSelected = it
                            selectedField = "routeId"
                            selectedQuery = it.id
                        },
                        itemLabel = { it.name },
                        label = "Ruta",
                    )
                }

                "Pagado" -> {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = isPaidAux == true,
                            onCheckedChange = {
                                isPaidAux = it
                                selectedField = "loan_is_paid"
                                selectedQuery = it.toString()
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Pagado")
                    }
                }

                "Préstamo Actual" -> {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = isCurrentLoanAux == true,
                            onCheckedChange = {
                                isCurrentLoanAux = it
                                selectedField = "is_current_loan"
                                selectedQuery = it.toString()
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Préstamo Actual")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismiss) {
                    Text("Cancelar")
                }

                Spacer(modifier = Modifier.width(16.dp))

                Button(
                    onClick = {
                        if (selectedField != null && selectedQuery != null) {
                            onApplyFilters(selectedField!!, selectedQuery!!)
                        }
                        onDismiss()
                    }
                ) {
                    Text("Aplicar")
                }
            }
        }
    }
}