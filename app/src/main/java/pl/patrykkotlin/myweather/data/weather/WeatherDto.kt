package pl.patrykkotlin.myweather.data.weather

import com.squareup.moshi.Json

data class WeatherDto(
    @Json(name = "hourly")
    val weatherData: WeatherDataDto
)
