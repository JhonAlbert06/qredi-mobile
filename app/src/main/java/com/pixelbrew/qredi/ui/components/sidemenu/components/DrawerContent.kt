package com.pixelbrew.qredi.ui.components.sidemenu.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
        drawerShape = MaterialTheme.shapes.large,
        drawerContentColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.padding(0.dp),
        windowInsets = WindowInsets(0.dp)
            .only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)
    ) {
        Text(
            "Qredi",
            modifier = Modifier
                .padding(16.dp),
            fontWeight = FontWeight.Bold,
            fontSize = MaterialTheme.typography.headlineLarge.fontSize,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.8f)
        )
        HorizontalDivider(modifier = Modifier.padding(bottom = 10.dp))

        drawerItems.forEach { (label, iconRes, screen) ->
            NavigationDrawerItem(
                label = {
                    if (currentScreen::class == screen::class) {
                        Text(
                            label,
                            color = MaterialTheme.colorScheme.inverseOnSurface,
                        )
                    } else {
                        Text(
                            label,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                    }
                },
                selected = currentScreen::class == screen::class,
                icon = {
                    Icon(
                        painter = painterResource(id = iconRes),
                        contentDescription = label,
                        modifier = Modifier.size(32.dp),
                        tint = if (currentScreen::class == screen::class) {
                            MaterialTheme.colorScheme.inverseOnSurface
                        } else {
                            MaterialTheme.colorScheme.onBackground
                        }
                    )
                },
                onClick = { onItemSelected(screen) },
                modifier = Modifier.padding(horizontal = 5.dp, vertical = 5.dp),
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = MaterialTheme.colorScheme.onSurface,
                )
            )
        }
    }
}