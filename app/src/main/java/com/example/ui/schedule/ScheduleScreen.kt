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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
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
import com.example.ui.theme.AccentRose
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.OnPrimaryContainer
import com.example.ui.theme.OnSurface
import com.example.ui.theme.OnSurfaceVariant
import com.example.ui.theme.OutlineColor
import com.example.ui.theme.OutlineVariant
import com.example.ui.theme.PrimaryContainer
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceContainerHigh
import com.example.ui.theme.SurfaceDark

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
            .background(BackgroundDark)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Horario Semanal",
                            style = MaterialTheme.typography.headlineMedium,
                            color = OnSurface,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Configuración recurrente de clases y bloques fijos",
                            style = MaterialTheme.typography.labelMedium,
                            color = OutlineColor
                        )
                    }

                    // Sync button
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = SurfaceContainerHigh,
                        border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant.copy(alpha = 0.5f)),
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
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Download,
                                contentDescription = "Sincronizar",
                                tint = PrimaryIndigo,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Importar a Hoy",
                                color = PrimaryIndigo,
                                style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Day Selector Pills with Badges
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(SurfaceContainer)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    DaysOfWeekLabels.forEach { (dayInt, label) ->
                        val isSelected = selectedDay == dayInt
                        val count = dayCountMap[dayInt] ?: 0

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) PrimaryContainer else Color.Transparent)
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
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 12.sp
                                    ),
                                    color = if (isSelected) OnPrimaryContainer else OnSurfaceVariant
                                )
                                if (count > 0) {
                                    Box(
                                        modifier = Modifier
                                            .size(5.dp)
                                            .clip(CircleShape)
                                            .background(if (isSelected) OnPrimaryContainer else PrimaryIndigo)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            HorizontalDivider(color = SurfaceContainer.copy(alpha = 0.6f), thickness = 1.dp)

            // Timeline List of Schedule
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(top = 16.dp, bottom = 120.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                if (scheduleList.isEmpty()) {
                    item {
                        Card(
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceContainer),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp, horizontal = 8.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(28.dp)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = SurfaceContainer,
                                    modifier = Modifier.size(64.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Filled.School,
                                            contentDescription = null,
                                            tint = PrimaryIndigo,
                                            modifier = Modifier.size(32.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Sin clases programadas",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = OnSurface,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Organiza tu semana agregando tus materias o bloques fijos de estudio.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = OnSurfaceVariant,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(18.dp))
                                Button(
                                    onClick = { showAddDialog = true },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = PrimaryContainer,
                                        contentColor = OnPrimaryContainer
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Añadir Asignatura", style = MaterialTheme.typography.labelLarge)
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
            containerColor = PrimaryContainer,
            contentColor = Color.Black,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 80.dp)
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
                containerColor = SurfaceDark,
                contentColor = OnSurface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleLarge,
                        color = OnSurface,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${item.startTime} - ${item.endTime} ${if (item.location.isNotEmpty()) "• " + item.location else ""}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnSurfaceVariant,
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
                                containerColor = PrimaryContainer,
                                contentColor = OnPrimaryContainer
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Filled.Timer, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Iniciar Temporizador Pomodoro")
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
                            containerColor = SurfaceContainerHigh,
                            contentColor = OnSurface
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Filled.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Editar Asignatura")
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Delete Button
                    Button(
                        onClick = {
                            viewModel.deleteSchedule(item)
                            selectedItemForAction = null
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SurfaceContainer,
                            contentColor = AccentRose
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Filled.Delete, contentDescription = null, tint = AccentRose, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Eliminar del Horario")
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
                style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp, fontWeight = FontWeight.SemiBold),
                color = OnSurface
            )
            Text(
                text = item.endTime,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                color = OutlineColor
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
                    .size(14.dp)
                    .clip(CircleShape)
                    .background(SurfaceDark)
                    .border(2.dp, if (item.isRecess) OutlineColor else nodeColor, CircleShape),
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
                        .background(SurfaceContainer)
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
                    shape = RoundedCornerShape(10.dp),
                    color = SurfaceContainer.copy(alpha = 0.5f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant.copy(alpha = 0.3f)),
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
                            tint = OutlineColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Receso (${item.startTime} - ${item.endTime})",
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                            color = OutlineColor
                        )
                    }
                }
            } else {
                // Regular Class Card
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceContainer),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onItemClick() }
                ) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        // Left color accent
                        Box(
                            modifier = Modifier
                                .width(4.dp)
                                .height(82.dp)
                                .background(nodeColor)
                        )

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp),
                                    color = OnSurface,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.weight(1f)
                                )
                                if (item.instructorOrTag.isNotEmpty()) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = nodeColor.copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = item.instructorOrTag,
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                            color = nodeColor,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
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
                                            tint = OutlineColor,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = item.location,
                                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                                            color = OutlineColor
                                        )
                                    }
                                } else {
                                    Text(
                                        text = "${item.startTime} - ${item.endTime}",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                                        color = OutlineColor
                                    )
                                }

                                // Quick focus chip
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(PrimaryContainer.copy(alpha = 0.15f))
                                        .clickable { onStartFocus() }
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Filled.PlayArrow,
                                            contentDescription = "Enfocar",
                                            tint = PrimaryIndigo,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text(
                                            text = "Enfocar",
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                            color = PrimaryIndigo,
                                            fontWeight = FontWeight.SemiBold
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
