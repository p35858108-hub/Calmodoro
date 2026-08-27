package com.example.ui.settings

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
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

val AvailableSounds = listOf(
    "digital_bell" to "Campana de Isla 🔔",
    "wind" to "Campanillas de Viento 🍃",
    "lofi" to "Melodía Acústica 🎶",
    "minimal" to "Gota de Rocío 💧"
)

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onRequestNotificationPermission: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val settings by viewModel.settingsState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    var showResetDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CozyCreamBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 18.dp, vertical = 14.dp)
                .padding(bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero Banner Illustration Card
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = CozyCardBg,
                border = androidx.compose.foundation.BorderStroke(2.dp, CozyBorder),
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Image(
                        painter = painterResource(id = R.drawable.img_cozy_banner),
                        contentDescription = "Pueblo Cozy",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .clip(RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Ajustes de la Isla",
                                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                                color = CozyForestDark
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(Icons.Filled.Eco, contentDescription = null, tint = CozyLeafGreen, modifier = Modifier.size(18.dp))
                        }
                        Text(
                            text = "Personaliza tus rutinas, sonidos relajantes y preferencias de estudio.",
                            style = MaterialTheme.typography.bodySmall,
                            color = CozyCocoaMuted,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }

            // Section 1: Pomodoro Durations Card
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = CozyCardBg,
                border = androidx.compose.foundation.BorderStroke(2.dp, CozyBorder),
                shadowElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = CozyHoneyContainer,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.Timer,
                                    contentDescription = null,
                                    tint = CozyHoney,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Tiempos del Pomodoro",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = CozyForestDark
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Focus Duration Slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Duración de Enfoque", color = CozyCocoaText, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = CozyLeafGreenContainer
                        ) {
                            Text(
                                "${settings.focusDurationMinutes} min",
                                color = CozyForestDark,
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Slider(
                        value = settings.focusDurationMinutes.toFloat(),
                        onValueChange = { viewModel.updateFocusDuration(it.toInt()) },
                        valueRange = 10f..60f,
                        steps = 9,
                        colors = SliderDefaults.colors(
                            thumbColor = CozyLeafGreen,
                            activeTrackColor = CozyLeafGreen,
                            inactiveTrackColor = CozyCardAlt
                        ),
                        modifier = Modifier.testTag("slider_focus_duration")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Short Break Slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Descanso Corto", color = CozyCocoaText, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = CozySkyContainer
                        ) {
                            Text(
                                "${settings.shortBreakMinutes} min",
                                color = CozySky,
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Slider(
                        value = settings.shortBreakMinutes.toFloat(),
                        onValueChange = { viewModel.updateShortBreak(it.toInt()) },
                        valueRange = 1f..15f,
                        steps = 13,
                        colors = SliderDefaults.colors(
                            thumbColor = CozySky,
                            activeTrackColor = CozySky,
                            inactiveTrackColor = CozyCardAlt
                        ),
                        modifier = Modifier.testTag("slider_short_break")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Long Break Slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Descanso Largo", color = CozyCocoaText, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = CozyPeachContainer
                        ) {
                            Text(
                                "${settings.longBreakMinutes} min",
                                color = CozyPeach,
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Slider(
                        value = settings.longBreakMinutes.toFloat(),
                        onValueChange = { viewModel.updateLongBreak(it.toInt()) },
                        valueRange = 5f..30f,
                        steps = 4,
                        colors = SliderDefaults.colors(
                            thumbColor = CozyPeach,
                            activeTrackColor = CozyPeach,
                            inactiveTrackColor = CozyCardAlt
                        ),
                        modifier = Modifier.testTag("slider_long_break")
                    )
                }
            }

            // Section 2: Audio & Alarm Tone Card
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = CozyCardBg,
                border = androidx.compose.foundation.BorderStroke(2.dp, CozyBorder),
                shadowElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = CozySkyContainer,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.VolumeUp,
                                    contentDescription = null,
                                    tint = CozySky,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Campanilla de Fin de Sesión",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = CozyForestDark
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    AvailableSounds.forEach { (id, name) ->
                        val isSelected = settings.soundChoice == id
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) CozyLeafGreenContainer else CozyCardAlt,
                            border = androidx.compose.foundation.BorderStroke(
                                1.5.dp,
                                if (isSelected) CozyLeafGreen else CozyBorderSubtle
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { viewModel.updateSoundChoice(id) }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (isSelected) Icons.Filled.CheckCircle else Icons.Filled.VolumeUp,
                                        contentDescription = null,
                                        tint = if (isSelected) CozyLeafGreen else CozyCocoaMuted,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = name,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        ),
                                        color = CozyCocoaText
                                    )
                                }

                                IconButton(
                                    onClick = { viewModel.previewSound(id) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.PlayArrow,
                                        contentDescription = "Escuchar prueba",
                                        tint = CozyForestDark,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Section 3: System, Notifications & Vibration
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = CozyCardBg,
                border = androidx.compose.foundation.BorderStroke(2.dp, CozyBorder),
                shadowElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = CozyPeachContainer,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.Notifications,
                                    contentDescription = null,
                                    tint = CozyPeach,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Avisos y Vibración",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = CozyForestDark
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Notification Switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Avisos en Segundo Plano", color = CozyCocoaText, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium))
                            Text("Muestra el temporizador en la barra de estado", color = CozyCocoaMuted, style = MaterialTheme.typography.labelSmall)
                        }
                        Switch(
                            checked = settings.notificationsEnabled,
                            onCheckedChange = { enabled ->
                                viewModel.updateNotifications(enabled)
                                if (enabled) onRequestNotificationPermission()
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = CozyLeafGreen,
                                uncheckedTrackColor = CozyCardAlt
                            )
                        )
                    }

                    HorizontalDivider(color = CozyBorderSubtle, modifier = Modifier.padding(vertical = 10.dp))

                    // Vibration Switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Vibración Háptica", color = CozyCocoaText, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium))
                            Text("Vibra suavemente al iniciar, pausar y terminar", color = CozyCocoaMuted, style = MaterialTheme.typography.labelSmall)
                        }
                        Switch(
                            checked = settings.vibrationEnabled,
                            onCheckedChange = { viewModel.updateVibration(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = CozyLeafGreen,
                                uncheckedTrackColor = CozyCardAlt
                            )
                        )
                    }
                }
            }

            // Section 4: Data Management & Reset
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = CozyCardBg,
                border = androidx.compose.foundation.BorderStroke(2.dp, CozyBorder),
                shadowElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = CozyPeachContainer,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.DeleteForever,
                                    contentDescription = null,
                                    tint = CozyPeach,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Gestión de Datos",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = CozyForestDark
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Tus datos se guardan de forma local y segura en este dispositivo.",
                        style = MaterialTheme.typography.bodySmall,
                        color = CozyCocoaMuted
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.clearAllTasks {
                                    Toast.makeText(context, "Se vaciaron todas las tareas", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CozyCardAlt,
                                contentColor = CozyCocoaText
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Limpiar Tareas", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold))
                        }

                        Button(
                            onClick = {
                                viewModel.clearAllSchedule {
                                    Toast.makeText(context, "Se borró el horario semanal", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CozyCardAlt,
                                contentColor = CozyCocoaText
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Limpiar Horario", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold))
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = { showResetDialog = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CozyPeachContainer,
                            contentColor = CozyPeach
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Filled.Refresh, contentDescription = null, tint = CozyPeach, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Restablecer todos los datos de la app", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                    }
                }
            }

            // App Info Card
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = CozyCardBg,
                border = androidx.compose.foundation.BorderStroke(2.dp, CozyBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = CozyLeafGreenContainer,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Filled.Info,
                                contentDescription = null,
                                tint = CozyLeafGreen,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Calmodoro • Isla de Productividad 🍃",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = CozyCocoaText
                        )
                        Text(
                            text = "Versión 2.0 • Estilo Cozy & Enfoque Académico",
                            style = MaterialTheme.typography.labelSmall,
                            color = CozyCocoaMuted
                        )
                    }
                }
            }
        }

        // Reset Confirmation Dialog
        if (showResetDialog) {
            AlertDialog(
                onDismissRequest = { showResetDialog = false },
                title = { Text("¿Restablecer todos los datos? 🍂", fontWeight = FontWeight.Bold, color = CozyForestDark) },
                text = {
                    Text("Esta acción eliminará de forma permanente todas tus tareas, horarios semanales y registros de concentración.", color = CozyCocoaText)
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.resetAllData {
                                Toast.makeText(context, "Todos los datos han sido restablecidos", Toast.LENGTH_SHORT).show()
                            }
                            showResetDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CozyPeach, contentColor = Color.White),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Sí, restablecer")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showResetDialog = false }) {
                        Text("Cancelar", color = CozyCocoaText)
                    }
                },
                containerColor = CozyCreamBg,
                shape = RoundedCornerShape(20.dp)
            )
        }
    }
}

