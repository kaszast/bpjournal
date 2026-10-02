package com.kaszast.bpjournal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.IosShare
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.kaszast.bpjournal.ui.screens.DashboardScreen
import com.kaszast.bpjournal.ui.screens.ExportScreen
import com.kaszast.bpjournal.ui.screens.HistoryScreen
import com.kaszast.bpjournal.ui.screens.SettingsScreen
import com.kaszast.bpjournal.ui.screens.StatisticsScreen
import com.kaszast.bpjournal.ui.theme.BPJournalTheme
import com.kaszast.bpjournal.ui.viewmodel.BloodPressureViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: BloodPressureViewModel by viewModels {
        val app = application as BPJournalApplication
        BloodPressureViewModel.Factory(app.repository, app.healthConnectHelper)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BPJournalTheme {
                MainAppScreen(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppScreen(viewModel: BloodPressureViewModel) {
    var selectedTab by remember { mutableIntStateOf(0) }

    val navItems = listOf(
        Pair(R.string.nav_dashboard, Icons.Default.Dashboard),
        Pair(R.string.nav_history, Icons.Default.History),
        Pair(R.string.nav_statistics, Icons.Default.BarChart),
        Pair(R.string.nav_export, Icons.Default.IosShare),
        Pair(R.string.nav_settings, Icons.Default.Settings)
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                navItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        icon = {
                            Icon(imageVector = item.second, contentDescription = stringResource(item.first))
                        },
                        label = {
                            Text(text = stringResource(item.first), fontSize = 10.sp)
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> DashboardScreen(
                    viewModel = viewModel,
                    onNavigateToHistory = { selectedTab = 1 }
                )
                1 -> HistoryScreen(viewModel = viewModel)
                2 -> StatisticsScreen(viewModel = viewModel)
                3 -> ExportScreen(viewModel = viewModel)
                4 -> SettingsScreen(viewModel = viewModel)
            }
        }
    }
}
