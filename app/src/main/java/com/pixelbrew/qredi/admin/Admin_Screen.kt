package com.pixelbrew.qredi.admin

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pixelbrew.qredi.MainActivity
import com.pixelbrew.qredi.admin.components.EmailField
import com.pixelbrew.qredi.admin.components.ForgotPassword
import com.pixelbrew.qredi.admin.components.HeaderImage
import com.pixelbrew.qredi.admin.components.LoginButton
import com.pixelbrew.qredi.admin.components.PasswordField
import kotlinx.coroutines.delay

@Composable
fun AdminScreen(viewModel: AdminViewModel, modifier: Modifier = Modifier, context: MainActivity) {
    Box(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.background,
                shape = MaterialTheme.shapes.large
            )
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Login(viewModel, modifier.align(Alignment.Center))
    }

    val viewModel: AdminViewModel = viewModel
    val toastMessage by viewModel.toastMessage.observeAsState()

    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            delay(200)
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
fun Login(viewModel: AdminViewModel, modifier: Modifier = Modifier) {

    val email: String by viewModel.email.observeAsState(initial = "")
    val password: String by viewModel.password.observeAsState(initial = "")
    val isLoginEnabled: Boolean by viewModel.isLoginEnabled.observeAsState(initial = false)
    val isLoading: Boolean by viewModel.isLoading.observeAsState(initial = false)

    if (isLoading) {
        // Show loading indicator
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(Modifier.align(Alignment.Center))
        }
    } else {

        Column(
            modifier = modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()) // <- Permite scroll si se necesita
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HeaderImage(modifier)
            Spacer(modifier = Modifier.height(16.dp))
            EmailField(email) { viewModel.onLoginChange(it, password) }
            Spacer(modifier = Modifier.height(4.dp))
            PasswordField(password) { viewModel.onLoginChange(email, it) }
            Spacer(modifier = Modifier.height(8.dp))
            ForgotPassword(modifier.align(Alignment.End))
            Spacer(modifier = Modifier.height(16.dp))
            LoginButton(modifier, isLoginEnabled) {
                viewModel.onLoginSelected()
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}





