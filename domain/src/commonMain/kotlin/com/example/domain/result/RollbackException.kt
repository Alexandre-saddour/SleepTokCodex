package com.example.domain.result

class RollbackException(val error: AppResult.Error) : Exception()

fun <T> AppResult<T>.getOrRollback(): T = when (this) {
    is AppResult.Success -> value
    is AppResult.Error -> throw RollbackException(this)
}
