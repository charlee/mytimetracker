package com.intelliavant.mytimetracker.data

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.intelliavant.mytimetracker.MainApplication
import com.intelliavant.mytimetracker.R
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
            val context = MainApplication.context
            val colors = context.resources.getIntArray(R.array.work_type_colors)

            val prepopulatedWorkTypes = listOf(
                WorkType(null, "Homework", Color.valueOf(colors[0]), true),
                WorkType(null, "Reading", Color.valueOf(colors[1]), true),
                WorkType(null, "Outdoor", Color.valueOf(colors[2]), true),
            )


            return Room.databaseBuilder(context, AppDatabase::class.java, "stopwatch")
                .addCallback(
                    object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)

                            GlobalScope.launch {
                                getInstance(context).workTypeDao().insertAll(prepopulatedWorkTypes)
                            }
                        }
                    }
                )
                .build()
        }
    }

}
