package com.example.data.mapper

import com.example.data.local.entity.TalentEntity
import com.example.data.local.entity.UserTalentEntity
import com.example.domain.model.Talent
import com.example.domain.model.UserTalent

fun TalentEntity.toDomain(): Talent {
    return Talent(
        id = id,
        branch = branch,
        tier = tier,
        nameKey = nameKey,
        descriptionKey = descriptionKey,
        costPoints = costPoints,
        effect = effectJson.toTalentEffect(),
        isActive = isActive,
    )
}

fun Talent.toEntity(): TalentEntity {
    return TalentEntity(
        id = id,
        branch = branch,
        tier = tier,
        nameKey = nameKey,
        descriptionKey = descriptionKey,
        costPoints = costPoints,
        effectJson = effect.toJson(),
        isActive = isActive,
    )
}

fun UserTalentEntity.toDomain(): UserTalent {
    return UserTalent(
        userId = userId,
        talentId = talentId,
        unlockedAt = unlockedAt,
    )
}

fun UserTalent.toEntity(): UserTalentEntity {
    return UserTalentEntity(
        userId = userId,
        talentId = talentId,
        unlockedAt = unlockedAt,
    )
}
