package com.cycletrack.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cycletrack.model.CycleStats

@Composable
fun StatsPanel(
    stats: CycleStats,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "统计概览",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        // 经期信息卡片
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            colors = CardDefaults.cardColors(
                containerColor = PeriodStartContainer
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "🔴 经期信息",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = PeriodStartColor,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatItem(
                        label = "上次开始",
                        value = stats.lastPeriodStart?.formatDate() ?: "无记录",
                        modifier = Modifier.weight(1f)
                    )
                    StatItem(
                        label = "持续天数",
                        value = if (stats.lastPeriodDays > 0) "${stats.lastPeriodDays}天" else "-",
                        modifier = Modifier.weight(1f)
                    )
                    StatItem(
                        label = "周期长度",
                        value = "${stats.cycleLength}天",
                        modifier = Modifier.weight(1f)
                    )
                }
                
                // 预测信息
                if (stats.isPredicting && stats.nextPeriodPredicted != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Divider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = PredictionColor.copy(alpha = 0.3f)
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "📅 下次预测",
                                style = MaterialTheme.typography.bodyMedium,
                                color = PredictionColor,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = stats.nextPeriodPredicted.formatDate(),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = PredictionColor
                            )
                        }
                        Surface(
                            color = PredictionContainer,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Icon(
                                imageVector = androidx.compose.material.icons.Icons.Default.CalendarMonth,
                                contentDescription = "预测",
                                tint = PredictionColor,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }
            }
        }
        
        // 亲密信息卡片
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            colors = CardDefaults.cardColors(
                containerColor = IntimacyContainer
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "💚 亲密记录",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = IntimacyColor,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatItem(
                        label = "近 30 天",
                        value = "${stats.intimacyLast30Days}次",
                        modifier = Modifier.weight(1f)
                    )
                    StatItem(
                        label = "本月",
                        value = "${stats.intimacyThisMonth}次",
                        modifier = Modifier.weight(1f)
                    )
                    StatItem(
                        label = "上月",
                        value = "${stats.intimacyLastMonth}次",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

private fun Long.formatDate(): String {
    val sdf = java.text.SimpleDateFormat("MM/dd", java.util.Locale.getDefault())
    return sdf.format(java.util.Date(this))
}
