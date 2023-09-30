package com.example.openweather.ui.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.openweather.R
import com.example.openweather.network.OpenWeatherApiService
import com.example.openweather.network.model.OpenWeatherResponse
import com.example.openweather.utils.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(private val openWeatherApiService: OpenWeatherApiService) :
    ViewModel() {

    var uiState by mutableStateOf(SearchUIState())
        private set

    fun onSearchUIEvent(event: SearchUIEvent) {
        when (event) {
            is SearchUIEvent.OnCityChange -> {
                uiState = uiState.copy(city = event.city)
            }

            is SearchUIEvent.OnStateCodeChange -> {
                uiState = uiState.copy(stateCode = event.stateCode)
            }

            is SearchUIEvent.OnCountryCodeChange -> {
                uiState = uiState.copy(countryCode = event.countryCode)
            }

            is SearchUIEvent.OnSearchClick -> {
                if (isFormValid()) {
                    val query = StringBuilder(uiState.city)
                    if (uiState.stateCode.isNotEmpty()) query.append(",${uiState.stateCode}")
                    if (uiState.countryCode.isNotEmpty()) query.append(",${uiState.countryCode}")

                    uiState = uiState.copy(isSearchInProgress = true, hasSearchError = false)

                    viewModelScope.launch {
                        try {
                            val response =
                                openWeatherApiService.getWeatherByQuery(query.toString())
                            handleResponse(response)
                        } catch (e: Exception) {
                            uiState = uiState.copy(isSearchInProgress = false, hasSearchError = true)
                        }
                    }
                }
            }

            is SearchUIEvent.OnLocationSearchClick -> {
                uiState = uiState.copy(hasSearchError = false, formError = null)

                viewModelScope.launch {
                    try {
                        val response = openWeatherApiService.getWeatherByCurrentLocation(
                            event.lat,
                            event.long
                        )
                        handleResponse(response)
                    } catch (e: Exception) {
                        uiState = uiState.copy(isSearchInProgress = false, hasSearchError = true)
                    }
                }
            }

            is SearchUIEvent.ShowProgressIndicator -> {
                uiState = uiState.copy(isSearchInProgress = event.showProgressIndicator)
            }
        }
    }

    fun resetState() {
        uiState = uiState.copy(
            apiResponse = null,
            navigateToNoResultsScreen = false,
            isSearchInProgress = false
        )
    }

    private fun isFormValid(): Boolean {
        if (uiState.city.isEmpty()) {
            uiState =
                uiState.copy(formError = UiText.StringResource(R.string.city_is_required_error))
            return false
        }

        // When searching by state code, city and country code can't be empty
        if (uiState.stateCode.isNotEmpty() && (uiState.city.isEmpty() || uiState.countryCode.isEmpty())) {
            uiState =
                uiState.copy(formError = UiText.StringResource(R.string.state_code_search_error))
            return false
        }

        uiState = uiState.copy(formError = null)
        return true
    }

    private fun handleResponse(response: Response<OpenWeatherResponse>) {
        uiState = if (response.isSuccessful) {
            uiState.copy(apiResponse = response.body())
        } else if (response.code() == 404) {
            uiState.copy(navigateToNoResultsScreen = true)
        } else {
            uiState.copy(isSearchInProgress = false)
        }
    }
}