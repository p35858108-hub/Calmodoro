package com.example.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel

// Imports de tus pantallas y modelos
import com.example.AppTab
import com.example.CalmodoroBottomNavBar
import com.example.CalendarScreen
import com.example.CalendarViewModel
import com.example.PomodoroScreen
import com.example.PomodoroViewModel
import com.example.ScheduleScreen
import com.example.ScheduleViewModel
import com.example.SettingsScreen
import com.example.SettingsViewModel

@Composable
fun MainScreen(
    calendarViewModel: CalendarViewModel = viewModel(),
    pomodoroViewModel: PomodoroViewModel = viewModel(),
    scheduleViewModel: ScheduleViewModel = viewModel(),
    settingsViewModel: SettingsViewModel = viewModel()
) {
    val context = LocalContext.current
    var currentTab by rememberSaveable { mutableStateOf(AppTab.SCHEDULE) }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { /* Manejar resultado si es necesario */ }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            
            if (!hasPermission) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            CalmodoroBottomNavBar(
                selectedTab = currentTab,
                onTabSelected = { currentTab = it }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "tab_navigation"
            ) { tab ->
                when (tab) {
                    AppTab.CALENDAR -> CalendarScreen(
                        viewModel = calendarViewModel,
                        onNavigateToSchedule = { currentTab = AppTab.SCHEDULE },
                        onStartPomodoroForTask = { task ->
                            pomodoroViewModel.attachTask(task)
                            currentTab = AppTab.POMODORO
                        }
                    )
                    AppTab.POMODORO -> PomodoroScreen(viewModel = pomodoroViewModel)
                    AppTab.SCHEDULE -> ScheduleScreen(
                        viewModel = scheduleViewModel,
                        onStartPomodoroForClass = { title, durationMin ->
                            pomodoroViewModel.setCustomFocusGoal(title, durationMin)
                            currentTab = AppTab.POMODORO
                        }
                    )
                    AppTab.SETTINGS -> SettingsScreen(
                        viewModel = settingsViewModel,
                        onRequestNotificationPermission = {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }
                        }
                    )
                }
            }
        }
    }
}
