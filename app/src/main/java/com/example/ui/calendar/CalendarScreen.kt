package com.example.ui.calendar

import com.example.ui.theme.*
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.entity.TaskEntity
import com.example.ui.components.AddTaskDialog
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Spa
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.AccentIndigo
import com.example.ui.theme.AccentRose
import com.example.ui.theme.AccentTeal
import com.example.ui.theme.CozyBorder
import com.example.ui.theme.CozyBorderSubtle
import com.example.ui.theme.CozyCardAlt
import com.example.ui.theme.CozyCardBg
import com.example.ui.theme.CozyCocoaMuted
import com.example.ui.theme.CozyCocoaText
import com.example.ui.theme.CozyCreamBg
import com.example.ui.theme.CozyForestDark
import com.example.ui.theme.CozyHoney
import com.example.ui.theme.CozyHoneyContainer
import com.example.ui.theme.CozyLeafGreen
import com.example.ui.theme.CozyLeafGreenContainer
import com.example.ui.theme.CozyPeach
import com.example.ui.theme.CozyPeachContainer
import com.example.ui.theme.CozySky
import com.example.ui.theme.CozySkyContainer
import com.example.data.local.entity.matchesDate
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class TaskFilter {
    ALL, PENDING, COMPLETED
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel,
    onNavigateToSchedule: () -> Unit,
    onStartPomodoroForTask: (TaskEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val tasks by viewModel.tasksForDate.collectAsStateWithLifecycle()
    val allTasks by viewModel.allTasks.collectAsStateWithLifecycle()
    val viewMode by viewModel.viewMode.collectAsStateWithLifecycle()
    val selectedCal by viewModel.selectedDateCalendar.collectAsStateWithLifecycle()
    val selectedIso by viewModel.selectedDateIso.collectAsStateWithLifecycle()
    val viewingMonthCal by viewModel.viewingMonthCalendar.collectAsStateWithLifecycle()

    var activeFilter by remember { mutableStateOf(TaskFilter.ALL) }
    var showAddDialog by remember { mutableStateOf(false) }
    var taskToEdit by remember { mutableStateOf<TaskEntity?>(null) }
    var selectedTaskForAction by remember { mutableStateOf<TaskEntity?>(null) }
    val sheetState = rememberModalBottomSheetState()

    val (titleMain, titleSubtitle) = viewModel.getFormattedDateTitle(selectedCal)

    val currentTasks = when (viewMode) {
        CalendarViewMode.DAY -> tasks
        CalendarViewMode.WEEK, CalendarViewMode.MONTH -> tasks // Filtered by selected day in week/month or full view
    }

    val filteredTasks = remember(currentTasks, activeFilter) {
        when (activeFilter) {
            TaskFilter.ALL -> currentTasks
            TaskFilter.PENDING -> currentTasks.filter { !it.isCompleted }
            TaskFilter.COMPLETED -> currentTasks.filter { it.isCompleted }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CozyCreamBg)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Sticky Top Header Controls
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CozyCreamBg)
                    .padding(horizontal = 18.dp, vertical = 12.dp)
            ) {
                // View Selector (Día | Semana | Mes) & Schedule Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Day / Week / Month Segmented Selector
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(CozyCardBg)
                            .border(1.5.dp, CozyBorder, RoundedCornerShape(14.dp))
                            .padding(3.dp)
                    ) {
                        listOf(
                            CalendarViewMode.DAY to "Día",
                            CalendarViewMode.WEEK to "Semana",
                            CalendarViewMode.MONTH to "Mes"
                        ).forEach { (mode, label) ->
                            val isSelected = viewMode == mode
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) CozyLeafGreen else Color.Transparent)
                                    .clickable { viewModel.setViewMode(mode) }
                                    .padding(horizontal = 12.dp, vertical = 7.dp)
                            ) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 12.sp
                                    ),
                                    color = if (isSelected) Color.White else CozyCocoaText
                                )
                            }
                        }
                    }

                    // Button to jump to Weekly Schedule
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = CozyHoneyContainer,
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, CozyHoney.copy(alpha = 0.4f)),
                        modifier = Modifier.clickable { onNavigateToSchedule() }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.DateRange,
                                contentDescription = "Horario",
                                tint = CozyForestDark,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Horario",
                                color = CozyForestDark,
                                style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Navigation Header depending on View Mode
                when (viewMode) {
                    CalendarViewMode.DAY -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = titleMain,
                                        style = MaterialTheme.typography.headlineMedium.copy(fontSize = 22.sp, fontWeight = FontWeight.Bold),
                                        color = CozyForestDark
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(Icons.Filled.Eco, contentDescription = null, tint = CozyLeafGreen, modifier = Modifier.size(18.dp))
                                }
                                Text(
                                    text = titleSubtitle,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = CozyCocoaMuted
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                IconButton(
                                    onClick = { viewModel.previousDay() },
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(CozyCardBg)
                                        .border(1.5.dp, CozyBorder, CircleShape)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.ChevronLeft,
                                        contentDescription = "Día anterior",
                                        tint = CozyCocoaText
                                    )
                                }
                                IconButton(
                                    onClick = { viewModel.nextDay() },
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(CozyCardBg)
                                        .border(1.5.dp, CozyBorder, CircleShape)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.ChevronRight,
                                        contentDescription = "Día siguiente",
                                        tint = CozyCocoaText
                                    )
                                }
                            }
                        }
                    }

                    CalendarViewMode.WEEK -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Vista Semanal",
                                        style = MaterialTheme.typography.headlineMedium.copy(fontSize = 22.sp, fontWeight = FontWeight.Bold),
                                        color = CozyForestDark
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(Icons.Filled.Eco, contentDescription = null, tint = CozyLeafGreen, modifier = Modifier.size(18.dp))
                                }
                                Text(
                                    text = "Toca un día para ver sus tareas",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = CozyCocoaMuted
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                IconButton(
                                    onClick = { viewModel.previousWeek() },
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(CozyCardBg)
                                        .border(1.5.dp, CozyBorder, CircleShape)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.ChevronLeft,
                                        contentDescription = "Semana anterior",
                                        tint = CozyCocoaText
                                    )
                                }
                                IconButton(
                                    onClick = { viewModel.nextWeek() },
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(CozyCardBg)
                                        .border(1.5.dp, CozyBorder, CircleShape)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.ChevronRight,
                                        contentDescription = "Semana siguiente",
                                        tint = CozyCocoaText
                                    )
                                }
                            }
                        }
                    }

                    CalendarViewMode.MONTH -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = viewModel.getFormattedMonthTitle(viewingMonthCal),
                                        style = MaterialTheme.typography.headlineMedium.copy(fontSize = 22.sp, fontWeight = FontWeight.Bold),
                                        color = CozyForestDark
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(Icons.Filled.Eco, contentDescription = null, tint = CozyLeafGreen, modifier = Modifier.size(18.dp))
                                }
                                Text(
                                    text = "Calendario Mensual de la Isla",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = CozyCocoaMuted
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                IconButton(
                                    onClick = { viewModel.previousMonth() },
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(CozyCardBg)
                                        .border(1.5.dp, CozyBorder, CircleShape)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.ChevronLeft,
                                        contentDescription = "Mes anterior",
                                        tint = CozyCocoaText
                                    )
                                }
                                IconButton(
                                    onClick = { viewModel.nextMonth() },
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(CozyCardBg)
                                        .border(1.5.dp, CozyBorder, CircleShape)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.ChevronRight,
                                        contentDescription = "Mes siguiente",
                                        tint = CozyCocoaText
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Filter Chips (Todas / Pendientes / Completadas)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val pendingCount = currentTasks.count { !it.isCompleted }
                    val completedCount = currentTasks.count { it.isCompleted }

                    FilterChip(
                        selected = activeFilter == TaskFilter.ALL,
                        onClick = { activeFilter = TaskFilter.ALL },
                        label = { Text("Todas (${currentTasks.size})", style = MaterialTheme.typography.labelSmall.copy(fontWeight = if (activeFilter == TaskFilter.ALL) FontWeight.Bold else FontWeight.Medium)) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CozyLeafGreen,
                            selectedLabelColor = Color.White,
                            containerColor = CozyCardBg,
                            labelColor = CozyCocoaText
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = if (activeFilter == TaskFilter.ALL) CozyLeafGreen else CozyBorder,
                            enabled = true,
                            selected = activeFilter == TaskFilter.ALL
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    FilterChip(
                        selected = activeFilter == TaskFilter.PENDING,
                        onClick = { activeFilter = TaskFilter.PENDING },
                        label = { Text("Pendientes ($pendingCount)", style = MaterialTheme.typography.labelSmall.copy(fontWeight = if (activeFilter == TaskFilter.PENDING) FontWeight.Bold else FontWeight.Medium)) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CozyLeafGreen,
                            selectedLabelColor = Color.White,
                            containerColor = CozyCardBg,
                            labelColor = CozyCocoaText
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = if (activeFilter == TaskFilter.PENDING) CozyLeafGreen else CozyBorder,
                            enabled = true,
                            selected = activeFilter == TaskFilter.PENDING
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    FilterChip(
                        selected = activeFilter == TaskFilter.COMPLETED,
                        onClick = { activeFilter = TaskFilter.COMPLETED },
                        label = { Text("Completadas ($completedCount)", style = MaterialTheme.typography.labelSmall.copy(fontWeight = if (activeFilter == TaskFilter.COMPLETED) FontWeight.Bold else FontWeight.Medium)) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CozyLeafGreen,
                            selectedLabelColor = Color.White,
                            containerColor = CozyCardBg,
                            labelColor = CozyCocoaText
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = if (activeFilter == TaskFilter.COMPLETED) CozyLeafGreen else CozyBorder,
                            enabled = true,
                            selected = activeFilter == TaskFilter.COMPLETED
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            HorizontalDivider(color = CozyBorderSubtle, thickness = 1.dp)

            // Body Content based on View Mode
            when (viewMode) {
                CalendarViewMode.DAY -> {
                    DayTimelineView(
                        tasks = filteredTasks,
                        onTaskClicked = { selectedTaskForAction = it },
                        onStartPomodoro = onStartPomodoroForTask,
                        onToggleComplete = { viewModel.toggleTaskCompletion(it) },
                        onAddNewTask = { showAddDialog = true }
                    )
                }

                CalendarViewMode.WEEK -> {
                    WeekCalendarView(
                        selectedCalendar = selectedCal,
                        allTasks = allTasks,
                        dayTasks = filteredTasks,
                        onSelectDate = { viewModel.selectDate(it) },
                        onTaskClicked = { selectedTaskForAction = it },
                        onStartPomodoro = onStartPomodoroForTask,
                        onToggleComplete = { viewModel.toggleTaskCompletion(it) },
                        onAddNewTask = { showAddDialog = true }
                    )
                }

                CalendarViewMode.MONTH -> {
                    MonthCalendarView(
                        viewingMonthCalendar = viewingMonthCal,
                        selectedCalendar = selectedCal,
                        allTasks = allTasks,
                        dayTasks = filteredTasks,
                        onSelectDate = { viewModel.selectDate(it) },
                        onTaskClicked = { selectedTaskForAction = it },
                        onStartPomodoro = onStartPomodoroForTask,
                        onToggleComplete = { viewModel.toggleTaskCompletion(it) },
                        onAddNewTask = { showAddDialog = true }
                    )
                }
            }
        }

        // Floating Action Button (+ Nueva Tarea)
        ExtendedFabButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 86.dp)
                .testTag("new_task_button")
        )

        // Add Task Dialog
        if (showAddDialog) {
            AddTaskDialog(
                initialDate = selectedIso,
                onDismiss = { showAddDialog = false },
                onSave = { newTask ->
                    viewModel.addTask(newTask)
                    showAddDialog = false
                }
            )
        }

        // Edit Task Dialog
        if (taskToEdit != null) {
            AddTaskDialog(
                initialDate = taskToEdit!!.date,
                taskToEdit = taskToEdit,
                onDismiss = { taskToEdit = null },
                onSave = { updated ->
                    viewModel.updateTask(updated)
                    taskToEdit = null
                }
            )
        }

        // Task Action Bottom Sheet
        if (selectedTaskForAction != null) {
            val task = selectedTaskForAction!!
            ModalBottomSheet(
                onDismissRequest = { selectedTaskForAction = null },
                sheetState = sheetState,
                containerColor = CozyCreamBg,
                contentColor = CozyCocoaText
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = task.title,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = CozyForestDark,
                            modifier = Modifier.weight(1f)
                        )
                        if (task.isFixed) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = CozyHoneyContainer,
                                border = androidx.compose.foundation.BorderStroke(1.dp, CozyHoney.copy(alpha = 0.4f)),
                                modifier = Modifier.padding(start = 8.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.PushPin,
                                        contentDescription = "Fijada",
                                        tint = CozyHoney,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Fijada",
                                        color = CozyForestDark,
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    )
                                }
                            }
                        }
                    }

                    if (task.description.isNotEmpty()) {
                        Text(
                            text = task.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = CozyCocoaMuted,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 6.dp)
                    ) {
                        Text(
                            text = "${task.date} • ${task.startTime} - ${task.endTime} (${task.durationMinutes} min)",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = parseColor(task.colorHex)
                        )

                        if (task.notifyOnStart || task.notifyOnEnd) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Filled.Alarm,
                                contentDescription = "Alarmas",
                                tint = CozyLeafGreen,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = if (task.notifyOnStart && task.notifyOnEnd) "Aviso inicio/fin" else if (task.notifyOnStart) "Aviso inicio" else "Aviso fin",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold),
                                color = CozyForestDark
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Start Pomodoro Button
                    Button(
                        onClick = {
                            val target = task
                            selectedTaskForAction = null
                            onStartPomodoroForTask(target)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CozyLeafGreen,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Filled.Timer, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Iniciar Pomodoro con esta tarea", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Toggle Complete
                    Button(
                        onClick = {
                            viewModel.toggleTaskCompletion(task)
                            selectedTaskForAction = null
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CozyCardBg,
                            contentColor = CozyCocoaText
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, CozyBorder),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            if (task.isCompleted) Icons.Filled.RadioButtonUnchecked else Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = if (task.isCompleted) CozyCocoaMuted else CozyLeafGreen,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (task.isCompleted) "Marcar como pendiente" else "Marcar como completada", fontWeight = FontWeight.SemiBold)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Edit Task
                    Button(
                        onClick = {
                            val toEdit = task
                            selectedTaskForAction = null
                            taskToEdit = toEdit
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CozyCardBg,
                            contentColor = CozyCocoaText
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, CozyBorder),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Filled.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Editar Tarea", fontWeight = FontWeight.SemiBold)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Delete Task
                    Button(
                        onClick = {
                            viewModel.deleteTask(task)
                            selectedTaskForAction = null
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CozyPeachContainer,
                            contentColor = CozyPeach
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Filled.Delete, contentDescription = null, tint = CozyPeach, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Eliminar Tarea", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}
@Composable
fun DayTimelineView(
    tasks: List<TaskEntity>,
    onTaskClicked: (TaskEntity) -> Unit,
    onStartPomodoro: (TaskEntity) -> Unit,
    onToggleComplete: (TaskEntity) -> Unit,
    onAddNewTask: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(bottom = 120.dp, top = 8.dp)
    ) {
        if (tasks.isEmpty()) {
            EmptyTaskCard(onAddNewTask = onAddNewTask)
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                tasks.forEach { task ->
                    TaskCardItem(
                        task = task,
                        onTaskClicked = { onTaskClicked(task) },
                        onStartPomodoro = { onStartPomodoro(task) },
                        onToggleComplete = { onToggleComplete(task) }
                    )
                }
            }
        }
    }
}

@Composable
fun WeekCalendarView(
    selectedCalendar: Calendar,
    allTasks: List<TaskEntity>,
    dayTasks: List<TaskEntity>,
    onSelectDate: (Calendar) -> Unit,
    onTaskClicked: (TaskEntity) -> Unit,
    onStartPomodoro: (TaskEntity) -> Unit,
    onToggleComplete: (TaskEntity) -> Unit,
    onAddNewTask: () -> Unit
) {
    val sdfIso = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    val dayNames = listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom")

    // Compute the 7 days of the current week (starting Monday)
    val weekDays = remember(selectedCalendar) {
        val cal = Calendar.getInstance().apply {
            timeInMillis = selectedCalendar.timeInMillis
            firstDayOfWeek = Calendar.MONDAY
            set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        }
        val days = mutableListOf<Calendar>()
        for (i in 0..6) {
            val dayCal = Calendar.getInstance().apply { timeInMillis = cal.timeInMillis }
            days.add(dayCal)
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }
        days
    }

    val todayCal = remember { Calendar.getInstance() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 120.dp)
    ) {
        // Week Days Strip
        Surface(
            color = CozyCardBg,
            shape = RoundedCornerShape(20.dp),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, CozyBorder),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp, horizontal = 6.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                weekDays.forEachIndexed { index, dayCal ->
                    val isSelected = dayCal.get(Calendar.YEAR) == selectedCalendar.get(Calendar.YEAR) &&
                            dayCal.get(Calendar.DAY_OF_YEAR) == selectedCalendar.get(Calendar.DAY_OF_YEAR)
                    val isToday = dayCal.get(Calendar.YEAR) == todayCal.get(Calendar.YEAR) &&
                            dayCal.get(Calendar.DAY_OF_YEAR) == todayCal.get(Calendar.DAY_OF_YEAR)

                    val iso = sdfIso.format(dayCal.time)
                    val countForDay = allTasks.count { it.matchesDate(iso, dayCal) }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isSelected) CozyLeafGreen else if (isToday) CozyHoneyContainer else Color.Transparent)
                            .border(
                                width = if (isToday && !isSelected) 1.5.dp else 0.dp,
                                color = if (isToday && !isSelected) CozyHoney else Color.Transparent,
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable { onSelectDate(dayCal) }
                            .padding(horizontal = 10.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = dayNames[index],
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold),
                            color = if (isSelected) Color.White else CozyCocoaMuted
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${dayCal.get(Calendar.DAY_OF_MONTH)}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 15.sp
                            ),
                            color = if (isSelected) Color.White else CozyForestDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        // Task count dot
                        if (countForDay > 0) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) Color.White else CozyLeafGreen)
                            )
                        } else {
                            Spacer(modifier = Modifier.size(6.dp))
                        }
                    }
                }
            }
        }

        // Selected Day Tasks
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            val sdfHeader = SimpleDateFormat("EEEE, d 'de' MMMM", Locale("es", "ES"))
            val headerText = sdfHeader.format(selectedCalendar.time)
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = headerText,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = CozyForestDark
                )
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = CozyHoneyContainer,
                    border = androidx.compose.foundation.BorderStroke(1.dp, CozyHoney.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = "${dayTasks.size} tareas",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = CozyForestDark,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (dayTasks.isEmpty()) {
                EmptyTaskCard(onAddNewTask = onAddNewTask)
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    dayTasks.forEach { task ->
                        TaskCardItem(
                            task = task,
                            onTaskClicked = { onTaskClicked(task) },
                            onStartPomodoro = { onStartPomodoro(task) },
                            onToggleComplete = { onToggleComplete(task) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MonthCalendarView(
    viewingMonthCalendar: Calendar,
    selectedCalendar: Calendar,
    allTasks: List<TaskEntity>,
    dayTasks: List<TaskEntity>,
    onSelectDate: (Calendar) -> Unit,
    onTaskClicked: (TaskEntity) -> Unit,
    onStartPomodoro: (TaskEntity) -> Unit,
    onToggleComplete: (TaskEntity) -> Unit,
    onAddNewTask: () -> Unit
) {
    val sdfIso = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    val dayHeaders = listOf("L", "M", "X", "J", "V", "S", "D")
    val todayCal = remember { Calendar.getInstance() }

    // Calculate calendar grid days for the month
    val monthCells = remember(viewingMonthCalendar) {
        val cal = Calendar.getInstance().apply {
            timeInMillis = viewingMonthCalendar.timeInMillis
            set(Calendar.DAY_OF_MONTH, 1)
        }
        val targetMonth = cal.get(Calendar.MONTH)
        val targetYear = cal.get(Calendar.YEAR)

        // Day of week for 1st day (Calendar.MONDAY = 2, Calendar.SUNDAY = 1)
        var firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY
        if (firstDayOfWeek < 0) firstDayOfWeek += 7

        // Go back to the start of the first week row
        cal.add(Calendar.DAY_OF_YEAR, -firstDayOfWeek)

        val cells = mutableListOf<MonthDayCell>()
        // Generate 42 cells (6 rows x 7 days)
        for (i in 0 until 42) {
            val cellCal = Calendar.getInstance().apply { timeInMillis = cal.timeInMillis }
            val isCurrentMonth = cellCal.get(Calendar.MONTH) == targetMonth && cellCal.get(Calendar.YEAR) == targetYear
            cells.add(MonthDayCell(cal = cellCal, isCurrentMonth = isCurrentMonth))
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }
        cells
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 120.dp)
    ) {
        // Month Grid Surface
        Surface(
            color = CozyCardBg,
            shape = RoundedCornerShape(22.dp),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, CozyBorder),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                // Day of Week Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    dayHeaders.forEach { header ->
                        Text(
                            text = header,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = CozyForestDark,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.width(36.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 6 rows of 7 days
                for (row in 0 until 6) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        for (col in 0 until 7) {
                            val cell = monthCells[row * 7 + col]
                            val cellIso = sdfIso.format(cell.cal.time)
                            val isSelected = cell.cal.get(Calendar.YEAR) == selectedCalendar.get(Calendar.YEAR) &&
                                    cell.cal.get(Calendar.DAY_OF_YEAR) == selectedCalendar.get(Calendar.DAY_OF_YEAR)
                            val isToday = cell.cal.get(Calendar.YEAR) == todayCal.get(Calendar.YEAR) &&
                                    cell.cal.get(Calendar.DAY_OF_YEAR) == todayCal.get(Calendar.DAY_OF_YEAR)

                            val tasksInCell = allTasks.filter { it.matchesDate(cellIso, cell.cal) }

                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) CozyLeafGreen else if (isToday) CozyHoneyContainer else Color.Transparent)
                                    .border(
                                        width = if (isToday && !isSelected) 1.5.dp else 0.dp,
                                        color = if (isToday && !isSelected) CozyHoney else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickable { onSelectDate(cell.cal) }
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "${cell.cal.get(Calendar.DAY_OF_MONTH)}",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Medium,
                                            fontSize = 13.sp
                                        ),
                                        color = if (isSelected) Color.White else if (cell.isCurrentMonth) CozyCocoaText else CozyCocoaMuted.copy(alpha = 0.4f)
                                    )
                                    if (tasksInCell.isNotEmpty()) {
                                        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                            tasksInCell.take(3).forEach { t ->
                                                Box(
                                                    modifier = Modifier
                                                        .size(4.dp)
                                                        .clip(CircleShape)
                                                        .background(if (isSelected) Color.White else parseColor(t.colorHex))
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (row < 5) Spacer(modifier = Modifier.height(6.dp))
                }
            }
        }

        // Tasks for selected day in month
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            val sdfHeader = SimpleDateFormat("EEEE, d 'de' MMMM", Locale("es", "ES"))
            val headerText = sdfHeader.format(selectedCalendar.time)
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = headerText,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = CozyForestDark
                )
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = CozyHoneyContainer,
                    border = androidx.compose.foundation.BorderStroke(1.dp, CozyHoney.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = "${dayTasks.size} tareas",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = CozyForestDark,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (dayTasks.isEmpty()) {
                EmptyTaskCard(onAddNewTask = onAddNewTask)
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    dayTasks.forEach { task ->
                        TaskCardItem(
                            task = task,
                            onTaskClicked = { onTaskClicked(task) },
                            onStartPomodoro = { onStartPomodoro(task) },
                            onToggleComplete = { onToggleComplete(task) }
                        )
                    }
                }
            }
        }
    }
}

data class MonthDayCell(
    val cal: Calendar,
    val isCurrentMonth: Boolean
)

@Composable
fun TaskCardItem(
    task: TaskEntity,
    onTaskClicked: () -> Unit,
    onStartPomodoro: () -> Unit,
    onToggleComplete: () -> Unit = {}
) {
    val categoryColor = parseColor(task.colorHex)
    val taskIcon = getTaskIcon(task.iconName)

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CozyCardBg),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, CozyBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onTaskClicked() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox
            IconButton(
                onClick = onToggleComplete,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = if (task.isCompleted) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked,
                    contentDescription = if (task.isCompleted) "Completada" else "Pendiente",
                    tint = if (task.isCompleted) CozyLeafGreen else CozyCocoaMuted,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Task Content
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = task.title,
                            style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp, fontWeight = FontWeight.Bold),
                            color = if (task.isCompleted) CozyCocoaMuted else CozyForestDark,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                        )
                        if (task.isFixed) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Filled.PushPin,
                                contentDescription = "Tarea Fija",
                                tint = CozyHoney,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        if (task.repeatMode == "DAILY") {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = CozyHoneyContainer
                            ) {
                                Text(
                                    text = "Cada día",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                                    color = CozyForestDark,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                        } else if (task.repeatMode == "WEEKLY") {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = CozySkyContainer
                            ) {
                                Text(
                                    text = "Semanal",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                                    color = CozyForestDark,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                        } else if (task.repeatMode == "WEEKDAYS") {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = CozyLeafGreenContainer
                            ) {
                                Text(
                                    text = "Lun-Vie",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                                    color = CozyForestDark,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                    Surface(
                        shape = CircleShape,
                        color = categoryColor.copy(alpha = 0.15f),
                        modifier = Modifier.size(28.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = taskIcon,
                                contentDescription = null,
                                tint = categoryColor,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                if (task.description.isNotEmpty()) {
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                        color = CozyCocoaMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (task.tag.isNotEmpty()) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = categoryColor.copy(alpha = 0.15f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, categoryColor.copy(alpha = 0.3f))
                            ) {
                                Text(
                                    text = task.tag,
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                                    color = categoryColor,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Text(
                            text = "${task.startTime} - ${task.endTime} • ${task.durationMinutes}m",
                            style = MaterialTheme.typography.labelMedium.copy(fontSize = 11.sp, fontWeight = FontWeight.Medium),
                            color = CozyCocoaMuted
                        )

                        if (task.notifyOnStart || task.notifyOnEnd) {
                            Icon(
                                imageVector = Icons.Filled.Alarm,
                                contentDescription = "Recordatorio",
                                tint = CozyLeafGreen,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }

                    // Quick start timer chip
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(CozyLeafGreenContainer)
                            .border(1.dp, CozyLeafGreen.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .clickable { onStartPomodoro() }
                            .padding(horizontal = 9.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Timer,
                                contentDescription = "Iniciar",
                                tint = CozyForestDark,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Enfocar",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = CozyForestDark,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyTaskCard(onAddNewTask: () -> Unit) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = CozyCardBg),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, CozyBorder),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = CozyLeafGreenContainer,
                border = androidx.compose.foundation.BorderStroke(1.5.dp, CozyLeafGreen.copy(alpha = 0.3f)),
                modifier = Modifier.size(68.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Filled.Spa,
                        contentDescription = null,
                        tint = CozyLeafGreen,
                        modifier = Modifier.size(34.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "¡Día libre o sin tareas!",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 18.sp),
                color = CozyForestDark
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Tómate un respiro en la isla o añade una nueva actividad.",
                style = MaterialTheme.typography.bodyMedium,
                color = CozyCocoaMuted,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(18.dp))
            Button(
                onClick = onAddNewTask,
                colors = ButtonDefaults.buttonColors(
                    containerColor = CozyLeafGreen,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Crear Actividad", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
            }
        }
    }
}

@Composable
fun ExtendedFabButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = CozyLeafGreen,
        contentColor = Color.White,
        shape = RoundedCornerShape(20.dp),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Nueva Tarea", modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Nueva Tarea",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}

fun parseColor(hex: String): Color {
    return try {
        Color(android.graphics.Color.parseColor(hex))
    } catch (e: Exception) {
        AccentTeal
    }
}

fun getTaskIcon(name: String): ImageVector {
    return when (name) {
        "history_edu" -> Icons.Filled.HistoryEdu
        "terminal" -> Icons.Filled.Terminal
        "book" -> Icons.Filled.Book
        "science" -> Icons.Filled.Science
        "calculate" -> Icons.Filled.Calculate
        "computer" -> Icons.Filled.Computer
        "edit" -> Icons.Filled.Edit
        "sports_esports" -> Icons.Filled.SportsEsports
        "task_alt" -> Icons.Filled.TaskAlt
        else -> Icons.Filled.Functions
    }
}
