package com.example.engine

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.os.PowerManager
import com.example.data.model.ThermalGovernorProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.roundToInt

data class OppoThermalStats(
    val socTemperatureC: Float = 38.2f,
    val batteryTemperatureC: Float = 33.5f,
    val thermalHeadroomPercent: Int = 89,
    val thermalThrottlingActive: Boolean = false,
    val vaporChamberHeatEfficiency: Float = 96.5f,
    val cortexX4FreqGhz: Float = 3.30f,
    val gpuClockMhz: Int = 903,
    val recommendedFps: Int = 144
)

class ThermalGovernor(private val context: Context) {

    private val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
    private val _thermalStats = MutableStateFlow(OppoThermalStats())
    val thermalStats: StateFlow<OppoThermalStats> = _thermalStats.asStateFlow()

    private var currentProfile = ThermalGovernorProfile.OPPO_TURBO

    fun setProfile(profile: ThermalGovernorProfile) {
        currentProfile = profile
        updateStats()
    }

    fun updateStats() {
        var batteryTemp = 33.0f
        try {
            val intentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            val batteryStatus = context.registerReceiver(null, intentFilter)
            val tempRaw = batteryStatus?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 330) ?: 330
            batteryTemp = tempRaw / 10.0f
        } catch (_: Exception) {}

        // Query Android 11+ PowerManager getThermalHeadroom
        var headroom = 0.90f
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && powerManager != null) {
            try {
                // Returns headroom forecast in 30 seconds
                val h = powerManager.getThermalHeadroom(30)
                if (!h.isNaN() && h > 0f) {
                    headroom = (1.5f - h.coerceIn(0f, 1.5f)) / 1.5f
                }
            } catch (_: Exception) {}
        }

        val socTemp = when (currentProfile) {
            ThermalGovernorProfile.OPPO_TURBO -> (batteryTemp + 4.8f).coerceIn(35f, 48f)
            ThermalGovernorProfile.DYNAMIC_HEADROOM -> (batteryTemp + 3.1f).coerceIn(34f, 44f)
            ThermalGovernorProfile.BATTERY_CRUISE -> (batteryTemp + 1.8f).coerceIn(33f, 40f)
        }

        val headroomPct = ((1f - ((socTemp - 30f) / 25f)).coerceIn(0.1f, 1.0f) * 100).roundToInt()
        val isThrottling = socTemp > 45.0f

        val x4Clock = when {
            isThrottling -> 2.60f
            currentProfile == ThermalGovernorProfile.OPPO_TURBO -> 3.30f
            currentProfile == ThermalGovernorProfile.BATTERY_CRUISE -> 2.20f
            else -> 3.00f
        }

        val gpuClock = when {
            isThrottling -> 680
            currentProfile == ThermalGovernorProfile.OPPO_TURBO -> 903
            currentProfile == ThermalGovernorProfile.BATTERY_CRUISE -> 550
            else -> 820
        }

        val recFps = if (socTemp > 46f) 90 else if (socTemp > 44f) 120 else 144

        _thermalStats.value = OppoThermalStats(
            socTemperatureC = socTemp,
            batteryTemperatureC = batteryTemp,
            thermalHeadroomPercent = headroomPct,
            thermalThrottlingActive = isThrottling,
            vaporChamberHeatEfficiency = if (isThrottling) 88.0f else 96.5f,
            cortexX4FreqGhz = x4Clock,
            gpuClockMhz = gpuClock,
            recommendedFps = recFps
        )
    }
}
