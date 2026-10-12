package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import com.example.engine.BeamNGPhysicsSimulator
import com.example.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SoftBodyCanvas(
    simulator: BeamNGPhysicsSimulator,
    isNodeGrabberActive: Boolean,
    onNodeInteracted: () -> Unit,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .fillMaxSize()
            .testTag("soft_body_canvas")
            .pointerInput(isNodeGrabberActive) {
                if (isNodeGrabberActive) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            val hit = simulator.onTouchDown(offset.x, offset.y)
                            if (hit) onNodeInteracted()
                        },
                        onDrag = { change, _ ->
                            simulator.onTouchMove(change.position.x, change.position.y)
                        },
                        onDragEnd = {
                            simulator.onTouchUp()
                        },
                        onDragCancel = {
                            simulator.onTouchUp()
                        }
                    )
                } else {
                    detectTapGestures(
                        onTap = { offset ->
                            // Tap on track to place an impulse
                            simulator.recoverVehicle()
                            onNodeInteracted()
                        }
                    )
                }
            }
    ) {
        // Update viewport dimensions for boundaries
        simulator.viewportWidth = size.width
        simulator.viewportHeight = size.height

        // 1. Draw Gridmap V2 background floor grid
        val gridSpacing = 40f
        val numCols = (size.width / gridSpacing).toInt()
        val numRows = (size.height / gridSpacing).toInt()

        for (i in 0..numCols) {
            val x = i * gridSpacing
            drawLine(
                color = CardBorder.copy(alpha = 0.35f),
                start = Offset(x, 0f),
                end = Offset(x, size.height),
                strokeWidth = 1f
            )
        }
        for (j in 0..numRows) {
            val y = j * gridSpacing
            drawLine(
                color = CardBorder.copy(alpha = 0.35f),
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = 1f
            )
        }

        // Center line
        drawLine(
            color = ElectricCyan.copy(alpha = 0.2f),
            start = Offset(0f, size.height / 2f),
            end = Offset(size.width, size.height / 2f),
            strokeWidth = 2f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 15f), 0f)
        )

        // 2. Draw Track Obstacles (Rigid crash barriers & pylons)
        simulator.obstacles.forEach { obs ->
            drawRect(
                color = obs.color,
                topLeft = Offset(obs.x, obs.y),
                size = Size(obs.width, obs.height)
            )
            // Hazard stripe border
            drawRect(
                color = Color.White.copy(alpha = 0.8f),
                topLeft = Offset(obs.x, obs.y),
                size = Size(obs.width, obs.height),
                style = Stroke(width = 2.5f)
            )
        }

        // 3. Draw Vehicle Soft-Body JBeam Beams (Stress Heatmap Coloring)
        simulator.beams.forEach { beam ->
            if (!beam.isBroken) {
                val nA = simulator.nodes.getOrNull(beam.nodeA)
                val nB = simulator.nodes.getOrNull(beam.nodeB)
                if (nA != null && nB != null) {
                    // Color based on mechanical stress:
                    // < 0.3 = cyan/green (nominal)
                    // 0.3 - 0.7 = yellow/orange (yielding)
                    // > 0.7 = red (severe plastic deformation)
                    val beamColor = when {
                        beam.currentStress > 0.8f -> ThermalRed
                        beam.currentStress > 0.4f -> ThermalYellow
                        beam.currentStress > 0.15f -> PhysicsLime
                        else -> ElectricCyan
                    }
                    val strokeW = if (beam.currentStress > 0.6f) 3.5f else 2.2f

                    drawLine(
                        color = beamColor.copy(alpha = 0.9f),
                        start = Offset(nA.x, nA.y),
                        end = Offset(nB.x, nB.y),
                        strokeWidth = strokeW,
                        cap = StrokeCap.Round
                    )
                }
            }
        }

        // 4. Draw Vehicle Body Panels / Aerodynamic Hull fill
        val hullNodes = listOf(0, 1, 2, 5, 8, 11, 14, 17, 16, 15, 12, 9, 6, 3)
        if (hullNodes.all { it in simulator.nodes.indices }) {
            val path = Path().apply {
                val first = simulator.nodes[hullNodes.first()]
                moveTo(first.x, first.y)
                for (i in 1 until hullNodes.size) {
                    val n = simulator.nodes[hullNodes[i]]
                    lineTo(n.x, n.y)
                }
                close()
            }
            // Translucent car body paint
            drawPath(
                path = path,
                color = RacingOrange.copy(alpha = 0.22f)
            )
            drawPath(
                path = path,
                color = RacingOrangeLight,
                style = Stroke(width = 2.8f)
            )
        }

        // 5. Draw Wheels & Tire Angles
        val wheelNodes = listOf(6, 8, 12, 14) // Front R/L, Rear R/L
        wheelNodes.forEach { wIdx ->
            val node = simulator.nodes.getOrNull(wIdx)
            if (node != null) {
                val isFront = wIdx in listOf(6, 8)
                val wheelAngle = simulator.carAngleRad + if (isFront) (simulator.steeringInput * 0.45f) else 0f
                val wLen = 22f
                val wWidth = 9f

                val cosA = cos(wheelAngle)
                val sinA = sin(wheelAngle)

                val start = Offset(node.x - (cosA * wLen), node.y - (sinA * wLen))
                val end = Offset(node.x + (cosA * wLen), node.y + (sinA * wLen))

                drawLine(
                    color = Color.Black,
                    start = start,
                    end = end,
                    strokeWidth = wWidth,
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = TextPrimary,
                    start = start,
                    end = end,
                    strokeWidth = 2f,
                    cap = StrokeCap.Round
                )
            }
        }

        // 6. Draw Headlights / Taillights
        val frontNode = simulator.nodes.getOrNull(1)
        if (frontNode != null) {
            val headDirX = cos(simulator.carAngleRad)
            val headDirY = sin(simulator.carAngleRad)
            // Headlight beam cone
            drawCircle(
                color = Color(0xFFFFF9C4).copy(alpha = 0.45f),
                radius = 35f,
                center = Offset(frontNode.x + (headDirX * 25f), frontNode.y + (headDirY * 25f))
            )
        }

        // 7. Draw JBeam Nodes (Joints)
        simulator.nodes.forEach { node ->
            val nodeColor = when {
                node.isGrabbed -> ThermalRed
                isNodeGrabberActive -> ElectricCyan
                else -> Color.White
            }
            val radius = if (node.isGrabbed) 8f else if (isNodeGrabberActive) 5.5f else 3.5f

            drawCircle(
                color = nodeColor,
                radius = radius,
                center = Offset(node.x, node.y)
            )

            if (node.isGrabbed) {
                drawCircle(
                    color = ThermalYellow.copy(alpha = 0.5f),
                    radius = 16f,
                    center = Offset(node.x, node.y),
                    style = Stroke(width = 2f)
                )
            }
        }
    }
}
