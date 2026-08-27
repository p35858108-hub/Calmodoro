package com.example.ui.feature

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

@Composable
fun DevelopmentScreen(
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CozyCreamBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 24.dp)
                .padding(bottom = 90.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = CozyCardBg,
                border = androidx.compose.foundation.BorderStroke(2.dp, CozyBorder),
                shadowElevation = 3.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 36.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Soft Icon Bubble
                    Surface(
                        shape = CircleShape,
                        color = CozyHoneyContainer,
                        border = androidx.compose.foundation.BorderStroke(2.dp, CozyHoney.copy(alpha = 0.5f)),
                        modifier = Modifier.size(80.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Filled.AutoAwesome,
                                contentDescription = null,
                                tint = CozyHoney,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Badge
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = CozyLeafGreenContainer,
                        border = androidx.compose.foundation.BorderStroke(1.dp, CozyLeafGreen.copy(alpha = 0.3f))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Eco,
                                contentDescription = null,
                                tint = CozyLeafGreen,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Próximamente",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                ),
                                color = CozyForestDark
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Title
                    Text(
                        text = "En desarrollo",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        ),
                        color = CozyForestDark,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Description
                    Text(
                        text = "Esta función se encuentra actualmente en desarrollo y estará disponible próximamente.",
                        style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                        color = CozyCocoaMuted,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }
        }
    }
}
