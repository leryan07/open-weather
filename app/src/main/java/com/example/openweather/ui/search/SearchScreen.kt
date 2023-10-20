package com.example.openweather.ui.search

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.openweather.R
import com.example.openweather.network.model.OpenWeatherResponse
import com.example.openweather.network.model.Result
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@SuppressLint("MissingPermission")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun SearchScreen(
    onApiResponseSuccess: (openWeatherResponse: OpenWeatherResponse) -> Unit,
    onApiResponseNone: () -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val locationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }
    val uiState = viewModel.uiState
    val locationPermissionsState = rememberPermissionState(
        android.Manifest.permission.ACCESS_FINE_LOCATION,
    )

    uiState.apiResponse?.let { response ->
        val currentOnApiResponseSuccess by rememberUpdatedState(onApiResponseSuccess)
        DisposableEffect(uiState) {
            when (response) {
                is Result.Success -> {
                    currentOnApiResponseSuccess(response.data)
                }

                is Result.None -> {
                    onApiResponseNone()
                }
            }

            onDispose {
                viewModel.apiResponseHandled()
            }
        }
    }

    uiState.errorMessage?.let { errorMessage ->
        LaunchedEffect(errorMessage) {
            snackbarHostState.showSnackbar(errorMessage.asString(context))
            viewModel.errorMessageShown()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (uiState.isLoading) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.width(64.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                )
            }
        } else {
            Text(text = "OpenWeather", fontSize = 24.sp)
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = uiState.city,
                onValueChange = { viewModel.onSearchUIEvent(SearchUIEvent.OnCityChange(it)) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = stringResource(R.string.city) + " *") },
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = uiState.stateCode,
                onValueChange = { viewModel.onSearchUIEvent(SearchUIEvent.OnStateCodeChange(it)) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = stringResource(R.string.state_code)) },
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = uiState.countryCode,
                onValueChange = { viewModel.onSearchUIEvent(SearchUIEvent.OnCountryCodeChange(it)) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = stringResource(R.string.country_code)) },
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column {
                Button(onClick = {
                    viewModel.onSearchUIEvent(SearchUIEvent.OnSearchClick)
                }) {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = stringResource(R.string.search_button)
                    )
                }
                Button(onClick = {
                    if (locationPermissionsState.status.isGranted) {
                        scope.launch(Dispatchers.IO) {
                            viewModel.onSearchUIEvent(SearchUIEvent.OnRetrieveLocationInitiated)

                            locationClient.getCurrentLocation(
                                Priority.PRIORITY_HIGH_ACCURACY,
                                CancellationTokenSource().token,
                            )
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        val lat = task.result.latitude
                                        val long = task.result.longitude

                                        viewModel.onSearchUIEvent(
                                            SearchUIEvent.OnRetrieveLocationSuccess(
                                                lat,
                                                long
                                            )
                                        )
                                    } else {
                                        viewModel.onSearchUIEvent(SearchUIEvent.OnRetrieveLocationFailure)
                                    }
                                }
                        }
                    } else {
                        scope.launch(Dispatchers.IO) {
                            locationPermissionsState.launchPermissionRequest()
                        }
                    }
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_my_location_24),
                        contentDescription = stringResource(
                            R.string.current_location_button
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            uiState.formError?.let {
                Text(text = it.asString(context), color = Color.Red)
            }
        }
    }
}