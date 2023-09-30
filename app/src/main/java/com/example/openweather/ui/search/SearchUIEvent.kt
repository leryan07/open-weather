package com.example.openweather.ui.search

sealed class SearchUIEvent {
    data class OnCityChange(val city: String) : SearchUIEvent()
    data class OnStateCodeChange(val stateCode: String) : SearchUIEvent()
    data class OnCountryCodeChange(val countryCode: String) : SearchUIEvent()
    object OnSearchClick : SearchUIEvent()
    data class OnLocationSearchClick(val lat: Double, val long: Double) : SearchUIEvent()
}
