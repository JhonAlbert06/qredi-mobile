package com.pixelbrew.qredi.ui.components.sidemenu.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pixelbrew.qredi.R

@Composable
fun DrawerContent(
    onItemSelected: (Screen) -> Unit,
    currentScreen: Screen
) {
    val drawerItems = listOf(
        Triple("Administrar", R.drawable.gear_solid, Screen.Admin),
        Triple("Cobrar", R.drawable.wallet_solid, Screen.Collect),
        Triple("Reimprimir", R.drawable.print_solid, Screen.Reprint),
        Triple("Cliente", R.drawable.user_solid, Screen.Customer),
        Triple("Préstamo", R.drawable.coins_solid, Screen.Loan),
        Triple("Estadísticas", R.drawable.chart_simple_solid, Screen.Statistics),
        Triple("Configuración", R.drawable.gears_solid, Screen.Settings),
    )

    ModalDrawerSheet(
        drawerShape = RoundedCornerShape(16.dp),
        drawerContentColor = MaterialTheme.colorScheme.surface
    ) {
        Text(
            "Qredi",
            modifier = Modifier.padding(20.dp),
            fontWeight = FontWeight.Bold,
            fontSize = MaterialTheme.typography.headlineMedium.fontSize,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        HorizontalDivider(Modifier.padding(bottom = 10.dp))

        drawerItems.forEach { (label, iconRes, screen) ->
            NavigationDrawerItem(
                label = {
                    Text(
                        label,
                        color = if (currentScreen::class == screen::class) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onBackground
                    )
                },
                selected = currentScreen::class == screen::class,
                icon = {
                    Icon(
                        painter = painterResource(id = iconRes),
                        contentDescription = label,
                        modifier = Modifier.size(28.dp),
                        tint = if (currentScreen::class == screen::class) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onBackground
                    )
                },
                onClick = { onItemSelected(screen) },
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
    }
}