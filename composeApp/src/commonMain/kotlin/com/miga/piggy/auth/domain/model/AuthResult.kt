package com.miga.piggy.auth.domain.model

sealed class AuthResult<out T> {
    data class Success<T>(val data: T) : AuthResult<T>()
    data class Error(val exception: Throwable) : AuthResult<Nothing>()
    object Loading : AuthResult<Nothing>()
}
