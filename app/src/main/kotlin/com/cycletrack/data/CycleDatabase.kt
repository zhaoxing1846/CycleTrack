package com.cycletrack.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.cycletrack.model.CycleRecord

@Database(entities = [CycleRecord::class], version = 1, exportSchema = false)
abstract class CycleDatabase : RoomDatabase() {
    abstract fun cycleDao(): CycleDao
    
    companion object {
        @Volatile
        private var INSTANCE: CycleDatabase? = null
        
        fun getDatabase(context: Context): CycleDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CycleDatabase::class.java,
                    "cycle_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
