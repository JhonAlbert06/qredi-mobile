package com.pixelbrew.qredi.ui.admin.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.pixelbrew.qredi.R

@Composable
fun HeaderImage() {
    Image(
        painter = painterResource(id = R.drawable.icon_app),
        contentDescription = null,
        modifier = Modifier
            .size(180.dp)
            .clip(CircleShape)
            .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
    )
}