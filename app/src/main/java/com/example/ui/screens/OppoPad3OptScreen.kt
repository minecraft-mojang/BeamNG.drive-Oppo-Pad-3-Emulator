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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ResolutionScale
import com.example.data.model.ScreenRefreshMode
import com.example.data.model.ThermalGovernorProfile
import com.example.engine.OppoThermalStats
import com.example.ui.theme.*

@Composable
fun OppoPad3OptScreen(
    refreshMode: ScreenRefreshMode,
    thermalProfile: ThermalGovernorProfile,
    resolutionScale: ResolutionScale,
    thermalStats: OppoThermalStats,
    zramGb: Int,
    onSetRefreshMode: (ScreenRefreshMode) -> Unit,
    onSetThermalProfile: (ThermalGovernorProfile) -> Unit,
    onSetResolutionScale: (ResolutionScale) -> Unit,
    onSetZramGb: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DeepCarbon)
            .padding(16.dp)
            .testTag("oppo_optimization_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Oppo Pad 3 Hardware Identification Banner
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SlateSurface),
                shape = RoundedCornerShape(16.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(OppoTeal.copy(alpha = 0.5f))),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.TabletAndroid, contentDescription = "Tablet", tint = OppoTeal)
                            Text(
                                text = "Oppo Pad 3 Platform Profile",
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp
                            )
                        }
                        Text(
                            text = "ColorOS 15 • 144Hz 3K LCD (2800x2000) • Multi-Layer Graphite Vapor Chamber",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = OppoTeal.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "HARDWARE TUNED",
                            color = OppoTeal,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // 2. Refresh Rate Selector (144Hz, 120Hz, 90Hz, 60Hz)
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
                        Column {
                            Text(
                                text = "Display Refresh Rate & Frame Pacer",
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "Hardware vsync syncs physics sub-ticks directly with panel scan-out",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }

                        Text(
                            text = "${refreshMode.hz} Hz",
                            color = PhysicsLime,
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ScreenRefreshMode.values().forEach { mode ->
                            val isSel = mode == refreshMode
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSel) RacingOrange.copy(alpha = 0.2f) else CardElevated
                                ),
                                shape = RoundedCornerShape(10.dp),
                                border = CardDefaults.outlinedCardBorder().copy(
                                    brush = androidx.compose.ui.graphics.SolidColor(
                                        if (isSel) RacingOrange else CardBorder
                                    )
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onSetRefreshMode(mode) }
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 12.dp, horizontal = 6.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "${mode.hz}Hz",
                                        color = if (isSel) RacingOrange else TextPrimary,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 16.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Text(
                                        text = when (mode) {
                                            ScreenRefreshMode.HZ_144 -> "Ultra Fluid"
                                            ScreenRefreshMode.HZ_120 -> "Balanced"
                                            ScreenRefreshMode.HZ_90 -> "Eco"
                                            ScreenRefreshMode.HZ_60 -> "Saver"
                                        },
                                        color = TextSecondary,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }

                    Text(
                        text = refreshMode.powerDescription,
                        color = ElectricCyan,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // 3. Oppo Thermal Governor Profiles & Live Telemetry
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
                    Text(
                        text = "ColorOS Thermal Management & Vapor Chamber",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )

                    // Live gauges row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = CardElevated,
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("SoC Temperature", color = TextMuted, fontSize = 11.sp)
                                Text(
                                    text = "${String.format("%.1f", thermalStats.socTemperatureC)}°C",
                                    color = if (thermalStats.socTemperatureC > 44f) ThermalRed else TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text("Limit: 48.0°C", color = TextMuted, fontSize = 10.sp)
                            }
                        }

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
                                Text("Android API 30+", color = TextMuted, fontSize = 10.sp)
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = CardElevated,
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("Cooling Efficiency", color = TextMuted, fontSize = 11.sp)
                                Text(
                                    text = "${String.format("%.1f", thermalStats.vaporChamberHeatEfficiency)}%",
                                    color = ElectricCyan,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text("VC Heatpipe", color = TextMuted, fontSize = 10.sp)
                            }
                        }
                    }

                    // Thermal Governor Profiles
                    ThermalGovernorProfile.values().forEach { prof ->
                        val isSel = prof == thermalProfile
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSel) CardElevated else SlateSurface,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSel) RacingOrange else CardBorder
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSetThermalProfile(prof) }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = prof.title,
                                        color = if (isSel) RacingOrange else TextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = prof.description,
                                        color = TextSecondary,
                                        fontSize = 11.sp
                                    )
                                }

                                if (isSel) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = "Active", tint = RacingOrange)
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4. Resolution Scaling & AMD FSR 2.1
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
                    Text(
                        text = "Resolution Scaling & AMD FSR 2.1 Upscaler",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )

                    ResolutionScale.values().forEach { scale ->
                        val isSel = scale == resolutionScale
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSel) CardElevated else SlateSurface,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSel) ElectricCyan else CardBorder
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSetResolutionScale(scale) }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(scale.label, color = if (isSel) ElectricCyan else TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                    Text(scale.renderRes, color = TextSecondary, fontSize = 11.sp)
                                }
                                if (isSel) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = "Active", tint = ElectricCyan)
                                }
                            }
                        }
                    }
                }
            }
        }

        // 5. Oppo RAM Expansion / ZRAM Memory Allocation
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
                        Column {
                            Text("Virtual RAM Expansion (ZRAM Swap)", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text("Allocates compressed memory for large BeamNG maps (e.g. West Coast USA)", color = TextSecondary, fontSize = 12.sp)
                        }
                        Text("$zramGb GB", color = RacingOrange, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(4, 8, 12).forEach { gb ->
                            val isSel = zramGb == gb
                            OutlinedButton(
                                onClick = { onSetZramGb(gb) },
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (isSel) RacingOrange.copy(alpha = 0.15f) else Color.Transparent,
                                    contentColor = if (isSel) RacingOrange else TextSecondary
                                ),
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) RacingOrange else CardBorder),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("+$gb GB", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
