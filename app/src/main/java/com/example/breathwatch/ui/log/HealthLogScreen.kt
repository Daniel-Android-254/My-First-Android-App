package com.example.breathwatch.ui.log

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.breathwatch.domain.model.HealthLog
import com.example.breathwatch.domain.model.Symptom
import com.example.breathwatch.ui.components.WeeklyHealthChart
import com.example.breathwatch.util.LocationHelper
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthLogScreen(
    onNavigateBack: () -> Unit,
    viewModel: HealthLogViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var currentLocation by remember { mutableStateOf<Pair<Double, Double>?>(null) }

    LaunchedEffect(Unit) {
        LocationHelper.getCurrentLocation(context) { lat, lon ->
            currentLocation = Pair(lat, lon)
        }
    }

    // Show success message
    LaunchedEffect(uiState.showSuccessMessage) {
        if (uiState.showSuccessMessage) {
            kotlinx.coroutines.delay(2000)
            viewModel.clearSuccessMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Health Log") },
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
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Success Message
            if (uiState.showSuccessMessage) {
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
                            text = "Health log saved successfully!",
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

            // Today's Check-in
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = if (uiState.hasLoggedToday) "Today's Log" else "Daily Check-in",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Overall Feeling Slider
                    Text(
                        text = "How are you feeling today?",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        (1..5).forEach { feeling ->
                            FeelingButton(
                                feeling = feeling,
                                isSelected = uiState.overallFeeling == feeling,
                                onClick = { viewModel.updateOverallFeeling(feeling) }
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Symptoms Checklist
                    Text(
                        text = "Any symptoms today?",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(Symptom.values()) { symptom ->
                            SymptomChip(
                                symptom = symptom,
                                isSelected = uiState.selectedSymptoms.contains(symptom),
                                onClick = { viewModel.toggleSymptom(symptom) }
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Notes
                    OutlinedTextField(
                        value = uiState.notes,
                        onValueChange = viewModel::updateNotes,
                        label = { Text("Notes (optional)") },
                        placeholder = { Text("How are you feeling? Any observations?") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        maxLines = 5
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Save Button
                    Button(
                        onClick = {
                            currentLocation?.let { (lat, lon) ->
                                viewModel.saveHealthLog(lat, lon)
                            } ?: viewModel.saveHealthLog()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isLoading
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text(if (uiState.hasLoggedToday) "Update Log" else "Save Log")
                        }
                    }
                }
            }

            // Weekly Overview
            if (uiState.weeklyLogs.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "This Week",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        WeeklyHealthChart(
                            healthLogs = uiState.weeklyLogs,
                            modifier = Modifier.height(200.dp)
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Recent Logs Summary
                        Text(
                            text = "Recent Entries",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        uiState.weeklyLogs.take(3).forEach { log ->
                            HealthLogSummaryItem(log = log)
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FeelingButton(
    feeling: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val emoji = when (feeling) {
        1 -> "😞"
        2 -> "😕"
        3 -> "😐"
        4 -> "🙂"
        5 -> "😊"
        else -> "😐"
    }
    
    val label = when (feeling) {
        1 -> "Very Poor"
        2 -> "Poor"
        3 -> "Fair"
        4 -> "Good"
        5 -> "Excellent"
        else -> "Fair"
    }
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(
                    if (isSelected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.surfaceVariant
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = emoji,
                style = MaterialTheme.typography.headlineSmall
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun SymptomChip(
    symptom: Symptom,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        onClick = onClick,
        label = { Text(symptom.displayName) },
        selected = isSelected
    )
}

@Composable
private fun HealthLogSummaryItem(log: HealthLog) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = log.date.format(DateTimeFormatter.ofPattern("MMM dd")),
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = log.feelingDescription,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (log.hasSymptoms) {
            Text(
                text = "${log.symptoms.size} symptoms",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}
