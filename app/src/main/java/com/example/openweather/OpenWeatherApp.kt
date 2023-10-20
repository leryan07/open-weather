package com.example.openweather

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.openweather.network.model.OpenWeatherResponse
import com.example.openweather.ui.results.NoResultsScreen
import com.example.openweather.ui.results.ResultsScreen
import com.example.openweather.ui.search.SearchScreen
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

sealed class Screen(val route: String) {
    object Search : Screen(route = "search")
    object Results : Screen(route = "results/{response}")
    object NoResults : Screen(route = "noResults")
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OpenWeatherApp(
    navController: NavHostController = rememberNavController()
) {
    val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()
    val jsonAdapter = moshi.adapter(OpenWeatherResponse::class.java).lenient()

    val onNavigateToSearch = {
        navController.navigate(Screen.Search.route) {
            popUpTo(Screen.Search.route)
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(snackbarHost = { SnackbarHost(hostState = snackbarHostState) }) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Search.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = Screen.Search.route) {
                SearchScreen(
                    onApiResponseSuccess = { openWeatherResponse ->
                        val openWeatherResponseJson = jsonAdapter.toJson(openWeatherResponse)

                        navController.navigate(
                            Screen.Results.route.replace(
                                "{response}",
                                openWeatherResponseJson
                            )
                        )
                    },
                    onApiResponseNone = {
                        navController.navigate(Screen.NoResults.route)
                    },
                    snackbarHostState = snackbarHostState,
                    modifier = Modifier.padding(top = 16.dp, start = 8.dp, end = 8.dp),
                )
            }
            composable(route = Screen.Results.route) { backStackEntry ->
                val openWeatherResponseJson = backStackEntry.arguments?.getString("response")
                val openWeatherResponse = openWeatherResponseJson?.let { jsonAdapter.fromJson(it) }

                ResultsScreen(
                    openWeatherResponse = openWeatherResponse,
                    onNavigateToSearch = onNavigateToSearch,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
            composable(route = Screen.NoResults.route) {
                NoResultsScreen(
                    onNavigateToSearch = onNavigateToSearch,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}