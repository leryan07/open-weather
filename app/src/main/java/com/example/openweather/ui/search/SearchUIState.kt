package com.example.openweather.ui.search

import com.example.openweather.network.model.OpenWeatherResponse
import com.example.openweather.network.model.Result
import com.example.openweather.utils.UiText

data class SearchUIState(
    var city: String = "",
    var stateCode: String = "",
    var countryCode: String = "",
    var formError: UiText? = null,
    var isLoading: Boolean = false,
    var apiResponse: Result<OpenWeatherResponse>? = null,
    var errorMessage: UiText? = null
)
