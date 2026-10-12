package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.repository.EmulatorConfigRepository
import com.example.data.repository.GameStorageRepository
import com.example.engine.BeamNGPhysicsSimulator
import com.example.engine.ThermalGovernor
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

enum class EmulatorScreen {
    DASHBOARD,
    GAME_FILES,
    DRIVERS_HUB,
    OPPO_OPTIMIZATION,
    TOUCH_CONTROLS,
    LIVE_EMULATION
}

class EmulatorViewModel(application: Application) : AndroidViewModel(application) {

    private val storageRepo = GameStorageRepository(application)
    private val configRepo = EmulatorConfigRepository(application)
    private val thermalGov = ThermalGovernor(application)
    val physicsSimulator = BeamNGPhysicsSimulator()

    val installation = storageRepo.installationState
    val availableDrivers = configRepo.availableDrivers
    val selectedDriver = configRepo.selectedDriver
    val refreshMode = configRepo.refreshMode
    val thermalProfile = configRepo.thermalProfile
    val resolutionScale = configRepo.resolutionScale
    val touchConfig = configRepo.touchConfig
    val thermalStats = thermalGov.thermalStats
    val zramExpansionGb = configRepo.zramExpansionGb
    val directSyscallBypass = configRepo.directSyscallBypass
    val asyncShaderCaching = configRepo.asyncShaderCaching
    val dynarecOptimizationLevel = configRepo.dynarecOptimizationLevel

    private val _currentScreen = MutableStateFlow(EmulatorScreen.DASHBOARD)
    val currentScreen: StateFlow<EmulatorScreen> = _currentScreen.asStateFlow()

    private val _selectedVehicle = MutableStateFlow("Gavril D-Series Pickup")
    val selectedVehicle: StateFlow<String> = _selectedVehicle.asStateFlow()

    private val _selectedLevel = MutableStateFlow("Gridmap V2 (Physics Testing)")
    val selectedLevel: StateFlow<String> = _selectedLevel.asStateFlow()

    private val _fpsHistory = MutableStateFlow<List<Float>>(listOf(144f, 143f, 144f, 144f, 142f, 144f, 144f, 141f, 144f))
    val fpsHistory: StateFlow<List<Float>> = _fpsHistory.asStateFlow()

    private val _notificationMessage = MutableStateFlow<String?>(null)
    val notificationMessage: StateFlow<String?> = _notificationMessage.asStateFlow()

    private var gameLoopJob: Job? = null
    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vm = application.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vm?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        application.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    init {
        startTelemetryLoop()
    }

    private fun startTelemetryLoop() {
        viewModelScope.launch {
            while (isActive) {
                thermalGov.updateStats()
                delay(1000)
            }
        }
    }

    fun navigateTo(screen: EmulatorScreen) {
        _currentScreen.value = screen
        if (screen == EmulatorScreen.LIVE_EMULATION) {
            startPhysicsGameLoop()
        } else {
            stopPhysicsGameLoop()
        }
    }

    fun selectVehicle(vehicle: String) {
        _selectedVehicle.value = vehicle
        physicsSimulator.resetVehicleToFactory()
        showNotification("Loaded vehicle: $vehicle")
    }

    fun selectLevel(level: String) {
        _selectedLevel.value = level
        physicsSimulator.resetVehicleToFactory()
        showNotification("Loaded map: $level")
    }

    fun selectDriver(driver: VulkanDriver) {
        configRepo.selectDriver(driver)
        showNotification("Active Vulkan Driver: ${driver.name}")
    }

    fun setRefreshMode(mode: ScreenRefreshMode) {
        configRepo.setRefreshMode(mode)
        showNotification("Display synced to ${mode.hz}Hz")
    }

    fun setThermalProfile(profile: ThermalGovernorProfile) {
        configRepo.setThermalProfile(profile)
        thermalGov.setProfile(profile)
        showNotification("Thermal Governor: ${profile.title}")
    }

    fun setResolutionScale(scale: ResolutionScale) {
        configRepo.setResolutionScale(scale)
    }

    fun updateTouchConfig(config: TouchControlsConfig) {
        configRepo.updateTouchConfig(config)
    }

    fun setZramExpansionGb(gb: Int) {
        configRepo.setZramExpansionGb(gb)
    }

    fun setDirectSyscallBypass(enabled: Boolean) {
        configRepo.setDirectSyscallBypass(enabled)
    }

    fun setAsyncShaderCaching(enabled: Boolean) {
        configRepo.setAsyncShaderCaching(enabled)
    }

    fun setDynarecOptimizationLevel(level: Int) {
        configRepo.setDynarecOptimizationLevel(level)
    }

    fun importUserFolder(folderName: String, uri: Uri?) {
        viewModelScope.launch {
            val res = storageRepo.importUserFolder(folderName, uri)
            showNotification("Successfully imported ${res.folderName} (${res.formattedSize})")
        }
    }

    fun resetToSampleInstallation() {
        viewModelScope.launch {
            storageRepo.clearInstallation()
            showNotification("Reset to Oppo Pad 3 optimized sample installation")
        }
    }

    private fun startPhysicsGameLoop() {
        gameLoopJob?.cancel()
        val targetHz = refreshMode.value.hz
        val frameIntervalMs = (1000L / targetHz).coerceAtLeast(6L)

        gameLoopJob = viewModelScope.launch {
            var lastTime = System.nanoTime()
            var frameCount = 0
            var fpsAccumulator = 0f
            val history = mutableListOf<Float>()

            while (isActive) {
                val now = System.nanoTime()
                val dtSec = ((now - lastTime) / 1_000_000_000.0).toFloat().coerceIn(0.001f, 0.05f)
                lastTime = now

                // Step BeamNG physics
                physicsSimulator.step(dtSec)

                // Track FPS
                val instantFps = (1.0f / dtSec).coerceIn(30f, 160f)
                fpsAccumulator += instantFps
                frameCount++

                if (frameCount >= 15) {
                    val avgFps = fpsAccumulator / frameCount
                    history.add(avgFps)
                    if (history.size > 20) history.removeAt(0)
                    _fpsHistory.value = history.toList()
                    frameCount = 0
                    fpsAccumulator = 0f
                }

                delay(frameIntervalMs)
            }
        }
    }

    private fun stopPhysicsGameLoop() {
        gameLoopJob?.cancel()
        gameLoopJob = null
    }

    fun triggerHaptic(durationMs: Long = 25) {
        if (!touchConfig.value.hapticFeedback) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(
                    VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE)
                )
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(durationMs)
            }
        } catch (_: Exception) {}
    }

    fun resetCar() {
        triggerHaptic(50)
        physicsSimulator.resetVehicleToFactory()
        showNotification("Vehicle Reset (R)")
    }

    fun recoverCar() {
        triggerHaptic(30)
        physicsSimulator.recoverVehicle()
        showNotification("Vehicle Recovered (Insert)")
    }

    fun showNotification(msg: String) {
        _notificationMessage.value = msg
        viewModelScope.launch {
            delay(2500)
            if (_notificationMessage.value == msg) {
                _notificationMessage.value = null
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopPhysicsGameLoop()
    }
}
