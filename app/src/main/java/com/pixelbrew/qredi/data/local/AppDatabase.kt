package com.pixelbrew.qredi.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.pixelbrew.qredi.data.local.dao.LoanDao
import com.pixelbrew.qredi.data.local.entities.FeeEntity
import com.pixelbrew.qredi.data.local.entities.LoanEntity
import com.pixelbrew.qredi.data.local.entities.NewFeeEntity

@Database(
    entities = [LoanEntity::class, FeeEntity::class, NewFeeEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun loanDao(): LoanDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "qredi_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}