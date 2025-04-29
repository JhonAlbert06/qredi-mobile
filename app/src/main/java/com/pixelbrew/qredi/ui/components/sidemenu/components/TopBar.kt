package com.pixelbrew.qredi.ui.components.sidemenu.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pixelbrew.qredi.R
import kotlinx.coroutines.Job

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(onOpenDrawer: () -> Unit, currentScreen: Screen, onProfileClick: () -> Job) {

    val drawerItems = listOf(
        Triple("Administrar", R.drawable.gear_solid, Screen.Admin),
        Triple("Cobrar", R.drawable.wallet_solid, Screen.Collect),
        Triple("Reimprimir", R.drawable.print_solid, Screen.Reprint),
        Triple("Cliente", R.drawable.user_solid, Screen.Customer),
        Triple("Préstamo", R.drawable.coins_solid, Screen.Loan),
        Triple("Estadísticas", R.drawable.chart_simple_solid, Screen.Statistics),
        Triple("Configuración", R.drawable.gears_solid, Screen.Settings),
    )


    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(0.6f),
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            actionIconContentColor = MaterialTheme.colorScheme.onSurface,
            navigationIconContentColor = MaterialTheme.colorScheme.onSurface
        ),
        navigationIcon = {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Menu",
                modifier = Modifier
                    .padding(start = 16.dp, end = 8.dp)
                    .clickable {
                        onOpenDrawer()
                    }
            )
        },
        title = {
            // Screen name or logo can be added here
            Text(
                text = drawerItems.first { it.third == currentScreen }.first,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        actions = {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Menu",
                modifier = Modifier
                    .padding(start = 8.dp, end = 16.dp)
                    .clickable {
                        onProfileClick()
                    }

            )
        }
    )
}
