package com.pixelbrew.qredi.ui.collect.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.pixelbrew.qredi.R
import com.pixelbrew.qredi.data.network.model.LoanDownloadModel
import com.pixelbrew.qredi.ui.collect.CollectViewModel

@Composable
fun LoanItemCollect(loan: LoanDownloadModel, viewModel: CollectViewModel) {
    Column(Modifier
        .fillMaxWidth()
        .padding(8.dp)) {
        LoanLabel(ImageVector.vectorResource(R.drawable.user_solid), loan.customer.name)
        Spacer(Modifier.height(8.dp))
        LoanLabel(
            ImageVector.vectorResource(R.drawable.address_card_solid),
            viewModel.formatCedula(loan.customer.cedula)
        )
        Spacer(Modifier.height(8.dp))
        LoanLabel(
            ImageVector.vectorResource(R.drawable.coins_solid),
            "${viewModel.formatNumber(loan.amount)} $"
        )
        Spacer(Modifier.height(8.dp))
        LoanLabel(
            ImageVector.vectorResource(R.drawable.hashtag_solid),
            "${loan.feesQuantity} cuotas"
        )
        Spacer(Modifier.height(8.dp))
        LoanLabel(
            ImageVector.vectorResource(R.drawable.percent_solid),
            "${viewModel.formatNumber(loan.interest)}% interés"
        )
        Spacer(Modifier.height(8.dp))
        LoanLabel(
            ImageVector.vectorResource(R.drawable.wallet_solid),
            "${viewModel.formatNumber(loan.amount + ((loan.interest / 100) * loan.amount) * loan.feesQuantity)} $"
        )
    }
}