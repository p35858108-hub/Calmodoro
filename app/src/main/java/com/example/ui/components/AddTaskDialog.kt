package com.example.ui.components

import android.app.DatePickerDialog
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.window.Dialog
import com.example.data.local.entity.TaskEntity
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.AccentIndigo
import com.example.ui.theme.AccentRose
import com.example.ui.theme.AccentTeal
import com.example.ui.theme.OnPrimaryContainer
import com.example.ui.theme.OnSurface
import com.example.ui.theme.OnSurfaceVariant
import com.example.ui.theme.OutlineVariant
import com.example.ui.theme.PrimaryContainer
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceDark
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

// Base quick palette
val BasePalette = listOf(
    "#2DD4BF" to AccentTeal,
    "#FBBF24" to AccentAmber,
    "#818CF8" to AccentIndigo,
    "#34D399" to AccentEmerald,
    "#FB7185" to AccentRose,
    "#38BDF8" to Color(0xFF38BDF8)
)

// Extended rich color palette revealed with "+ Más"
val ExtendedPalette = listOf(
    "#A855F7" to Color(0xFFA855F7), // Purple
    "#EC4899" to Color(0xFFEC4899), // Pink
    "#F43F5E" to Color(0xFFF43F5E), // Crimson
    "#F97316" to Color(0xFFF97316), // Orange
    "#84CC16" to Color(0xFF84CC16), // Lime
    "#06B6D4" to Color(0xFF06B6D4), // Cyan
    "#3B82F6" to Color(0xFF3B82F6), // Electric Blue
    "#6366F1" to Color(0xFF6366F1), // Royal Indigo
    "#D946EF" to Color(0xFFD946EF), // Fuchsia
    "#14B8A6" to Color(0xFF14B8A6), // Dark Teal
    "#EAB308" to Color(0xFFEAB308), // Gold
    "#10B981" to Color(0xFF10B981), // Green
    "#64748B" to Color(0xFF64748B), // Slate Grey
    "#E11D48" to Color(0xFFE11D48), // Ruby
    "#7C3AED" to Color(0xFF7C3AED), // Deep Violet
    "#0284C7" to Color(0xFF0284C7), // Ocean Blue
    "#059669" to Color(0xFF059669), // Forest Green
    "#D97706" to Color(0xFFD97706)  // Amber Bronze
)

