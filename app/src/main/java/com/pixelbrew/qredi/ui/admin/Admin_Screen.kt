package com.pixelbrew.qredi.ui.admin

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
import androidx.hilt.navigation.compose.hiltViewModel
import com.pixelbrew.qredi.MainActivity
import com.pixelbrew.qredi.ui.admin.components.EmailField
import com.pixelbrew.qredi.ui.admin.components.ForgotPassword
import com.pixelbrew.qredi.ui.admin.components.HeaderImage
import com.pixelbrew.qredi.ui.admin.components.LoginButton
import com.pixelbrew.qredi.ui.admin.components.PasswordField

@Composable
fun AdminScreen(modifier: Modifier = Modifier, context: MainActivity) {
    val viewModel: AdminViewModel = hiltViewModel()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Login(viewModel)
    }

    val toastEvent by viewModel.toastMessage.observeAsState()
    LaunchedEffect(toastEvent) {
        toastEvent?.getContentIfNotHandled()?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
fun Login(viewModel: AdminViewModel, modifier: Modifier = Modifier) {
    val email by viewModel.email.observeAsState("")
    val password by viewModel.password.observeAsState("")
    val isLoginEnabled by viewModel.isLoginEnabled.observeAsState(false)
    val isLoading by viewModel.isLoading.observeAsState(false)

    if (isLoading) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HeaderImage()
            Spacer(Modifier.height(24.dp))
            EmailField(email) { viewModel.onLoginChange(it, password) }
            Spacer(Modifier.height(12.dp))
            PasswordField(password) { viewModel.onLoginChange(email, it) }
            Spacer(Modifier.height(8.dp))
            ForgotPassword()
            Spacer(Modifier.height(24.dp))
            LoginButton(isLoginEnabled) { viewModel.onLoginSelected() }
        }
    }
}



