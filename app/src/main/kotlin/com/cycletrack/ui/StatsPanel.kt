package com.cycletrack.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cycletrack.model.CycleStats

private val Ink = Color(0xFF422B3D)
private val MutedInk = Color(0xFF82737E)
private val Rose = Color(0xFFBD5573)
private val Blush = Color(0xFFFFEAF0)
private val Lavender = Color(0xFF7466A5)
private val LavenderWash = Color(0xFFF0EDFA)

@Composable
fun StatsPanel(stats: CycleStats, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text("CYCLE OVERVIEW", style = MaterialTheme.typography.labelMedium,
                    color = Rose, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
                Text("身体节律", style = MaterialTheme.typography.headlineSmall,
                    color = Ink, fontWeight = FontWeight.Bold)
            }
            Icon(Icons.Default.AutoGraph, null, tint = Rose, modifier = Modifier
                .clip(CircleShape).background(Blush).padding(12.dp).size(22.dp))
        }
        Spacer(Modifier.height(16.dp))
        Card(shape = RoundedCornerShape(26.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8F6))) {
            Column(Modifier.fillMaxWidth().padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.WaterDrop, null, tint = Rose, modifier = Modifier.size(19.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("经期追踪", style = MaterialTheme.typography.titleMedium,
                        color = Ink, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.weight(1f))
                    Surface(color = Blush, shape = RoundedCornerShape(50)) {
                        Text(if (stats.isPredicting) "周期预测" else "持续记录",
                            Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            color = Rose, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold)
                    }
                }
                Spacer(Modifier.height(20.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1.25f)) {
                        Text("预计下次经期", style = MaterialTheme.typography.labelMedium, color = MutedInk)
                        Spacer(Modifier.height(5.dp))
                        Text(stats.nextPeriodPredicted?.formatDate() ?: "记录后生成",
                            style = MaterialTheme.typography.titleLarge, color = Ink, fontWeight = FontWeight.Bold)
                    }
                    Box(Modifier.width(1.dp).height(38.dp).background(Color(0xFFEADDE1)))
                    Metric("周期", if (stats.cycleLength > 0) "${stats.cycleLength} 天" else "—",
                        Modifier.weight(0.8f))
                    Metric("经期", if (stats.lastPeriodDays > 0) "${stats.lastPeriodDays} 天" else "—",
                        Modifier.weight(0.8f))
                }
                if (stats.nextPeriodPredicted != null) {
                    Spacer(Modifier.height(16.dp))
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CalendarMonth, null, tint = Rose, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(7.dp))
                        Text("上次开始  ${stats.lastPeriodStart?.formatDate() ?: "暂无记录"}",
                            style = MaterialTheme.typography.bodySmall, color = MutedInk)
                    }
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = LavenderWash)) {
            Row(Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 17.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Favorite, null, tint = Lavender, modifier = Modifier.size(19.dp))
                Spacer(Modifier.width(9.dp))
                Text("亲密记录", style = MaterialTheme.typography.titleSmall,
                    color = Ink, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.weight(1f))
                MiniMetric("${stats.intimacyLast30Days}", "近30天", Lavender)
                Spacer(Modifier.width(16.dp))
                MiniMetric("${stats.intimacyThisMonth}", "本月", Lavender)
            }
        }
    }
}

@Composable
private fun Metric(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier.padding(start = 14.dp), horizontalAlignment = Alignment.Start) {
        Text(value, fontSize = 19.sp, color = Ink, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.labelSmall, color = MutedInk)
    }
}

@Composable
private fun MiniMetric(value: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleMedium, color = color, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.labelSmall, color = MutedInk)
    }
}

private fun Long.formatDate(): String =
    java.text.SimpleDateFormat("M月d日", java.util.Locale.CHINA).format(java.util.Date(this))
