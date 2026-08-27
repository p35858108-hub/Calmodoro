package com.example.ui.pomodoro

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.audio.AmbientSound
import com.example.data.local.entity.TaskEntity
import com.example.timer.PomodoroMode
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PomodoroScreen(
    viewModel: PomodoroViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.timerState.collectAsStateWithLifecycle()
    val completedCount by viewModel.todayCompletedCount.collectAsStateWithLifecycle()
    val allTasks by viewModel.allTasks.collectAsStateWithLifecycle()
    val currentAmbient by viewModel.ambientSound.collectAsStateWithLifecycle()

    var showTaskPicker by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scrollState = rememberScrollState()

    val animatedProgress by animateFloatAsState(
        targetValue = state.progress,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = "pomodoro_progress"
    )

    val modeColor = when (state.mode) {
        PomodoroMode.FOCUS -> CozyLeafGreen
        PomodoroMode.SHORT_BREAK -> CozySky
        PomodoroMode.LONG_BREAK -> CozyPeach
        else -> CozyLeafGreen
    }

    val modeContainerColor = when (state.mode) {
        PomodoroMode.FOCUS -> CozyLeafGreenContainer
        PomodoroMode.SHORT_BREAK -> CozySkyContainer
        PomodoroMode.LONG_BREAK -> CozyPeachContainer
        else -> CozyLeafGreenContainer
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CozyCreamBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 14.dp)
                .padding(bottom = 85.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Cozy Mascot & Header Greeting Card
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = CozyCardBg,
                border = androidx.compose.foundation.BorderStroke(2.dp, CozyBorder),
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_cozy_mascot),
                        contentDescription = "Mascota Cozy",
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .border(2.dp, CozyLeafGreenContainer, CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Isla de Concentración",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = CozyForestDark
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Filled.Eco,
                                contentDescription = null,
                                tint = CozyLeafGreen,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Text(
                            text = if (state.isRunning) "¡Buen trabajo! Sigue así 🍃" else "Tómate tu tiempo y respira ☕",
                            style = MaterialTheme.typography.bodySmall,
                            color = CozyCocoaMuted
                        )
                    }
                }
            }

            // Active Task Banner (Cozy Speech-Bubble style)
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = if (state.activeTaskTitle.isNotEmpty()) CozyHoneyContainer else CozyCardBg,
                border = androidx.compose.foundation.BorderStroke(
                    2.dp,
                    if (state.activeTaskTitle.isNotEmpty()) CozyHoney else CozyBorder
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showTaskPicker = true }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = if (state.activeTaskTitle.isNotEmpty()) CozyHoney else CozyCardAlt,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.Assignment,
                                    contentDescription = null,
                                    tint = if (state.activeTaskTitle.isNotEmpty()) CozyCocoaText else CozyCocoaMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (state.activeTaskTitle.isNotEmpty()) state.activeTaskTitle else "Vincular tarea o materia...",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = if (state.activeTaskTitle.isNotEmpty()) FontWeight.Bold else FontWeight.Medium
                                ),
                                color = CozyCocoaText,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = if (state.activeTaskTitle.isNotEmpty()) "Toca para cambiar de objetivo" else "Opcional para organizar tu progreso",
                                style = MaterialTheme.typography.labelSmall,
                                color = CozyCocoaMuted
                            )
                        }
                    }
                    if (state.activeTaskTitle.isNotEmpty()) {
                        IconButton(
                            onClick = { viewModel.attachTask(null) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Quitar tarea",
                                tint = CozyCocoaMuted,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // Mode Selector Pills
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(CozyCardAlt)
                    .border(1.5.dp, CozyBorder, RoundedCornerShape(20.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                PomodoroMode.values().forEach { mode ->
                    val isSelected = state.mode == mode
                    val activeColor = when (mode) {
                        PomodoroMode.FOCUS -> CozyLeafGreen
                        PomodoroMode.SHORT_BREAK -> CozySky
                        PomodoroMode.LONG_BREAK -> CozyPeach
                        else -> CozyLeafGreen
                    }
                    val activePillBg = when (mode) {
                        PomodoroMode.FOCUS -> CozyLeafGreenContainer
                        PomodoroMode.SHORT_BREAK -> CozySkyContainer
                        PomodoroMode.LONG_BREAK -> CozyPeachContainer
                        else -> CozyLeafGreenContainer
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) activePillBg else Color.Transparent)
                            .clickable { viewModel.setMode(mode) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = when (mode) {
                                PomodoroMode.FOCUS -> "Enfoque"
                                PomodoroMode.SHORT_BREAK -> "Pausa Corta"
                                PomodoroMode.LONG_BREAK -> "Pausa Larga"
                                else -> "Enfoque"
                            },
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 12.sp
                            ),
                            color = if (isSelected) activeColor else CozyCocoaMuted
                        )
                    }
                }
            }

            // Cozy Circular Progress Indicator & Countdown Display
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .padding(vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 14.dp.toPx()
                    val diameter = size.minDimension - strokeWidth
                    val topLeft = Offset(
                        (size.width - diameter) / 2f,
                        (size.height - diameter) / 2f
                    )
                    val arcSize = Size(diameter, diameter)

                    // Track Background (Cream Biscuit)
                    drawArc(
                        color = CozyCardAlt,
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )

                    // Progress Arc (Leaf/Sky/Peach)
                    drawArc(
                        color = modeColor,
                        startAngle = -90f,
                        sweepAngle = 360f * animatedProgress,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }

                // Inner Timer Text & State
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = state.formattedTime,
                        style = TimerDisplayTextStyle.copy(fontSize = 46.sp),
                        color = CozyCocoaText,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = modeContainerColor
                    ) {
                        Text(
                            text = when {
                                state.isRunning -> "EN MARCHA 🍃"
                                state.isPaused -> "EN PAUSA ☕"
                                else -> "LISTO 🌸"
                            },
                            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp, fontWeight = FontWeight.Bold),
                            color = modeColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // Quick Adjust Time Buttons (+5m / -5m) & Skip Button
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = CozyCardBg,
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, CozyBorder),
                    modifier = Modifier.clickable { viewModel.addMinutes(-5) }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Filled.Remove, contentDescription = null, modifier = Modifier.size(14.dp), tint = CozyCocoaText)
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("-5 min", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold), color = CozyCocoaText)
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = CozyCardBg,
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, CozyBorder),
                    modifier = Modifier.clickable { viewModel.addMinutes(5) }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(14.dp), tint = CozyCocoaText)
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("+5 min", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold), color = CozyCocoaText)
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = CozyCardBg,
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, CozyBorder),
                    modifier = Modifier.clickable { viewModel.skipMode() }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Filled.SkipNext, contentDescription = "Saltar", modifier = Modifier.size(14.dp), tint = CozyCocoaText)
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("Saltar", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold), color = CozyCocoaText)
                    }
                }
            }

            // Cozy Timer Controls: Reset, Play/Pause
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Reset Button
                Surface(
                    shape = CircleShape,
                    color = CozyCardBg,
                    border = androidx.compose.foundation.BorderStroke(2.dp, CozyBorder),
                    shadowElevation = 2.dp,
                    modifier = Modifier
                        .size(52.dp)
                        .clickable { viewModel.reset() }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Filled.RestartAlt,
                            contentDescription = "Reiniciar",
                            tint = CozyCocoaText,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(24.dp))

                // Play / Pause Button (Cozy Leaf Green Pebble)
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .shadow(
                            elevation = 6.dp,
                            shape = CircleShape,
                            spotColor = modeColor.copy(alpha = 0.5f)
                        )
                        .clip(CircleShape)
                        .background(modeColor)
                        .clickable {
                            if (state.isRunning) {
                                viewModel.pause()
                            } else {
                                viewModel.startOrResume()
                            }
                        }
                        .testTag("play_pause_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (state.isRunning) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        contentDescription = if (state.isRunning) "Pausar" else "Iniciar",
                        tint = Color.White,
                        modifier = Modifier.size(38.dp)
                    )
                }
            }

            // Mark Task Completed Button
            if (state.activeTaskId != null) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = CozyLeafGreenContainer,
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, CozyLeafGreen.copy(alpha = 0.4f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.markCurrentTaskCompleted() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = CozyLeafGreen, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Completar tarea: '${state.activeTaskTitle}' 🌟",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = CozyForestDark
                        )
                    }
                }
            }

            // Ambient Sound Generator Card
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = CozyCardBg,
                border = androidx.compose.foundation.BorderStroke(2.dp, CozyBorder),
                shadowElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (currentAmbient != AmbientSound.NONE) Icons.Filled.VolumeUp else Icons.Filled.VolumeOff,
                                contentDescription = null,
                                tint = if (currentAmbient != AmbientSound.NONE) CozyLeafGreen else CozyCocoaMuted,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Sonidos Relajantes de Fondo",
                                style = MaterialTheme.typography.titleSmall,
                                color = CozyCocoaText,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(AmbientSound.values()) { sound ->
                            val isSelected = currentAmbient == sound
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.setAmbientSound(sound) },
                                label = {
                                    Text(
                                        "${sound.emoji} ${sound.title}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CozyLeafGreenContainer,
                                    selectedLabelColor = CozyForestDark,
                                    containerColor = CozyCardAlt,
                                    labelColor = CozyCocoaText
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    borderColor = if (isSelected) CozyLeafGreen else CozyBorderSubtle,
                                    enabled = true,
                                    selected = isSelected
                                )
                            )
                        }
                    }
                }
            }

            // Daily Stat Card (Bells & Fruit style)
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = CozyCardBg,
                border = androidx.compose.foundation.BorderStroke(2.dp, CozyBorder),
                shadowElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Racha de Hoy",
                            style = MaterialTheme.typography.labelSmall,
                            color = CozyCocoaMuted
                        )
                        Text(
                            text = "$completedCount sesiones completadas",
                            style = MaterialTheme.typography.titleSmall,
                            color = CozyCocoaText,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = CozyHoneyContainer
                    ) {
                        Text(
                            text = "🍯 ${completedCount * 25} min",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            color = CozyHoney,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // Task Picker Bottom Sheet
        if (showTaskPicker) {
            ModalBottomSheet(
                onDismissRequest = { showTaskPicker = false },
                sheetState = sheetState,
                containerColor = CozyCreamBg,
                contentColor = CozyCocoaText
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp)
                ) {
                    Text(
                        text = "Vincular Tarea al Temporizador 🍃",
                        style = MaterialTheme.typography.titleLarge,
                        color = CozyForestDark,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Selecciona una tarea de tu lista para enfocar tu tiempo.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = CozyCocoaMuted,
                        modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
                    )

                    HorizontalDivider(color = CozyBorder, thickness = 1.dp)

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 12.dp)
                    ) {
                        // Quick default focus option
                        item {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = CozyCardBg,
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, CozyBorder),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.attachTask(null)
                                        showTaskPicker = false
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Filled.Assignment, contentDescription = null, tint = CozyLeafGreen)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text("Sesión Libre (Sin tarea vinculada)", color = CozyCocoaText, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium))
                                }
                            }
                        }

                        if (allTasks.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(20.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("No hay tareas registradas. Puedes crearlas en la pestaña Calendario u Horario.", color = CozyCocoaMuted, textAlign = TextAlign.Center)
                                }
                            }
                        } else {
                            items(allTasks) { task ->
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = if (state.activeTaskId == task.id) CozyLeafGreenContainer else CozyCardBg,
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.5.dp,
                                        if (state.activeTaskId == task.id) CozyLeafGreen else CozyBorder
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            viewModel.attachTask(task)
                                            showTaskPicker = false
                                        }
                                    ) {
                                    Row(
                                        modifier = Modifier.padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(task.title, style = MaterialTheme.typography.titleSmall, color = CozyCocoaText, fontWeight = FontWeight.Bold)
                                            Text(
                                                "${task.startTime} - ${task.endTime} • ${task.durationMinutes} min",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = CozyCocoaMuted
                                            )
                                        }
                                        if (state.activeTaskId == task.id) {
                                            Icon(Icons.Filled.Check, contentDescription = null, tint = CozyLeafGreen)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

