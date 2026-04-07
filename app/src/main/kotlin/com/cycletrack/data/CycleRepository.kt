package com.cycletrack.data

import android.content.Context
import com.cycletrack.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.Calendar

class CycleRepository(context: Context) {
    private val dao = CycleDatabase.getDatabase(context).cycleDao()
    
    val allRecords: Flow<List<CycleRecord>> = dao.getAllRecords()
    
    val stats: Flow<CycleStats> = allRecords.map { records ->
        calculateStats(records)
    }
    
    private fun calculateStats(records: List<CycleRecord>): CycleStats {
        val now = System.currentTimeMillis()
        val calendar = Calendar.getInstance()
        
        val periodStartRecords = records.filter { it.type == RecordType.PERIOD_START }.sortedBy { it.date }
        val periodEndRecords = records.filter { it.type == RecordType.PERIOD_END }.sortedBy { it.date }
        
        val lastPeriodStart = periodStartRecords.lastOrNull()?.date
        val lastPeriodEnd = periodEndRecords.lastOrNull()?.date
        
        var lastPeriodDays = 0
        if (lastPeriodStart != null && lastPeriodEnd != null && lastPeriodEnd >= lastPeriodStart) {
            lastPeriodDays = ((lastPeriodEnd - lastPeriodStart) / (24 * 60 * 60 * 1000) + 1).toInt()
        } else if (lastPeriodStart != null && lastPeriodEnd == null) {
            val today = calendar.apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
            lastPeriodDays = ((today - lastPeriodStart) / (24 * 60 * 60 * 1000) + 1).toInt()
        }
        
        var cycleLength = 0
        if (periodStartRecords.size >= 2) {
            val sortedDesc = periodStartRecords.sortedByDescending { it.date }
            val diff = sortedDesc[0].date - sortedDesc[1].date
            cycleLength = (diff / (24 * 60 * 60 * 1000)).toInt()
        }
        
        var nextPeriodPredicted: Long? = null
        var isPredicting = false
        if (lastPeriodStart != null && cycleLength > 0) {
            val hasOngoingPeriod = lastPeriodEnd == null || lastPeriodEnd < lastPeriodStart
            if (!hasOngoingPeriod || lastPeriodEnd != null) {
                nextPeriodPredicted = lastPeriodStart + (cycleLength.toLong() * 24 * 60 * 60 * 1000)
                isPredicting = true
            }
        }
        
        val intimacyRecords = records.filter { it.type == RecordType.INTIMACY }
        
        val thirtyDaysAgo = now - (30L * 24 * 60 * 60 * 1000)
        val intimacyLast30Days = intimacyRecords.count { it.date >= thirtyDaysAgo }
        
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val monthStart = calendar.timeInMillis
        val intimacyThisMonth = intimacyRecords.count { it.date >= monthStart }
        
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentYear = calendar.get(Calendar.YEAR)
        
        val lastMonthCalendar = Calendar.getInstance().apply {
            set(currentYear, currentMonth, 1)
            add(Calendar.MONTH, -1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val lastMonthStart = lastMonthCalendar.timeInMillis
        
        val thisMonthCalendar = Calendar.getInstance().apply {
            set(currentYear, currentMonth, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val thisMonthStart = thisMonthCalendar.timeInMillis
        
        val intimacyLastMonth = intimacyRecords.count { 
            it.date >= lastMonthStart && it.date < thisMonthStart 
        }
        
        val monthlyStats = calculateMonthlyStats(records, 6)
        
        return CycleStats(
            lastPeriodStart = lastPeriodStart,
            lastPeriodEnd = lastPeriodEnd,
            lastPeriodDays = lastPeriodDays,
            cycleLength = if (cycleLength > 0) cycleLength else 28,
            nextPeriodPredicted = nextPeriodPredicted,
            isPredicting = isPredicting,
            intimacyLast30Days = intimacyLast30Days,
            intimacyThisMonth = intimacyThisMonth,
            intimacyLastMonth = intimacyLastMonth,
            monthlyStats = monthlyStats
        )
    }
    
    private fun calculateMonthlyStats(records: List<CycleRecord>, months: Int): List<MonthlyStats> {
        val now = Calendar.getInstance()
        val result = mutableListOf<MonthlyStats>()
        
        for (i in 0 until months) {
            val monthCalendar = Calendar.getInstance().apply {
                set(Calendar.YEAR, now.get(Calendar.YEAR))
                set(Calendar.MONTH, now.get(Calendar.MONTH) - i)
                set(Calendar.DAY_OF_MONTH, 1)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            
            val monthStart = monthCalendar.timeInMillis
            val nextMonthCalendar = Calendar.getInstance().apply {
                timeInMillis = monthStart
                add(Calendar.MONTH, 1)
            }
            val monthEnd = nextMonthCalendar.timeInMillis
            
            val year = monthCalendar.get(Calendar.YEAR)
            val month = monthCalendar.get(Calendar.MONTH)
            
            val periodStartRecords = records.filter { 
                it.type == RecordType.PERIOD_START && 
                it.date >= monthStart && it.date < monthEnd 
            }
            val periodEndRecords = records.filter { 
                it.type == RecordType.PERIOD_END && 
                it.date >= monthStart && it.date < monthEnd 
            }
            
            var periodDays = 0
            for (startRecord in periodStartRecords) {
                val endRecord = periodEndRecords.firstOrNull { it.date >= startRecord.date }
                if (endRecord != null) {
                    val days = ((endRecord.date - startRecord.date) / (24 * 60 * 60 * 1000) + 1).toInt()
                    periodDays += days
                }
            }
            
            val intimacyCount = records.count { 
                it.type == RecordType.INTIMACY && 
                it.date >= monthStart && it.date < monthEnd 
            }
            
            result.add(MonthlyStats(year, month, periodDays, intimacyCount))
        }
        
        return result.sortedBy { it.year * 12 + it.month }
    }
    
    fun generatePredictedDates(stats: CycleStats): List<Long> {
        val predicted = stats.nextPeriodPredicted ?: return emptyList()
        if (!stats.isPredicting) return emptyList()
        
        val result = mutableListOf<Long>()
        val calendar = Calendar.getInstance().apply {
            timeInMillis = predicted
        }
        
        for (i in 0 until 30) {
            result.add(calendar.timeInMillis)
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        
        return result
    }
    
    suspend fun toggleRecord(dateMillis: Long, type: RecordType, context: Context) {
        withContext(Dispatchers.IO) {
            val calendar = Calendar.getInstance().apply {
                timeInMillis = dateMillis
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val dayStart = calendar.timeInMillis
            val dayEnd = dayStart + (24L * 60 * 60 * 1000) - 1
            
            val existingList = dao.getRecordsInRangeAndType(dayStart, dayEnd, type)
            
            if (existingList.isNotEmpty()) {
                dao.delete(existingList.first())
            } else {
                dao.insert(CycleRecord(date = dayStart, type = type))
            }
        }
    }
    
    suspend fun deleteRecord(record: CycleRecord) {
        withContext(Dispatchers.IO) {
            dao.delete(record)
        }
    }
}
