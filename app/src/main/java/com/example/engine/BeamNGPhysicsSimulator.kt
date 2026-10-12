package com.example.engine

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.example.data.model.TelemetryData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.*

/**
 * Node in BeamNG JBeam soft-body chassis
 */
data class PhysicsNode(
    val id: Int,
    val name: String,
    var x: Float,
    var y: Float,
    val origX: Float,
    val origY: Float,
    var vx: Float = 0f,
    var vy: Float = 0f,
    val mass: Float = 1.0f,
    var isFixed: Boolean = false,
    var isGrabbed: Boolean = false
)

/**
 * Beam connecting two nodes with stiffness, damping, and plastic deformation
 */
data class PhysicsBeam(
    val nodeA: Int,
    val nodeB: Int,
    var restLength: Float,
    val origLength: Float,
    val springK: Float = 4200f,
    val dampD: Float = 180f,
    val yieldStrain: Float = 0.22f, // Above this, beam undergoes permanent plastic deformation
    val breakStrain: Float = 0.55f, // Above this, beam snaps completely
    var isBroken: Boolean = false,
    var currentStress: Float = 0.0f // 0.0 to 1.0+ for stress heatmap coloring
)

/**
 * Obstacle on test track
 */
data class TrackObstacle(
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
    val name: String,
    val color: Color
)

class BeamNGPhysicsSimulator {

    private val _telemetry = MutableStateFlow(TelemetryData())
    val telemetry: StateFlow<TelemetryData> = _telemetry.asStateFlow()

    // Physics state
    var nodes = mutableListOf<PhysicsNode>()
    var beams = mutableListOf<PhysicsBeam>()
    val obstacles = mutableListOf<TrackObstacle>()

    // Vehicle kinematic state
    var vehicleSpeedKph = 0f
    var engineRpm = 850
    var currentGear = 1
    var gearMode = "D" // P, R, N, D, S
    var throttleInput = 0f // 0.0 to 1.0
    var brakeInput = 0f    // 0.0 to 1.0
    var steeringInput = 0f // -1.0 (left) to 1.0 (right)
    var handbrakeEngaged = false
    var turboPsi = 0f
    var slowMotionRatio = 1.0f // 1.0, 0.5, 0.2, 0.1, 0.01, 0.0 (pause)
    var isPaused = false

    var carCenterX = 400f
    var carCenterY = 320f
    var carAngleRad = 0f
    var carAngularVelocity = 0f

    // Interactive Node Grabber state
    var grabbedNodeIndex: Int? = null

    // Track width and bounds
    var viewportWidth = 1000f
    var viewportHeight = 600f

    init {
        resetVehicleToFactory()
        setupTrackObstacles()
    }

    fun setupTrackObstacles() {
        obstacles.clear()
        // Concrete crash barrier
        obstacles.add(TrackObstacle(x = 880f, y = 200f, width = 45f, height = 240f, name = "Rigid Crash Barrier", color = Color(0xFFE53935)))
        // Bollards
        obstacles.add(TrackObstacle(x = 650f, y = 110f, width = 30f, height = 30f, name = "Pylon #1", color = Color(0xFFFFB300)))
        obstacles.add(TrackObstacle(x = 650f, y = 490f, width = 30f, height = 30f, name = "Pylon #2", color = Color(0xFFFFB300)))
    }

