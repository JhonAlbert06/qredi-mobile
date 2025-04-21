package com.pixelbrew.qredi.ui.collect

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.pixelbrew.qredi.MainActivity
import com.pixelbrew.qredi.R
import com.pixelbrew.qredi.data.network.model.LoanDownloadModel
import com.pixelbrew.qredi.ui.collect.components.LoanDetailDialog
import com.pixelbrew.qredi.ui.collect.components.LoanItemCollect
import com.pixelbrew.qredi.ui.collect.components.RouteSelectionDialog
import kotlinx.coroutines.delay

@RequiresApi(Build.VERSION_CODES.O)
@RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
@Composable
fun CollectScreen(
    viewModel: CollectViewModel,
    modifier: Modifier = Modifier,
    context: MainActivity,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
    ) {
        Collect(viewModel, modifier, context)
        Spacer(modifier = Modifier.height(8.dp))
    }

    val viewModel: CollectViewModel = viewModel
    val toastMessage by viewModel.toastMessage.observeAsState()

    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            delay(200)
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
@Composable
fun Collect(
    viewModel: CollectViewModel,
    modifier: Modifier,
    context: MainActivity,
) {
    val loans by viewModel.downloadedLoans.observeAsState(emptyList())

    Column {
        HeaderCollect(viewModel, modifier)

        LoansList(
            loans = loans,
            viewModel = viewModel,
            context = context
        )
    }
}

@Composable
fun HeaderCollect(
    viewModel: CollectViewModel,
    modifier: Modifier = Modifier
) {

    var showDialogRoute by remember { mutableStateOf(false) }

    val routes by viewModel.routes.observeAsState(emptyList())

    val downloadedRoutes by viewModel.downloadedLoans.observeAsState(emptyList())

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "Rutas",
            style = MaterialTheme.typography.headlineMedium
        )

        Button(
            onClick = {
                showDialogRoute = true
                viewModel.getRoutes()
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
            enabled = downloadedRoutes.isEmpty()
        ) {
            Text("Descargar")
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.download_solid),
                contentDescription = "Download Route",
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(20.dp)
            )
        }

    }

    RouteSelectionDialog(
        showDialog = showDialogRoute,
        onDismiss = { showDialogRoute = false },
        routes = routes,
        viewModel = viewModel,
        onRouteSelected = { showDialogRoute = false }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
@Composable
fun LoansList(
    loans: List<LoanDownloadModel>,
    viewModel: CollectViewModel,
    context: MainActivity
) {
    var showDialogLoan by remember { mutableStateOf(false) }
    val loanSelectedState =
        viewModel.downloadLoanSelected.observeAsState(initial = LoanDownloadModel())
    val loanSelected: LoanDownloadModel = loanSelectedState.value
    val isLoading by viewModel.isLoading.observeAsState(initial = false)


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
        LazyColumn {
            items(loans) { loan ->
                Card(
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .clickable {
                            viewModel.setDownloadRouteSelected(loan)
                            showDialogLoan = true
                        }
                ) {
                    LoanItemCollect(loan, viewModel)
                }
            }
        }
    }

    LoanDetailDialog(
        showDialog = showDialogLoan,
        onDismiss = { showDialogLoan = false },
        loan = loanSelected,
        viewModel = viewModel
    )

}





