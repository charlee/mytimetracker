package com.intelliavant.mytimetracker.data

import android.graphics.Color
import androidx.room.TypeConverter
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

class DateTypeConverter {
    @TypeConverter
    fun fromTimestamp(value: String): LocalDate {
        return LocalDate.parse(value)
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDate): String {
        return date.toString()
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

