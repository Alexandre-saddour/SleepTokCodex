package com.example.kmpbackbone.home

sealed interface HomeUiState {
    data object Loading : HomeUiState

    data class Success(
        val welcomeMessage: String,
    ) : HomeUiState

    data class Error(
        val message: String,
    ) : HomeUiState
}