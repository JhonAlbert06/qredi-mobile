package com.pixelbrew.qredi.ui.components.sidemenu

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.unit.dp
import com.pixelbrew.qredi.MainActivity
import com.pixelbrew.qredi.R
import com.pixelbrew.qredi.admin.AdminScreen
import com.pixelbrew.qredi.admin.AdminViewModel
import com.pixelbrew.qredi.collect.CollectScreen
import com.pixelbrew.qredi.collect.CollectViewModel
import com.pixelbrew.qredi.network.api.ApiService
import com.pixelbrew.qredi.reprint.ReprintScreen
import com.pixelbrew.qredi.settings.SettingsScreen
import com.pixelbrew.qredi.statistics.StatisticsScreen
import com.pixelbrew.qredi.ui.components.services.SessionManager
import kotlinx.coroutines.launch

sealed class Screen(val route: String) {
    object Admin : Screen("admin")
    object Collect : Screen("collect")
    object Reprint : Screen("reprint")
    object Statistics : Screen("statistics")
    object Settings : Screen("settings")

    companion object {
        fun fromRoute(route: String): Screen {
            return when (route) {
                "admin" -> Admin
                "collect" -> Collect
                "reprint" -> Reprint
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
                Screen.Admin -> AdminScreen(AdminViewModel(apiService, sessionManager, context))
                Screen.Collect -> CollectScreen(
                    CollectViewModel(apiService, sessionManager, context),
                    modifier = modifier.padding(top = 25.dp),
                )

                Screen.Reprint -> ReprintScreen(modifier = modifier.padding(padding))
                Screen.Statistics -> StatisticsScreen(modifier = modifier.padding(padding))
                Screen.Settings -> SettingsScreen(modifier = modifier.padding(padding))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(onOpenDrawer: () -> Unit) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(0.6f)
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
        title = { Text("Qredi") },
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

    ModalDrawerSheet {
        Text(
            "Qredi",
            modifier = Modifier
                .padding(16.dp),

            )
        HorizontalDivider(modifier = Modifier.padding(bottom = 10.dp))


        NavigationDrawerItem(
            label = { Text("Administrar") },
            selected = currentScreen is Screen.Admin,
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.gear_solid),
                    contentDescription = "Administrar",
                    modifier = Modifier
                        .size(32.dp)
                )
            },
            onClick = { onItemSelected(Screen.Admin) },
            modifier = Modifier.padding(start = 5.dp, end = 5.dp, top = 5.dp, bottom = 5.dp)
        )

        NavigationDrawerItem(
            label = { Text("Cobrar") },
            selected = currentScreen is Screen.Collect,
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.wallet_solid),
                    contentDescription = "Cobrar",
                    modifier = Modifier.size(32.dp)
                )
            },
            onClick = { onItemSelected(Screen.Collect) },
            modifier = Modifier.padding(start = 5.dp, end = 5.dp, top = 5.dp, bottom = 5.dp)
        )

        NavigationDrawerItem(
            label = { Text("Reimprimir") },
            selected = currentScreen is Screen.Reprint,
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.print_solid),
                    contentDescription = "Reimprimir",
                    modifier = Modifier.size(32.dp)
                )
            },
            onClick = { onItemSelected(Screen.Reprint) },
            modifier = Modifier.padding(start = 5.dp, end = 5.dp, top = 5.dp, bottom = 5.dp)
        )

        NavigationDrawerItem(
            label = { Text("Estadísticas") },
            selected = currentScreen is Screen.Statistics,
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.chart_simple_solid),
                    contentDescription = "Estadísticas",
                    modifier = Modifier.size(32.dp)
                )
            },
            onClick = { onItemSelected(Screen.Statistics) },
            modifier = Modifier.padding(start = 5.dp, end = 5.dp, top = 5.dp, bottom = 5.dp)
        )

        NavigationDrawerItem(
            label = { Text("Configuración") },
            selected = currentScreen is Screen.Settings,
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.gears_solid),
                    contentDescription = "Configuración",
                    modifier = Modifier.size(32.dp)
                )
            },
            onClick = { onItemSelected(Screen.Settings) },
            modifier = Modifier.padding(start = 5.dp, end = 5.dp, top = 5.dp, bottom = 5.dp)
        )


    }
}
