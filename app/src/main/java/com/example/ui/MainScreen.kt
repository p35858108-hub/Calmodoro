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
