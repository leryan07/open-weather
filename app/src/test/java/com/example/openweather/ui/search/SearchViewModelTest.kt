package com.example.openweather.ui.search

import com.example.openweather.network.FakeOpenWeatherApiService
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
        assert(viewModel.uiState.navigateToNoResultsScreen)
    }

    @Test
    fun onSearchUIEvent_OnLocationSearchClick_200() = runTest(UnconfinedTestDispatcher()) {
        launch {
            viewModel.onSearchUIEvent(SearchUIEvent.OnLocationSearchClick(0.0, 0.0))
        }
        assertEquals("Indianapolis", viewModel.uiState.apiResponse?.name)
    }

    @Test
    fun onSearchUIEvent_ShowProgressIndicator_true() {
        viewModel.onSearchUIEvent(SearchUIEvent.ShowProgressIndicator(true))
        assert(viewModel.uiState.isSearchInProgress)
    }
}