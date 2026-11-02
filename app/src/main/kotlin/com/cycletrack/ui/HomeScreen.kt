package com.cycletrack.ui

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import com.cycletrack.data.CycleRepository
import com.cycletrack.model.CycleRecord
import com.cycletrack.model.CycleStats
import com.cycletrack.model.RecordType
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    repository: CycleRepository,
    context: Context = LocalContext.current
) {
    val scope = rememberCoroutineScope()
    
    // 使用 collectAsState 收集 Flow 数据
    val records: List<CycleRecord> by repository.allRecords.collectAsState(initial = emptyList())
    val stats: CycleStats by repository.stats.collectAsState(initial = CycleStats())
    
    // 生成预测日期
    val predictedDates by remember(stats) {
        derivedStateOf { repository.generatePredictedDates(stats) }
    }
    
    var selectedDate by remember { mutableStateOf(System.currentTimeMillis()) }
    var showStatsScreen by remember { mutableStateOf(false) }
    
    Scaffold(
        containerColor = Color(0xFFFFF9F7),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("CycleTrack", style = MaterialTheme.typography.titleLarge, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                        Text("温柔记录，读懂自己的节奏", style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    IconButton(onClick = { showStatsScreen = true }) {
                        Icon(
                            imageVector = Icons.Default.BarChart,
                            contentDescription = "统计",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 18.dp)
        ) {
            // 统计面板
            StatsPanel(stats = stats)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Text("我的日历", style = MaterialTheme.typography.titleLarge,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                Spacer(Modifier.weight(1f))
                androidx.compose.material3.Surface(color = Color(0xFFFFEAF0), shape = RoundedCornerShape(50)) {
                    Text("点选日期记录", Modifier.padding(horizontal = 11.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelSmall, color = Color(0xFFBD5573))
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 日历视图
            CalendarView(
                selectedDate = selectedDate,
                records = records,
                predictedDates = predictedDates,
                onDateSelected = { selectedDate = it },
                onPeriodStartToggle = { date ->
                    scope.launch {
                        try {
                            repository.toggleRecord(date, RecordType.PERIOD_START, context)
                            Toast.makeText(context, "✓ 标记经期开始", Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            Toast.makeText(context, "✗ 标记失败", Toast.LENGTH_LONG).show()
                        }
                    }
                },
                onPeriodEndToggle = { date ->
                    scope.launch {
                        try {
                            repository.toggleRecord(date, RecordType.PERIOD_END, context)
                            Toast.makeText(context, "✓ 标记经期结束", Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            Toast.makeText(context, "✗ 标记失败", Toast.LENGTH_LONG).show()
                        }
                    }
                },
                onIntimacyToggle = { date ->
                    scope.launch {
                        try {
                            repository.toggleRecord(date, RecordType.INTIMACY, context)
                            Toast.makeText(context, "✓ 标记亲密", Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            Toast.makeText(context, "✗ 标记失败", Toast.LENGTH_LONG).show()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 提示
            Text(
                text = "分别标记经期开始与结束，持续记录可让周期趋势更贴近你的实际情况。",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
    
    // 统计屏幕对话框
    if (showStatsScreen) {
        StatsScreen(
            stats = stats,
            onDismiss = { showStatsScreen = false }
        )
    }
}
