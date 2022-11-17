package com.driver.reliantdispatch.data.secondary

import androidx.room.TypeConverter
import java.util.*

class DBConverters{
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}