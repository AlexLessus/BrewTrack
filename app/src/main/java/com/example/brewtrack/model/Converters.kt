package com.example.brewtrack.model

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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

    @TypeConverter
    fun fromPourStepList(value: String?): List<PourStep> {
        if (value == null) {
            return emptyList()
        }
        val listType = object : TypeToken<List<PourStep>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun pourStepListToString(list: List<PourStep>?): String {
        return Gson().toJson(list ?: emptyList<PourStep>())
    }
}