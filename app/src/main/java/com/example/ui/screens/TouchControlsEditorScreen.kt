package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SteeringMode
import com.example.data.model.TouchControlsConfig
import com.example.data.model.TransmissionMode
import com.example.ui.theme.*

@Composable
fun TouchControlsEditorScreen(
    config: TouchControlsConfig,
    onUpdateConfig: (TouchControlsConfig) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DeepCarbon)
            .padding(16.dp)
            .testTag("touch_controls_editor_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Header Banner
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SlateSurface),
                shape = RoundedCornerShape(16.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CardBorder)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.SportsEsports, contentDescription = "Controls", tint = RacingOrange)
                        Text(
                            text = "Touch Controls & Vehicle Interface",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                    }
                    Text(
                        text = "Calibrated for 11.6\" & 12.1\" Oppo Pad 3 tablet ergonomics with multi-touch pedal heights.",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // 2. Steering Input Mode
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SlateSurface),
                shape = RoundedCornerShape(16.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CardBorder)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Steering Control Type", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)

                    SteeringMode.values().forEach { mode ->
                        val isSel = mode == config.steeringMode
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSel) CardElevated else SlateSurface,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSel) RacingOrange else CardBorder
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onUpdateConfig(config.copy(steeringMode = mode)) }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(
                                        imageVector = when (mode) {
                                            SteeringMode.VIRTUAL_WHEEL -> Icons.Default.Adjust
                                            SteeringMode.ANALOG_JOYSTICK -> Icons.Default.SportsEsports
                                            SteeringMode.GYRO_TILT -> Icons.Default.ScreenRotation
                                            SteeringMode.TOUCH_SLIDER -> Icons.Default.LinearScale
                                        },
                                        contentDescription = mode.displayName,
                                        tint = if (isSel) RacingOrange else TextSecondary
                                    )
                                    Text(
                                        text = mode.displayName,
                                        color = if (isSel) RacingOrange else TextPrimary,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp
                                    )
                                }
                                if (isSel) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = "Selected", tint = RacingOrange)
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. Transmission Shifter Mode
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SlateSurface),
                shape = RoundedCornerShape(16.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CardBorder)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Transmission Shifter Layout", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)

                    TransmissionMode.values().forEach { tMode ->
                        val isSel = tMode == config.transmissionMode
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSel) CardElevated else SlateSurface,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSel) ElectricCyan else CardBorder
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onUpdateConfig(config.copy(transmissionMode = tMode)) }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = tMode.displayName,
                                    color = if (isSel) ElectricCyan else TextPrimary,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp
                                )
                                if (isSel) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = "Selected", tint = ElectricCyan)
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4. Overlay Opacity & Haptic Feedback
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SlateSurface),
                shape = RoundedCornerShape(16.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CardBorder)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text("Haptics & Visual Opacity", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)

                    // Opacity Slider
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("On-Screen Controls Opacity", color = TextPrimary, fontSize = 13.sp)
                            Text("${(config.opacity * 100).toInt()}%", color = RacingOrange, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        Slider(
                            value = config.opacity,
                            onValueChange = { onUpdateConfig(config.copy(opacity = it)) },
                            valueRange = 0.3f..1.0f,
                            colors = SliderDefaults.colors(
                                thumbColor = RacingOrange,
                                activeTrackColor = RacingOrange,
                                inactiveTrackColor = CardBorder
                            )
                        )
                    }

                    HorizontalDivider(color = CardBorder)

                    // Haptic feedback toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Haptic Feedback on Shifts & Collisions", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text("Uses Oppo Pad 3 linear vibration motor for wheel slip and crash impacts", color = TextSecondary, fontSize = 11.sp)
                        }
                        Switch(
                            checked = config.hapticFeedback,
                            onCheckedChange = { onUpdateConfig(config.copy(hapticFeedback = it)) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = RacingOrange,
                                checkedTrackColor = RacingOrange.copy(alpha = 0.3f)
                            )
                        )
                    }
                }
            }
        }
    }
}
