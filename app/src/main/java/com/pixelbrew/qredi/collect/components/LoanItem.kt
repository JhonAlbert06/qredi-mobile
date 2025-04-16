package com.pixelbrew.qredi.collect.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.pixelbrew.qredi.R
import com.pixelbrew.qredi.collect.CollectViewModel
import com.pixelbrew.qredi.data.network.model.LoanDownloadModel

@Composable
fun LoanItem(
    loan: LoanDownloadModel,
    viewModel: CollectViewModel
) {

    Column(modifier = Modifier.padding(16.dp)) {
        LoanLabel(
            icon = ImageVector.vectorResource(id = R.drawable.user_solid),
            text = loan.customer.name
        )
        Spacer(modifier = Modifier.height(8.dp))

        LoanLabel(
            icon = ImageVector.vectorResource(id = R.drawable.address_card_solid),
            text = viewModel.formatCedula(loan.customer.cedula)
        )
        Spacer(modifier = Modifier.height(8.dp))

        LoanLabel(
            icon = ImageVector.vectorResource(id = R.drawable.coins_solid),
            text = "${viewModel.formatNumber(loan.amount)} $"
        )
        Spacer(modifier = Modifier.height(8.dp))

        LoanLabel(
            icon = ImageVector.vectorResource(id = R.drawable.hashtag_solid),
            text = "${loan.feesQuantity} cuotas"
        )
        Spacer(modifier = Modifier.height(8.dp))

        LoanLabel(
            icon = ImageVector.vectorResource(id = R.drawable.percent_solid),
            text = "${viewModel.formatNumber(loan.interest)} interes"
        )
        Spacer(modifier = Modifier.height(8.dp))

        LoanLabel(
            icon = ImageVector.vectorResource(id = R.drawable.wallet_solid),
            text = "${viewModel.formatNumber(loan.amount + ((loan.interest / 100) * loan.amount) * loan.feesQuantity)} $"
        )

    }

}