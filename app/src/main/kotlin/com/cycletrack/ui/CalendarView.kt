package com.cycletrack.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cycletrack.model.CycleRecord
import com.cycletrack.model.RecordType
import java.util.Calendar
import androidx.compose.ui.graphics.vector.ImageVector

// ========== 颜色定义 ==========
val PeriodStartColor = Color(0xFFE53935)         // 经期内始：红色
val PeriodStartContainer = Color(0xFFFFEBEE)     // 经期内始容器：浅红色
val PeriodEndColor = Color(0xFFFF7043)           // 经期结束：浅红色/橙色
val PeriodEndContainer = Color(0xFFFFCCBC)       // 经期结束容器：浅粉色
val IntimacyColor = Color(0xFF66BB6A)            // 亲密：浅绿色
val IntimacyContainer = Color(0xFFC8E6C9)        // 亲密容器：浅绿色
val PredictionColor = Color(0xFFF48FB1)          // 预测：粉色
val PredictionContainer = Color(0xFFF8BBD0)      // 预测容器：浅粉色

@Composable
fun CalendarView(
    selectedDate: Long,
    records: List<CycleRecord>,
    predictedDates: List<Long>,  // 预测的经期日期
    onDateSelected: (Long) -> Unit,
    onPeriodStartToggle: (Long) -> Unit,
    onPeriodEndToggle: (Long) -> Unit,
    onIntimacyToggle: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var currentYear by remember { mutableStateOf(Calendar.getInstance().get(Calendar.YEAR)) }
    var currentMonth by remember { mutableStateOf(Calendar.getInstance().get(Calendar.MONTH)) }
    
    Column(modifier = modifier) {
        // 月份导航
        MonthNavigation(
            year = currentYear,
            month = currentMonth,
            onPrevious = {
                val cal = Calendar.getInstance().apply { set(currentYear, currentMonth, 1) }
                cal.add(Calendar.MONTH, -1)
                currentYear = cal.get(Calendar.YEAR)
                currentMonth = cal.get(Calendar.MONTH)
            },
            onNext = {
                val cal = Calendar.getInstance().apply { set(currentYear, currentMonth, 1) }
                cal.add(Calendar.MONTH, 1)
                currentYear = cal.get(Calendar.YEAR)
                currentMonth = cal.get(Calendar.MONTH)
            }
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // 星期标题
        WeekDayHeaders()
        
        // 日历网格
        CalendarGrid(
            year = currentYear,
            month = currentMonth,
            selectedDate = selectedDate,
            records = records,
            predictedDates = predictedDates,
            onDateSelected = onDateSelected
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 操作按钮
        ActionButtons(
            selectedDate = selectedDate,
            records = records,
            onPeriodStartToggle = onPeriodStartToggle,
            onPeriodEndToggle = onPeriodEndToggle,
            onIntimacyToggle = onIntimacyToggle
        )
    }
}

@Composable
private fun MonthNavigation(
    year: Int,
    month: Int,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPrevious) {
            Icon(Icons.Default.ChevronLeft, contentDescription = "上个月")
        }
        
        Text(
            text = "${year}年 ${month + 1}月",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        
        IconButton(onClick = onNext) {
            Icon(Icons.Default.ChevronRight, contentDescription = "下个月")
        }
    }
}

@Composable
private fun WeekDayHeaders() {
    Row(modifier = Modifier.fillMaxWidth()) {
        val weekdays = listOf("日", "一", "二", "三", "四", "五", "六")
        for (day in weekdays) {
            Text(
                text = day,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
private fun CalendarGrid(
    year: Int,
    month: Int,
    selectedDate: Long,
    records: List<CycleRecord>,
    predictedDates: List<Long>,
    onDateSelected: (Long) -> Unit
) {
    val calendar = Calendar.getInstance().apply {
        set(year, month, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    
    val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    val today = Calendar.getInstance()
    
    Column {
        for (row in 0 until ((firstDayOfWeek - 1 + daysInMonth + 6) / 7)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (col in 0 until 7) {
                    val cellIndex = row * 7 + col
                    val dayOffset = cellIndex - (firstDayOfWeek - 1)
                    
                    if (dayOffset < 0 || dayOffset >= daysInMonth) {
                        Box(modifier = Modifier.weight(1f).aspectRatio(1f))
                    } else {
                        val day = dayOffset + 1
                        val dateCal = Calendar.getInstance().apply {
                            set(year, month, day)
                            set(Calendar.HOUR_OF_DAY, 0)
                            set(Calendar.MINUTE, 0)
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                        }
                        val dateMillis = dateCal.timeInMillis
                        
                        // 简单过滤：比较年、月、日
                        val dayRecords = records.filter { record ->
                            val recordCal = Calendar.getInstance().apply { timeInMillis = record.date }
                            recordCal.get(Calendar.YEAR) == year &&
                            recordCal.get(Calendar.MONTH) == month &&
                            recordCal.get(Calendar.DAY_OF_MONTH) == day
                        }
                        
                        val isSelected = isSameDay(dateMillis, selectedDate)
                        val isToday = isSameDay(dateMillis, today.timeInMillis)
                        val hasPeriodStart = dayRecords.any { it.type == RecordType.PERIOD_START }
                        val hasPeriodEnd = dayRecords.any { it.type == RecordType.PERIOD_END }
                        val hasIntimacy = dayRecords.any { it.type == RecordType.INTIMACY }
                        val isPredicted = predictedDates.any { isSameDay(dateMillis, it) }
                        
                        CalendarDay(
                            day = day,
                            isSelected = isSelected,
                            isToday = isToday,
                            hasPeriodStart = hasPeriodStart,
                            hasPeriodEnd = hasPeriodEnd,
                            hasIntimacy = hasIntimacy,
                            isPredicted = isPredicted,
                            onClick = { onDateSelected(dateMillis) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarDay(
    day: Int,
    isSelected: Boolean,
    isToday: Boolean,
    hasPeriodStart: Boolean,
    hasPeriodEnd: Boolean,
    hasIntimacy: Boolean,
    isPredicted: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var backgroundColor by mutableStateOf(Color.Transparent)
    var textColor by mutableStateOf(MaterialTheme.colorScheme.onSurface)
    var icon: ImageVector? = null
    var hasIcon by mutableStateOf(false)
    
    when {
        isSelected -> {
            backgroundColor = MaterialTheme.colorScheme.primary
            textColor = MaterialTheme.colorScheme.onPrimary
        }
        hasPeriodStart -> {
            backgroundColor = PeriodStartContainer
            textColor = PeriodStartColor
            icon = Icons.Default.Favorite
            hasIcon = true
        }
        hasPeriodEnd -> {
            backgroundColor = PeriodEndContainer
            textColor = PeriodEndColor
            icon = Icons.Default.Check
            hasIcon = true
        }
        hasIntimacy -> {
            backgroundColor = IntimacyContainer
            textColor = IntimacyColor
            icon = Icons.Default.Spa
            hasIcon = true
        }
        isPredicted -> {
            backgroundColor = PredictionContainer
            textColor = PredictionColor
            icon = Icons.Default.CalendarMonth
            hasIcon = true
        }
        isToday -> {
            backgroundColor = MaterialTheme.colorScheme.primaryContainer
            textColor = MaterialTheme.colorScheme.onPrimaryContainer
        }
    }
    
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = day.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = textColor,
                fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal
            )
            if (hasIcon && !isSelected && icon != null) {
                Icon(
                    imageVector = icon as ImageVector,
                    contentDescription = null,
                    modifier = Modifier.size(10.dp),
                    tint = textColor
                )
            }
        }
    }
}

@Composable
private fun ActionButtons(
    selectedDate: Long,
    records: List<CycleRecord>,
    onPeriodStartToggle: (Long) -> Unit,
    onPeriodEndToggle: (Long) -> Unit,
    onIntimacyToggle: (Long) -> Unit
) {
    // 标准化时间戳到 00:00:00
    val selectedCal = Calendar.getInstance().apply {
        timeInMillis = selectedDate
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    val dayStart = selectedCal.timeInMillis
    
    // 过滤当天的记录
    val dayRecords = records.filter { record ->
        val recordCal = Calendar.getInstance().apply { timeInMillis = record.date }
        recordCal.get(Calendar.YEAR) == selectedCal.get(Calendar.YEAR) &&
        recordCal.get(Calendar.MONTH) == selectedCal.get(Calendar.MONTH) &&
        recordCal.get(Calendar.DAY_OF_MONTH) == selectedCal.get(Calendar.DAY_OF_MONTH)
    }
    
    val hasPeriodStart = dayRecords.any { it.type == RecordType.PERIOD_START }
    val hasPeriodEnd = dayRecords.any { it.type == RecordType.PERIOD_END }
    val hasIntimacy = dayRecords.any { it.type == RecordType.INTIMACY }
    
    // 第一行：经期内始和结束
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        // 经期开始按钮
        Button(
            onClick = { onPeriodStartToggle(dayStart) },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (hasPeriodStart) PeriodStartColor else PeriodStartContainer
            ),
            modifier = Modifier.weight(1f).padding(end = 4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = if (hasPeriodStart) Color.White else PeriodStartColor
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (hasPeriodStart) "已标记" else "经期开始",
                color = if (hasPeriodStart) Color.White else PeriodStartColor
            )
        }
        
        // 经期结束按钮
        Button(
            onClick = { onPeriodEndToggle(dayStart) },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (hasPeriodEnd) PeriodEndColor else PeriodEndContainer
            ),
            modifier = Modifier.weight(1f).padding(start = 4.dp, end = 4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = if (hasPeriodEnd) Color.White else PeriodEndColor
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (hasPeriodEnd) "已结束" else "经期结束",
                color = if (hasPeriodEnd) Color.White else PeriodEndColor
            )
        }
    }
    
    Spacer(modifier = Modifier.height(8.dp))
    
    // 第二行：亲密
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        // 亲密按钮
        Button(
            onClick = { onIntimacyToggle(dayStart) },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (hasIntimacy) IntimacyColor else IntimacyContainer
            ),
            modifier = Modifier.weight(1f).padding(start = 4.dp, end = 4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Spa,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = if (hasIntimacy) Color.White else IntimacyColor
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (hasIntimacy) "已标记" else "亲密",
                color = if (hasIntimacy) Color.White else IntimacyColor
            )
        }
    }
}

private fun isSameDay(millis1: Long, millis2: Long): Boolean {
    val cal1 = Calendar.getInstance().apply { timeInMillis = millis1 }
    val cal2 = Calendar.getInstance().apply { timeInMillis = millis2 }
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
           cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
           cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)
}
