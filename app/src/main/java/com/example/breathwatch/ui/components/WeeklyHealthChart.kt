package com.example.breathwatch.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.breathwatch.domain.model.HealthLog
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.max
import kotlin.math.min

@Composable
fun WeeklyHealthChart(
    healthLogs: List<HealthLog>,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
    
    // Prepare data for the last 7 days
    val endDate = LocalDate.now()
    val startDate = endDate.minusDays(6)
    val dateRange = (0..6).map { startDate.plusDays(it.toLong()) }
    
    val chartData = dateRange.map { date ->
        val log = healthLogs.find { it.date.toLocalDate() == date }
        ChartDataPoint(
            date = date,
            feeling = log?.overallFeeling ?: 0,
            hasSymptoms = log?.hasSymptoms ?: false,
            aqi = log?.environmentalData?.aqi
        )
    }
    
    Column(modifier = modifier) {
        // Chart
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        ) {
            drawHealthChart(
                chartData = chartData,
                primaryColor = primaryColor,
                surfaceVariant = surfaceVariant
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Date labels
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            dateRange.forEach { date ->
                Text(
                    text = date.format(DateTimeFormatter.ofPattern("EEE")),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Legend
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            LegendItem(
                color = primaryColor,
                label = "Feeling",
                modifier = Modifier.weight(1f)
            )
            LegendItem(
                color = MaterialTheme.colorScheme.error,
                label = "Symptoms",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

private fun DrawScope.drawHealthChart(
    chartData: List<ChartDataPoint>,
    primaryColor: Color,
    surfaceVariant: Color
) {
    val width = size.width
    val height = size.height
    val padding = 20.dp.toPx()
    
    val chartWidth = width - (padding * 2)
    val chartHeight = height - (padding * 2)
    
    // Draw feeling line chart
    val feelingPath = Path()
    val pointRadius = 4.dp.toPx()
    
    chartData.forEachIndexed { index, dataPoint ->
        val x = padding + (index * chartWidth / (chartData.size - 1))
        val y = if (dataPoint.feeling > 0) {
            padding + chartHeight - (dataPoint.feeling - 1) * chartHeight / 4
        } else {
            padding + chartHeight / 2 // Default middle position for no data
        }
        
        if (index == 0) {
            feelingPath.moveTo(x, y)
        } else {
            feelingPath.lineTo(x, y)
        }
        
        // Draw feeling points
        if (dataPoint.feeling > 0) {
            drawCircle(
                color = primaryColor,
                radius = pointRadius,
                center = Offset(x, y)
            )
        }
        
        // Draw symptom indicators
        if (dataPoint.hasSymptoms) {
            drawCircle(
                color = Color.Red,
                radius = pointRadius / 2,
                center = Offset(x, padding + chartHeight + 10.dp.toPx())
            )
        }
    }
    
    // Draw the feeling line
    drawPath(
        path = feelingPath,
        color = primaryColor,
        style = Stroke(width = 2.dp.toPx())
    )
    
    // Draw grid lines
    for (i in 1..4) {
        val y = padding + (i * chartHeight / 5)
        drawLine(
            color = surfaceVariant,
            start = Offset(padding, y),
            end = Offset(width - padding, y),
            strokeWidth = 1.dp.toPx()
        )
    }
}

@Composable
private fun LegendItem(
    color: Color,
    label: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium
        )
    }
}

private data class ChartDataPoint(
    val date: LocalDate,
    val feeling: Int,
    val hasSymptoms: Boolean,
    val aqi: Int?
)
