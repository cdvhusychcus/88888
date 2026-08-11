package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.VpnDatabase
import com.example.data.repository.VpnRepository
import com.example.ui.screens.ConnectionLogsScreen
import com.example.ui.screens.MainDashboardScreen
import com.example.ui.screens.SecuritySettingsScreen
import com.example.ui.screens.ServerListScreen
import com.example.ui.screens.SpeedTestScreen
import com.example.ui.theme.CyberCard
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NetShieldVpnTheme
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.viewmodel.VpnViewModel
import com.example.ui.viewmodel.VpnViewModelFactory

sealed class Screen(val route: String, val title: String, val activeIcon: ImageVector, val inactiveIcon: ImageVector) {
    object Dashboard : Screen("dashboard", "Connect", Icons.Filled.Shield, Icons.Outlined.Shield)
    object Servers : Screen("servers", "Locations", Icons.Filled.Public, Icons.Outlined.Public)
    object SpeedTest : Screen("speed_test", "Speed Test", Icons.Filled.Speed, Icons.Outlined.Speed)
    object Security : Screen("security", "Security", Icons.Filled.Security, Icons.Outlined.Security)
    object ConnectionLogs : Screen("logs", "Logs", Icons.Filled.Shield, Icons.Outlined.Shield)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            NetShieldVpnTheme {
                val context = LocalContext.current
                val database = remember { VpnDatabase.getDatabase(context) }
                val repository = remember { VpnRepository(database.vpnDao()) }
                val vpnViewModel: VpnViewModel = viewModel(
                    factory = VpnViewModelFactory(repository)
                )

                VpnAppMain(viewModel = vpnViewModel)
            }
        }
    }
}

@Composable
fun VpnAppMain(viewModel: VpnViewModel) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Dashboard) }

    val mainNavigationItems = listOf(
        Screen.Dashboard,
        Screen.Servers,
        Screen.SpeedTest,
        Screen.Security
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        bottomBar = {
            if (currentScreen != Screen.ConnectionLogs) {
                NavigationBar(
                    containerColor = CyberCard,
                    tonalElevation = 8.dp,
                    modifier = Modifier.navigationBarsPadding()
                ) {
                    mainNavigationItems.forEach { screen ->
                        val isSelected = currentScreen == screen
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { currentScreen = screen },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) screen.activeIcon else screen.inactiveIcon,
                                    contentDescription = screen.title
                                )
                            },
                            label = {
                                Text(
                                    text = screen.title,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = NeonCyan,
                                selectedTextColor = NeonCyan,
                                indicatorColor = NeonCyan.copy(alpha = 0.15f),
                                unselectedIconColor = TextMuted,
                                unselectedTextColor = TextMuted
                            ),
                            modifier = Modifier.testTag("nav_item_${screen.route}")
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                Screen.Dashboard -> MainDashboardScreen(
                    viewModel = viewModel,
                    onNavigateToServers = { currentScreen = Screen.Servers },
                    onNavigateToSettings = { currentScreen = Screen.Security }
                )
                Screen.Servers -> ServerListScreen(
                    viewModel = viewModel,
                    onServerSelected = {
                        currentScreen = Screen.Dashboard
                    }
                )
                Screen.SpeedTest -> SpeedTestScreen(
                    viewModel = viewModel
                )
                Screen.Security -> SecuritySettingsScreen(
                    viewModel = viewModel,
                    onNavigateToLogs = { currentScreen = Screen.ConnectionLogs }
                )
                Screen.ConnectionLogs -> ConnectionLogsScreen(
                    viewModel = viewModel,
                    onBackClick = { currentScreen = Screen.Security }
                )
            }
        }
    }
}

