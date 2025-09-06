package com.example.breathwatch.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showLocationDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var showAqiThresholdDialog by remember { mutableStateOf(false) }

    // Show export success message
    LaunchedEffect(uiState.showExportSuccess) {
        if (uiState.showExportSuccess) {
            kotlinx.coroutines.delay(3000)
            viewModel.clearExportSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Export Success Message
            if (uiState.showExportSuccess) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Success",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Health data exported successfully!",
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            // Error Message
            uiState.error?.let { error ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Error",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Text(
                            text = error,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.clearError() }) {
                            Text("Dismiss")
                        }
                    }
                }
            }

            // Notifications Section
            SettingsSection(title = "Notifications") {
                SettingsItem(
                    icon = Icons.Default.Notifications,
                    title = "Air Quality Alerts",
                    subtitle = "Get notified when air quality exceeds threshold",
                    trailing = {
                        Switch(
                            checked = uiState.notificationsEnabled,
                            onCheckedChange = viewModel::updateNotificationsEnabled
                        )
                    }
                )
                
                SettingsItem(
                    icon = Icons.Default.Info,
                    title = "AQI Alert Threshold",
                    subtitle = "Current: ${uiState.aqiThreshold} µg/m³ PM2.5",
                    onClick = { showAqiThresholdDialog = true }
                )
            }

            // Location Section
            SettingsSection(title = "Location") {
                SettingsItem(
                    icon = Icons.Default.LocationOn,
                    title = "Location",
                    subtitle = if (uiState.userLocationName.isNotBlank()) 
                        uiState.userLocationName 
                    else "Not set",
                    onClick = { showLocationDialog = true }
                )
            }

            // Appearance Section
            SettingsSection(title = "Appearance") {
                SettingsItem(
                    icon = Icons.Default.Palette,
                    title = "Theme",
                    subtitle = when (uiState.themeMode) {
                        "light" -> "Light"
                        "dark" -> "Dark"
                        else -> "System default"
                    },
                    onClick = { showThemeDialog = true }
                )
            }

            // Data Section
            SettingsSection(title = "Data") {
                SettingsItem(
                    icon = Icons.Default.Download,
                    title = "Export Health Data",
                    subtitle = "Download your health logs as CSV",
                    onClick = { viewModel.exportHealthData() },
                    trailing = if (uiState.isExporting) {
                        {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    } else null
                )
            }

            // Privacy Section
            SettingsSection(title = "Privacy") {
                SettingsItem(
                    icon = Icons.Default.Security,
                    title = "Privacy Policy",
                    subtitle = "Learn how we protect your data"
                )
                
                SettingsItem(
                    icon = Icons.Default.Info,
                    title = "About BreathWatch",
                    subtitle = "Version 1.0.0"
                )
            }
        }
    }

    // Location Dialog
    if (showLocationDialog) {
        LocationDialog(
            currentLocation = uiState.userLocationName,
            onLocationChanged = { location ->
                viewModel.updateUserLocation(location)
                showLocationDialog = false
            },
            onDismiss = { showLocationDialog = false }
        )
    }

    // Theme Dialog
    if (showThemeDialog) {
        ThemeDialog(
            currentTheme = uiState.themeMode,
            onThemeChanged = { theme ->
                viewModel.updateThemeMode(theme)
                showThemeDialog = false
            },
            onDismiss = { showThemeDialog = false }
        )
    }

    // AQI Threshold Dialog
    if (showAqiThresholdDialog) {
        AqiThresholdDialog(
            currentThreshold = uiState.aqiThreshold,
            onThresholdChanged = { threshold ->
                viewModel.updateAqiThreshold(threshold)
                showAqiThresholdDialog = false
            },
            onDismiss = { showAqiThresholdDialog = false }
        )
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                content()
            }
        }
    }
}

@Composable
private fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String? = null,
    onClick: (() -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null
) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = subtitle?.let { { Text(it) } },
        leadingContent = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        trailingContent = trailing,
        modifier = if (onClick != null) {
            Modifier.clickable { onClick() }
        } else Modifier
    )
}

@Composable
private fun LocationDialog(
    currentLocation: String,
    onLocationChanged: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var location by remember { mutableStateOf(currentLocation) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Set Location") },
        text = {
            Column {
                Text("Enter your city and country for accurate air quality data.")
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location") },
                    placeholder = { Text("e.g., Nairobi, Kenya") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onLocationChanged(location) },
                enabled = location.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun ThemeDialog(
    currentTheme: String,
    onThemeChanged: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val themes = listOf(
        "system" to "System default",
        "light" to "Light",
        "dark" to "Dark"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Choose Theme") },
        text = {
            Column {
                themes.forEach { (value, label) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onThemeChanged(value) }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = currentTheme == value,
                            onClick = { onThemeChanged(value) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(label)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Done")
            }
        }
    )
}

@Composable
private fun AqiThresholdDialog(
    currentThreshold: Int,
    onThresholdChanged: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var threshold by remember { mutableStateOf(currentThreshold.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("AQI Alert Threshold") },
        text = {
            Column {
                Text("Set the PM2.5 level (µg/m³) at which you want to receive alerts.")
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = threshold,
                    onValueChange = { threshold = it },
                    label = { Text("PM2.5 µg/m³") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Recommended: 35 µg/m³ (WHO guideline)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    threshold.toIntOrNull()?.let { onThresholdChanged(it) }
                },
                enabled = threshold.toIntOrNull() != null && threshold.toInt() > 0
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
