package com.example.openweather.ui.results

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.openweather.R
import com.example.openweather.network.model.OpenWeatherResponse

@Composable
fun ResultsScreen(
    openWeatherResponse: OpenWeatherResponse?,
    onNavigateToSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val cityName = openWeatherResponse?.name ?: ""
    val temp = openWeatherResponse?.main?.temp ?: 0
    val feelsLike = openWeatherResponse?.main?.feelsLike ?: 0
    val tempMin = openWeatherResponse?.main?.tempMin ?: 0
    val tempMax = openWeatherResponse?.main?.tempMax ?: 0
    val weather = openWeatherResponse?.weather?.get(0)
    val weatherMainDescription = weather?.main ?: ""
    val weatherDescription = weather?.description ?: ""
    val icon = weather?.icon ?: ""
    val iconImageUrl = "https://openweathermap.org/img/wn/$icon@2x.png"

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = cityName, fontSize = 24.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = context.getString(R.string.temperature_placeholder, temp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = weatherMainDescription)
        AsyncImage(
            model = iconImageUrl,
            contentDescription = "$weatherDescription icon image",
            modifier = Modifier.size(128.dp),
            error = painterResource(id = R.drawable.baseline_broken_image_24),
            placeholder = painterResource(id = R.drawable.loading_img)
        )
        Text(text = context.getString(R.string.high_low_temperature_placeholder, tempMax, tempMin))
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = context.getString(R.string.feels_like_temperature_placeholder, feelsLike))
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { onNavigateToSearch() }) {
            Text(text = stringResource(R.string.retry_search))
        }
    }
}