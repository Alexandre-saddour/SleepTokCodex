package com.example.domain.model

data class ProfileSummary(
    val user: User,
    val totalNights: Int,
    val totalWins: Int,
    val bestStreak: Int,
    val userRewards: List<UserReward>,
)
