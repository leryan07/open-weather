package com.example.openweather.ui.search

import com.example.openweather.network.FakeOpenWeatherApiService
import com.example.openweather.network.model.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test


@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    private val viewModel = SearchViewModel(FakeOpenWeatherApiService())

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun onSearchUIEvent_OnCityChange_Dallas() {
        viewModel.onSearchUIEvent(SearchUIEvent.OnCityChange("Dallas"))
        assertEquals("Dallas", viewModel.uiState.city)
    }

    @Test
    fun onSearchUIEvent_OnStateCodeChange_TX() {
        viewModel.onSearchUIEvent(SearchUIEvent.OnStateCodeChange("TX"))
        assertEquals("TX", viewModel.uiState.stateCode)
    }

    @Test
    fun onSearchUIEvent_OnCountryCodeChange_US() {
        viewModel.onSearchUIEvent(SearchUIEvent.OnCountryCodeChange("US"))
        assertEquals("US", viewModel.uiState.countryCode)
    }

    @Test
    fun onSearchUIEvent_OnSearchClick_404() = runTest(UnconfinedTestDispatcher()) {
        viewModel.onSearchUIEvent(SearchUIEvent.OnCityChange("Dallas"))
        launch {
            viewModel.onSearchUIEvent(SearchUIEvent.OnSearchClick)
        }

        assert(viewModel.uiState.apiResponse is Result.None)
    }

    @Test
    fun onSearchUIEvent_OnLocationSearchClick_200() = runTest(UnconfinedTestDispatcher()) {
        launch {
            viewModel.onSearchUIEvent(SearchUIEvent.OnRetrieveLocationSuccess(0.0, 0.0))
        }

        assert(viewModel.uiState.apiResponse is Result.Success)
        val apiResponseSuccess = viewModel.uiState.apiResponse as Result.Success
        assertEquals("Indianapolis", apiResponseSuccess.data.name)
    }
}