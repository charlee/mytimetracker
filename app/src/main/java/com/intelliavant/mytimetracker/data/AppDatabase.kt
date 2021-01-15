package com.intelliavant.mytimetracker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters


@Database(entities = [Work::class, WorkType::class], version = 1)
@TypeConverters(DateTypeConverter::class, ColorTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun workDao(): WorkDao
    abstract fun workTypeDao(): WorkTypeDao

    companion object {

        @Volatile
        private var instance: AppDatabase? = null

        // https://gorillalogic.com/blog/android-room-tutorial-simplifying-how-you-work-with-app-data/
        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance?: buildDatabase(context).also { instance = it }
            }

        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, "stopwatch").build()
        }
    }

}
