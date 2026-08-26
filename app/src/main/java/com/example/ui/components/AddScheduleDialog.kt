package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.data.local.entity.WeeklyScheduleEntity
import com.example.ui.theme.OnPrimaryContainer
import com.example.ui.theme.OnSurface
import com.example.ui.theme.OnSurfaceVariant
import com.example.ui.theme.OutlineVariant
import com.example.ui.theme.PrimaryContainer
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceDark

val DaysOfWeekLabels = listOf(
    1 to "Lun",
    2 to "Mar",
    3 to "Mié",
    4 to "Jue",
    5 to "Vie",
    6 to "Sáb",
    7 to "Dom"
)

@Composable
fun AddScheduleDialog(
    initialDay: Int = 1,
    scheduleToEdit: WeeklyScheduleEntity? = null,
    onDismiss: () -> Unit,
    onSave: (WeeklyScheduleEntity) -> Unit
) {
    var selectedDay by remember { mutableIntStateOf(scheduleToEdit?.dayOfWeek ?: initialDay) }
    var title by remember { mutableStateOf(scheduleToEdit?.title ?: "") }
    var location by remember { mutableStateOf(scheduleToEdit?.location ?: "") }
    var instructor by remember { mutableStateOf(scheduleToEdit?.instructorOrTag ?: "") }
    var startTime by remember { mutableStateOf(scheduleToEdit?.startTime ?: "08:00") }
    var endTime by remember { mutableStateOf(scheduleToEdit?.endTime ?: "09:30") }
    var selectedColorHex by remember { mutableStateOf(scheduleToEdit?.colorHex ?: "#2DD4BF") }
    var isRecess by remember { mutableStateOf(scheduleToEdit?.isRecess ?: false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = SurfaceDark,
            border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceContainer),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
            ) {
                Text(
                    text = if (scheduleToEdit == null) "Añadir al Horario Semanal" else "Editar Horario",
                    style = MaterialTheme.typography.titleLarge,
                    color = OnSurface
                )
                Spacer(modifier = Modifier.height(14.dp))

                // Day of Week Selector
                Text("Día de la semana", style = MaterialTheme.typography.labelMedium, color = OnSurfaceVariant)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    DaysOfWeekLabels.forEach { (dayInt, label) ->
                        val isSelected = selectedDay == dayInt
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) PrimaryContainer else SurfaceContainer)
                                .clickable { selectedDay = dayInt },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isSelected) OnPrimaryContainer else OnSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Title
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Asignatura / Título") },
                    placeholder = { Text("Ej. Matemáticas Avanzadas") },
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
                        .testTag("input_schedule_title")
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Location
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Aula / Ubicación") },
                    placeholder = { Text("Ej. Aula 102 - Edificio Central") },
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

                Spacer(modifier = Modifier.height(8.dp))

                // Instructor / Tag
                OutlinedTextField(
                    value = instructor,
                    onValueChange = { instructor = it },
                    label = { Text("Docente / Etiqueta") },
                    placeholder = { Text("Ej. Dr. Ramírez o Práctica") },
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

                Spacer(modifier = Modifier.height(8.dp))

                // Time
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

                Spacer(modifier = Modifier.height(10.dp))

                // Is Recess Checkbox
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isRecess = !isRecess }
                ) {
                    Checkbox(
                        checked = isRecess,
                        onCheckedChange = { isRecess = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = PrimaryContainer,
                            checkmarkColor = OnPrimaryContainer,
                            uncheckedColor = OutlineVariant
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Es un Receso / Pausa", color = OnSurface, style = MaterialTheme.typography.bodyMedium)
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Color palette
                Text("Color", style = MaterialTheme.typography.labelMedium, color = OnSurfaceVariant)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    BasePalette.forEach { (hex, color) ->
                        val isSelected = selectedColorHex.equals(hex, ignoreCase = true)
                        Box(
                            modifier = Modifier
                                .size(28.dp)
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
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

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
                                onSave(
                                    WeeklyScheduleEntity(
                                        id = scheduleToEdit?.id ?: 0L,
                                        dayOfWeek = selectedDay,
                                        title = title.trim(),
                                        location = location.trim(),
                                        instructorOrTag = instructor.trim(),
                                        startTime = startTime.trim(),
                                        endTime = endTime.trim(),
                                        colorHex = selectedColorHex,
                                        isRecess = isRecess
                                    )
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryContainer,
                            contentColor = OnPrimaryContainer
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("save_schedule_button")
                    ) {
                        Text("Guardar", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }
    }
}
