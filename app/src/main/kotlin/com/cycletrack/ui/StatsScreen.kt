package com.cycletrack.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.geometry.Offset
import com.cycletrack.model.CycleStats
import com.cycletrack.model.MonthlyStats

@Composable
fun StatsScreen(
    stats: CycleStats,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                Text(
                    text = "📊 近 6 个月统计",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                Text(
                    text = "🔴 经期天数趋势",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                PeriodTrendChart(
                    monthlyStats = stats.monthlyStats,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "💚 亲密次数趋势",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                IntimacyTrendChart(
                    monthlyStats = stats.monthlyStats,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("关闭")
                }
            }
        }
    }
}

@Composable
private fun PeriodTrendChart(
    monthlyStats: List<MonthlyStats>,
    modifier: Modifier = Modifier
) {
    val maxPeriodDays = monthlyStats.maxOfOrNull { it.periodDays } ?: 1
    val dataPoints = monthlyStats.map { it.periodDays.toFloat() }
    
    ChartCanvas(
        dataPoints = dataPoints,
        color = PeriodStartColor,
        maxValue = maxPeriodDays.coerceAtLeast(10),
        modifier = modifier
    )
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        monthlyStats.forEach { stat ->
            Text(
                text = "${stat.month + 1}月",
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun IntimacyTrendChart(
    monthlyStats: List<MonthlyStats>,
    modifier: Modifier = Modifier
) {
    val maxIntimacy = monthlyStats.maxOfOrNull { it.intimacyCount } ?: 1
    val dataPoints = monthlyStats.map { it.intimacyCount.toFloat() }
    
    ChartCanvas(
        dataPoints = dataPoints,
        color = IntimacyColor,
        maxValue = maxIntimacy.coerceAtLeast(5),
        modifier = modifier
    )
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        monthlyStats.forEach { stat ->
            Text(
                text = "${stat.month + 1}月",
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ChartCanvas(
    dataPoints: List<Float>,
    color: Color,
    maxValue: Int,
    modifier: Modifier = Modifier
) {
    if (dataPoints.isEmpty()) return
    
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        val width = size.width
        val height = size.height
        val padding = 20.dp.toPx()
        val chartWidth = width - 2 * padding
        val chartHeight = height - 2 * padding
        
        for (i in 0..4) {
            val y = padding + (i.toFloat() / 4) * chartHeight
            drawLine(
                color = Color.Gray.copy(alpha = 0.3f),
                start = Offset(padding, y),
                end = Offset(width - padding, y),
                strokeWidth = 1.dp.toPx()
            )
        }
        
        if (dataPoints.size > 1) {
            val stepX = chartWidth / (dataPoints.size - 1)
            val path = Path()
            path.moveTo(
                padding,
                padding + chartHeight - (dataPoints[0] / maxValue) * chartHeight
            )
            
            for (i in 1 until dataPoints.size) {
                path.lineTo(
                    padding + i * stepX,
                    padding + chartHeight - (dataPoints[i] / maxValue) * chartHeight
                )
            }
            
            drawPath(
                path = path,
                color = color,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )
            
            for (i in dataPoints.indices) {
                drawCircle(
                    color = color,
                    radius = 5.dp.toPx(),
                    center = Offset(
                        padding + i * stepX,
                        padding + chartHeight - (dataPoints[i] / maxValue) * chartHeight
                    )
                )
            }
        }
    }
}
