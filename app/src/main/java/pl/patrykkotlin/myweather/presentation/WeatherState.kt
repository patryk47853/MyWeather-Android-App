package pl.patrykkotlin.myweather.presentation

import pl.patrykkotlin.myweather.domain.weather.WeatherInfo

data class WeatherState(
    val weatherInfo: WeatherInfo? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
