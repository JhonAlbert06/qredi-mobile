package com.pixelbrew.qredi.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.pixelbrew.qredi.data.dao.LoanDao
import com.pixelbrew.qredi.data.entities.FeeEntity
import com.pixelbrew.qredi.data.entities.LoanEntity
import com.pixelbrew.qredi.data.entities.NewFeeEntity

@Database(
    entities = [LoanEntity::class, FeeEntity::class, NewFeeEntity::class],
    version = 2,
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
                    "qredi_database1"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}