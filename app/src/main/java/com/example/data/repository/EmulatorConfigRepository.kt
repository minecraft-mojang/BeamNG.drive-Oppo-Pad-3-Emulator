package com.example.data.repository

import android.content.Context
import com.example.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class EmulatorConfigRepository(private val context: Context) {

    private val prefs = context.getSharedPreferences("beampad_config_prefs", Context.MODE_PRIVATE)

    val availableDrivers: List<VulkanDriver> = listOf(
        VulkanDriver(
            id = "turnip_24_3_rev10",
            name = "Mesa Turnip Vulkan v24.3.0 Rev 10",
            version = "24.3.0-devel-rev10",
            architecture = DriverArchitecture.TURNIP_QUALCOMM,
            recommendedDevice = "Oppo Pad 3 Pro (Snapdragon 8 Gen 3)",
            description = "Custom compiled Turnip with Direct Syscall KGSL bypass and GMEM zero-copy tiling. Delivers ~40% higher framerate than standard Winlator Turnip.",
            directSyscall = true,
            asyncShaders = true,
            dxvkVersion = "DXVK 2.4-async (Low Jitter)",
            vkd3dVersion = "VKD3D-Proton 2.13-git",
            isRecommendedForOppoPad3 = true
        ),
        VulkanDriver(
            id = "mali_panvk_24_2",
            name = "Mesa PanVK / Panfrost Mali Driver",
            version = "24.2.4-valhall",
            architecture = DriverArchitecture.MALI_PANVK,
            recommendedDevice = "Oppo Pad 3 (MediaTek Dimensity 8350)",
            description = "Specially patched open-source Vulkan driver for Mali-G615/G720 GPUs. Eliminates Mali vertex buffer crashes in BeamNG mesh deformation.",
            directSyscall = true,
            asyncShaders = true,
            dxvkVersion = "DXVK 2.4-async",
            vkd3dVersion = "VKD3D-Proton 2.12",
            isRecommendedForOppoPad3 = true
        ),
        VulkanDriver(
            id = "qualcomm_v757",
            name = "Qualcomm KGSL Vulkan v757.0",
            version = "v757.0-official",
            architecture = DriverArchitecture.QUALCOMM_PROPRIETARY,
            recommendedDevice = "Snapdragon Adreno 750/740 devices",
            description = "OEM proprietary binary driver extracted from ColorOS 15 firmware. Solid stability on complex particle effects and tire smoke.",
            directSyscall = false,
            asyncShaders = true,
            dxvkVersion = "DXVK 2.3.1",
            vkd3dVersion = "VKD3D 2.11"
        ),
        VulkanDriver(
            id = "mali_system_oem",
            name = "ARM Mali System OEM Vulkan v38.0",
            version = "r38p0-vulkan1.3",
            architecture = DriverArchitecture.MALI_SYSTEM,
            recommendedDevice = "Dimensity 9300 / 8300 / 8200 series",
            description = "Stock vendor driver with Vulkan 1.3 compliance. Reliable compatibility across wide range of Android tablets.",
            directSyscall = false,
            asyncShaders = false,
            dxvkVersion = "DXVK 2.2",
            vkd3dVersion = "VKD3D 2.10"
        ),
        VulkanDriver(
            id = "universal_angle",
            name = "Universal Vulkan ANGLE / LLVMpipe",
            version = "24.1.0-llvmpipe",
            architecture = DriverArchitecture.UNIVERSAL_FALLBACK,
            recommendedDevice = "Any Android device / CPU software fallback",
            description = "High compatibility universal software & hardware rasterizer fallback for testing and edge cases.",
            directSyscall = false,
            asyncShaders = true,
            dxvkVersion = "DXVK 2.0",
            vkd3dVersion = "VKD3D 2.8"
        )
    )

    private val _selectedDriver = MutableStateFlow<VulkanDriver>(availableDrivers[0])
    val selectedDriver: StateFlow<VulkanDriver> = _selectedDriver.asStateFlow()

    private val _refreshMode = MutableStateFlow(ScreenRefreshMode.HZ_144)
    val refreshMode: StateFlow<ScreenRefreshMode> = _refreshMode.asStateFlow()

    private val _thermalProfile = MutableStateFlow(ThermalGovernorProfile.OPPO_TURBO)
    val thermalProfile: StateFlow<ThermalGovernorProfile> = _thermalProfile.asStateFlow()

    private val _resolutionScale = MutableStateFlow(ResolutionScale.FSR_BALANCED)
    val resolutionScale: StateFlow<ResolutionScale> = _resolutionScale.asStateFlow()

    private val _touchConfig = MutableStateFlow(TouchControlsConfig())
    val touchConfig: StateFlow<TouchControlsConfig> = _touchConfig.asStateFlow()

    private val _zramExpansionGb = MutableStateFlow(8)
    val zramExpansionGb: StateFlow<Int> = _zramExpansionGb.asStateFlow()

    private val _directSyscallBypass = MutableStateFlow(true)
    val directSyscallBypass: StateFlow<Boolean> = _directSyscallBypass.asStateFlow()

    private val _asyncShaderCaching = MutableStateFlow(true)
    val asyncShaderCaching: StateFlow<Boolean> = _asyncShaderCaching.asStateFlow()

    private val _dynarecOptimizationLevel = MutableStateFlow(2) // 0=safe, 1=fast, 2=extreme
    val dynarecOptimizationLevel: StateFlow<Int> = _dynarecOptimizationLevel.asStateFlow()

    init {
        val savedDriverId = prefs.getString("selected_driver_id", availableDrivers[0].id)
        _selectedDriver.value = availableDrivers.find { it.id == savedDriverId } ?: availableDrivers[0]

        val savedHz = prefs.getInt("refresh_hz", 144)
        _refreshMode.value = ScreenRefreshMode.values().find { it.hz == savedHz } ?: ScreenRefreshMode.HZ_144

        val savedThermal = prefs.getString("thermal_profile", ThermalGovernorProfile.OPPO_TURBO.name)
        _thermalProfile.value = runCatching { ThermalGovernorProfile.valueOf(savedThermal!!) }.getOrDefault(ThermalGovernorProfile.OPPO_TURBO)

        val savedRes = prefs.getString("res_scale", ResolutionScale.FSR_BALANCED.name)
        _resolutionScale.value = runCatching { ResolutionScale.valueOf(savedRes!!) }.getOrDefault(ResolutionScale.FSR_BALANCED)

        val savedOpacity = prefs.getFloat("touch_opacity", 0.85f)
        val savedHaptics = prefs.getBoolean("touch_haptics", true)
        val savedSteering = prefs.getString("touch_steering", SteeringMode.VIRTUAL_WHEEL.name)
        val steeringMode = runCatching { SteeringMode.valueOf(savedSteering!!) }.getOrDefault(SteeringMode.VIRTUAL_WHEEL)

        _touchConfig.value = TouchControlsConfig(
            steeringMode = steeringMode,
            opacity = savedOpacity,
            hapticFeedback = savedHaptics
        )
    }

    fun selectDriver(driver: VulkanDriver) {
        _selectedDriver.value = driver
        prefs.edit().putString("selected_driver_id", driver.id).apply()
    }

    fun setRefreshMode(mode: ScreenRefreshMode) {
        _refreshMode.value = mode
        prefs.edit().putInt("refresh_hz", mode.hz).apply()
    }

    fun setThermalProfile(profile: ThermalGovernorProfile) {
        _thermalProfile.value = profile
        prefs.edit().putString("thermal_profile", profile.name).apply()
    }

    fun setResolutionScale(scale: ResolutionScale) {
        _resolutionScale.value = scale
        prefs.edit().putString("res_scale", scale.name).apply()
    }

    fun updateTouchConfig(config: TouchControlsConfig) {
        _touchConfig.value = config
        prefs.edit()
            .putFloat("touch_opacity", config.opacity)
            .putBoolean("touch_haptics", config.hapticFeedback)
            .putString("touch_steering", config.steeringMode.name)
            .apply()
    }

    fun setZramExpansionGb(gb: Int) {
        _zramExpansionGb.value = gb
    }

    fun setDirectSyscallBypass(enabled: Boolean) {
        _directSyscallBypass.value = enabled
    }

    fun setAsyncShaderCaching(enabled: Boolean) {
        _asyncShaderCaching.value = enabled
    }

    fun setDynarecOptimizationLevel(level: Int) {
        _dynarecOptimizationLevel.value = level
    }
}
