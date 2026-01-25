package com.example.kmpbackbone.di

import com.example.data.local.DatabaseContext
import com.example.data.local.DatabaseFactory
import com.example.data.local.SleepTokDatabase
import com.example.data.local.dao.NightDao
import com.example.data.local.dao.RewardDao
import com.example.data.local.dao.ShieldDao
import com.example.data.local.dao.SleepPlanDao
import com.example.data.local.dao.TalentDao
import com.example.data.local.dao.UserDao
import com.example.data.local.dao.XpEventDao
import com.example.data.repository.NightRepositoryImpl
import com.example.data.repository.RewardRepositoryImpl
import com.example.data.repository.SleepPlanRepositoryImpl
import com.example.data.repository.StreakShieldRepositoryImpl
import com.example.data.repository.TalentRepositoryImpl
import com.example.data.repository.UserRepositoryImpl
import com.example.data.repository.XpEventRepositoryImpl
import com.example.data.local.RoomTransactionRunner
import com.example.data.seed.SeedDataInitializer
import com.example.data.seed.SeedDataProvider
import com.example.domain.repository.NightRepository
import com.example.domain.repository.RewardRepository
import com.example.domain.repository.SleepPlanRepository
import com.example.domain.repository.StreakShieldRepository
import com.example.domain.repository.TalentRepository
import com.example.domain.repository.TransactionRunner
import com.example.domain.repository.UserRepository
import com.example.domain.repository.XpEventRepository
import com.example.domain.scoring.DefaultNightResultCalculator
import com.example.domain.scoring.NightResultCalculator
import com.example.domain.usecase.ApplyNightResultUseCase
import com.example.domain.usecase.ClaimDailyChestUseCase
import com.example.domain.usecase.CompleteOnboardingUseCase
import com.example.domain.usecase.ComputeNightResultUseCase
import com.example.domain.usecase.GetActiveNightUseCase
import com.example.domain.usecase.GetActivePlanUseCase
import com.example.domain.usecase.GetBadgesAndCosmeticsUseCase
import com.example.domain.usecase.GetCalendarMonthUseCase
import com.example.domain.usecase.GetHomeSummaryUseCase
import com.example.domain.usecase.GetNightDetailUseCase
import com.example.domain.usecase.GetOnboardingStateUseCase
import com.example.domain.usecase.GetProfileSummaryUseCase
import com.example.domain.usecase.GetTalentTreeUseCase
import com.example.domain.usecase.GetWeeklyRecapUseCase
import com.example.domain.usecase.StartNightUseCase
import com.example.domain.usecase.StopNightUseCase
import com.example.domain.usecase.UnlockTalentUseCase
import com.example.domain.usecase.UpdatePlanUseCase
import com.example.kmpbackbone.resources.ResourceSeedDataProvider
import com.example.kmpbackbone.viewmodel.OnboardingViewModel
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
fun initKoin(
    databaseContext: DatabaseContext,
    appDeclaration: KoinAppDeclaration = {},
) = startKoin {
    appDeclaration()
    modules(
        networkModule,
        dataModule,
        domainModule,
        presentationModule,
        platformModule(databaseContext)
    )
    // Initialize Napier for logging
    Napier.base(DebugAntilog())
}

expect fun platformModule(databaseContext: DatabaseContext): Module

val networkModule = module {

}

val dataModule = module {
    single { DatabaseFactory(get()).create() }
    single<UserDao> { get<SleepTokDatabase>().userDao() }
    single<SleepPlanDao> { get<SleepTokDatabase>().sleepPlanDao() }
    single<NightDao> { get<SleepTokDatabase>().nightDao() }
    single<XpEventDao> { get<SleepTokDatabase>().xpEventDao() }
    single<TalentDao> { get<SleepTokDatabase>().talentDao() }
    single<RewardDao> { get<SleepTokDatabase>().rewardDao() }
    single<ShieldDao> { get<SleepTokDatabase>().shieldDao() }

    singleOf(::UserRepositoryImpl) bind UserRepository::class
    singleOf(::SleepPlanRepositoryImpl) bind SleepPlanRepository::class
    singleOf(::NightRepositoryImpl) bind NightRepository::class
    singleOf(::TalentRepositoryImpl) bind TalentRepository::class
    singleOf(::RewardRepositoryImpl) bind RewardRepository::class
    singleOf(::StreakShieldRepositoryImpl) bind StreakShieldRepository::class
    singleOf(::XpEventRepositoryImpl) bind XpEventRepository::class

    singleOf(::SeedDataInitializer)
    singleOf(::RoomTransactionRunner) bind TransactionRunner::class
}

val domainModule = module {
    single<NightResultCalculator> { DefaultNightResultCalculator() }

    factoryOf(::GetOnboardingStateUseCase)
    factoryOf(::CompleteOnboardingUseCase)
    factoryOf(::GetActivePlanUseCase)
    factoryOf(::UpdatePlanUseCase)
    factoryOf(::GetHomeSummaryUseCase)
    factoryOf(::GetActiveNightUseCase)
    factoryOf(::StartNightUseCase)
    factoryOf(::StopNightUseCase)
    factoryOf(::ComputeNightResultUseCase)
    factoryOf(::ApplyNightResultUseCase)
    factoryOf(::GetCalendarMonthUseCase)
    factoryOf(::GetWeeklyRecapUseCase)
    factoryOf(::GetNightDetailUseCase)
    factoryOf(::GetTalentTreeUseCase)
    factoryOf(::UnlockTalentUseCase)
    factoryOf(::GetProfileSummaryUseCase)
    factoryOf(::ClaimDailyChestUseCase)
    factoryOf(::GetBadgesAndCosmeticsUseCase)
}

val presentationModule = module {
    single<SeedDataProvider> { ResourceSeedDataProvider() }
    factoryOf(::OnboardingViewModel)
}
