package com.example.streamingvideoapp.activity

import org.json.JSONException

import org.json.JSONObject


class WeatherData {
    private var micon: String? = null
    private var mcity: String? = null
    var mTemperature: String? = null
    var mWeatherType: String? = null
    var mCondition = 0
    var humidity:String?=null
    var mainPressure:String?=null
    var windSpeed:String?=null

    fun fromJson(jsonObject: JSONObject): WeatherData? {
        return try {
            var weatherD = WeatherData()
            weatherD.mcity = jsonObject.getString("name")
            weatherD.mCondition = jsonObject.getJSONArray("weather").getJSONObject(0).getInt("id")
            weatherD.mWeatherType =
                jsonObject.getJSONArray("weather").getJSONObject(0).getString("main")
            weatherD.micon = updateWeatherIcon(weatherD.mCondition)
            val tempResult = jsonObject.getJSONObject("main").getDouble("temp") - 273.15
            val roundedValue = Math.rint(tempResult).toInt()
            weatherD.mTemperature = Integer.toString(roundedValue)
            weatherD.humidity=
                jsonObject.getJSONObject("main").getString("humidity")
            weatherD.mainPressure=
                jsonObject.getJSONObject("main").getString("pressure")
            weatherD.windSpeed=
                jsonObject.getJSONObject("wind").getDouble("speed").toString()
            return weatherD
        } catch (e: JSONException) {
            e.printStackTrace()
            null
        }
    }

    private fun updateWeatherIcon(condition: Int): String? {
        if (condition in 0..300) {
            return "thunderstrom1"
        } else if (condition in 300..500) {
            return "lightrain"
        } else if (condition in 500..600) {
            return "shower"
        } else if (condition in 600..700) {
            return "snow2"
        } else if (condition in 701..771) {
            return "fog"
        } else if (condition in 772..800) {
            return "overcast"
        } else if (condition == 800) {
            return "sunny"
        } else if (condition in 801..804) {
            return "cloudy"
        } else if (condition in 900..902) {
            return "thunderstrom1"
        }
        if (condition == 903) {
            return "snow1"
        }
        if (condition == 904) {
            return "sunny"
        }
        return if (condition in 905..1000) {
            "thunderstrom2"
        } else "dunno"
    }

    fun getmTemperature(): String? {
        return "$mTemperature°C"
    }

    fun getMicon(): String? {
        return micon
    }

    fun getMcity(): String? {
        return mcity
    }

    fun getmWeatherType(): String? {
        return mWeatherType
    }
    fun getmhumidity(): String? {
        return humidity
    }
    fun getPressure(): String? {
        return mainPressure
    }
    fun getwindSpeed(): String? {
        return windSpeed
    }


}