    /**
     * Reconstructs the vehicle's soft-body JBeam skeleton in pristine state
     */
    fun resetVehicleToFactory() {
        nodes.clear()
        beams.clear()

        carCenterX = 240f
        carCenterY = 320f
        carAngleRad = 0f
        carAngularVelocity = 0f
        vehicleSpeedKph = 0f
        engineRpm = 850
        currentGear = 1
        gearMode = "D"
        turboPsi = 0f

        // Build a classic BeamNG Gavril / Covet vehicle structure:
        // Chassis perimeter + internal cross-bracing + front crumple zone + cabin + engine block
        val localPoints = listOf(
            // Front bumper & crumple zone
            Pair(140f, -45f), // 0: Front bumper right
            Pair(140f, 0f),   // 1: Front bumper center
            Pair(140f, 45f),  // 2: Front bumper left
            Pair(100f, -48f), // 3: Front quarter right
            Pair(100f, 0f),   // 4: Engine block front
            Pair(100f, 48f),  // 5: Front quarter left

            // Front suspension / firewall
            Pair(50f, -48f),  // 6: A-pillar / Wheel right
            Pair(50f, 0f),    // 7: Firewall center
            Pair(50f, 48f),   // 8: A-pillar / Wheel left

            // Cabin & B-pillar
            Pair(-20f, -48f), // 9: B-pillar right
            Pair(-20f, 0f),   // 10: Center floorboard
            Pair(-20f, 48f),  // 11: B-pillar left

            // Rear axle & C-pillar
            Pair(-90f, -48f), // 12: C-pillar right / Rear wheel
            Pair(-90f, 0f),   // 13: Rear differential
            Pair(-90f, 48f),  // 14: C-pillar left / Rear wheel

            // Rear bumper
            Pair(-140f, -45f),// 15: Rear bumper right
            Pair(-140f, 0f),  // 16: Rear bumper center
            Pair(-140f, 45f)  // 17: Rear bumper left
        )

        localPoints.forEachIndexed { i, pt ->
            nodes.add(
                PhysicsNode(
                    id = i,
                    name = "node_$i",
                    x = carCenterX + pt.first,
                    y = carCenterY + pt.second,
                    origX = pt.first,
                    origY = pt.second,
                    mass = if (i in listOf(4, 7)) 2.8f else 1.2f
                )
            )
        }

        fun addBeam(a: Int, b: Int, k: Float = 3600f, damp: Float = 140f) {
            val nA = nodes[a]
            val nB = nodes[b]
            val dist = hypot(nA.x - nB.x, nA.y - nB.y)
            beams.add(PhysicsBeam(nodeA = a, nodeB = b, restLength = dist, origLength = dist, springK = k, dampD = damp))
        }

        // Perimeter beams
        val perim = listOf(0, 1, 2, 5, 8, 11, 14, 17, 16, 15, 12, 9, 6, 3, 0)
        for (i in 0 until perim.size - 1) {
            addBeam(perim[i], perim[i + 1], 4200f)
        }

        // Central spine beams
        addBeam(1, 4, 3800f)
        addBeam(4, 7, 5200f) // Rigid engine mount
        addBeam(7, 10, 4800f)
        addBeam(10, 13, 4800f)
        addBeam(13, 16, 3800f)

        // Lateral structural cross-beams
        addBeam(0, 4); addBeam(2, 4)
        addBeam(3, 4); addBeam(5, 4)
        addBeam(3, 7); addBeam(5, 7)
        addBeam(6, 7); addBeam(8, 7)
        addBeam(6, 10); addBeam(8, 10)
        addBeam(9, 10); addBeam(11, 10)
        addBeam(9, 13); addBeam(11, 13)
        addBeam(12, 13); addBeam(14, 13)
        addBeam(12, 16); addBeam(14, 16)
        addBeam(15, 16); addBeam(17, 16)

        // Diagonal triangulation (shear rigidity)
        addBeam(0, 3); addBeam(2, 5)
        addBeam(3, 8); addBeam(5, 6)
        addBeam(6, 11); addBeam(8, 9)
        addBeam(9, 14); addBeam(11, 12)
        addBeam(12, 17); addBeam(14, 15)
    }

    /**
     * Recovers vehicle safely back to starting track position
     */
    fun recoverVehicle() {
        carCenterX = 240f
        carCenterY = 320f
        carAngleRad = 0f
        carAngularVelocity = 0f
        vehicleSpeedKph = 0f
        engineRpm = 850
        nodes.forEach { n ->
            n.x = carCenterX + n.origX
            n.y = carCenterY + n.origY
            n.vx = 0f
            n.vy = 0f
        }
    }

    /**
     * Node Grabber: user touches near a chassis node to manipulate and pull metal
     */
    fun onTouchDown(x: Float, y: Float): Boolean {
        // Find closest node within 60dp touch threshold
        var bestIndex = -1
        var bestDist = 60f
        nodes.forEachIndexed { idx, node ->
            val dist = hypot(node.x - x, node.y - y)
            if (dist < bestDist) {
                bestDist = dist
                bestIndex = idx
            }
        }
        return if (bestIndex != -1) {
            grabbedNodeIndex = bestIndex
            nodes[bestIndex].isGrabbed = true
            true
        } else {
            false
        }
    }

    fun onTouchMove(x: Float, y: Float) {
        grabbedNodeIndex?.let { idx ->
            if (idx in nodes.indices) {
                nodes[idx].x = x
                nodes[idx].y = y
                nodes[idx].vx = 0f
                nodes[idx].vy = 0f
            }
        }
    }

    fun onTouchUp() {
        grabbedNodeIndex?.let { idx ->
            if (idx in nodes.indices) {
                nodes[idx].isGrabbed = false
            }
        }
        grabbedNodeIndex = null
    }

