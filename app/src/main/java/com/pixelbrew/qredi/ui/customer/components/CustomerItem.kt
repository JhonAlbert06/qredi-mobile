package com.pixelbrew.qredi.ui.customer.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.pixelbrew.qredi.R
import com.pixelbrew.qredi.data.network.model.CustomerModelRes

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
            Spacer(modifier = Modifier.height(8.dp))
        }
    }


}