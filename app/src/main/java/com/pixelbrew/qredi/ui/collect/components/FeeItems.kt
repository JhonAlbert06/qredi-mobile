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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableDoubleStateOf
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

    val amount: String by viewModel.amount.observeAsState("")

    var showDialogCollect by remember { mutableStateOf(false) }
    var totalLoanPaid = 0.0

    loan.fees.forEach { fee ->
        totalLoanPaid += fee.paymentAmount
    }

    FeeLabel(
        "Cobrado:",
        "${viewModel.formatNumber(totalLoanPaid)}$ / ${viewModel.formatNumber(loan.amount)}$"
    )

    var cuote by remember { mutableDoubleStateOf(0.0) }
    cuote = ((loan.interest / 100) * loan.amount) + (loan.amount / loan.feesQuantity)

    LazyColumn {
        items(loan.fees.filter { it.paymentAmount < cuote }) { fee ->

            Card(
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    Spacer(modifier = Modifier.height(8.dp))

                    FeeLabel(
                        "${fee.number}/${loan.feesQuantity}",
                        "${fee.dateModel.day}/${fee.dateModel.month}/${fee.dateModel.year}"
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    FeeLabel(
                        "${
                            viewModel.formatNumber(fee.paymentAmount)
                        }$ / ${
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
                            viewModel.getCuote(fee.paymentAmount);
                            //viewModel.resetAmount()
                        },
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00BCD4),
                            contentColor = Color.Black,
                            disabledContainerColor = Color(0x2C00BCD4),
                            disabledContentColor = Color(0xFF0C0C0C)
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