    /**
     * Executes one frame of simulation at delta time dt (seconds)
     */
    fun step(dtRaw: Float) {
        if (isPaused) return

        val dt = (dtRaw * slowMotionRatio).coerceIn(0.0001f, 0.05f)

        // 1. Drivetrain & Powertrain Dynamics
        updatePowertrain(dt)

        // 2. Kinematic vehicle steering & propulsion forces applied to wheels
        applyDrivingForces(dt)

        // 3. JBeam Soft-Body Sub-Tick Solver (simulates spring-damper network and plastic buckling)
        val subTicks = 8
        val subDt = dt / subTicks
        for (st in 0 until subTicks) {
            solveSpringDamperNetwork(subDt)
            solveObstacleCollisions()
            integrateVerlet(subDt)
        }

        // 4. Update overall vehicle center & telemetry metrics
        updateVehicleCenterAndTelemetry(dt)
    }

    private fun updatePowertrain(dt: Float) {
        // Engine RPM calculation
        val targetRpm = when (gearMode) {
            "P", "N" -> 850 + (throttleInput * 6800).toInt()
            "R" -> 1200 + (abs(vehicleSpeedKph) * 110).toInt()
            "D", "S" -> {
                val base = 900 + (vehicleSpeedKph * (90 / currentGear)).toInt()
                (base + (throttleInput * 1800)).toInt().coerceIn(850, 7800)
            }
            else -> 850
        }

        engineRpm += ((targetRpm - engineRpm) * (dt * 9f)).toInt()

        // Turbocharger boost spool
        val targetPsi = if (throttleInput > 0.15f && engineRpm > 2600) {
            (throttleInput * 22.4f)
        } else {
            0f
        }
        turboPsi += (targetPsi - turboPsi) * (dt * 5f)

        // Automatic gear shifting
        if (gearMode == "D" || gearMode == "S") {
            if (engineRpm > 6200 && currentGear < 6 && vehicleSpeedKph > 15) {
                currentGear++
            } else if (engineRpm < 2200 && currentGear > 1) {
                currentGear--
            }
        }
    }

    private fun applyDrivingForces(dt: Float) {
        // Acceleration propulsion
        val maxSpeedKph = 240f
        val accelPower = 180f * (1.0f + (turboPsi / 25f))

        var forwardForce = 0f
        when (gearMode) {
            "D", "S" -> {
                if (throttleInput > 0.05f && vehicleSpeedKph < maxSpeedKph) {
                    forwardForce = throttleInput * accelPower
                }
            }
            "R" -> {
                if (throttleInput > 0.05f && vehicleSpeedKph > -60f) {
                    forwardForce = -throttleInput * (accelPower * 0.6f)
                }
            }
        }

        // Braking
        if (brakeInput > 0.05f || handbrakeEngaged) {
            val brakeForce = (brakeInput * 320f) + if (handbrakeEngaged) 280f else 0f
            if (vehicleSpeedKph > 0.5f) {
                vehicleSpeedKph -= brakeForce * dt
                if (vehicleSpeedKph < 0f) vehicleSpeedKph = 0f
            } else if (vehicleSpeedKph < -0.5f) {
                vehicleSpeedKph += brakeForce * dt
                if (vehicleSpeedKph > 0f) vehicleSpeedKph = 0f
            }
        }

        vehicleSpeedKph += (forwardForce * 0.25f) * dt
        // Rolling resistance / air drag
        vehicleSpeedKph *= (1f - (0.015f * dt))

        // Steering rotation
        val forwardSpeedWorld = (vehicleSpeedKph * 1000f / 3600f) * 4.2f // pixels/sec
        if (abs(vehicleSpeedKph) > 0.5f) {
            val steerEffect = (steeringInput * 1.8f) * (if (vehicleSpeedKph >= 0) 1f else -1f)
            carAngleRad += steerEffect * dt * (forwardSpeedWorld / 250f).coerceIn(-1.5f, 1.5f)
        }

        // Apply forward vector to driven nodes
        val headingX = cos(carAngleRad)
        val headingY = sin(carAngleRad)

        val velX = headingX * forwardSpeedWorld
        val velY = headingY * forwardSpeedWorld

        nodes.forEach { node ->
            if (!node.isGrabbed) {
                node.vx += (velX - node.vx) * 0.12f
                node.vy += (velY - node.vy) * 0.12f
            }
        }
    }

