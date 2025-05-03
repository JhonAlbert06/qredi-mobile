package com.pixelbrew.qredi.ui.components.sidemenu

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.pixelbrew.qredi.MainActivity
import com.pixelbrew.qredi.data.network.model.UserModel
import com.pixelbrew.qredi.ui.admin.AdminScreen
import com.pixelbrew.qredi.ui.collect.CollectScreen
import com.pixelbrew.qredi.ui.components.sidemenu.components.DrawerContent
import com.pixelbrew.qredi.ui.components.sidemenu.components.Screen
import com.pixelbrew.qredi.ui.components.sidemenu.components.ScreenSaver
import com.pixelbrew.qredi.ui.components.sidemenu.components.TopBar
import com.pixelbrew.qredi.ui.components.sidemenu.components.UserInfoSheet
import com.pixelbrew.qredi.ui.customer.CustomerScreen
import com.pixelbrew.qredi.ui.loan.LoanScreen
import com.pixelbrew.qredi.ui.reprint.ReprintScreen
import com.pixelbrew.qredi.ui.settings.SettingsScreen
import com.pixelbrew.qredi.ui.settings.SettingsViewModel
import com.pixelbrew.qredi.ui.statistics.StatisticsScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@androidx.annotation.RequiresPermission(android.Manifest.permission.BLUETOOTH_CONNECT)
@Composable
fun SideMenu(
    modifier: Modifier = Modifier,
    context: android.content.Context
) {
    val settingsViewModel: SettingsViewModel = hiltViewModel()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var currentScreen by rememberSaveable(stateSaver = ScreenSaver) { mutableStateOf(Screen.Admin) }
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by rememberSaveable { mutableStateOf(false) }
    val user = settingsViewModel.getUser() ?: UserModel()
    val navController = rememberNavController()

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = bottomSheetState
        ) {
            UserInfoSheet(user = user)
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                onItemSelected = { screen ->
                    currentScreen = screen
                    scope.launch { drawerState.close() }
                },
                currentScreen = currentScreen
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopBar(
                    onOpenDrawer = {
                        scope.launch {
                            if (drawerState.isClosed) drawerState.open() else drawerState.close()
                        }
                    },
                    onProfileClick = { scope.launch { showBottomSheet = true } },
                    currentScreen = currentScreen
                )
            }
        ) { padding ->
            when (currentScreen) {
                Screen.Admin -> AdminScreen(modifier = modifier, context = context as MainActivity)
                Screen.Collect -> CollectScreen(
                    modifier = modifier.padding(padding),
                    context = context as MainActivity
                )

                Screen.Customer -> CustomerScreen(
                    modifier = modifier.padding(padding),
                    context = context as MainActivity,
                    navController = navController
                )

                Screen.Loan -> LoanScreen(
                    modifier = modifier.padding(padding),
                    context = context as MainActivity,
                    navController = navController
                )

                Screen.Reprint -> ReprintScreen(
                    modifier = modifier.padding(padding),
                    context = context as MainActivity
                )

                Screen.Statistics -> StatisticsScreen(modifier = modifier.padding(padding))
                Screen.Settings -> SettingsScreen(
                    viewModel = settingsViewModel,
                    modifier = modifier.padding(padding),
                    context = context as MainActivity
                )
            }
        }
    }
}