package com.pixelbrew.qredi.ui.admin.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun LoginButton(isLoginEnabled: Boolean, onLoginSelected: () -> Unit) {
    Button(
        onClick = onLoginSelected,
        enabled = isLoginEnabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Text("Iniciar sesión")
    }
}
