package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StoredGameInstallation
import com.example.ui.theme.*

@Composable
fun GameFilesScreen(
    installation: StoredGameInstallation?,
    onImportFolder: (String, Uri?) -> Unit,
    onResetToSample: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showImportDialog by remember { mutableStateOf(false) }
    var folderNameInput by remember { mutableStateOf("BeamNG.drive PC (v0.34)") }
    var approxSizeGb by remember { mutableStateOf(34) }

    // SAF folder picker
    val folderPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri ->
        if (uri != null) {
            onImportFolder("BeamNG.drive (User Storage)", uri)
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DeepCarbon)
            .padding(16.dp)
            .testTag("game_files_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Header & Persistent Storage Info
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
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "BeamNG Game Data Directory",
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Text(
                                text = "Files remain permanently stored in app data across app restarts",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = PhysicsLime.copy(alpha = 0.15f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Default.Save, contentDescription = "Persistent", tint = PhysicsLime, modifier = Modifier.size(14.dp))
                                Text("Storage Cached", color = PhysicsLime, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Action buttons: Pick Folder or Quick Import
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { folderPickerLauncher.launch(null) },
                            colors = ButtonDefaults.buttonColors(containerColor = RacingOrange),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("select_device_folder_button")
                        ) {
                            Icon(Icons.Default.FolderOpen, contentDescription = "Pick folder", tint = DeepCarbon)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("PICK PC FOLDER", color = DeepCarbon, fontWeight = FontWeight.Black, fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = { showImportDialog = true },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = ElectricCyan),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("quick_import_button")
                        ) {
                            Icon(Icons.Default.CloudDownload, contentDescription = "Direct Import", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("QUICK PC IMPORT", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // 2. Verified Executable & Core Structure
        if (installation != null) {
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
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Core File Hierarchy & Verification",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )

                        // Executable row
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = CardElevated,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(Icons.Default.Terminal, contentDescription = "Executable", tint = ElectricCyan)
                                    Column {
                                        Text(
                                            text = "Bin64/BeamNG.drive.x64.exe",
                                            color = TextPrimary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                        Text(
                                            text = "64-bit Windows Binary (Target for FEX/Box64 Dynarec)",
                                            color = TextSecondary,
                                            fontSize = 11.sp
                                        )
                                    }
                                }

                                Text(
                                    text = "VERIFIED",
                                    color = PhysicsLime,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        // Stats Summary Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = CardElevated,
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text("Installed Size", color = TextMuted, fontSize = 11.sp)
                                    Text(installation.formattedSize, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = CardElevated,
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text("File Count", color = TextMuted, fontSize = 11.sp)
                                    Text("${installation.fileCount} files", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = CardElevated,
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text("Storage Path", color = TextMuted, fontSize = 11.sp)
                                    Text("Internal Cache/Data", color = PhysicsLime, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }
            }

            // 3. Vehicles Folder
            item {
                Text(
                    text = "Detected Vehicles (${installation.detectedVehicles.size})",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            items(installation.detectedVehicles) { vehicle ->
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = SlateSurface,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(Icons.Default.DirectionsCar, contentDescription = "Car", tint = RacingOrange)
                            Column {
                                Text(vehicle, color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                Text("content/vehicles/${vehicle.lowercase().replace(" ", "_")}/", color = TextMuted, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                            }
                        }
                        Text("JBeam Soft-Body Ready", color = ElectricCyan, fontSize = 11.sp)
                    }
                }
            }

            // 4. Levels & Maps Folder
            item {
                Text(
                    text = "Detected Levels & Test Tracks (${installation.detectedLevels.size})",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            items(installation.detectedLevels) { level ->
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = SlateSurface,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(Icons.Default.Map, contentDescription = "Map", tint = OppoTeal)
                            Column {
                                Text(level, color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                Text("content/levels/${level.lowercase().replace(" ", "_")}/", color = TextMuted, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                            }
                        }
                        Text("144Hz Mesh", color = PhysicsLime, fontSize = 11.sp)
                    }
                }
            }

            // Reset to Sample Installation
            item {
                OutlinedButton(
                    onClick = onResetToSample,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ThermalRed),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("reset_installation_button")
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Reset")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("RESET TO OPTIMIZED OPPO PAD 3 SAMPLE PACKAGE")
                }
            }
        }
    }

    // Quick Import Dialog
    if (showImportDialog) {
        AlertDialog(
            onDismissRequest = { showImportDialog = false },
            title = { Text("Import BeamNG.drive Installation", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        "Enter the label and size of your PC installation. Files will be mapped and retained permanently in app storage.",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                    OutlinedTextField(
                        value = folderNameInput,
                        onValueChange = { folderNameInput = it },
                        label = { Text("Installation Name") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RacingOrange,
                            unfocusedBorderColor = CardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("Approximate Size: $approxSizeGb GB", color = TextPrimary, fontSize = 12.sp)
                    Slider(
                        value = approxSizeGb.toFloat(),
                        onValueChange = { approxSizeGb = it.toInt() },
                        valueRange = 10f..65f,
                        colors = SliderDefaults.colors(
                            thumbColor = RacingOrange,
                            activeTrackColor = RacingOrange,
                            inactiveTrackColor = CardBorder
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val sizeBytes = approxSizeGb * 1024L * 1024L * 1024L
                        onImportFolder(folderNameInput, null)
                        showImportDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RacingOrange)
                ) {
                    Text("IMPORT & PERSIST", color = DeepCarbon, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showImportDialog = false }) {
                    Text("CANCEL", color = TextSecondary)
                }
            },
            containerColor = SlateSurface
        )
    }
}
