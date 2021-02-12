package com.intelliavant.mytimetracker.data

import android.content.Context
import android.graphics.Color
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.intelliavant.mytimetracker.utils.ioThread
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


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
                instance ?: buildDatabase(context).also { instance = it }
            }

        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, "stopwatch")
                .addCallback(
                    object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)

                            GlobalScope.launch {
                                getInstance(context).workTypeDao().insertAll(PREPOPULATED_WORKTYPES)
                            }
                        }
                    }
                )
                .build()
        }

        val PREPOPULATED_WORKTYPES = listOf(
            WorkType(null, "Computer", Color.valueOf(Color.parseColor("#fce4ec")), true),
            WorkType(null, "English", Color.valueOf(Color.parseColor("#dcedc8")), true),
            WorkType(null, "Math", Color.valueOf(Color.parseColor("#fff9c4")), true),
        )
    }

}
