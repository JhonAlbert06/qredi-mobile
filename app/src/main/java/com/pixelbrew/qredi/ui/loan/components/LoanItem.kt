package com.pixelbrew.qredi.ui.loan.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
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

        // Sección de la fecha
        LoanLabel(
            icon = ImageVector.vectorResource(id = R.drawable.calendar_solid),
            text = "${loan.date.day}/${loan.date.month}/${loan.date.year}",

            )

        Spacer(modifier = Modifier.height(8.dp))

        // Información del cliente: nombre y cédula
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            LoanLabel(
                icon = ImageVector.vectorResource(id = R.drawable.user_solid),
                text = "${loan.customer.firstName} ${loan.customer.lastName}"
            )
            LoanLabel(
                icon = ImageVector.vectorResource(id = R.drawable.address_card_solid),
                text = viewModel.formatCedula(loan.customer.cedula)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Monto y cuotas
        LoanLabel(
            icon = ImageVector.vectorResource(id = R.drawable.coins_solid),
            text = "${viewModel.formatNumber(loan.amount)} $",

            )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            LoanLabel(
                icon = ImageVector.vectorResource(id = R.drawable.hashtag_solid),
                text = "${loan.feesQuantity} Cuotas"
            )
            LoanLabel(
                icon = ImageVector.vectorResource(id = R.drawable.percent_solid),
                text = "${viewModel.formatNumber(loan.interest)} Interés"
            )

        }

        Spacer(modifier = Modifier.height(16.dp))

        LoanLabel(
            icon = ImageVector.vectorResource(id = R.drawable.percent_solid),
            text = "0.00 Mora"
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            LoanLabel(
                icon = ImageVector.vectorResource(id = R.drawable.wallet_solid),
                text = "0.00 $ Mora"
            )

            LoanLabel(
                icon = ImageVector.vectorResource(id = R.drawable.wallet_solid),
                text = "${viewModel.formatNumber(loan.amount + ((loan.interest / 100) * loan.amount) * loan.feesQuantity)} $"
            )
        }


        Spacer(modifier = Modifier.height(16.dp))

        // Tags de estado (Pagado, No Pagado, Actual)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Estado de pago
            if (loan.loanIsPaid) {
                Tag(
                    text = "Pagado",
                    icon = R.drawable.check_solid,
                    backgroundColor = Color(0xFFEEFFFA),
                    contentColor = Color(0xFF00D455)
                )
            } else {
                Tag(
                    text = "No Pagado",
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

            // Ruta del préstamo
            Tag(
                text = loan.route.name,
                icon = R.drawable.location_dot_solid,
                backgroundColor = Color(0xFF000000),
                contentColor = Color(0xFFFFFFFF)
            )
        }
    }
}