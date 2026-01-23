package com.example.kmpbackbone.di

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
    initKoin()
}
