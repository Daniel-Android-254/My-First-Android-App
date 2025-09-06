package com.example.breathwatch.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.breathwatch.domain.model.AirQualityData
import com.example.breathwatch.domain.model.AqiCategory
import com.example.breathwatch.ui.theme.*

@Composable
fun AirQualityCard(
    airQuality: AirQualityData,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Air Quality",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                if (airQuality.isStale) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Stale data",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // AQI Display
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(30.dp))
                        .background(getAqiColor(airQuality.aqiCategory)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = airQuality.aqi?.toString() ?: "?",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column {
                    Text(
                        text = airQuality.aqiCategoryText,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    airQuality.locationName?.let { location ->
                        Text(
                            text = location,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Pollutant Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                airQuality.pm25?.let { pm25 ->
                    PollutantItem(
                        label = "PM2.5",
                        value = "${pm25.toInt()}",
                        unit = "µg/m³"
                    )
                }
                
                airQuality.pm10?.let { pm10 ->
                    PollutantItem(
                        label = "PM10",
                        value = "${pm10.toInt()}",
                        unit = "µg/m³"
                    )
                }
                
                airQuality.o3?.let { o3 ->
                    PollutantItem(
                        label = "O₃",
                        value = "${o3.toInt()}",
                        unit = "µg/m³"
                    )
                }
            }
        }
    }
}

@Composable
private fun PollutantItem(
    label: String,
    value: String,
    unit: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = unit,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun getAqiColor(category: AqiCategory): Color {
    return when (category) {
        AqiCategory.GOOD -> AqiGood
        AqiCategory.MODERATE -> AqiModerate
        AqiCategory.UNHEALTHY_FOR_SENSITIVE -> AqiUnhealthySensitive
        AqiCategory.UNHEALTHY -> AqiUnhealthy
        AqiCategory.VERY_UNHEALTHY -> AqiVeryUnhealthy
        AqiCategory.HAZARDOUS -> AqiHazardous
        AqiCategory.UNKNOWN -> Grey60
    }
}
