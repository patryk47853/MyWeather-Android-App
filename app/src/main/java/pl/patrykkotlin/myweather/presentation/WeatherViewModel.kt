package pl.patrykkotlin.myweather.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import pl.patrykkotlin.myweather.domain.location.LocationTracker
import pl.patrykkotlin.myweather.domain.repository.WeatherRepository
import pl.patrykkotlin.myweather.domain.util.Resource
import pl.patrykkotlin.myweather.domain.weather.WeatherInfo
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository,
    private val locationTracker: LocationTracker
) : ViewModel() {

    var state by mutableStateOf(WeatherState())
        private set

    fun loadWeatherInfo() {
        viewModelScope.launch {
            state = state.copy(
                isLoading = true,
                error = null
            )
            locationTracker.getCurrentLocation()?.let { location ->
                val result = repository.getWeatherData(location.latitude, location.longitude)
                handleWeatherResult(result)
            } ?: run {
                state = state.copy(
                    isLoading = false,
                    error = "Couldn't retrieve location, please check if you have granted permission for location access."
                )
            }
        }
    }

    private fun handleWeatherResult(result: Resource<WeatherInfo>) {
        when (result) {
            is Resource.Success -> {
                state = state.copy(
                    weatherInfo = result.data,
                    isLoading = false,
                    error = null
                )
            }
            is Resource.Error -> {
                state = state.copy(
                    weatherInfo = null,
                    isLoading = false,
                    error = result.message
                )
            }
        }
    }
}
