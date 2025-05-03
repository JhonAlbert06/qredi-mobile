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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pixelbrew.qredi.MainActivity
import com.pixelbrew.qredi.R
import com.pixelbrew.qredi.data.network.model.LoanDownloadModel
import com.pixelbrew.qredi.ui.collect.components.LoanDetailCollectBottomSheet
import com.pixelbrew.qredi.ui.collect.components.LoanItemCollect
import com.pixelbrew.qredi.ui.collect.components.RouteSelectionDialog

@RequiresApi(Build.VERSION_CODES.O)
@RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
@Composable
fun CollectScreen(
    modifier: Modifier = Modifier,
    context: MainActivity,
) {
    val viewModel: CollectViewModel = hiltViewModel()

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Collect(viewModel)
    }

    val toastEvent by viewModel.toastMessage.observeAsState()

    LaunchedEffect(toastEvent) {
        toastEvent?.getContentIfNotHandled()?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
@Composable
fun Collect(
    viewModel: CollectViewModel
) {
    val loans by viewModel.downloadedLoans.observeAsState(emptyList())

    Column {
        HeaderCollect(viewModel)
        LoansList(loans, viewModel)
    }
}

@Composable
fun HeaderCollect(viewModel: CollectViewModel) {
    var showDialogRoute by remember { mutableStateOf(false) }
    val routes by viewModel.routes.observeAsState(emptyList())
    val downloadedRoutes by viewModel.downloadedLoans.observeAsState(emptyList())

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "Rutas",
            style = MaterialTheme.typography.titleLarge
        )

        Button(
            onClick = {
                showDialogRoute = true
                viewModel.getRoutes()
            },
            shape = RoundedCornerShape(12.dp),
            enabled = downloadedRoutes.isEmpty()
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.download_solid),
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text("Descargar")
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
fun LoansList(loans: List<LoanDownloadModel>, viewModel: CollectViewModel) {
    val showDialogLoan = remember { mutableStateOf(false) }
    val loanSelected by viewModel.downloadLoanSelected.observeAsState(LoanDownloadModel())
    val isLoading by viewModel.isLoading.observeAsState(false)

    if (isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn {
            items(loans) { loan ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable {
                            viewModel.setDownloadRouteSelected(loan)
                            showDialogLoan.value = true
                        },
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    LoanItemCollect(loan, viewModel)
                }
            }
        }
    }

    LoanDetailCollectBottomSheet(
        showBottomSheet = showDialogLoan.value,
        onDismiss = { showDialogLoan.value = false },
        loan = loanSelected,
        viewModel = viewModel
    )
}




