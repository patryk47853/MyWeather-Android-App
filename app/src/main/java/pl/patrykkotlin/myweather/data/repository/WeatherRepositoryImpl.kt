package pl.patrykkotlin.myweather.data.repository

import pl.patrykkotlin.myweather.data.mappers.toWeatherInfo
import pl.patrykkotlin.myweather.data.weather.WeatherApi
import pl.patrykkotlin.myweather.domain.repository.WeatherRepository
import pl.patrykkotlin.myweather.domain.util.Resource
import pl.patrykkotlin.myweather.domain.weather.WeatherInfo
import java.lang.Exception
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val api: WeatherApi
): WeatherRepository {
    override suspend fun getWeatherData(lat: Double, lon: Double): Resource<WeatherInfo> {
        return try {
            Resource.Success(
                data = api.getWeatherData(
                    lat = lat,
                    lon = lon
                ).toWeatherInfo()
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(e.message ?: "An unknown error occurred.")
        }
    }
}