package com.example.breathwatch.ui.onboarding

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.breathwatch.R
import com.example.breathwatch.util.LocationHelper

@Composable
fun OnboardingScreen(
    onNavigateToHome: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        
        if (fineLocationGranted || coarseLocationGranted) {
            viewModel.onLocationPermissionGranted()
        } else {
            viewModel.onLocationPermissionDenied()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        
        // App Logo/Icon (placeholder)
        Card(
            modifier = Modifier.size(120.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🫁",
                    style = MaterialTheme.typography.displayLarge
                )
            }
        }
        
        // Welcome Text
        Text(
            text = "Welcome to BreathWatch",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        
        Text(
            text = "Your personal air quality companion",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        
        // Privacy Information
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Privacy First",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "• All your health logs stay on your device\n• No personal data is shared with third parties\n• Analytics are completely optional\n• You control your data",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        
        // Features Overview
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "What BreathWatch Does",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "• Real-time air quality monitoring\n• Personalized health advice\n• Daily symptom tracking\n• Weekly health insights\n• Offline support",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        
        // Location Permission Section
        if (uiState.showLocationPermission) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Location Access",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "We need your location to provide accurate air quality data for your area. This helps us give you relevant health advice.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            locationPermissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Grant Location Permission")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(
                        onClick = { viewModel.onSkipLocationPermission() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Skip (Enter location manually)")
                    }
                }
            }
        }
        
        // Manual Location Entry
        if (uiState.showManualLocationEntry) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Enter Your Location",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = uiState.manualLocation,
                        onValueChange = viewModel::onManualLocationChanged,
                        label = { Text("City, Country") },
                        placeholder = { Text("e.g., Nairobi, Kenya") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (uiState.locationError != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = uiState.locationError,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Continue Button
        Button(
            onClick = {
                if (uiState.showManualLocationEntry && uiState.manualLocation.isNotBlank()) {
                    viewModel.saveManualLocation(uiState.manualLocation)
                }
                viewModel.completeOnboarding()
                onNavigateToHome()
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = uiState.canProceed
        ) {
            Text("Get Started")
        }
        
        // Terms and Privacy
        Text(
            text = "By continuing, you agree to our Terms of Service and Privacy Policy",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}