    private fun solveSpringDamperNetwork(dt: Float) {
        beams.forEach { beam ->
            if (beam.isBroken) return@forEach

            val nA = nodes[beam.nodeA]
            val nB = nodes[beam.nodeB]

            val dx = nB.x - nA.x
            val dy = nB.y - nA.y
            val dist = hypot(dx, dy).coerceAtLeast(0.001f)

            val delta = dist - beam.restLength
            val strain = abs(delta) / beam.origLength
            beam.currentStress = (strain / beam.yieldStrain).coerceIn(0f, 2f)

            // Plastic deformation check
            if (strain > beam.yieldStrain) {
                // Beam yields permanently
                beam.restLength += delta * 0.15f
            }
            if (strain > beam.breakStrain) {
                beam.isBroken = true
                return@forEach
            }

            // Hooke's spring force + velocity damping
            val normalX = dx / dist
            val normalY = dy / dist

            val relVx = nB.vx - nA.vx
            val relVy = nB.vy - nA.vy
            val dampVel = (relVx * normalX) + (relVy * normalY)

            val springForce = (beam.springK * delta) + (beam.dampD * dampVel)

            val fx = normalX * springForce
            val fy = normalY * springForce

            if (!nA.isFixed && !nA.isGrabbed) {
                nA.vx += (fx / nA.mass) * dt
                nA.vy += (fy / nA.mass) * dt
            }
            if (!nB.isFixed && !nB.isGrabbed) {
                nB.vx -= (fx / nB.mass) * dt
                nB.vy -= (fy / nB.mass) * dt
            }
        }
    }

    private fun solveObstacleCollisions() {
        obstacles.forEach { obs ->
            val obsLeft = obs.x
            val obsRight = obs.x + obs.width
            val obsTop = obs.y
            val obsBottom = obs.y + obs.height

            nodes.forEach { node ->
                if (node.x in obsLeft..obsRight && node.y in obsTop..obsBottom) {
                    // Collision impact! Rapid deceleration
                    val penetrationLeft = node.x - obsLeft
                    val penetrationRight = obsRight - node.x
                    val minPenX = min(penetrationLeft, penetrationRight)

                    if (minPenX == penetrationLeft) {
                        node.x = obsLeft - 1f
                        node.vx = -abs(node.vx) * 0.25f // bounce with high energy absorption
                    } else {
                        node.x = obsRight + 1f
                        node.vx = abs(node.vx) * 0.25f
                    }
                    node.vy *= 0.5f

                    // Vehicle overall velocity absorption
                    vehicleSpeedKph *= 0.7f
                }
            }
        }

        // Viewport boundaries
        nodes.forEach { node ->
            if (node.y < 30f) {
                node.y = 30f
                node.vy = -node.vy * 0.3f
            } else if (node.y > viewportHeight - 30f) {
                node.y = viewportHeight - 30f
                node.vy = -node.vy * 0.3f
            }
            if (node.x > viewportWidth - 20f) {
                node.x = viewportWidth - 20f
                node.vx = -node.vx * 0.3f
                vehicleSpeedKph = -vehicleSpeedKph * 0.4f
            } else if (node.x < 20f) {
                node.x = 20f
                node.vx = -node.vx * 0.3f
                vehicleSpeedKph = 0f
            }
        }
    }

    private fun integrateVerlet(dt: Float) {
        nodes.forEach { node ->
            if (!node.isGrabbed) {
                node.x += node.vx * dt
                node.y += node.vy * dt
                // Ambient drag
                node.vx *= (1f - (0.02f * dt))
                node.vy *= (1f - (0.02f * dt))
            }
        }
    }

    private fun updateVehicleCenterAndTelemetry(dt: Float) {
        // Average coordinates of nodes
        var sumX = 0f
        var sumY = 0f
        nodes.forEach {
            sumX += it.x
            sumY += it.y
        }
        carCenterX = sumX / nodes.size.coerceAtLeast(1)
        carCenterY = sumY / nodes.size.coerceAtLeast(1)

        val activeNodesCount = nodes.size
        val activeBeamsCount = beams.count { !it.isBroken }

        // Lateral and Longitudinal G forces
        val gLat = (steeringInput * (vehicleSpeedKph / 60f)).coerceIn(-2.5f, 2.5f)
        val gLong = ((throttleInput * 1.2f) - (brakeInput * 1.8f)).coerceIn(-2.2f, 1.5f)

        // Telemetry state update
        _telemetry.value = _telemetry.value.copy(
            vehicleSpeedKph = vehicleSpeedKph,
            rpm = engineRpm,
            gear = when (gearMode) {
                "D" -> "D$currentGear"
                "S" -> "S$currentGear"
                else -> gearMode
            },
            turboBoostPsi = turboPsi,
            slowMotionRatio = slowMotionRatio,
            jbeamActiveNodes = activeNodesCount,
            jbeamActiveBeams = activeBeamsCount,
            gForceLateral = gLat,
            gForceLongitudinal = gLong
        )
    }

    fun setSlowMo(ratio: Float) {
        slowMotionRatio = ratio
        isPaused = (ratio == 0.0f)
    }

    fun togglePause() {
        isPaused = !isPaused
    }
}
