package com.example.data.model

import java.io.File

/**
 * Driver vendor classification for Vulkan translation layer
 */
enum class DriverArchitecture(val displayName: String) {
    TURNIP_QUALCOMM("Mesa Turnip (Adreno 7xx / 8 Gen 3)"),
    MALI_PANVK("Mesa Panfrost / PanVK (Mali Valhall / Immortalis)"),
    QUALCOMM_PROPRIETARY("Qualcomm KGSL Proprietary v757"),
    MALI_SYSTEM("ARM Mali OpenCL / Vulkan System Driver"),
    UNIVERSAL_FALLBACK("Universal Vulkan (ANGLE / Gallium)")
}

/**
 * Custom Vulkan Driver profile
 */
data class VulkanDriver(
    val id: String,
    val name: String,
    val version: String,
    val architecture: DriverArchitecture,
    val recommendedDevice: String,
    val description: String,
    val directSyscall: Boolean = true,
    val asyncShaders: Boolean = true,
    val dxvkVersion: String = "DXVK 2.4-async",
    val vkd3dVersion: String = "VKD3D-Proton 2.13",
    val isCustom: Boolean = true,
    val isRecommendedForOppoPad3: Boolean = false
)

/**
 * Refresh rate modes tailored for Oppo Pad 3 display
 */
enum class ScreenRefreshMode(val hz: Int, val label: String, val powerDescription: String) {
    HZ_144(144, "144 Hz (Ultra Fluid)", "Matches Oppo Pad 3 144Hz 3K LCD. Lowest input lag for vehicle physics."),
    HZ_120(120, "120 Hz (Balanced High)", "Standard gaming high-refresh rate with great battery efficiency."),
    HZ_90(90, "90 Hz (Eco Smooth)", "Ideal for heavy traffic & multi-vehicle softbody simulation."),
    HZ_60(60, "60 Hz (Power Saver)", "Locks to 60 FPS for extended battery sessions.")
}

/**
 * Thermal governor states for Oppo Pad 3
 */
enum class ThermalGovernorProfile(val title: String, val description: String) {
    OPPO_TURBO("Oppo SuperVOOC Turbo", "Maximum clocks on Cortex-X4 prime core & Adreno GPU. Sustained performance mode."),
    DYNAMIC_HEADROOM("Adaptive Thermal Guard", "Dynamically throttles physics sub-ticks only when tablet SoC exceeds 44°C."),
    BATTERY_CRUISE("Cool & Quiet", "Caps maximum GPU draw to 4.5W, ideal for prolonged play without warm hands.")
}

/**
 * Upscaling & Resolution scaling
 */
enum class ResolutionScale(val multiplier: Float, val label: String, val renderRes: String) {
    NATIVE_3K(1.0f, "Native 3K (2800x2000)", "Full tablet clarity (Oppo Pad 3 Pro Native)"),
    FSR_ULTRA_QUALITY(0.85f, "FSR 2.1 Ultra Quality (2380x1700)", "Crisp edges with ~35% performance boost"),
    FSR_BALANCED(0.70f, "FSR 2.1 Balanced (1960x1400)", "Recommended for multi-car collision physics (144 FPS)"),
    FSR_PERFORMANCE(0.55f, "FSR 2.1 Performance (1540x1100)", "Maximum frame rate on budget hardware")
}

/**
 * Touch control layouts & Steering types
 */
enum class SteeringMode(val displayName: String) {
    VIRTUAL_WHEEL("Virtual Steering Wheel"),
    ANALOG_JOYSTICK("Dual Analog Sticks"),
    GYRO_TILT("Tablet Gyroscope / Tilt"),
    TOUCH_SLIDER("Progressive Touch Ribbon")
}

enum class TransmissionMode(val displayName: String) {
    AUTOMATIC("Automatic (PRNDS)"),
    SEQUENTIAL("Sequential (+ / - Clicker)"),
    MANUAL_H_PATTERN("Manual 6-Speed H-Pattern")
}

data class TouchControlsConfig(
    val steeringMode: SteeringMode = SteeringMode.VIRTUAL_WHEEL,
    val transmissionMode: TransmissionMode = TransmissionMode.AUTOMATIC,
    val opacity: Float = 0.85f,
    val wheelRadiusDp: Int = 85,
    val hapticFeedback: Boolean = true,
    val showPedals: Boolean = true,
    val showSlowMoPill: Boolean = true,
    val showNodeGrabber: Boolean = true,
    val showBeamNGHotkeys: Boolean = true,
    val gyroSensitivity: Float = 1.0f
)

/**
 * Stored BeamNG Game Installation details
 */
data class StoredGameInstallation(
    val folderPath: String,
    val folderName: String,
    val totalSizeBytes: Long,
    val fileCount: Int,
    val hasExecutable: Boolean,
    val executablePath: String,
    val hasContentFolder: Boolean,
    val hasVehiclesFolder: Boolean,
    val hasLevelsFolder: Boolean,
    val detectedVehicles: List<String> = emptyList(),
    val detectedLevels: List<String> = emptyList(),
    val importedTimestamp: Long = System.currentTimeMillis(),
    val isSampleInstallation: Boolean = false
) {
    val formattedSize: String
        get() {
            val gb = totalSizeBytes / (1024.0 * 1024.0 * 1024.0)
            return if (gb >= 1.0) {
                String.format("%.2f GB", gb)
            } else {
                val mb = totalSizeBytes / (1024.0 * 1024.0)
                String.format("%.1f MB", mb)
            }
        }
}

/**
 * Real-time Telemetry state
 */
data class TelemetryData(
    val currentFps: Int = 144,
    val targetFps: Int = 144,
    val frameTimeMs: Float = 6.94f,
    val minFps: Int = 138,
    val onePercentLowFps: Int = 129,
    val jbeamPhysicsTickRate: Int = 2000,
    val jbeamActiveNodes: Int = 384,
    val jbeamActiveBeams: Int = 1120,
    val socTemperatureC: Float = 37.8f,
    val thermalHeadroomPercent: Int = 88,
    val gpuLoadPercent: Int = 64,
    val vramUsedMb: Int = 3450,
    val vramTotalMb: Int = 8192,
    val cpuGovernorCoreUsage: List<Int> = listOf(78, 62, 59, 54, 42, 38, 25, 22),
    val activeDriverName: String = "Mesa Turnip 24.3.0 Rev 10",
    val slowMotionRatio: Float = 1.0f,
    val vehicleSpeedKph: Float = 0f,
    val rpm: Int = 900,
    val gear: String = "P",
    val turboBoostPsi: Float = 0f,
    val gForceLateral: Float = 0f,
    val gForceLongitudinal: Float = 0f
)
