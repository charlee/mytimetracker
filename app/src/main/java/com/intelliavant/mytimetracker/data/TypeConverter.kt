package com.intelliavant.mytimetracker.data

import android.graphics.Color
import androidx.room.TypeConverter
import java.util.*

class DateTypeConverter {
    @TypeConverter
    fun fromTimestamp(value: Long): Date {
        return Date(value)
    }

    @TypeConverter
    fun dateToTimestamp(date: Date): Long {
        return date.time
    }
}

class ColorTypeConverter {
    @TypeConverter
    fun fromColorValue(value: Int): Color {
        return Color.valueOf(value)
    }

    @TypeConverter
    fun valueToColor(color: Color): Int {
        return color.toArgb()
    }
}

