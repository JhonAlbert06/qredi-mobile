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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.pixelbrew.qredi.R
import com.pixelbrew.qredi.data.network.model.LoanDownloadModel
import com.pixelbrew.qredi.ui.collect.CollectViewModel

@RequiresApi(Build.VERSION_CODES.O)
@RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
@Composable
fun FeeItems(
    viewModel: CollectViewModel,
    loan: LoanDownloadModel
) {
    val updatedLoans by viewModel.downloadedLoans.observeAsState(emptyList())

    // Busca el loan actualizado de la lista LiveData
    val updatedLoan = updatedLoans.find { it.id == loan.id } ?: loan

    val amount: String by viewModel.amount.observeAsState("")

    var showDialogCollect by remember { mutableStateOf(false) }
    val totalLoanPaid = updatedLoan.fees.sumOf { it.paymentAmount }

    FeeLabel(
        "Cobrado:",
        "${viewModel.formatNumber(totalLoanPaid)}$ / ${viewModel.formatNumber(updatedLoan.amount)}$"
    )

    val cuote =
        ((updatedLoan.interest / 100) * updatedLoan.amount) + (updatedLoan.amount / updatedLoan.feesQuantity)

    LazyColumn {
        items(updatedLoan.fees.filter { it.paymentAmount < cuote }) { fee ->

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    Spacer(modifier = Modifier.height(8.dp))

                    FeeLabel(
                        "${fee.number}/${updatedLoan.feesQuantity}",
                        "${fee.date.day}/${fee.date.month}/${fee.date.year}"
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    FeeLabel(
                        "${viewModel.formatNumber(fee.paymentAmount)}$ / ${
                            viewModel.formatNumber(
                                cuote
                            )
                        }$",
                        ""
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            showDialogCollect = true
                            viewModel.setFeeSelected(fee)
                            viewModel.getCuote(fee.paymentAmount)
                        },
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.inversePrimary,
                            contentColor = Color.Black,
                            disabledContainerColor = Color(0x2C00BCD4),
                            disabledContentColor = Color(0xFFD9D3D3)
                        ),
                        enabled = fee.paymentAmount < cuote
                    ) {
                        Text("Cobrar")
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.coins_solid),
                            contentDescription = "",
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .size(20.dp)
                        )
                    }
                }
            }
        }
    }

    DialogCollect(
        showDialog = showDialogCollect,
        onDismiss = { showDialogCollect = false },
        viewModel = viewModel,
        amount
    )
}
