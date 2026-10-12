package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StoredGameInstallation
import com.example.data.model.TouchControlsConfig
import com.example.data.model.VulkanDriver
import com.example.engine.BeamNGPhysicsSimulator
import com.example.ui.components.SoftBodyCanvas
import com.example.ui.components.TelemetryHUD
import com.example.ui.components.TouchOverlayControls
import com.example.ui.theme.*

@Composable
fun SimulationRunnerScreen(
    simulator: BeamNGPhysicsSimulator,
    installation: StoredGameInstallation?,
    activeDriver: VulkanDriver,
    refreshHz: Int,
    touchConfig: TouchControlsConfig,
    selectedVehicle: String,
    selectedLevel: String,
    onSelectVehicle: (String) -> Unit,
    onSelectLevel: (String) -> Unit,
    onResetCar: () -> Unit,
    onRecoverCar: () -> Unit,
    onHaptic: () -> Unit,
    onExit: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Handle back button to return to dashboard
    BackHandler {
        onExit()
    }

    val telemetry by simulator.telemetry.collectAsState()
    var isNodeGrabberActive by remember { mutableStateOf(false) }
    var showVehicleMenu by remember { mutableStateOf(false) }
    var showLevelMenu by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DeepCarbon)
            .testTag("simulation_runner_screen")
    ) {
        // 1. Interactive Soft-Body Physics Canvas (144Hz Render Loop)
        SoftBodyCanvas(
            simulator = simulator,
            isNodeGrabberActive = isNodeGrabberActive,
            onNodeInteracted = { onHaptic() },
            modifier = Modifier.fillMaxSize()
        )

        // 2. Real-Time Telemetry HUD Overlay
        TelemetryHUD(
            telemetry = telemetry,
            activeDriverName = activeDriver.name,
            refreshHz = refreshHz,
            modifier = Modifier.align(Alignment.TopCenter)
        )

        // 3. Top-Right Navigation & Selector Controls
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 70.dp, end = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Vehicle Selector Dropdown Button
            Box {
                Button(
                    onClick = { showVehicleMenu = true },
                    colors = ButtonDefaults.buttonColors(containerColor = CardElevated.copy(alpha = 0.9f)),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.border(1.dp, CardBorder, RoundedCornerShape(10.dp))
                ) {
                    Icon(Icons.Default.DirectionsCar, contentDescription = "Vehicle", modifier = Modifier.size(15.dp), tint = RacingOrange)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = selectedVehicle.take(14),
                        color = TextPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                DropdownMenu(
                    expanded = showVehicleMenu,
                    onDismissRequest = { showVehicleMenu = false },
                    modifier = Modifier.background(SlateSurface)
                ) {
                    val vehicles = installation?.detectedVehicles ?: listOf("Gavril D-Series", "Ibishu Covet", "Cherrier FCV")
                    vehicles.forEach { v ->
                        DropdownMenuItem(
                            text = { Text(v, color = TextPrimary, fontSize = 12.sp) },
                            onClick = {
                                onSelectVehicle(v)
                                showVehicleMenu = false
                            }
                        )
                    }
                }
            }

            // Map Selector Dropdown Button
            Box {
                Button(
                    onClick = { showLevelMenu = true },
                    colors = ButtonDefaults.buttonColors(containerColor = CardElevated.copy(alpha = 0.9f)),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.border(1.dp, CardBorder, RoundedCornerShape(10.dp))
                ) {
                    Icon(Icons.Default.Map, contentDescription = "Map", modifier = Modifier.size(15.dp), tint = OppoTeal)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = selectedLevel.take(12),
                        color = TextPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                DropdownMenu(
                    expanded = showLevelMenu,
                    onDismissRequest = { showLevelMenu = false },
                    modifier = Modifier.background(SlateSurface)
                ) {
                    val levels = installation?.detectedLevels ?: listOf("Gridmap V2", "West Coast USA", "Utah, USA")
                    levels.forEach { l ->
                        DropdownMenuItem(
                            text = { Text(l, color = TextPrimary, fontSize = 12.sp) },
                            onClick = {
                                onSelectLevel(l)
                                showLevelMenu = false
                            }
                        )
                    }
                }
            }

            // Exit Sandbox Button
            IconButton(
                onClick = onExit,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(CardElevated.copy(alpha = 0.9f))
                    .border(1.dp, CardBorder, CircleShape)
                    .testTag("exit_simulation_button")
            ) {
                Icon(Icons.Default.Close, contentDescription = "Exit to Dashboard", tint = TextPrimary, modifier = Modifier.size(18.dp))
            }
        }

        // 4. On-Screen Touch Controls Overlay
        TouchOverlayControls(
            simulator = simulator,
            config = touchConfig,
            isNodeGrabberActive = isNodeGrabberActive,
            onToggleNodeGrabber = { isNodeGrabberActive = !isNodeGrabberActive },
            onResetCar = onResetCar,
            onRecoverCar = onRecoverCar,
            onHaptic = onHaptic,
            modifier = Modifier.fillMaxSize()
        )
    }
}
