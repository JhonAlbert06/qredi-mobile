package com.pixelbrew.qredi.ui.components.sidemenu

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pixelbrew.qredi.MainActivity
import com.pixelbrew.qredi.R
import com.pixelbrew.qredi.admin.AdminScreen
import com.pixelbrew.qredi.admin.AdminViewModel
import com.pixelbrew.qredi.collect.CollectScreen
import com.pixelbrew.qredi.collect.CollectViewModel
import com.pixelbrew.qredi.customer.CustomerScreen
import com.pixelbrew.qredi.customer.CustomerViewModel
import com.pixelbrew.qredi.data.local.repository.LoanRepository
import com.pixelbrew.qredi.data.network.api.ApiService
import com.pixelbrew.qredi.loan.LoanScreen
import com.pixelbrew.qredi.loan.LoanViewModel
import com.pixelbrew.qredi.reprint.ReprintScreen
import com.pixelbrew.qredi.reprint.ReprintViewModel
import com.pixelbrew.qredi.settings.SettingsScreen
import com.pixelbrew.qredi.settings.SettingsViewModel
import com.pixelbrew.qredi.statistics.StatisticsScreen
import com.pixelbrew.qredi.ui.components.services.SessionManager
import kotlinx.coroutines.launch

sealed class Screen(val route: String) {
    object Admin : Screen("admin")
    object Collect : Screen("collect")
    object Reprint : Screen("reprint")
    object Statistics : Screen("statistics")
    object Settings : Screen("settings")
    object Customer : Screen("customer")
    object Loan : Screen("loan")

    companion object {
        fun fromRoute(route: String): Screen {
            return when (route) {
                "admin" -> Admin
                "collect" -> Collect
                "reprint" -> Reprint
                "customer" -> Customer
                "loan" -> Loan
                "statistics" -> Statistics
                "settings" -> Settings
                else -> Admin // default
            }
        }
    }
}

object ScreenSaver : Saver<Screen, String> {
    override fun restore(value: String): Screen? = Screen.fromRoute(value)
    override fun SaverScope.save(value: Screen): String = value.route
}

@RequiresApi(Build.VERSION_CODES.O)
@androidx.annotation.RequiresPermission(android.Manifest.permission.BLUETOOTH_CONNECT)
@Composable
fun SideMenu(
    modifier: Modifier = Modifier,
    apiService: ApiService,
    sessionManager: SessionManager,
    context: MainActivity
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var currentScreen by rememberSaveable(stateSaver = ScreenSaver) {
        mutableStateOf(Screen.Admin)
    }

    val loanRepository = LoanRepository(context)

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                onItemSelected = { screen ->
                    currentScreen = screen
                    scope.launch {
                        drawerState.close()
                    }
                },
                currentScreen = currentScreen
            )
        },
    ) {
        Scaffold(
            topBar = {
                TopBar(
                    onOpenDrawer = {
                        scope.launch {
                            drawerState.apply {
                                if (isClosed) open() else close()
                            }
                        }
                    }
                )
            }
        ) { padding ->
            when (currentScreen) {

                Screen.Admin -> AdminScreen(
                    AdminViewModel(apiService, sessionManager),
                    modifier = modifier,
                    context = context
                )

                Screen.Collect -> CollectScreen(
                    CollectViewModel(loanRepository, apiService, sessionManager),
                    modifier = modifier.padding(top = 25.dp),
                    context,
                )

                Screen.Customer -> CustomerScreen(
                    CustomerViewModel(apiService, sessionManager),
                    modifier = modifier.padding(top = 25.dp),
                    context,
                )

                Screen.Loan -> LoanScreen(
                    LoanViewModel(loanRepository, apiService, sessionManager),
                    modifier = modifier.padding(top = 25.dp),
                    context,
                )

                Screen.Reprint -> ReprintScreen(
                    ReprintViewModel(loanRepository, apiService, sessionManager),
                    modifier = modifier.padding(top = 25.dp),
                    context
                )

                Screen.Statistics -> StatisticsScreen(modifier = modifier.padding(padding))
                Screen.Settings -> SettingsScreen(
                    SettingsViewModel(sessionManager),
                    modifier = modifier.padding(padding),
                    context = context
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(onOpenDrawer: () -> Unit) {
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
            Text("Qredi")
        },
        actions = {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Menu",
                modifier = Modifier
                    .padding(start = 8.dp, end = 16.dp)
                    .clickable {
                        // Handle account click
                    }

            )
        }
    )
}

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
