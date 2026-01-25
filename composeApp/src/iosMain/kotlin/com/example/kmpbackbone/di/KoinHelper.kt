package com.example.kmpbackbone.di

import com.example.data.local.IosDatabaseContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class KoinHelper : KoinComponent {
//    fun getTasksViewModel(): TasksViewModel {
//        val viewModel: TasksViewModel by inject()
//        return viewModel
//    }
}

/**
 * Initialize Koin for iOS.
 * Call this from AppDelegate or App init.
 */
fun doInitKoin() {
    val koinApp = initKoin(IosDatabaseContext())
    val initializer = koinApp.koin.get<AppInitializer>()
    CoroutineScope(Dispatchers.Main + SupervisorJob()).launch {
        initializer.initialize()
    }
}
