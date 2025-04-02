package com.pixelbrew.qredi.reprint


import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pixelbrew.qredi.MainActivity
import com.pixelbrew.qredi.R
import java.time.LocalDateTime

@androidx.annotation.RequiresPermission(android.Manifest.permission.BLUETOOTH_CONNECT)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ReprintScreen(
    viewModel: ReprintViewModel,
    modifier: Modifier = Modifier,
    context: MainActivity
) {

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
    ) {
        Reprint(
            viewModel = viewModel,
            modifier = modifier,
            context = context
        )
    }

    val viewModel: ReprintViewModel = viewModel
    val toastMessage by viewModel.toastMessage.observeAsState()

    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }
}

@androidx.annotation.RequiresPermission(android.Manifest.permission.BLUETOOTH_CONNECT)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Reprint(
    viewModel: ReprintViewModel,
    modifier: Modifier = Modifier,
    context: MainActivity
) {

    val newFees by viewModel.newFees.observeAsState(emptyList())
    val showReprintDialog by viewModel.showReprintDialog.observeAsState(false)

    val sortedFees = newFees.sortedByDescending { fee ->
        LocalDateTime.of(
            fee.dateYear,
            fee.dateMonth,
            fee.dateDay,
            fee.dateHour,
            fee.dateMinute,
            fee.dateSecond
        )
    }

    Column {

        HeaderReprint(
            viewModel = viewModel,
            modifier = modifier
        )

        LazyColumn {
            items(sortedFees) { fee ->
                Card(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                        .clickable {
                            viewModel.setShowReprintDialog(true)
                            viewModel.setFeeSelected(fee)
                        }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {

                        Text(
                            "Cuota #${fee.number}",
                            style = MaterialTheme.typography.titleLarge
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                viewModel.formatDate(fee.dateDay, fee.dateMonth, fee.dateYear),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                viewModel.formatTime(fee.dateHour, fee.dateMinute, fee.dateSecond),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween

                        ) {
                            Text(
                                fee.clientName,
                                style = MaterialTheme.typography.titleLarge
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Monto: $${fee.paymentAmount}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

    }

    if (showReprintDialog) {
        RePrintDialogConfirmation(
            onDismiss = {
                viewModel.setShowReprintDialog(false)
            },
            onConfirm = {
                viewModel.setShowReprintDialog(false)
                viewModel.printCollect(context)
            }
        )
    }
}

@Composable
fun HeaderReprint(
    viewModel: ReprintViewModel,
    modifier: Modifier
) {

    val showUploadDialog by viewModel.showUploadDialog.observeAsState(false)

    Row(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            "Reimprimir",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )

        Button(
            onClick = {
                viewModel.setShowUploadDialog(true)
            },
            modifier = Modifier.padding(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Subir Data")
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.cloud_arrow_up_solid),
                contentDescription = "Download Route",
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(20.dp)
            )
        }
    }

    if (showUploadDialog) {
        UploadDialogConfirmation(
            onDismiss = {
                viewModel.setShowUploadDialog(false)
            },
            onConfirm = {
                viewModel.uploadFees()
                viewModel.setShowUploadDialog(false)
            }
        )
    }
}

@Composable
fun RePrintDialogConfirmation(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        title = {
            Text(text = "Seguro que quieres reimprimir la cuota?")
        },
        onDismissRequest = {
            onDismiss()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm()
                }
            ) {
                Text("SI")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismiss()
                }
            ) {
                Text("NO")
            }
        }
    )
}


@Composable
fun UploadDialogConfirmation(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        title = {
            Text(text = "Seguro que quieres subir la data?")
        },
        text = {
            Text(text = "Al subir la data, se eliminará la información local")
        },
        onDismissRequest = {
            onDismiss()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm()
                }
            ) {
                Text("SI")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismiss()
                }
            ) {
                Text("NO")
            }
        }
    )

}