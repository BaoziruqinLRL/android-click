package com.example.androidclick.data.local

import androidx.room.TypeConverter
import com.example.androidclick.domain.model.ClickMode
import com.example.androidclick.domain.model.ClickPoint
import org.json.JSONArray
import org.json.JSONObject

class ClickerConverters {

    @TypeConverter
    fun fromClickMode(mode: ClickMode): String = mode.name

    @TypeConverter
    fun toClickMode(value: String): ClickMode = ClickMode.valueOf(value)

    @TypeConverter
    fun fromClickPointList(points: List<ClickPoint>): String {
        val jsonArray = JSONArray()
        points.forEach { point ->
            val obj = JSONObject().apply {
                put("x", point.x.toDouble())
                put("y", point.y.toDouble())
                put("delayAfterMs", point.delayAfterMs)
            }
            jsonArray.put(obj)
        }
        return jsonArray.toString()
    }

    @TypeConverter
    fun toClickPointList(json: String): List<ClickPoint> {
        val jsonArray = JSONArray(json)
        val points = mutableListOf<ClickPoint>()
        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            points.add(
                ClickPoint(
                    x = obj.getDouble("x").toFloat(),
                    y = obj.getDouble("y").toFloat(),
                    delayAfterMs = obj.getLong("delayAfterMs")
                )
            )
        }
        return points
    }
}
