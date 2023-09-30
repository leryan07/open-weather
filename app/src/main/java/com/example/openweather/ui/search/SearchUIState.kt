package com.example.openweather.ui.search

import com.example.openweather.network.model.OpenWeatherResponse
import com.example.openweather.utils.UiText

data class SearchUIState(
    var city: String = "",
    var stateCode: String = "",
    var countryCode: String = "",
    var formError: UiText? = null,
    var apiResponse: OpenWeatherResponse? = null,
    var navigateToNoResultsScreen: Boolean = false,
    var isSearchInProgress: Boolean = false
)
