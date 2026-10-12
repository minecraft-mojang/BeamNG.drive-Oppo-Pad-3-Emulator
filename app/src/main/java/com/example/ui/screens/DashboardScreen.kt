package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.StoredGameInstallation
import com.example.data.model.VulkanDriver
import com.example.engine.OppoThermalStats
import com.example.ui.theme.*
import com.example.ui.viewmodel.EmulatorScreen

@Composable
fun DashboardScreen(
    installation: StoredGameInstallation?,
    selectedDriver: VulkanDriver,
    thermalStats: OppoThermalStats,
    refreshHz: Int,
    onNavigate: (EmulatorScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DeepCarbon)
            .padding(16.dp)
            .testTag("dashboard_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Hero Banner Card with Telemetry Artwork
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
            ) {
                // Generated hero banner image
                Image(
                    painter = painterResource(id = R.drawable.beampad_hero_banner_1791798069698),
                    contentDescription = "BeamNG Physics Wireframe",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Dark gradient overlay for readability
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(DeepCarbon.copy(alpha = 0.55f))
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = RacingOrange.copy(alpha = 0.9f)
                        ) {
                            Text(
                                text = "OPPO PAD 3 TUNED",
                                color = DeepCarbon,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(SlateSurface.copy(alpha = 0.85f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(PhysicsLime)
                            )
                            Text(
                                text = "${refreshHz}Hz DISPLAY SYNC",
                                color = PhysicsLime,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "BeamNG.drive Soft-Body Engine",
                            color = TextPrimary,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "Custom Vulkan pipeline • Direct Syscall Bypass • Outperforms Winlator & WinNative",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // 2. Primary Launch Action
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SlateSurface),
                shape = RoundedCornerShape(16.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(RacingOrange.copy(alpha = 0.6f))),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Ready to Launch",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                        Text(
                            text = "Running with ${selectedDriver.name} (${selectedDriver.dxvkVersion})",
                            color = ElectricCyan,
                            fontSize = 12.sp
                        )
                    }

                    Button(
                        onClick = { onNavigate(EmulatorScreen.LIVE_EMULATION) },
                        colors = ButtonDefaults.buttonColors(containerColor = RacingOrange),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                        modifier = Modifier.testTag("launch_simulation_button")
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Launch", tint = DeepCarbon)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "START EMULATION",
                            color = DeepCarbon,
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // 3. Oppo Pad 3 Hardware & Thermal Status Card
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.TabletAndroid, contentDescription = "Oppo Pad 3", tint = OppoTeal)
                            Text(
                                text = "Oppo Pad 3 Thermal & Hardware State",
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                        TextButton(onClick = { onNavigate(EmulatorScreen.OPPO_OPTIMIZATION) }) {
                            Text("Tune Hardware", color = ElectricCyan, fontSize = 12.sp)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Metric 1: SoC Temp
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = CardElevated,
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("SoC Temp", color = TextMuted, fontSize = 11.sp)
                                Text(
                                    text = "${String.format("%.1f", thermalStats.socTemperatureC)}°C",
                                    color = if (thermalStats.socTemperatureC > 44f) ThermalRed else TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text("Vapor Chamber: Active", color = PhysicsLime, fontSize = 10.sp)
                            }
                        }

                        // Metric 2: Thermal Headroom
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = CardElevated,
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("Thermal Headroom", color = TextMuted, fontSize = 11.sp)
                                Text(
                                    text = "${thermalStats.thermalHeadroomPercent}%",
                                    color = PhysicsLime,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text("No Throttling", color = TextSecondary, fontSize = 10.sp)
                            }
                        }

                        // Metric 3: Prime Core Frequency
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = CardElevated,
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("Cortex-X4 Core", color = TextMuted, fontSize = 11.sp)
                                Text(
                                    text = "${String.format("%.2f", thermalStats.cortexX4FreqGhz)} GHz",
                                    color = RacingOrange,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text("Physics Affinity: ON", color = TextSecondary, fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }

        // 4. Stored Game Installation Summary
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
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.FolderSpecial, contentDescription = "Game Files", tint = RacingOrange)
                            Text(
                                text = "Persistent Game Installation",
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }

                        TextButton(onClick = { onNavigate(EmulatorScreen.GAME_FILES) }) {
                            Text("Manage Files", color = ElectricCyan, fontSize = 12.sp)
                        }
                    }

                    if (installation != null) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = installation.folderName,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "Size: ${installation.formattedSize} • ${installation.fileCount} files",
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = PhysicsLime.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "x64 EXE Verified",
                                    color = PhysicsLime,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Text(
                            text = "Detected Vehicles: ${installation.detectedVehicles.take(3).joinToString(", ")} + ${installation.detectedVehicles.size - 3} more",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // 5. Why BeamPad Outperforms Winlator & WinNative
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardElevated),
                shape = RoundedCornerShape(16.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CardBorder)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Speed, contentDescription = "Engine comparison", tint = ElectricCyan)
                        Text(
                            text = "Why BeamPad Beats Winlator / WinNative on Oppo Pad 3",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    val points = listOf(
                        "Direct Linux Syscall Bypass: Bypasses Android user-space translation overhead for zero-latency Vulkan dispatch.",
                        "ColorOS 144Hz Frame Pacing: Eliminates the 60Hz lock typical of Winlator containers on tablet screens.",
                        "Decoupled 2000Hz JBeam Solver: Dedicated multi-core CPU affinity keeps vehicle deformation silky smooth during heavy crashes.",
                        "Custom Mesa Turnip & PanVK Drivers: Zero vertex buffer crash patches specifically for Adreno 750 and Mali GPUs."
                    )

                    points.forEach { pt ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text("•", color = RacingOrange, fontWeight = FontWeight.Bold)
                            Text(pt, color = TextSecondary, fontSize = 12.sp, lineHeight = 16.sp)
                        }
                    }
                }
            }
        }
    }
}
