package com.example.openweather.network.model

sealed class Result<out R> {
    data class Success<out T>(val data: T) : Result<T>()
    object None : Result<Nothing>()
}
