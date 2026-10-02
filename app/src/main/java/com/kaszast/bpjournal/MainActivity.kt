package com.kaszast.bpjournal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.IosShare
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kaszast.bpjournal.data.AppThemeMode
import com.kaszast.bpjournal.ui.screens.AddEditEntryDialog
import com.kaszast.bpjournal.ui.screens.DashboardScreen
import com.kaszast.bpjournal.ui.screens.ExportScreen
import com.kaszast.bpjournal.ui.screens.HistoryScreen
import com.kaszast.bpjournal.ui.screens.SettingsScreen
import com.kaszast.bpjournal.ui.screens.StatisticsScreen
import android.content.Context
import android.content.ContextWrapper
import android.content.res.AssetManager
import android.content.res.Resources
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import com.kaszast.bpjournal.ui.theme.AccentTeal
import com.kaszast.bpjournal.ui.theme.BPJournalTheme
import com.kaszast.bpjournal.ui.viewmodel.BloodPressureViewModel
import com.kaszast.bpjournal.util.LocaleHelper

class MainActivity : ComponentActivity() {

    private val viewModel: BloodPressureViewModel by viewModels {
        val app = application as BPJournalApplication
        BloodPressureViewModel.Factory(app.repository, app.healthConnectHelper, app.userSettingsManager)
    }

    override fun attachBaseContext(newBase: Context) {
        val prefs = newBase.getSharedPreferences("bpjournal_settings", Context.MODE_PRIVATE)
        val lang = prefs.getString("pref_app_language", "system") ?: "system"
        val context = LocaleHelper.applyLanguage(newBase, lang)
        super.attachBaseContext(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            val userSettings by viewModel.userSettings.collectAsState()
            val localizedContext = remember(userSettings.appLanguage) {
                val configContext = LocaleHelper.applyLanguage(this@MainActivity, userSettings.appLanguage)
                LocalizedActivityContext(this@MainActivity, configContext)
            }
            val isDark = when (userSettings.themeMode) {
                AppThemeMode.SYSTEM -> isSystemInDarkTheme()
                AppThemeMode.LIGHT -> false
                AppThemeMode.DARK -> true
            }
            CompositionLocalProvider(
                LocalConfiguration provides localizedContext.resources.configuration,
                LocalContext provides localizedContext,
                LocalActivityResultRegistryOwner provides this@MainActivity
            ) {
                BPJournalTheme(darkTheme = isDark) {
                    MainAppScreen(viewModel = viewModel)
                }
            }
        }
    }
}

/**
 * ContextWrapper wrapping the [ComponentActivity] to supply localized resources and configuration
 * while retaining all Activity capabilities (window tokens, themes, [ActivityResultRegistryOwner]).
 */
private class LocalizedActivityContext(
    activity: ComponentActivity,
    private val configContext: Context
) : ContextWrapper(activity), ActivityResultRegistryOwner by activity {
    override fun getResources(): Resources = configContext.resources
    override fun getAssets(): AssetManager = configContext.assets
}

@Composable
fun MainAppScreen(viewModel: BloodPressureViewModel) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var showAddDialog by remember { mutableStateOf(false) }
    val userSettings by viewModel.userSettings.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            if (selectedTab == 0 || selectedTab == 1) {
                androidx.compose.material3.FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = AccentTeal,
                    contentColor = Color.White,
                    shape = CircleShape,
                    modifier = Modifier.size(54.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.record_new),
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .padding(horizontal = 4.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val navTabs = listOf(
                            Triple(0, Icons.Default.Dashboard, R.string.nav_dashboard),
                            Triple(1, Icons.Default.History, R.string.nav_history),
                            Triple(2, Icons.Default.BarChart, R.string.nav_statistics),
                            Triple(3, Icons.Default.IosShare, R.string.nav_export),
                            Triple(4, Icons.Default.Settings, R.string.nav_settings)
                        )

                        navTabs.forEach { (index, icon, labelRes) ->
                            IconButton(
                                onClick = { selectedTab = index },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = stringResource(labelRes),
                                    tint = if (selectedTab == index) AccentTeal else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
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
            when (selectedTab) {
                0 -> DashboardScreen(
                    viewModel = viewModel,
                    onNavigateToHistory = { selectedTab = 1 },
                    onNavigateToSettings = { selectedTab = 4 },
                    onAddNewEntry = { showAddDialog = true }
                )
                1 -> HistoryScreen(viewModel = viewModel)
                2 -> StatisticsScreen(viewModel = viewModel)
                3 -> ExportScreen(viewModel = viewModel)
                4 -> SettingsScreen(viewModel = viewModel)
            }
        }
    }

    if (showAddDialog) {
        AddEditEntryDialog(
            defaultArm = userSettings.defaultArm,
            defaultPosition = userSettings.defaultPosition,
            onDismiss = { showAddDialog = false },
            onSave = { entry ->
                viewModel.addEntry(entry)
                showAddDialog = false
            }
        )
    }
}
