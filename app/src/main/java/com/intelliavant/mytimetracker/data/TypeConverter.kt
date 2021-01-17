package com.intelliavant.mytimetracker.data

import android.graphics.Color
import androidx.room.TypeConverter
import java.text.SimpleDateFormat
import java.util.*

class DateTypeConverter {
    @TypeConverter
    fun fromTimestamp(value: String): Date {
        val date = SimpleDateFormat("yyyy-mm-dd").parse(value)
        return date ?: Date()
    }

    @TypeConverter
    fun dateToTimestamp(date: Date): String {
        return SimpleDateFormat("yyyy-mm-dd").format(date)
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

