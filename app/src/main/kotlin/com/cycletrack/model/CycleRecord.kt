package com.cycletrack.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 记录类型枚举
 */
enum class RecordType {
    PERIOD_START,   // 经期开始（红色）
    PERIOD_END,     // 经期结束（浅红色）
    INTIMACY        // 亲密（浅绿色）
}

/**
 * 数据模型
 * @param id 唯一标识
 * @param date 日期（时间戳，毫秒）
 * @param type 记录类型
 * @param notes 备注（可选）
 */
@Entity(tableName = "records")
data class CycleRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: Long,
    val type: RecordType,
    val notes: String = ""
) {
    /**
     * 获取格式化日期：yyyy-MM-dd
     */
    fun formatDate(): String {
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        return sdf.format(java.util.Date(date))
    }
    
    /**
     * 获取简洁日期：MM/dd
     */
    fun formatShortDate(): String {
        val sdf = java.text.SimpleDateFormat("MM/dd", java.util.Locale.getDefault())
        return sdf.format(java.util.Date(date))
    }
}

/**
 * 月度统计（用于 6 个月趋势图）
 */
data class MonthlyStats(
    val year: Int,
    val month: Int,           // 0-11
    val periodDays: Int,      // 经期天数
    val intimacyCount: Int    // 亲密次数
)

/**
 * 统计信息
 */
data class CycleStats(
    val lastPeriodStart: Long? = null,          // 上次经期开始
    val lastPeriodEnd: Long? = null,            // 上次经期结束
    val lastPeriodDays: Int = 0,                // 上次经期天数
    val cycleLength: Int = 0,                   // 周期长度（天）
    val nextPeriodPredicted: Long? = null,      // 下次经期预测开始日期
    val isPredicting: Boolean = false,          // 是否在预测状态
    val intimacyLast30Days: Int = 0,            // 近 30 天亲密次数
    val intimacyThisMonth: Int = 0,             // 本月亲密次数
    val intimacyLastMonth: Int = 0,             // 上月亲密次数
    val monthlyStats: List<MonthlyStats> = emptyList()  // 近 6 个月统计
)
