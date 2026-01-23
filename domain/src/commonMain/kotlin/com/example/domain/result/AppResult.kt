package com.example.domain.result

sealed class AppResult<out T> {
    data class Success<T>(val value: T) : AppResult<T>()
    data class Error(val error: DomainError) : AppResult<Nothing>()
}

sealed class DomainError {
    data object NotFound : DomainError()
    data object Validation : DomainError()
    data object Storage : DomainError()
    data object Conflict : DomainError()
    data object Unknown : DomainError()
}
