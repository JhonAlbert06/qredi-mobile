package com.pixelbrew.qredi.ui.reprint


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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pixelbrew.qredi.MainActivity
import com.pixelbrew.qredi.R
import java.time.LocalDateTime

@RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ReprintScreen(
    modifier: Modifier = Modifier,
    context: MainActivity
) {
    val viewModel: ReprintViewModel = hiltViewModel()
    val toastEvent by viewModel.toastMessage.observeAsState()

    LaunchedEffect(toastEvent) {
        toastEvent?.getContentIfNotHandled()?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Reprint(viewModel)
    }
}

@RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Reprint(
    viewModel: ReprintViewModel
) {
    val newFees by viewModel.newFees.observeAsState(emptyList())
    val showReprintDialog by viewModel.showReprintDialog.observeAsState(false)

    val sortedFees = newFees.sortedByDescending { fee ->
        LocalDateTime.of(
            fee.dateYear, fee.dateMonth, fee.dateDay,
            fee.dateHour, fee.dateMinute, fee.dateSecond
        )
    }

    Column {
        HeaderReprint(viewModel)

        LazyColumn {
            items(sortedFees) { fee ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable {
                            viewModel.setShowReprintDialog(true)
                            viewModel.setFeeSelected(fee)
                        },
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(2.dp),
                    colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            "Cuota #${fee.number}",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                viewModel.formatDate(fee.dateDay, fee.dateMonth, fee.dateYear),
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                viewModel.formatTime(fee.dateHour, fee.dateMinute, fee.dateSecond),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        Spacer(Modifier.height(8.dp))

                        Text(
                            fee.clientName,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(Modifier.height(8.dp))

                        Text(
                            "Monto: $${fee.paymentAmount}",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }

    if (showReprintDialog) {
        RePrintDialogConfirmation(
            onDismiss = { viewModel.setShowReprintDialog(false) },
            onConfirm = {
                viewModel.setShowReprintDialog(false)
                viewModel.printCollect()
            }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
@Composable
fun HeaderReprint(viewModel: ReprintViewModel) {
    val showUploadDialog by viewModel.showUploadDialog.observeAsState(false)
    val fees by viewModel.newFees.observeAsState(emptyList())

    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "Reimpresiones",
            style = MaterialTheme.typography.titleLarge
        )

        Button(
            onClick = { viewModel.setShowUploadDialog(true) },
            enabled = fees.isNotEmpty(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.cloud_arrow_up_solid),
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text("Sincronizar")
        }
    }

    if (showUploadDialog) {
        UploadDialogConfirmation(
            onDismiss = { viewModel.setShowUploadDialog(false) },
            onConfirm = {
                viewModel.uploadFees()
                viewModel.setShowUploadDialog(false)
            }
        )
    }
}

@Composable
fun RePrintDialogConfirmation(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        title = { Text("¿Seguro que quieres reimprimir la cuota?") },
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onConfirm) { Text("Sí") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("No") }
        }
    )
}

@Composable
fun UploadDialogConfirmation(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        title = { Text("¿Seguro que quieres subir la data?") },
        text = { Text("Al subir la data, se eliminará la información local") },
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onConfirm) { Text("Sí") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("No") }
        }
    )
}