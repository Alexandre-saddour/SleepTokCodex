package com.example.kmpbackbone.di

import com.example.data.seed.SeedDataInitializer
import com.example.domain.result.AppResult
import io.github.aakira.napier.Napier
import com.example.domain.dispatchers.DispatcherProvider
import kotlinx.coroutines.withContext

/**
 * Handles application initialization tasks.
 * This should be called from the platform-specific entry points after Koin is initialized.
 */
class AppInitializer(
    private val seedDataInitializer: SeedDataInitializer,
    private val dispatcherProvider: DispatcherProvider,
) {
    /**
     * Initialize the application.
     * This method should be called from a coroutine scope managed by the platform entry point.
     */
    suspend fun initialize() {
        withContext(dispatcherProvider.io) {
            Napier.d("Starting app initialization...")
            when (val result = seedDataInitializer.seedIfNeeded()) {
                is AppResult.Success -> Napier.d("App initialization completed successfully")
                is AppResult.Error -> Napier.e("App initialization failed: ${result.error}")
            }
        }
    }
}
