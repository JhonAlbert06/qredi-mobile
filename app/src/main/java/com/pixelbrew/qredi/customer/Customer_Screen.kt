package com.pixelbrew.qredi.customer

import android.widget.Toast
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.pixelbrew.qredi.MainActivity
import com.pixelbrew.qredi.R
import com.pixelbrew.qredi.customer.components.CreateCustomerDialog
import com.pixelbrew.qredi.data.network.model.CustomerModelRes
import kotlinx.coroutines.delay

@Composable
fun CustomerScreen(
    viewModel: CustomerViewModel,
    modifier: Modifier = Modifier,
    context: MainActivity,
) {

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
    ) {
        Customer(viewModel, modifier, context)
        Spacer(modifier = Modifier.height(8.dp))
    }

    val viewModel: CustomerViewModel = viewModel
    val toastMessage by viewModel.toastMessage.observeAsState()

    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            delay(200)
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

}


@Composable
fun Customer(
    viewModel: CustomerViewModel,
    modifier: Modifier = Modifier,
    context: MainActivity,
) {

    val customerList by viewModel.customerList.observeAsState(initial = emptyList())
    val isLoading by viewModel.isLoading.observeAsState(initial = false)

    Column {
        HeaderCustomer(
            viewModel = viewModel,
            modifier = modifier
        )

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
                    .fillMaxSize()
                    .padding(8.dp),
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

@Composable
fun CustomerItem(
    customer: CustomerModelRes,
    modifier: Modifier
) {

    Card {
        Column {
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, bottom = 2.dp, top = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.user_solid),
                    contentDescription = "Cliente",
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "${customer.firstName} ${customer.lastName}",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, bottom = 2.dp, top = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.address_card_solid),
                    contentDescription = "Cedula",
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = customer.cedula,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, bottom = 2.dp, top = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.phone_solid),
                    contentDescription = "Telefono",
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = customer.phone,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, bottom = 2.dp, top = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.location_dot_solid),
                    contentDescription = "Direccion",
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = customer.address,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, bottom = 2.dp, top = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.comment_solid),
                    contentDescription = "Referencia",
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = customer.reference,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }


}


@Composable
fun HeaderCustomer(
    viewModel: CustomerViewModel,
    modifier: Modifier = Modifier
) {

    val showCreationDialog by viewModel.showCreationDialog.observeAsState(initial = false)

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "Clientes",
            style = MaterialTheme.typography.headlineMedium
        )

        Button(
            onClick = {
                viewModel.showCreationDialog()
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
            Text("Crear Cliente")
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.user_plus_solid),
                contentDescription = "Crear Cliente",
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(20.dp)
            )
        }

    }

    if (showCreationDialog) {
        CreateCustomerDialog(
            onDismiss = {
                viewModel.hideCreationDialog()
            },
            onSubmit = { cedula, names, lastNames, address, phone, reference ->
                viewModel.createCustomer(
                    cedula = cedula,
                    names = names,
                    lastNames = lastNames,
                    address = address,
                    phone = phone,
                    reference = reference,
                )
            }
        )
    }
}

