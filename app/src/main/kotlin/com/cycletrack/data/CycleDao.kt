package com.cycletrack.data

import androidx.room.*
import com.cycletrack.model.CycleRecord
import com.cycletrack.model.RecordType
import kotlinx.coroutines.flow.Flow

@Dao
interface CycleDao {
    
    // ========== 查询 ==========
    
    @Query("SELECT * FROM records ORDER BY date DESC")
    fun getAllRecords(): Flow<List<CycleRecord>>
    
    @Query("SELECT * FROM records WHERE type = :type ORDER BY date DESC")
    fun getRecordsByType(type: RecordType): Flow<List<CycleRecord>>
    
    @Query("SELECT * FROM records WHERE date >= :start AND date < :end ORDER BY date ASC")
    fun getRecordsInRange(start: Long, end: Long): Flow<List<CycleRecord>>
    
    @Query("SELECT * FROM records WHERE date >= :start AND date <= :end AND type = :type ORDER BY date ASC")
    fun getRecordsInRangeAndType(start: Long, end: Long, type: RecordType): List<CycleRecord>
    
    // ========== 插入/删除 ==========
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: CycleRecord): Long
    
    @Delete
    suspend fun delete(record: CycleRecord)
    
    @Query("DELETE FROM records WHERE id = :id")
    suspend fun deleteById(id: Long)
    
    @Query("DELETE FROM records WHERE type = :type AND date = :date")
    suspend fun deleteByTypeAndDate(type: RecordType, date: Long)
}
