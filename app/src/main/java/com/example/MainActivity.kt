package com.example

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.ScreenRefreshMode
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.EmulatorScreen
import com.example.ui.viewmodel.EmulatorViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Keep screen awake for gaming session
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setContent {
            MyApplicationTheme {
                val viewModel: EmulatorViewModel = viewModel()
                MainAppContent(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppContent(viewModel: EmulatorViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val installation by viewModel.installation.collectAsStateWithLifecycle()
    val selectedDriver by viewModel.selectedDriver.collectAsStateWithLifecycle()
    val refreshMode by viewModel.refreshMode.collectAsStateWithLifecycle()
    val thermalProfile by viewModel.thermalProfile.collectAsStateWithLifecycle()
    val resolutionScale by viewModel.resolutionScale.collectAsStateWithLifecycle()
    val touchConfig by viewModel.touchConfig.collectAsStateWithLifecycle()
    val thermalStats by viewModel.thermalStats.collectAsStateWithLifecycle()
    val zramGb by viewModel.zramExpansionGb.collectAsStateWithLifecycle()
    val directSyscall by viewModel.directSyscallBypass.collectAsStateWithLifecycle()
    val asyncShaders by viewModel.asyncShaderCaching.collectAsStateWithLifecycle()
    val dynarecLevel by viewModel.dynarecOptimizationLevel.collectAsStateWithLifecycle()
    val notificationMessage by viewModel.notificationMessage.collectAsStateWithLifecycle()
    val selectedVehicle by viewModel.selectedVehicle.collectAsStateWithLifecycle()
    val selectedLevel by viewModel.selectedLevel.collectAsStateWithLifecycle()

    // Handle back button when navigating sub-screens
    BackHandler(enabled = currentScreen != EmulatorScreen.DASHBOARD) {
        viewModel.navigateTo(EmulatorScreen.DASHBOARD)
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepCarbon),
        containerColor = DeepCarbon,
        topBar = {
            if (currentScreen != EmulatorScreen.LIVE_EMULATION) {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(RacingOrange),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Speed,
                                    contentDescription = "Logo",
                                    tint = DeepCarbon,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "BeamPad Drive",
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 17.sp
                                )
                                Text(
                                    text = "Oppo Pad 3 Soft-Body Emulator",
                                    color = TextSecondary,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    },
                    actions = {
                        // 144Hz Active Sync Badge
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = SlateSurface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                            modifier = Modifier.padding(end = 6.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(PhysicsLime)
                                )
                                Text(
                                    text = "${refreshMode.hz}Hz",
                                    color = PhysicsLime,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        // Driver Badge
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = SlateSurface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text(
                                text = selectedDriver.name.take(18) + "...",
                                color = ElectricCyan,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                            )
                        }

                        // Quick Start Simulation button
                        Button(
                            onClick = { viewModel.navigateTo(EmulatorScreen.LIVE_EMULATION) },
                            colors = ButtonDefaults.buttonColors(containerColor = RacingOrange),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .height(36.dp)
                                .testTag("top_launch_button")
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = "Play", tint = DeepCarbon, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("RUN", color = DeepCarbon, fontWeight = FontWeight.Black, fontSize = 11.sp)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = SlateSurface,
                        titleContentColor = TextPrimary
                    )
                )
            }
        },
        bottomBar = {
            if (currentScreen != EmulatorScreen.LIVE_EMULATION) {
                NavigationBar(
                    containerColor = SlateSurface,
                    contentColor = TextPrimary,
                    tonalElevation = 8.dp,
                    modifier = Modifier.testTag("main_navigation_bar")
                ) {
                    NavigationBarItem(
                        selected = currentScreen == EmulatorScreen.DASHBOARD,
                        onClick = { viewModel.navigateTo(EmulatorScreen.DASHBOARD) },
                        icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
                        label = { Text("Dashboard", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = RacingOrange,
                            selectedTextColor = RacingOrange,
                            indicatorColor = RacingOrange.copy(alpha = 0.15f),
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        ),
                        modifier = Modifier.testTag("nav_dashboard")
                    )

                    NavigationBarItem(
                        selected = currentScreen == EmulatorScreen.GAME_FILES,
                        onClick = { viewModel.navigateTo(EmulatorScreen.GAME_FILES) },
                        icon = { Icon(Icons.Default.Folder, contentDescription = "Files") },
                        label = { Text("Game Files", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = RacingOrange,
                            selectedTextColor = RacingOrange,
                            indicatorColor = RacingOrange.copy(alpha = 0.15f),
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        ),
                        modifier = Modifier.testTag("nav_files")
                    )

                    NavigationBarItem(
                        selected = currentScreen == EmulatorScreen.DRIVERS_HUB,
                        onClick = { viewModel.navigateTo(EmulatorScreen.DRIVERS_HUB) },
                        icon = { Icon(Icons.Default.DeveloperBoard, contentDescription = "Drivers") },
                        label = { Text("Drivers Hub", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = RacingOrange,
                            selectedTextColor = RacingOrange,
                            indicatorColor = RacingOrange.copy(alpha = 0.15f),
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        ),
                        modifier = Modifier.testTag("nav_drivers")
                    )

                    NavigationBarItem(
                        selected = currentScreen == EmulatorScreen.OPPO_OPTIMIZATION,
                        onClick = { viewModel.navigateTo(EmulatorScreen.OPPO_OPTIMIZATION) },
                        icon = { Icon(Icons.Default.TabletAndroid, contentDescription = "Oppo Pad 3") },
                        label = { Text("Oppo Pad 3", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = RacingOrange,
                            selectedTextColor = RacingOrange,
                            indicatorColor = RacingOrange.copy(alpha = 0.15f),
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        ),
                        modifier = Modifier.testTag("nav_oppo_pad3")
                    )

                    NavigationBarItem(
                        selected = currentScreen == EmulatorScreen.TOUCH_CONTROLS,
                        onClick = { viewModel.navigateTo(EmulatorScreen.TOUCH_CONTROLS) },
                        icon = { Icon(Icons.Default.SportsEsports, contentDescription = "Controls") },
                        label = { Text("Controls", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = RacingOrange,
                            selectedTextColor = RacingOrange,
                            indicatorColor = RacingOrange.copy(alpha = 0.15f),
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        ),
                        modifier = Modifier.testTag("nav_controls")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(if (currentScreen == EmulatorScreen.LIVE_EMULATION) PaddingValues(0.dp) else innerPadding)
        ) {
            when (currentScreen) {
                EmulatorScreen.DASHBOARD -> DashboardScreen(
                    installation = installation,
                    selectedDriver = selectedDriver,
                    thermalStats = thermalStats,
                    refreshHz = refreshMode.hz,
                    onNavigate = { viewModel.navigateTo(it) }
                )
                EmulatorScreen.GAME_FILES -> GameFilesScreen(
                    installation = installation,
                    onImportFolder = { name, uri -> viewModel.importUserFolder(name, uri) },
                    onResetToSample = { viewModel.resetToSampleInstallation() }
                )
                EmulatorScreen.DRIVERS_HUB -> DriverHubScreen(
                    availableDrivers = viewModel.availableDrivers,
                    selectedDriver = selectedDriver,
                    directSyscallBypass = directSyscall,
                    asyncShaderCaching = asyncShaders,
                    dynarecLevel = dynarecLevel,
                    onSelectDriver = { viewModel.selectDriver(it) },
                    onToggleDirectSyscall = { viewModel.setDirectSyscallBypass(it) },
                    onToggleAsyncShader = { viewModel.setAsyncShaderCaching(it) },
                    onSetDynarecLevel = { viewModel.setDynarecOptimizationLevel(it) }
                )
                EmulatorScreen.OPPO_OPTIMIZATION -> OppoPad3OptScreen(
                    refreshMode = refreshMode,
                    thermalProfile = thermalProfile,
                    resolutionScale = resolutionScale,
                    thermalStats = thermalStats,
                    zramGb = zramGb,
                    onSetRefreshMode = { viewModel.setRefreshMode(it) },
                    onSetThermalProfile = { viewModel.setThermalProfile(it) },
                    onSetResolutionScale = { viewModel.setResolutionScale(it) },
                    onSetZramGb = { viewModel.setZramExpansionGb(it) }
                )
                EmulatorScreen.TOUCH_CONTROLS -> TouchControlsEditorScreen(
                    config = touchConfig,
                    onUpdateConfig = { viewModel.updateTouchConfig(it) }
                )
                EmulatorScreen.LIVE_EMULATION -> SimulationRunnerScreen(
                    simulator = viewModel.physicsSimulator,
                    installation = installation,
                    activeDriver = selectedDriver,
                    refreshHz = refreshMode.hz,
                    touchConfig = touchConfig,
                    selectedVehicle = selectedVehicle,
                    selectedLevel = selectedLevel,
                    onSelectVehicle = { viewModel.selectVehicle(it) },
                    onSelectLevel = { viewModel.selectLevel(it) },
                    onResetCar = { viewModel.resetCar() },
                    onRecoverCar = { viewModel.recoverCar() },
                    onHaptic = { viewModel.triggerHaptic() },
                    onExit = { viewModel.navigateTo(EmulatorScreen.DASHBOARD) }
                )
            }

            // Notification message toast pill
            AnimatedVisibility(
                visible = notificationMessage != null,
                enter = fadeIn() + slideInVertically(initialOffsetY = { -it }),
                exit = fadeOut() + slideOutVertically(targetOffsetY = { -it }),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp)
            ) {
                notificationMessage?.let { msg ->
                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = DeepCarbon.copy(alpha = 0.95f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, RacingOrange),
                        shadowElevation = 8.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Info, contentDescription = "Notification", tint = RacingOrange, modifier = Modifier.size(16.dp))
                            Text(
                                text = msg,
                                color = TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}
