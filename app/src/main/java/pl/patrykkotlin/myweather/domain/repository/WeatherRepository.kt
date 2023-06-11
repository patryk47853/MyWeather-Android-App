package pl.patrykkotlin.myweather.domain.repository

import pl.patrykkotlin.myweather.domain.util.Resource
import pl.patrykkotlin.myweather.domain.weather.WeatherInfo

interface WeatherRepository {
    suspend fun getWeatherData(lat: Double, lon: Double): Resource<WeatherInfo>
}