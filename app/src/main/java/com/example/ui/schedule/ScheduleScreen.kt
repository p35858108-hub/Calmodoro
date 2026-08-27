package com.example.ui.schedule

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.entity.WeeklyScheduleEntity
import com.example.ui.calendar.parseColor
import com.example.ui.components.AddScheduleDialog
import com.example.ui.components.DaysOfWeekLabels
import com.example.ui.components.calculateMinutesDiff
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    viewModel: ScheduleViewModel,
    onStartPomodoroForClass: (title: String, durationMin: Int) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val selectedDay by viewModel.selectedDay.collectAsStateWithLifecycle()
    val scheduleList by viewModel.scheduleForSelectedDay.collectAsStateWithLifecycle()
    val allSchedule by viewModel.allSchedule.collectAsStateWithLifecycle()

    var showAddDialog by remember { mutableStateOf(false) }
    var scheduleToEdit by remember { mutableStateOf<WeeklyScheduleEntity?>(null) }
    var selectedItemForAction by remember { mutableStateOf<WeeklyScheduleEntity?>(null) }
    val sheetState = rememberModalBottomSheetState()

    // Map day to count
    val dayCountMap = remember(allSchedule) {
        allSchedule.groupBy { it.dayOfWeek }.mapValues { it.value.size }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CozyCreamBg)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Horario Semanal",
                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                                color = CozyForestDark
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(Icons.Filled.Eco, contentDescription = null, tint = CozyLeafGreen, modifier = Modifier.size(20.dp))
                        }
                        Text(
                            text = "Tus clases y bloques de estudio recurrentes",
                            style = MaterialTheme.typography.labelMedium,
                            color = CozyCocoaMuted
                        )
                    }

                    // Sync button
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = CozySkyContainer,
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, CozySky.copy(alpha = 0.4f)),
                        modifier = Modifier
                            .clickable {
                                viewModel.importScheduleToToday { count ->
                                    if (count > 0) {
                                        Toast.makeText(
                                            context,
                                            "¡Se sincronizaron $count clases a tu agenda de hoy!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "No hay clases en este día para sincronizar",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Download,
                                contentDescription = "Sincronizar",
                                tint = CozySky,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Importar a Hoy",
                                color = CozySky,
                                style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Day Selector Pills with Badges
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(CozyCardBg)
                        .border(1.5.dp, CozyBorder, RoundedCornerShape(18.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    DaysOfWeekLabels.forEach { (dayInt, label) ->
                        val isSelected = selectedDay == dayInt
                        val count = dayCountMap[dayInt] ?: 0

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isSelected) CozyLeafGreen else Color.Transparent)
                                .clickable { viewModel.selectDay(dayInt) },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 12.sp
                                    ),
                                    color = if (isSelected) Color.White else CozyCocoaText
                                )
                                if (count > 0) {
                                    Box(
                                        modifier = Modifier
                                            .size(5.dp)
                                            .clip(CircleShape)
                                            .background(if (isSelected) Color.White else CozyLeafGreen)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            HorizontalDivider(color = CozyBorderSubtle, thickness = 1.dp)

            // Timeline List of Schedule
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 18.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(top = 16.dp, bottom = 120.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                if (scheduleList.isEmpty()) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(22.dp),
                            color = CozyCardBg,
                            border = androidx.compose.foundation.BorderStroke(2.dp, CozyBorder),
                            shadowElevation = 1.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp, horizontal = 4.dp)
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
                                    modifier = Modifier.size(68.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Filled.School,
                                            contentDescription = null,
                                            tint = CozyLeafGreen,
                                            modifier = Modifier.size(32.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Sin clases programadas",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = CozyForestDark
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Organiza tu semana agregando tus materias o bloques fijos de estudio.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = CozyCocoaMuted,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(18.dp))
                                Button(
                                    onClick = { showAddDialog = true },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = CozyLeafGreen,
                                        contentColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(14.dp)
                                ) {
                                    Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Añadir Asignatura", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                                }
                            }
                        }
                    }
                } else {
                    items(scheduleList, key = { it.id }) { item ->
                        val isLast = scheduleList.lastOrNull()?.id == item.id
                        ScheduleTimelineRow(
                            item = item,
                            isLast = isLast,
                            onItemClick = { selectedItemForAction = item },
                            onStartFocus = {
                                val dur = calculateMinutesDiff(item.startTime, item.endTime)
                                onStartPomodoroForClass(item.title, dur)
                            }
                        )
                    }
                }
            }
        }

        // Floating Action Button
        FloatingActionButton(
            onClick = { showAddDialog = true },
            containerColor = CozyLeafGreen,
            contentColor = Color.White,
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 86.dp)
                .testTag("add_schedule_button")
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Añadir Clase", modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Añadir Clase",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
        }

        // Add Dialog
        if (showAddDialog) {
            AddScheduleDialog(
                initialDay = selectedDay,
                onDismiss = { showAddDialog = false },
                onSave = { newSchedule ->
                    viewModel.addSchedule(newSchedule)
                    showAddDialog = false
                }
            )
        }

        // Edit Dialog
        if (scheduleToEdit != null) {
            AddScheduleDialog(
                initialDay = scheduleToEdit!!.dayOfWeek,
                scheduleToEdit = scheduleToEdit,
                onDismiss = { scheduleToEdit = null },
                onSave = { updated ->
                    viewModel.updateSchedule(updated)
                    scheduleToEdit = null
                }
            )
        }

        // Action Modal
        if (selectedItemForAction != null) {
            val item = selectedItemForAction!!
            ModalBottomSheet(
                onDismissRequest = { selectedItemForAction = null },
                sheetState = sheetState,
                containerColor = CozyCreamBg,
                contentColor = CozyCocoaText
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = CozyForestDark
                    )
                    Text(
                        text = "${item.startTime} - ${item.endTime} ${if (item.location.isNotEmpty()) "• " + item.location else ""}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = CozyCocoaMuted,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    if (!item.isRecess) {
                        Button(
                            onClick = {
                                val dur = calculateMinutesDiff(item.startTime, item.endTime)
                                val title = item.title
                                selectedItemForAction = null
                                onStartPomodoroForClass(title, dur)
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
                            Text("Iniciar Temporizador Pomodoro", fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    // Edit Button
                    Button(
                        onClick = {
                            val toEdit = item
                            selectedItemForAction = null
                            scheduleToEdit = toEdit
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
                        Text("Editar Asignatura", fontWeight = FontWeight.SemiBold)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Delete Button
                    Button(
                        onClick = {
                            viewModel.deleteSchedule(item)
                            selectedItemForAction = null
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
                        Text("Eliminar del Horario", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
fun ScheduleTimelineRow(
    item: WeeklyScheduleEntity,
    isLast: Boolean,
    onItemClick: () -> Unit,
    onStartFocus: () -> Unit = {}
) {
    val nodeColor = parseColor(item.colorHex)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        // Time Column (64dp)
        Column(
            modifier = Modifier
                .width(64.dp)
                .padding(top = 10.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = item.startTime,
                style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp, fontWeight = FontWeight.Bold),
                color = CozyCocoaText
            )
            Text(
                text = item.endTime,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                color = CozyCocoaMuted
            )
        }

        // Timeline Node & Vertical Connector
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .width(28.dp)
                .fillMaxHeight()
        ) {
            // Node
            Box(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(CozyCardBg)
                    .border(2.5.dp, if (item.isRecess) CozyCocoaMuted else nodeColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (!item.isRecess) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(nodeColor)
                    )
                }
            }

            // Connecting Vertical Line
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .weight(1f)
                        .background(CozyBorder)
                )
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }
        }

        // Schedule Card
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp, bottom = 14.dp)
        ) {
            if (item.isRecess) {
                // Recess Bar
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = CozyCardAlt,
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, CozyBorderSubtle),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onItemClick() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Coffee,
                            contentDescription = "Receso",
                            tint = CozyCocoaMuted,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Receso (${item.startTime} - ${item.endTime})",
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp, fontWeight = FontWeight.Medium),
                            color = CozyCocoaMuted
                        )
                    }
                }
            } else {
                // Regular Class Card
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = CozyCardBg,
                    border = androidx.compose.foundation.BorderStroke(2.dp, CozyBorder),
                    shadowElevation = 1.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onItemClick() }
                ) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        // Left color accent
                        Box(
                            modifier = Modifier
                                .width(5.dp)
                                .height(84.dp)
                                .background(nodeColor)
                        )

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 14.dp, vertical = 12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp, fontWeight = FontWeight.Bold),
                                    color = CozyCocoaText,
                                    modifier = Modifier.weight(1f)
                                )
                                if (item.instructorOrTag.isNotEmpty()) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = nodeColor.copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = item.instructorOrTag,
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp, fontWeight = FontWeight.SemiBold),
                                            color = nodeColor,
                                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (item.location.isNotEmpty()) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Filled.LocationOn,
                                            contentDescription = null,
                                            tint = CozyCocoaMuted,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = item.location,
                                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                                            color = CozyCocoaMuted
                                        )
                                    }
                                } else {
                                    Text(
                                        text = "${item.startTime} - ${item.endTime}",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                                        color = CozyCocoaMuted
                                    )
                                }

                                // Quick focus chip
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(CozyLeafGreenContainer)
                                        .clickable { onStartFocus() }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Filled.PlayArrow,
                                            contentDescription = "Enfocar",
                                            tint = CozyForestDark,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text(
                                            text = "Enfocar",
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                                            color = CozyForestDark
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

