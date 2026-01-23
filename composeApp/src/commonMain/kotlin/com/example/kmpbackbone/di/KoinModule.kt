package com.example.kmpbackbone.di

import com.example.data.repository.WelcomeMessageRepositoryImpl
import com.example.domain.repository.WelcomeMessageRepository
import com.example.domain.usecase.GetWelcomeMessageUseCase
import com.example.kmpbackbone.home.HomeViewModel
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * Initialize Koin DI for shared module.
 */
fun initKoin(appDeclaration: KoinAppDeclaration = {}) = startKoin {
    appDeclaration()
    modules(
        networkModule,
        dataModule,
        domainModule,
        presentationModule,
        platformModule()
    )
    // Initialize Napier for logging
    Napier.base(DebugAntilog())
}

expect fun platformModule(): Module

val networkModule = module {

}

val dataModule = module {
    singleOf(::WelcomeMessageRepositoryImpl) bind WelcomeMessageRepository::class
}

val domainModule = module {
    factoryOf(::GetWelcomeMessageUseCase)
}

val presentationModule = module {
    factoryOf(::HomeViewModel)
}
