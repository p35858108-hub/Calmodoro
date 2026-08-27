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
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.entity.TaskEntity
import com.example.ui.calendar.CalendarScreen
import com.example.ui.calendar.CalendarViewModel
import com.example.ui.components.AppTab
import com.example.ui.components.CalmodoroBottomNavBar
import com.example.ui.pomodoro.PomodoroScreen
import com.example.ui.pomodoro.PomodoroViewModel
import com.example.ui.schedule.ScheduleScreen
import com.example.ui.schedule.ScheduleViewModel
import com.example.ui.settings.SettingsScreen
import com.example.ui.settings.SettingsViewModel
import com.example.ui.theme.CozyCreamBg

@Composable
fun MainScreen(
    calendarViewModel: CalendarViewModel = viewModel(),
    pomodoroViewModel: PomodoroViewModel = viewModel(),
    scheduleViewModel: ScheduleViewModel = viewModel(),
    settingsViewModel: SettingsViewModel = viewModel()
) {
    val context = LocalContext.current
    var currentTab by remember { mutableStateOf(AppTab.CALENDAR) }

    // Request POST_NOTIFICATIONS permission for Android 13+
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        // Updated state if needed
    }

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
        containerColor = CozyCreamBg,
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
                .background(CozyCreamBg)
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "tab_navigation"
            ) { tab ->
                when (tab) {
                    AppTab.CALENDAR -> {
                        CalendarScreen(
                            viewModel = calendarViewModel,
                            onNavigateToSchedule = { currentTab = AppTab.SCHEDULE },
                            onStartPomodoroForTask = { task: TaskEntity ->
                                pomodoroViewModel.attachTask(task)
                                currentTab = AppTab.POMODORO
                            }
                        )
                    }
                    AppTab.POMODORO -> {
                        PomodoroScreen(
                            viewModel = pomodoroViewModel
                        )
                    }
                    AppTab.SCHEDULE -> {
                        ScheduleScreen(
                            viewModel = scheduleViewModel,
                            onStartPomodoroForClass = { title, durationMin ->
                                pomodoroViewModel.setCustomFocusGoal(title, durationMin)
                                currentTab = AppTab.POMODORO
                            }
                        )
                    }
                    AppTab.SETTINGS -> {
                        SettingsScreen(
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
}
