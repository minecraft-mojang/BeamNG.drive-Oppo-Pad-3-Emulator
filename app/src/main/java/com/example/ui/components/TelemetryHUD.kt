package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TelemetryData
import com.example.ui.theme.*

@Composable
fun TelemetryHUD(
    telemetry: TelemetryData,
    activeDriverName: String,
    refreshHz: Int,
    modifier: Modifier = Modifier
) {
    val animatedRpm by animateFloatAsState(targetValue = telemetry.rpm.toFloat(), label = "rpm")
    val animatedSpeed by animateFloatAsState(targetValue = telemetry.vehicleSpeedKph, label = "speed")

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("telemetry_hud_bar"),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        // Left Column: Performance & Driver Telemetry
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(DeepCarbon.copy(alpha = 0.88f))
                .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // FPS & Refresh rate
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(PhysicsLime)
                )
                Text(
                    text = "${telemetry.currentFps} FPS",
                    color = PhysicsLime,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "@ ${refreshHz}Hz SYNC",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "${String.format("%.2f", telemetry.frameTimeMs)} ms",
                    color = ElectricCyan,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            // Driver & Syscall
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Bolt,
                    contentDescription = "Vulkan Driver",
                    tint = ElectricCyan,
                    modifier = Modifier.size(13.dp)
                )
                Text(
                    text = activeDriverName,
                    color = TextPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // JBeam calculations / sec
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Memory,
                    contentDescription = "JBeam Physics",
                    tint = RacingOrange,
                    modifier = Modifier.size(13.dp)
                )
                Text(
                    text = "JBeam: ${telemetry.jbeamPhysicsTickRate}Hz | Nodes: ${telemetry.jbeamActiveNodes} | Beams: ${telemetry.jbeamActiveBeams}",
                    color = TextSecondary,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        // Center: Speedometer, Gear, and Tachometer Cluster
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(DeepCarbon.copy(alpha = 0.90f))
                .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Gear box badge
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(RacingOrange.copy(alpha = 0.25f))
                        .border(1.dp, RacingOrange, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = telemetry.gear,
                        color = RacingOrange,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp
                    )
                }

                // Speed
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${animatedSpeed.toInt()}",
                        color = TextPrimary,
                        fontWeight = FontWeight.Black,
                        fontSize = 24.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "KM/H",
                        color = TextSecondary,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // RPM and Turbo Boost
                Column(
                    modifier = Modifier.width(130.dp),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "RPM: ${animatedRpm.toInt()}",
                            color = if (animatedRpm > 6500) ThermalRed else TextPrimary,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "${String.format("%.1f", telemetry.turboBoostPsi)} PSI",
                            color = ElectricCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    // RPM Progress bar
                    LinearProgressIndicator(
                        progress = { (animatedRpm / 8000f).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = if (animatedRpm > 6500) ThermalRed else RacingOrange,
                        trackColor = SlateSurface
                    )

                    // Turbo progress bar
                    LinearProgressIndicator(
                        progress = { (telemetry.turboBoostPsi / 25f).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = ElectricCyan,
                        trackColor = SlateSurface
                    )
                }
            }
        }

        // Right Column: Oppo Pad 3 Thermal Governor & VRAM
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(DeepCarbon.copy(alpha = 0.88f))
                .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Thermal Headroom & SoC Temp
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Thermostat,
                    contentDescription = "SoC Temperature",
                    tint = if (telemetry.socTemperatureC > 44f) ThermalRed else ElectricCyan,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "SoC: ${String.format("%.1f", telemetry.socTemperatureC)}°C",
                    color = TextPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "(${telemetry.thermalHeadroomPercent}% Headroom)",
                    color = PhysicsLime,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            // VRAM & GPU load
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Speed,
                    contentDescription = "GPU Load",
                    tint = OppoTeal,
                    modifier = Modifier.size(13.dp)
                )
                Text(
                    text = "GPU: ${telemetry.gpuLoadPercent}% | VRAM: ${telemetry.vramUsedMb}MB",
                    color = TextSecondary,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            // G-Force meter
            Text(
                text = "G-Force: Lat ${String.format("%+.2f", telemetry.gForceLateral)}G | Long ${String.format("%+.2f", telemetry.gForceLongitudinal)}G",
                color = TextMuted,
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}
