package com.pixelbrew.qredi.ui.customer.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
fun CustomerItem(customer: CustomerModelRes, onSelect: () -> Unit) {
    Card(
        onClick = onSelect,
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            InfoRow(R.drawable.user_solid, "${customer.firstName} ${customer.lastName}")
            InfoRow(R.drawable.address_card_solid, customer.cedula)
            InfoRow(R.drawable.phone_solid, customer.phone)
            InfoRow(R.drawable.comment_solid, customer.reference)
            InfoRow(R.drawable.location_dot_solid, customer.address)
        }
    }
}

@Composable
fun InfoRow(iconRes: Int, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = ImageVector.vectorResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium)
    }
    Spacer(Modifier.height(4.dp))
}