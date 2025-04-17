package com.pixelbrew.qredi.loan

import android.widget.Toast
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.pixelbrew.qredi.MainActivity
import com.pixelbrew.qredi.R
import kotlinx.coroutines.delay

@Composable
fun LoanScreen(
    viewModel: LoanViewModel,
    modifier: Modifier = Modifier,
    context: MainActivity,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
    ) {
        Loan(viewModel, modifier, context)
        Spacer(modifier = Modifier.height(8.dp))
    }

    val viewModel: LoanViewModel = viewModel
    val toastMessage by viewModel.toastMessage.observeAsState()

    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            delay(200)
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
fun Loan(
    viewModel: LoanViewModel,
    modifier: Modifier = Modifier,
    context: MainActivity,
) {
    val isLoading by viewModel.isLoading.observeAsState(initial = false)

    Column {
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            HeaderLoan(viewModel, modifier)
            Spacer(modifier = Modifier.height(1.dp))
        }
    }
}

@Composable
fun HeaderLoan(
    viewModel: LoanViewModel,
    modifier: Modifier = Modifier
) {

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "Prestamos",
            style = MaterialTheme.typography.headlineMedium
        )

        Button(
            onClick = {
                // viewModel.showCreationDialog()
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
        ) {
            Text("Nuevo Prestamo")
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.user_plus_solid),
                contentDescription = "Crear Prestamo",
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(20.dp)
            )
        }

    }

}

