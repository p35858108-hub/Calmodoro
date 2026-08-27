package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CozyBorder
import com.example.ui.theme.CozyCardBg
import com.example.ui.theme.CozyCocoaMuted
import com.example.ui.theme.CozyForestDark
import com.example.ui.theme.CozyLeafGreen
import com.example.ui.theme.CozyLeafGreenContainer

enum class AppTab(val label: String, val testTag: String) {
    CALENDAR("Calendario", "tab_calendar"),
    POMODORO("Pomodoro", "tab_pomodoro"),
    SCHEDULE("Horario", "tab_schedule"),
    SETTINGS("Ajustes", "tab_settings")
}

@Composable
fun CalmodoroBottomNavBar(
    selectedTab: AppTab,
    onTabSelected: (AppTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        SurfaceNavCard(
            selectedTab = selectedTab,
            onTabSelected = onTabSelected
        )
    }
}

@Composable
private fun SurfaceNavCard(
    selectedTab: AppTab,
    onTabSelected: (AppTab) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(26.dp),
                spotColor = Color(0x3348A868),
                ambientColor = Color(0x223A2E24)
            )
            .clip(RoundedCornerShape(26.dp))
            .background(CozyCardBg)
            .border(2.dp, CozyBorder, RoundedCornerShape(26.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppTab.entries.forEach { tab ->
                val isSelected = tab == selectedTab
                val interactionSource = remember { MutableInteractionSource() }

                val (activeIcon, inactiveIcon) = when (tab) {
                    AppTab.CALENDAR -> Icons.Filled.CalendarToday to Icons.Outlined.CalendarToday
                    AppTab.POMODORO -> Icons.Filled.Timer to Icons.Outlined.Timer
                    AppTab.SCHEDULE -> Icons.Filled.School to Icons.Outlined.School
                    AppTab.SETTINGS -> Icons.Filled.Settings to Icons.Outlined.Settings
                }

                val pillBg by animateColorAsState(
                    targetValue = if (isSelected) CozyLeafGreenContainer else Color.Transparent,
                    animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                    label = "pill_bg"
                )
                val contentColor by animateColorAsState(
                    targetValue = if (isSelected) CozyForestDark else CozyCocoaMuted,
                    label = "content_color"
                )

                Box(
                    modifier = Modifier
                        .testTag(tab.testTag)
                        .clip(RoundedCornerShape(20.dp))
                        .background(pillBg)
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null
                        ) { onTabSelected(tab) }
                        .padding(horizontal = if (isSelected) 14.dp else 10.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = if (isSelected) activeIcon else inactiveIcon,
                            contentDescription = tab.label,
                            tint = if (isSelected) CozyLeafGreen else CozyCocoaMuted,
                            modifier = Modifier.size(20.dp)
                        )
                        if (isSelected) {
                            Text(
                                text = tab.label,
                                color = contentColor,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

