package com.pixelbrew.qredi.ui.loan.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelbrew.qredi.R
import com.pixelbrew.qredi.data.network.model.LoanModelRes
import com.pixelbrew.qredi.ui.collect.components.LoanLabel
import com.pixelbrew.qredi.ui.loan.LoanViewModel

@Composable
fun LoanItem(
    loan: LoanModelRes,
    viewModel: LoanViewModel
) {
    Column(modifier = Modifier.padding(16.dp)) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Tag(
                text = loan.route.name,
                icon = R.drawable.location_dot_solid,
                backgroundColor = Color(0xFF000000),
                contentColor = Color(0xFFFFFFFF)
            )

            Text(
                text = "${loan.date.day}/${loan.date.month}/${loan.date.year}",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.user_solid),
                contentDescription = "Loan Icon",
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = "${loan.customer.firstName} ${loan.customer.lastName}",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(start = 8.dp),
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.address_card_solid),
                contentDescription = "Loan Icon",
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = viewModel.formatCedula(loan.customer.cedula),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(start = 8.dp),
                fontSize = 18.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        LoanLabel(
            icon = ImageVector.vectorResource(id = R.drawable.coins_solid),
            text = "${viewModel.formatNumber(loan.amount)} $"
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.End
        ) {
            // Estado de pago
            if (loan.loanIsPaid) {
                Tag(
                    text = "Finalizado",
                    icon = R.drawable.check_solid,
                    backgroundColor = Color(0xFFEEFFFA),
                    contentColor = Color(0xFF00D455)
                )
            } else {
                Tag(
                    text = "No Finalizado",
                    icon = R.drawable.xmark_solid,
                    backgroundColor = Color(0xFFFFEDED),
                    contentColor = Color(0xFFEF4444)
                )
            }

            Spacer(modifier = Modifier.width(3.dp))

            // Estado de préstamo actual
            if (loan.isCurrentLoan) {
                Tag(
                    text = "Actual",
                    icon = R.drawable.check_to_slot_solid,
                    backgroundColor = Color(0xFFEFFBF5),
                    contentColor = Color(0xFF00D455)
                )
            }

            Spacer(modifier = Modifier.width(3.dp))
        }
    }
}