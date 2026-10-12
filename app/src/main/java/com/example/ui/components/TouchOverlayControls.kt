package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SteeringMode
import com.example.data.model.TouchControlsConfig
import com.example.engine.BeamNGPhysicsSimulator
import com.example.ui.theme.*
import kotlin.math.*

@Composable
fun TouchOverlayControls(
    simulator: BeamNGPhysicsSimulator,
    config: TouchControlsConfig,
    isNodeGrabberActive: Boolean,
    onToggleNodeGrabber: () -> Unit,
    onResetCar: () -> Unit,
    onRecoverCar: () -> Unit,
    onHaptic: () -> Unit,
    modifier: Modifier = Modifier
) {
    var steeringAngleDeg by remember { mutableStateOf(0f) }
    var throttleProgress by remember { mutableStateOf(0f) }
    var brakeProgress by remember { mutableStateOf(0f) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .alpha(config.opacity)
            .testTag("touch_overlay_controls")
    ) {
        // TOP CENTER: Slow Motion & Camera Hotkeys Toolbar
        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 70.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(DeepCarbon.copy(alpha = 0.90f))
                .border(1.dp, CardBorder, RoundedCornerShape(24.dp))
                .padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Reset Car R
            Button(
                onClick = {
                    onHaptic()
                    onResetCar()
                },
                colors = ButtonDefaults.buttonColors(containerColor = RacingOrange),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                modifier = Modifier
                    .height(34.dp)
                    .testTag("reset_car_button")
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Reset Car", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("R", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }

            // Recover Car Insert
            Button(
                onClick = {
                    onHaptic()
                    onRecoverCar()
                },
                colors = ButtonDefaults.buttonColors(containerColor = SlateSurface),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                modifier = Modifier
                    .height(34.dp)
                    .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                    .testTag("recover_car_button")
            ) {
                Icon(Icons.Default.Restore, contentDescription = "Recover Car", modifier = Modifier.size(15.dp), tint = ElectricCyan)
                Spacer(modifier = Modifier.width(3.dp))
                Text("Insert", color = TextPrimary, fontSize = 11.sp)
            }

            // Slow Motion Ratios: 1x, 0.5x, 0.2x, 0.1x, Pause
            val slowMoOptions = listOf(
                Pair(1.0f, "1x"),
                Pair(0.5f, "1/2"),
                Pair(0.2f, "1/5"),
                Pair(0.1f, "1/10"),
                Pair(0.01f, "1/100")
            )

            slowMoOptions.forEach { (ratio, label) ->
                val isSelected = simulator.slowMotionRatio == ratio && !simulator.isPaused
                Box(
                    modifier = Modifier
                        .height(30.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) ElectricCyan else CardElevated)
                        .clickable {
                            onHaptic()
                            simulator.setSlowMo(ratio)
                        }
                        .padding(horizontal = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        color = if (isSelected) DeepCarbon else TextPrimary,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }

            // Pause Physics J
            IconButton(
                onClick = {
                    onHaptic()
                    simulator.togglePause()
                },
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(if (simulator.isPaused) ThermalRed else CardElevated)
                    .testTag("pause_physics_button")
            ) {
                Icon(
                    imageVector = if (simulator.isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                    contentDescription = "Pause",
                    tint = if (simulator.isPaused) TextPrimary else ElectricCyan,
                    modifier = Modifier.size(16.dp)
                )
            }

            // Node Grabber Mode Toggle
            FilterChip(
                selected = isNodeGrabberActive,
                onClick = {
                    onHaptic()
                    onToggleNodeGrabber()
                },
                label = { Text("Node Grabber", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.PanTool,
                        contentDescription = "Node Grabber",
                        modifier = Modifier.size(14.dp)
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = ElectricCyan,
                    selectedLabelColor = DeepCarbon,
                    selectedLeadingIconColor = DeepCarbon,
                    containerColor = CardElevated,
                    labelColor = TextPrimary
                ),
                modifier = Modifier
                    .height(32.dp)
                    .testTag("node_grabber_toggle")
            )
        }

        // BOTTOM LEFT: Steering Wheel or Joystick + Handbrake
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 24.dp, bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Steering Wheel
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .clip(CircleShape)
                        .background(DeepCarbon.copy(alpha = 0.85f))
                        .border(2.dp, CardBorder, CircleShape)
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDrag = { change, dragAmount ->
                                    val centerX = size.width / 2f
                                    val centerY = size.height / 2f
                                    val touchX = change.position.x - centerX
                                    val touchY = change.position.y - centerY
                                    val angleRad = atan2(touchY, touchX)
                                    val deg = Math.toDegrees(angleRad.toDouble()).toFloat()

                                    // Rotate steer
                                    steeringAngleDeg = (steeringAngleDeg + (dragAmount.x * 0.9f)).coerceIn(-180f, 180f)
                                    val steerNorm = (steeringAngleDeg / 180f).coerceIn(-1f, 1f)
                                    simulator.steeringInput = steerNorm
                                },
                                onDragEnd = {
                                    // Spring back to center
                                    steeringAngleDeg = 0f
                                    simulator.steeringInput = 0f
                                    onHaptic()
                                },
                                onDragCancel = {
                                    steeringAngleDeg = 0f
                                    simulator.steeringInput = 0f
                                }
                            )
                        }
                        .testTag("virtual_steering_wheel"),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val c = Offset(size.width / 2f, size.height / 2f)
                        val r = size.width / 2f - 14f

                        // Outer rim
                        drawCircle(
                            color = SlateSurface,
                            radius = r,
                            center = c,
                            style = Stroke(width = 16f)
                        )
                        drawCircle(
                            color = RacingOrange.copy(alpha = 0.5f),
                            radius = r,
                            center = c,
                            style = Stroke(width = 2f)
                        )

                        // Center hub
                        drawCircle(
                            color = DeepCarbon,
                            radius = 24f,
                            center = c
                        )
                        drawCircle(
                            color = ElectricCyan,
                            radius = 24f,
                            center = c,
                            style = Stroke(width = 2f)
                        )

                        // Spokes rotated by steering angle
                        val rad = Math.toRadians(steeringAngleDeg.toDouble())
                        val cosR = cos(rad).toFloat()
                        val sinR = sin(rad).toFloat()

                        // Spoke 1 (top)
                        val spokeTop = Offset(c.x + (cosR * 0f - sinR * (-r)), c.y + (sinR * 0f + cosR * (-r)))
                        drawLine(color = TextPrimary, start = c, end = spokeTop, strokeWidth = 5f, cap = StrokeCap.Round)

                        // Spoke 2 (bottom left)
                        val cosBL = cos(rad + 2.2).toFloat()
                        val sinBL = sin(rad + 2.2).toFloat()
                        val spokeBL = Offset(c.x + (cosBL * r), c.y + (sinBL * r))
                        drawLine(color = TextSecondary, start = c, end = spokeBL, strokeWidth = 4f, cap = StrokeCap.Round)

                        // Spoke 3 (bottom right)
                        val cosBR = cos(rad + 0.94).toFloat()
                        val sinBR = sin(rad + 0.94).toFloat()
                        val spokeBR = Offset(c.x + (cosBR * r), c.y + (sinBR * r))
                        drawLine(color = TextSecondary, start = c, end = spokeBR, strokeWidth = 4f, cap = StrokeCap.Round)
                    }

                    Text(
                        text = "${steeringAngleDeg.toInt()}°",
                        color = ElectricCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                // Handbrake Button
                Box(
                    modifier = Modifier
                        .size(62.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (simulator.handbrakeEngaged) ThermalRed else CardElevated.copy(alpha = 0.9f))
                        .border(
                            1.dp,
                            if (simulator.handbrakeEngaged) ThermalRed else CardBorder,
                            RoundedCornerShape(16.dp)
                        )
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onPress = {
                                    simulator.handbrakeEngaged = true
                                    onHaptic()
                                    tryAwaitRelease()
                                    simulator.handbrakeEngaged = false
                                }
                            )
                        }
                        .testTag("handbrake_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Handbrake",
                            tint = if (simulator.handbrakeEngaged) DeepCarbon else TextPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "P-BRAKE",
                            color = if (simulator.handbrakeEngaged) DeepCarbon else TextPrimary,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }

        // BOTTOM RIGHT: Progressive Throttle & Brake Pedals + PRNDS Shifter
        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = 20.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Gear Selector Column (P, R, N, D, S)
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(DeepCarbon.copy(alpha = 0.88f))
                    .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
                    .padding(4.dp),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                val gears = listOf("P", "R", "N", "D", "S")
                gears.forEach { g ->
                    val isSelected = simulator.gearMode == g
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) RacingOrange else Color.Transparent)
                            .clickable {
                                onHaptic()
                                simulator.gearMode = g
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = g,
                            color = if (isSelected) DeepCarbon else TextSecondary,
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            // Progressive Brake Pedal
            Box(
                modifier = Modifier
                    .width(68.dp)
                    .height(150.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(DeepCarbon.copy(alpha = 0.90f))
                    .border(
                        2.dp,
                        if (brakeProgress > 0.05f) ThermalRed else CardBorder,
                        RoundedCornerShape(14.dp)
                    )
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                val norm = (1f - (offset.y / size.height)).coerceIn(0f, 1f)
                                brakeProgress = norm
                                simulator.brakeInput = norm
                                onHaptic()
                            },
                            onDrag = { change, _ ->
                                val norm = (1f - (change.position.y / size.height)).coerceIn(0f, 1f)
                                brakeProgress = norm
                                simulator.brakeInput = norm
                            },
                            onDragEnd = {
                                brakeProgress = 0f
                                simulator.brakeInput = 0f
                            },
                            onDragCancel = {
                                brakeProgress = 0f
                                simulator.brakeInput = 0f
                            }
                        )
                    }
                    .testTag("brake_pedal"),
                contentAlignment = Alignment.BottomCenter
            ) {
                // Fill height based on pressure
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(brakeProgress.coerceAtLeast(0.02f))
                        .background(ThermalRed.copy(alpha = 0.6f))
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 12.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${(brakeProgress * 100).toInt()}%",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Stop,
                            contentDescription = "Brake",
                            tint = if (brakeProgress > 0.05f) ThermalRed else TextPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "BRAKE",
                            color = TextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }

            // Progressive Throttle / Gas Pedal
            Box(
                modifier = Modifier
                    .width(68.dp)
                    .height(180.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(DeepCarbon.copy(alpha = 0.90f))
                    .border(
                        2.dp,
                        if (throttleProgress > 0.05f) RacingOrange else CardBorder,
                        RoundedCornerShape(14.dp)
                    )
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                val norm = (1f - (offset.y / size.height)).coerceIn(0f, 1f)
                                throttleProgress = norm
                                simulator.throttleInput = norm
                                onHaptic()
                            },
                            onDrag = { change, _ ->
                                val norm = (1f - (change.position.y / size.height)).coerceIn(0f, 1f)
                                throttleProgress = norm
                                simulator.throttleInput = norm
                            },
                            onDragEnd = {
                                throttleProgress = 0f
                                simulator.throttleInput = 0f
                            },
                            onDragCancel = {
                                throttleProgress = 0f
                                simulator.throttleInput = 0f
                            }
                        )
                    }
                    .testTag("throttle_pedal"),
                contentAlignment = Alignment.BottomCenter
            ) {
                // Fill height based on throttle pressure
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(throttleProgress.coerceAtLeast(0.02f))
                        .background(RacingOrange.copy(alpha = 0.7f))
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 12.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${(throttleProgress * 100).toInt()}%",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Speed,
                            contentDescription = "Throttle",
                            tint = if (throttleProgress > 0.05f) RacingOrange else TextPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "GAS",
                            color = TextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }
    }
}
