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
    onApplyFilters: (String?, String?, Boolean?, Boolean?) -> Unit,
    usersList: List<CustomerModelRes> = emptyList(),
    routesList: List<RouteModel> = emptyList(),
    userId: String?,
    routeId: String?,
    isPaid: Boolean?,
    isCurrentLoan: Boolean?,
) {
    var userSelected by remember {
        mutableStateOf(usersList.find { it.id == userId } ?: CustomerModelRes())
    }
    var routeSelected by remember {
        mutableStateOf(routesList.find { it.id == routeId } ?: RouteModel())
    }
    var isPaidAux by remember { mutableStateOf(isPaid) }
    var isCurrentLoanAux by remember { mutableStateOf(isCurrentLoan) }

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

            // Dropdown para seleccionar el cliente
            GenericDropdown(
                items = usersList,
                selectedItem = userSelected,
                onItemSelected = { userSelected = it },
                itemLabel = { it.firstName + " " + it.lastName },
                label = "Cliente",
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Dropdown para seleccionar la ruta
            GenericDropdown(
                items = routesList,
                selectedItem = routeSelected,
                onItemSelected = { routeSelected = it },
                itemLabel = { it.name },
                label = "Ruta",
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Checkbox para "Pagado"
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = isPaidAux == true,  // Si es true, está marcado
                    onCheckedChange = { isPaidAux = it }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Pagado")
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Checkbox para "Préstamo Actual"
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = isCurrentLoanAux == true,  // Si es true, está marcado
                    onCheckedChange = { isCurrentLoanAux = it }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Préstamo Actual")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Fila con los botones
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                // Botón para cancelar
                TextButton(onClick = onDismiss) {
                    Text("Cancelar")
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Botón para aplicar los filtros
                Button(
                    onClick = {
                        val userIdAux = userSelected.id.takeIf { it.isNotEmpty() } ?: userId
                        val routeIdAux = routeSelected.id.takeIf { it.isNotEmpty() } ?: routeId
                        onApplyFilters(userIdAux, routeIdAux, isPaidAux, isCurrentLoanAux)
                        onDismiss()
                    }
                ) {
                    Text("Aplicar")
                }
            }
        }
    }
}

