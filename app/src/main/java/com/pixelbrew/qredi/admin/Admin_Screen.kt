package com.pixelbrew.qredi.admin

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelbrew.qredi.R
import kotlinx.coroutines.launch

@Composable
fun AdminScreen(viewModel: AdminViewModel, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Login(viewModel, modifier.align(Alignment.Center))
    }
}

@Composable
fun Login(viewModel: AdminViewModel, modifier: Modifier = Modifier) {

    val email: String by viewModel.email.observeAsState(initial = "")
    val password: String by viewModel.password.observeAsState(initial = "")
    val isLoginEnabled: Boolean by viewModel.isLoginEnabled.observeAsState(initial = false)
    val isLoading: Boolean by viewModel.isLoading.observeAsState(initial = false)

    val coroutineScope = rememberCoroutineScope()

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
            modifier = modifier.fillMaxWidth(),
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
                coroutineScope.launch {
                    viewModel.onLoginSelected()
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun LoginButton(modifier: Modifier, isLoginEnabled: Boolean, onLoginSelected: () -> Unit) {
    Button(
        onClick = { onLoginSelected() },
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = MaterialTheme.shapes.large,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF00BCD4),
            contentColor = Color.Black,
            disabledContainerColor = Color(0x2C00BCD4),
            disabledContentColor = Color(0xFF0C0C0C)
        ),
        enabled = isLoginEnabled

    ) {
        Text(
            text = "Iniciar sesión",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White
        )
    }

}

@Composable
fun ForgotPassword(modifier: Modifier) {
    Text(
        text = "¿Olvidaste tu contraseña?",
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier
            .clickable {}
            .padding(8.dp),
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp
    )
}

@Composable
fun PasswordField(password: String, onValueChange: (String) -> Unit) {
    TextField(
        value = password,
        onValueChange = { onValueChange(it) },
        modifier = Modifier.fillMaxWidth(),
        placeholder = {
            Text(
                text = "Contraseña",
                style = MaterialTheme.typography.bodyLarge
            )
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password
        ),
        singleLine = true,
        maxLines = 1,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        ),

        )
}

@Composable
fun EmailField(email: String, onValueChange: (String) -> Unit) {
    TextField(
        value = email,
        onValueChange = { onValueChange(it) },
        modifier = Modifier.fillMaxWidth(),
        placeholder = {
            Text(
                text = "Usuario",
                style = MaterialTheme.typography.bodyLarge
            )
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next
        ),
        singleLine = true,
        maxLines = 1,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        )
    )
}

@Composable
fun HeaderImage(modifier: Modifier) {
    Image(
        painter = painterResource(id = R.drawable.icon_app),
        contentDescription = "Icono de la aplicación",
        modifier = modifier
            .size(280.dp)
            .clip(CircleShape)

    )
}
