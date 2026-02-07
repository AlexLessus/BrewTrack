package com.example.brewtrack.model

import androidx.room.TypeConverter
import java.util.Date

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromTurbulence(value: String?): TurbulenceType {
        return try {
            if (value != null) TurbulenceType.valueOf(value) else TurbulenceType.NONE
        } catch (e: Exception) {
            TurbulenceType.NONE
        }
    }

    @TypeConverter
    fun turbulenceToString(turbulence: TurbulenceType?): String {
        return turbulence?.name ?: TurbulenceType.NONE.name
    }
}