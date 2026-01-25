package com.example.kmpbackbone

import android.app.Application
import com.example.kmpbackbone.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.logger.Level
import com.example.kmpbackbone.di.AppInitializer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        initKoin(this) {
            androidLogger(Level.DEBUG)
            androidContext(this@MainApplication)
        }.koin.get<AppInitializer>().let { initializer ->
            CoroutineScope(Dispatchers.Main + SupervisorJob()).launch {
                initializer.initialize()
            }
        }
    }
}
