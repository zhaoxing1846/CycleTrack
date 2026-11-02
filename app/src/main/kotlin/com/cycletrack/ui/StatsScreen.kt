package com.cycletrack.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.geometry.Offset
import com.cycletrack.model.CycleStats
import com.cycletrack.model.MonthlyStats

private val Ink = Color(0xFF422B3D)
private val Muted = Color(0xFF82737E)

@Composable
fun StatsScreen(stats: CycleStats, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(Modifier.fillMaxSize().padding(vertical = 12.dp), shape = RoundedCornerShape(30.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9F7))) {
            Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("YOUR PATTERNS", style = MaterialTheme.typography.labelMedium,
                            color = Color(0xFFBD5573), fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
                        Text("周期统计", style = MaterialTheme.typography.headlineSmall,
                            color = Ink, fontWeight = FontWeight.Bold)
                    }
                    Text("近 6 个月", style = MaterialTheme.typography.labelMedium, color = Muted)
                }
                Spacer(Modifier.height(18.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    SummaryCard("平均周期", stats.cycleLength.takeIf { it > 0 }?.let { "$it 天" } ?: "—",
                        Color(0xFFFFEAF0), Color(0xFFBD5573), Modifier.weight(1f))
                    SummaryCard("平均经期", stats.monthlyStats.map { it.periodDays }.filter { it > 0 }
                        .takeIf { it.isNotEmpty() }?.average()?.let { "${it.toInt()} 天" } ?: "—",
                        Color(0xFFF0EDFA), Color(0xFF7466A5), Modifier.weight(1f))
                }
                Spacer(Modifier.height(20.dp))
                TrendCard("经期天数", "每月记录的经期天数", stats.monthlyStats,
                    values = { it.periodDays }, color = Color(0xFFBD5573), suffix = "天")
                Spacer(Modifier.height(14.dp))
                TrendCard("亲密记录", "按月查看记录次数", stats.monthlyStats,
                    values = { it.intimacyCount }, color = Color(0xFF7466A5), suffix = "次")
                Spacer(Modifier.height(20.dp))
                OutlinedButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)) { Text("返回日历") }
            }
        }
    }
}

@Composable
private fun SummaryCard(label: String, value: String, bg: Color, accent: Color, modifier: Modifier = Modifier) {
    Card(modifier, shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = bg)) {
        Column(Modifier.fillMaxWidth().padding(16.dp)) {
            Text(label, style = MaterialTheme.typography.labelMedium, color = Muted)
            Spacer(Modifier.height(7.dp))
            Text(value, style = MaterialTheme.typography.headlineSmall, color = accent, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun TrendCard(title: String, subtitle: String, monthly: List<MonthlyStats>, values: (MonthlyStats) -> Int,
                      color: Color, suffix: String) {
    Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(Modifier.fillMaxWidth().padding(17.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = Ink, fontWeight = FontWeight.Bold)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = Muted)
            Spacer(Modifier.height(10.dp))
            if (monthly.all { values(it) == 0 }) {
                Box(Modifier.fillMaxWidth().height(110.dp), contentAlignment = Alignment.Center) {
                    Text("暂无记录，添加记录后会显示趋势", style = MaterialTheme.typography.bodySmall, color = Muted)
                }
            } else {
                TrendChart(monthly.map(values), color, Modifier.fillMaxWidth().height(135.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    monthly.forEach { month ->
                        Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("${values(month)}$suffix", style = MaterialTheme.typography.labelSmall,
                                color = color, fontWeight = FontWeight.Bold)
                            Text("${month.month + 1}月", style = MaterialTheme.typography.labelSmall, color = Muted)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TrendChart(values: List<Int>, color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        if (values.isEmpty()) return@Canvas
        val inset = 12.dp.toPx()
        val graphWidth = size.width - 2 * inset
        val graphHeight = size.height - 2 * inset
        val maxValue = (values.maxOrNull() ?: 1).coerceAtLeast(1).toFloat()
        for (i in 0..3) {
            val y = inset + i * graphHeight / 3f
            drawLine(Color(0xFFF0E9EC), Offset(inset, y), Offset(size.width - inset, y), 1.dp.toPx())
        }
        val points = values.mapIndexed { index, value ->
            val x = if (values.size == 1) size.width / 2 else inset + index * graphWidth / (values.size - 1)
            Offset(x, inset + graphHeight - (value / maxValue) * graphHeight)
        }
        if (points.size > 1) {
            val path = Path().apply { moveTo(points.first().x, points.first().y); points.drop(1).forEach { lineTo(it.x, it.y) } }
            drawPath(path, color, style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round))
        }
        points.forEach { point ->
            drawCircle(Color.White, radius = 6.dp.toPx(), center = point)
            drawCircle(color, radius = 4.dp.toPx(), center = point)
        }
    }
}
