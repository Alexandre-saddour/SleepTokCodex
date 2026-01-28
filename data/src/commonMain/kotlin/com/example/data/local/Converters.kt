package com.example.data.local

import androidx.room.TypeConverter
import com.example.domain.model.CoachStyle
import com.example.domain.model.NightStatus
import com.example.domain.model.PremiumStatus
import com.example.domain.model.RewardRarity
import com.example.domain.model.RewardSource
import com.example.domain.model.RewardType
import com.example.domain.model.ShieldSource
import com.example.domain.model.TalentBranch
import com.example.domain.model.TalentTier
import com.example.domain.model.XpEventType
import kotlin.time.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime

class Converters {
    @TypeConverter
    fun instantToEpochMillis(value: Instant?): Long? = value?.toEpochMilliseconds()

    @TypeConverter
    fun epochMillisToInstant(value: Long?): Instant? = value?.let { Instant.fromEpochMilliseconds(it) }

    @TypeConverter
    fun localDateToString(value: LocalDate?): String? = value?.toString()

    @TypeConverter
    fun stringToLocalDate(value: String?): LocalDate? = value?.let { LocalDate.parse(it) }

    @TypeConverter
    fun localDateTimeToString(value: LocalDateTime?): String? = value?.toString()

    @TypeConverter
    fun stringToLocalDateTime(value: String?): LocalDateTime? = value?.let { LocalDateTime.parse(it) }

    @TypeConverter
    fun localTimeToString(value: LocalTime?): String? = value?.toString()

    @TypeConverter
    fun stringToLocalTime(value: String?): LocalTime? = value?.let { LocalTime.parse(it) }

    @TypeConverter
    fun coachStyleToString(value: CoachStyle?): String? = value?.name

    @TypeConverter
    fun stringToCoachStyle(value: String?): CoachStyle? = value?.let { CoachStyle.valueOf(it) }

    @TypeConverter
    fun premiumStatusToString(value: PremiumStatus?): String? = value?.name

    @TypeConverter
    fun stringToPremiumStatus(value: String?): PremiumStatus? = value?.let { PremiumStatus.valueOf(it) }

    @TypeConverter
    fun nightStatusToString(value: NightStatus?): String? = value?.name

    @TypeConverter
    fun stringToNightStatus(value: String?): NightStatus? = value?.let { NightStatus.valueOf(it) }

    @TypeConverter
    fun rewardTypeToString(value: RewardType?): String? = value?.name

    @TypeConverter
    fun stringToRewardType(value: String?): RewardType? = value?.let { RewardType.valueOf(it) }

    @TypeConverter
    fun rewardRarityToString(value: RewardRarity?): String? = value?.name

    @TypeConverter
    fun stringToRewardRarity(value: String?): RewardRarity? = value?.let { RewardRarity.valueOf(it) }

    @TypeConverter
    fun rewardSourceToString(value: RewardSource?): String? = value?.name

    @TypeConverter
    fun stringToRewardSource(value: String?): RewardSource? = value?.let { RewardSource.valueOf(it) }

    @TypeConverter
    fun shieldSourceToString(value: ShieldSource?): String? = value?.name

    @TypeConverter
    fun stringToShieldSource(value: String?): ShieldSource? = value?.let { ShieldSource.valueOf(it) }

    @TypeConverter
    fun talentBranchToString(value: TalentBranch?): String? = value?.name

    @TypeConverter
    fun stringToTalentBranch(value: String?): TalentBranch? = value?.let { TalentBranch.valueOf(it) }

    @TypeConverter
    fun talentTierToString(value: TalentTier?): String? = value?.name

    @TypeConverter
    fun stringToTalentTier(value: String?): TalentTier? = value?.let { TalentTier.valueOf(it) }

    @TypeConverter
    fun xpEventTypeToString(value: XpEventType?): String? = value?.name

    @TypeConverter
    fun stringToXpEventType(value: String?): XpEventType? = value?.let { XpEventType.valueOf(it) }
}
