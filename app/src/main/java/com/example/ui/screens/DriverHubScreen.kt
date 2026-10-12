package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VulkanDriver
import com.example.ui.theme.*

@Composable
fun DriverHubScreen(
    availableDrivers: List<VulkanDriver>,
    selectedDriver: VulkanDriver,
    directSyscallBypass: Boolean,
    asyncShaderCaching: Boolean,
    dynarecLevel: Int,
    onSelectDriver: (VulkanDriver) -> Unit,
    onToggleDirectSyscall: (Boolean) -> Unit,
    onToggleAsyncShader: (Boolean) -> Unit,
    onSetDynarecLevel: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DeepCarbon)
            .padding(16.dp)
            .testTag("driver_hub_screen"),
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
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.DeveloperBoard, contentDescription = "Drivers", tint = ElectricCyan)
                        Text(
                            text = "Custom Vulkan Drivers & Translation Pipeline",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                    }
                    Text(
                        text = "Hardware-tuned drivers for Oppo Pad 3 (Snapdragon & Dimensity), plus universal Mali/Adreno/CPU compatibility layers.",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // 2. Active Driver Selector Header
        item {
            Text(
                text = "Available Vulkan Driver Packages (${availableDrivers.size})",
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }

        // Driver Cards
        items(availableDrivers) { driver ->
            val isSelected = driver.id == selectedDriver.id
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) CardElevated else SlateSurface
                ),
                shape = RoundedCornerShape(14.dp),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(
                        if (isSelected) RacingOrange else CardBorder
                    )
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectDriver(driver) }
                    .testTag("driver_card_${driver.id}")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .border(2.dp, if (isSelected) RacingOrange else CardBorder, CircleShape)
                                    .background(if (isSelected) RacingOrange else Color.Transparent),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(DeepCarbon)
                                    )
                                }
                            }

                            Column {
                                Text(
                                    text = driver.name,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "Target: ${driver.recommendedDevice}",
                                    color = if (driver.isRecommendedForOppoPad3) OppoTeal else TextSecondary,
                                    fontWeight = if (driver.isRecommendedForOppoPad3) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        if (driver.isRecommendedForOppoPad3) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = OppoTeal.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "OPPO PAD 3 TUNED",
                                    color = OppoTeal,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }

                    Text(
                        text = driver.description,
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )

                    // Badges for translation versions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = SlateSurface
                        ) {
                            Text(
                                text = driver.dxvkVersion,
                                color = ElectricCyan,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = SlateSurface
                        ) {
                            Text(
                                text = driver.vkd3dVersion,
                                color = PhysicsLime,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        if (driver.directSyscall) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = SlateSurface
                            ) {
                                Text(
                                    text = "Direct Syscall",
                                    color = RacingOrange,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. Advanced Translation Optimizations (Winlator / WinNative Killer Features)
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
                        text = "Translation Engine Optimizations",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )

                    // Toggle: Direct Syscall Bypass
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Direct Kernel Syscall KGSL Bypass", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text("Bypasses Android NDK socket bridging. Grants Direct3D 11 bare-metal GPU queue access.", color = TextSecondary, fontSize = 11.sp)
                        }
                        Switch(
                            checked = directSyscallBypass,
                            onCheckedChange = onToggleDirectSyscall,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = RacingOrange,
                                checkedTrackColor = RacingOrange.copy(alpha = 0.3f)
                            )
                        )
                    }

                    HorizontalDivider(color = CardBorder)

                    // Toggle: Async Shader Compilation
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("DXVK 2.4 Asynchronous Pipeline Shaders", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text("Stops micro-stuttering during metal impacts, particle sparks, and tire smoke.", color = TextSecondary, fontSize = 11.sp)
                        }
                        Switch(
                            checked = asyncShaderCaching,
                            onCheckedChange = onToggleAsyncShader,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = ElectricCyan,
                                checkedTrackColor = ElectricCyan.copy(alpha = 0.3f)
                            )
                        )
                    }

                    HorizontalDivider(color = CardBorder)

                    // Dynarec Optimization Level (Safe, Fast, Extreme)
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Box64 / FEX Dynarec Level", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text(
                                text = when (dynarecLevel) {
                                    0 -> "Safe (Strict IEEE-754)"
                                    1 -> "Fast (JIT Aggressive)"
                                    else -> "Extreme (AVX2 + Fast NaN)"
                                },
                                color = RacingOrange,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("Safe", "Fast", "Extreme").forEachIndexed { idx, label ->
                                val isLvl = dynarecLevel == idx
                                OutlinedButton(
                                    onClick = { onSetDynarecLevel(idx) },
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = if (isLvl) RacingOrange.copy(alpha = 0.15f) else Color.Transparent,
                                        contentColor = if (isLvl) RacingOrange else TextSecondary
                                    ),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isLvl) RacingOrange else CardBorder),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
