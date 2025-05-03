package com.pixelbrew.qredi.ui.collect.components

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.pixelbrew.qredi.R
import com.pixelbrew.qredi.data.network.model.LoanDownloadModel
import com.pixelbrew.qredi.ui.collect.CollectViewModel

@RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FeeItems(viewModel: CollectViewModel, loan: LoanDownloadModel) {
    val updatedLoans by viewModel.downloadedLoans.observeAsState(emptyList())
    val updatedLoan = updatedLoans.find { it.id == loan.id } ?: loan
    val amount by viewModel.amount.observeAsState("")
    val showDialogCollect = remember { mutableStateOf(false) }
    val totalLoanPaid = updatedLoan.fees.sumOf { it.paymentAmount }
    val cuote =
        ((updatedLoan.interest / 100) * updatedLoan.amount) + (updatedLoan.amount / updatedLoan.feesQuantity)

    FeeLabel(
        "Cobrado:",
        "${viewModel.formatNumber(totalLoanPaid)}$ / ${viewModel.formatNumber(updatedLoan.amount)}$"
    )

    LazyColumn {
        items(updatedLoan.fees.filter { it.paymentAmount < cuote }) { fee ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    FeeLabel(
                        "${fee.number}/${updatedLoan.feesQuantity}",
                        "${fee.date.day}/${fee.date.month}/${fee.date.year}"
                    )
                    Spacer(Modifier.height(8.dp))
                    FeeLabel(
                        "${viewModel.formatNumber(fee.paymentAmount)}$ / ${
                            viewModel.formatNumber(
                                cuote
                            )
                        }$",
                        ""
                    )
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = {
                            showDialogCollect.value = true
                            viewModel.setFeeSelected(fee)
                            viewModel.getCuote(fee.paymentAmount)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        enabled = fee.paymentAmount < cuote
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.coins_solid),
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Cobrar")
                    }
                }
            }
        }
    }

    DialogCollect(
        showDialog = showDialogCollect.value,
        onDismiss = { showDialogCollect.value = false },
        viewModel = viewModel,
        amount = amount
    )
}