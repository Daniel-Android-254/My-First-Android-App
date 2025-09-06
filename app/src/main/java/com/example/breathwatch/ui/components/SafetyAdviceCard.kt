package com.example.breathwatch.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
fun SafetyAdviceCard(
    airQuality: AirQualityData,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded }
            .animateContentSize(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = getAdviceCardColor(airQuality.aqiCategory)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Safety advice",
                        tint = getAdviceTextColor(airQuality.aqiCategory),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Safety Advice",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = getAdviceTextColor(airQuality.aqiCategory)
                    )
                }
                
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    tint = getAdviceTextColor(airQuality.aqiCategory)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = airQuality.safetyAdvice,
                style = MaterialTheme.typography.bodyMedium,
                color = getAdviceTextColor(airQuality.aqiCategory)
            )
            
            if (isExpanded) {
                Spacer(modifier = Modifier.height(12.dp))
                
                // Additional detailed advice based on AQI category
                when (airQuality.aqiCategory) {
                    AqiCategory.UNHEALTHY_FOR_SENSITIVE, AqiCategory.UNHEALTHY -> {
                        Column {
                            Text(
                                text = "Recommendations:",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = getAdviceTextColor(airQuality.aqiCategory)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "• Wear N95 or equivalent masks outdoors\n• Keep windows and doors closed\n• Use air purifiers indoors\n• Avoid outdoor exercise",
                                style = MaterialTheme.typography.bodySmall,
                                color = getAdviceTextColor(airQuality.aqiCategory)
                            )
                        }
                    }
                    AqiCategory.VERY_UNHEALTHY, AqiCategory.HAZARDOUS -> {
                        Column {
                            Text(
                                text = "Emergency Measures:",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = getAdviceTextColor(airQuality.aqiCategory)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "• Stay indoors at all times\n• Seal gaps around doors and windows\n• Run air purifiers continuously\n• Seek medical attention if experiencing symptoms",
                                style = MaterialTheme.typography.bodySmall,
                                color = getAdviceTextColor(airQuality.aqiCategory)
                            )
                        }
                    }
                    else -> {
                        Text(
                            text = "Enjoy the fresh air! This is a great time for outdoor activities.",
                            style = MaterialTheme.typography.bodySmall,
                            color = getAdviceTextColor(airQuality.aqiCategory)
                        )
                    }
                }
            }
        }
    }
}

private fun getAdviceCardColor(category: AqiCategory): Color {
    return when (category) {
        AqiCategory.GOOD -> AqiGood.copy(alpha = 0.1f)
        AqiCategory.MODERATE -> AqiModerate.copy(alpha = 0.1f)
        AqiCategory.UNHEALTHY_FOR_SENSITIVE -> AqiUnhealthySensitive.copy(alpha = 0.1f)
        AqiCategory.UNHEALTHY -> AqiUnhealthy.copy(alpha = 0.1f)
        AqiCategory.VERY_UNHEALTHY -> AqiVeryUnhealthy.copy(alpha = 0.1f)
        AqiCategory.HAZARDOUS -> AqiHazardous.copy(alpha = 0.1f)
        AqiCategory.UNKNOWN -> Grey30
    }
}

private fun getAdviceTextColor(category: AqiCategory): Color {
    return when (category) {
        AqiCategory.GOOD -> AqiGood
        AqiCategory.MODERATE -> AqiModerate
        AqiCategory.UNHEALTHY_FOR_SENSITIVE -> AqiUnhealthySensitive
        AqiCategory.UNHEALTHY -> AqiUnhealthy
        AqiCategory.VERY_UNHEALTHY -> AqiVeryUnhealthy
        AqiCategory.HAZARDOUS -> AqiHazardous
        AqiCategory.UNKNOWN -> Grey70
    }
}