val AvailableIcons = listOf(
    "functions" to Icons.Filled.Functions,
    "history_edu" to Icons.Filled.HistoryEdu,
    "terminal" to Icons.Filled.Terminal,
    "book" to Icons.Filled.Book,
    "science" to Icons.Filled.Science,
    "calculate" to Icons.Filled.Calculate,
    "computer" to Icons.Filled.Computer,
    "edit" to Icons.Filled.Edit,
    "sports_esports" to Icons.Filled.SportsEsports,
    "task_alt" to Icons.Filled.TaskAlt
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddTaskDialog(
    initialDate: String,
    taskToEdit: TaskEntity? = null,
    onDismiss: () -> Unit,
    onSave: (TaskEntity) -> Unit
) {
    val context = LocalContext.current
    val sdfIso = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    val sdfDisplay = remember { SimpleDateFormat("EEEE, d 'de' MMMM", Locale("es", "ES")) }

    var selectedDate by remember { mutableStateOf(taskToEdit?.date ?: initialDate) }
    var title by remember { mutableStateOf(taskToEdit?.title ?: "") }
    var description by remember { mutableStateOf(taskToEdit?.description ?: "") }
    var startTime by remember { mutableStateOf(taskToEdit?.startTime ?: "09:00") }
    var endTime by remember { mutableStateOf(taskToEdit?.endTime ?: "10:30") }
    var selectedColorHex by remember { mutableStateOf(taskToEdit?.colorHex ?: "#2DD4BF") }
    var selectedIconName by remember { mutableStateOf(taskToEdit?.iconName ?: "functions") }
    var tag by remember { mutableStateOf(taskToEdit?.tag ?: "") }
    var isFixed by remember { mutableStateOf(taskToEdit?.isFixed ?: false) }
    var repeatMode by remember { mutableStateOf(taskToEdit?.repeatMode ?: "NONE") }
    var notifyOnStart by remember { mutableStateOf(taskToEdit?.notifyOnStart ?: false) }
    var notifyOnEnd by remember { mutableStateOf(taskToEdit?.notifyOnEnd ?: false) }

    var showMoreColors by remember { mutableStateOf(false) }

    val formattedDisplayDate = remember(selectedDate) {
        try {
            val parsed = sdfIso.parse(selectedDate)
            if (parsed != null) {
                val formatted = sdfDisplay.format(parsed)
                formatted.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
            } else selectedDate
        } catch (e: Exception) {
            selectedDate
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = SurfaceDark,
            border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceContainer),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Dialog Title
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (taskToEdit == null) "Nueva Tarea de Agenda" else "Editar Tarea",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = OnSurface
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Date Selection Section
                Text(
                    text = "Fecha de la Tarea",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = OnSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = SurfaceContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.CalendarMonth,
                                    contentDescription = "Fecha",
                                    tint = PrimaryContainer,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = formattedDisplayDate,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                    color = OnSurface
                                )
                            }

                            TextButton(
                                onClick = {
                                    val calendar = Calendar.getInstance()
                                    try {
                                        val parsed = sdfIso.parse(selectedDate)
                                        if (parsed != null) calendar.time = parsed
                                    } catch (e: Exception) {
                                        // fallback to today
                                    }

                                    DatePickerDialog(
                                        context,
                                        { _, year, month, dayOfMonth ->
                                            val newCal = Calendar.getInstance().apply {
                                                set(Calendar.YEAR, year)
                                                set(Calendar.MONTH, month)
                                                set(Calendar.DAY_OF_MONTH, dayOfMonth)
                                            }
                                            selectedDate = sdfIso.format(newCal.time)
                                        },
                                        calendar.get(Calendar.YEAR),
                                        calendar.get(Calendar.MONTH),
                                        calendar.get(Calendar.DAY_OF_MONTH)
                                    ).show()
                                }
                            ) {
                                Text("Cambiar fecha", color = PrimaryContainer, fontSize = 13.sp)
                            }
                        }

                        // Quick buttons for Today, Tomorrow, Day after
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            val todayIso = sdfIso.format(Date())
                            val tomorrowCal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }
                            val tomorrowIso = sdfIso.format(tomorrowCal.time)
                            val dayAfterCal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 2) }
                            val dayAfterIso = sdfIso.format(dayAfterCal.time)

                            listOf(
                                "Hoy" to todayIso,
                                "Mañana" to tomorrowIso,
                                "Pasado" to dayAfterIso
                            ).forEach { (label, iso) ->
                                val isSelected = selectedDate == iso
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) PrimaryContainer.copy(alpha = 0.25f) else SurfaceDark,
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isSelected) PrimaryContainer else Color.Transparent
                                    ),
                                    modifier = Modifier
                                        .clickable { selectedDate = iso }
                                        .padding(vertical = 2.dp)
                                ) {
                                    Text(
                                        text = label,
                                        color = if (isSelected) PrimaryContainer else OnSurfaceVariant,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Title
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título de la tarea") },
                    placeholder = { Text("Ej. Estudio de Matemáticas") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = OnSurface,
                        unfocusedTextColor = OnSurface,
                        focusedBorderColor = PrimaryContainer,
                        unfocusedBorderColor = OutlineVariant,
                        focusedLabelColor = PrimaryContainer,
                        unfocusedLabelColor = OnSurfaceVariant
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_task_title")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Description
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción / Notas (opcional)") },
                    placeholder = { Text("Ej. Repasar teoría y ejercicios") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = OnSurface,
                        unfocusedTextColor = OnSurface,
                        focusedBorderColor = PrimaryContainer,
                        unfocusedBorderColor = OutlineVariant,
                        focusedLabelColor = PrimaryContainer,
                        unfocusedLabelColor = OnSurfaceVariant
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Start & End Time
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = startTime,
                        onValueChange = { startTime = it },
                        label = { Text("Inicio (HH:mm)") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = OnSurface,
                            unfocusedTextColor = OnSurface,
                            focusedBorderColor = PrimaryContainer,
                            unfocusedBorderColor = OutlineVariant,
                            focusedLabelColor = PrimaryContainer,
                            unfocusedLabelColor = OnSurfaceVariant
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = endTime,
                        onValueChange = { endTime = it },
                        label = { Text("Fin (HH:mm)") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = OnSurface,
                            unfocusedTextColor = OnSurface,
                            focusedBorderColor = PrimaryContainer,
                            unfocusedBorderColor = OutlineVariant,
                            focusedLabelColor = PrimaryContainer,
                            unfocusedLabelColor = OnSurfaceVariant
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Color Palette Picker with "+ Más" toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Color de la Tarea",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = OnSurfaceVariant
                    )
                    TextButton(
                        onClick = { showMoreColors = !showMoreColors },
                        modifier = Modifier.height(28.dp)
                    ) {
                        Text(
                            text = if (showMoreColors) "− Menos" else "+ Más colores",
                            color = PrimaryContainer,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))

                // Base Colors row with "+ Más" Pill
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    BasePalette.forEach { (hex, color) ->
                        val isSelected = selectedColorHex.equals(hex, ignoreCase = true)
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(color)
                                .clickable { selectedColorHex = hex }
                                .border(
                                    width = if (isSelected) 3.dp else 0.dp,
                                    color = if (isSelected) Color.White else Color.Transparent,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = "Selected",
                                    tint = Color.Black,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    // "+ Más" pill button right next to current colors
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (showMoreColors) PrimaryContainer else SurfaceContainer,
                        modifier = Modifier
                            .height(34.dp)
                            .clickable { showMoreColors = !showMoreColors }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        ) {
                            Icon(
                                imageVector = if (showMoreColors) Icons.Filled.ExpandLess else Icons.Filled.Add,
                                contentDescription = "Más colores",
                                tint = if (showMoreColors) OnPrimaryContainer else OnSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (showMoreColors) "Menos" else "Más",
                                color = if (showMoreColors) OnPrimaryContainer else OnSurfaceVariant,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }

                // Extended Palette Expansion
                AnimatedVisibility(
                    visible = showMoreColors,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column(modifier = Modifier.padding(top = 10.dp)) {
                        Text(
                            text = "Paleta extendida",
                            style = MaterialTheme.typography.labelSmall,
                            color = OnSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            ExtendedPalette.forEach { (hex, color) ->
                                val isSelected = selectedColorHex.equals(hex, ignoreCase = true)
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                        .clickable { selectedColorHex = hex }
                                        .border(
                                            width = if (isSelected) 3.dp else 0.dp,
                                            color = if (isSelected) Color.White else Color.Transparent,
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Filled.Check,
                                            contentDescription = "Selected",
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Fixed Task & Repetition Options
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = SurfaceContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.PushPin,
                                    contentDescription = "Tarea Fija",
                                    tint = if (isFixed) AccentAmber else OnSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                                Column {
                                    Text(
                                        text = "Tarea Fija / Rutina",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                        color = OnSurface
                                    )
                                    Text(
                                        text = "Destacar como compromiso fijo",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = OnSurfaceVariant
                                    )
                                }
                            }
                            Switch(
                                checked = isFixed,
                                onCheckedChange = { isFixed = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = AccentAmber,
                                    checkedTrackColor = AccentAmber.copy(alpha = 0.35f),
                                    uncheckedThumbColor = OnSurfaceVariant,
                                    uncheckedTrackColor = SurfaceDark
                                )
                            )
                        }

                        if (isFixed) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Repetición",
                                style = MaterialTheme.typography.labelSmall,
                                color = OnSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                listOf(
                                    "NONE" to "Solo esta fecha",
                                    "DAILY" to "Todos los días",
                                    "WEEKLY" to "Semanal"
                                ).forEach { (mode, label) ->
                                    val isSelected = repeatMode == mode
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { repeatMode = mode },
                                        label = { Text(label, fontSize = 11.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = AccentAmber.copy(alpha = 0.25f),
                                            selectedLabelColor = AccentAmber,
                                            containerColor = SurfaceDark,
                                            labelColor = OnSurfaceVariant
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Notifications Section
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = SurfaceContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Recordatorios y Notificaciones",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = OnSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        // Notify on Start
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Alarm,
                                    contentDescription = "Notificar al inicio",
                                    tint = if (notifyOnStart) PrimaryContainer else OnSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                                Column {
                                    Text(
                                        text = "Notificar al iniciar ($startTime)",
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                        color = OnSurface
                                    )
                                    Text(
                                        text = "Alarma y aviso para comenzar a tiempo",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = OnSurfaceVariant
                                    )
                                }
                            }
                            Switch(
                                checked = notifyOnStart,
                                onCheckedChange = { notifyOnStart = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = PrimaryContainer,
                                    checkedTrackColor = PrimaryContainer.copy(alpha = 0.35f),
                                    uncheckedThumbColor = OnSurfaceVariant,
                                    uncheckedTrackColor = SurfaceDark
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Notify on End
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.NotificationsActive,
                                    contentDescription = "Notificar al finalizar",
                                    tint = if (notifyOnEnd) AccentEmerald else OnSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                                Column {
                                    Text(
                                        text = "Notificar al finalizar ($endTime)",
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                        color = OnSurface
                                    )
                                    Text(
                                        text = "Aviso de cierre de bloque de tiempo",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = OnSurfaceVariant
                                    )
                                }
                            }
                            Switch(
                                checked = notifyOnEnd,
                                onCheckedChange = { notifyOnEnd = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = AccentEmerald,
                                    checkedTrackColor = AccentEmerald.copy(alpha = 0.35f),
                                    uncheckedThumbColor = OnSurfaceVariant,
                                    uncheckedTrackColor = SurfaceDark
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Icon Picker
                Text(
                    text = "Icono de Tarea",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = OnSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(AvailableIcons) { (name, icon) ->
                        val isSelected = selectedIconName == name
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) PrimaryContainer else SurfaceContainer)
                                .clickable { selectedIconName = name },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = name,
                                tint = if (isSelected) OnPrimaryContainer else OnSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancelar", color = OnSurfaceVariant)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                val dur = calculateMinutesDiff(startTime, endTime)
                                onSave(
                                    TaskEntity(
                                        id = taskToEdit?.id ?: 0L,
                                        title = title.trim(),
                                        description = description.trim(),
                                        date = selectedDate,
                                        startTime = startTime.trim(),
                                        endTime = endTime.trim(),
                                        durationMinutes = dur,
                                        colorHex = selectedColorHex,
                                        iconName = selectedIconName,
                                        tag = tag.trim(),
                                        isFixed = isFixed,
                                        repeatMode = repeatMode,
                                        notifyOnStart = notifyOnStart,
                                        notifyOnEnd = notifyOnEnd,
                                        isCompleted = taskToEdit?.isCompleted ?: false
                                    )
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryContainer,
                            contentColor = OnPrimaryContainer
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.testTag("save_task_button")
                    ) {
                        Text("Guardar Tarea", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                    }
                }
            }
        }
    }
}

fun calculateMinutesDiff(start: String, end: String): Int {
    return try {
        val sParts = start.split(":")
        val eParts = end.split(":")
        val sMin = sParts[0].toInt() * 60 + sParts[1].toInt()
        val eMin = eParts[0].toInt() * 60 + eParts[1].toInt()
        val diff = eMin - sMin
        if (diff > 0) diff else 60
    } catch (e: Exception) {
        60
    }
}
