# Roadmap Post-MVP - Implementation Exhaustive

**Document de reference pour implementer V1.1, V1.2, V2, et V3**

Ce document contient TOUTES les specifications necessaires pour implementer les versions futures de l'application SleepTok. Il est concu pour etre autonome - une nouvelle instance de Claude avec un contexte vide peut l'utiliser pour implementer n'importe quelle feature sans avoir acces aux specs originales.

---

# CONTEXTE TECHNIQUE

## Architecture du Projet

```
SleepTokCodex/
├── composeApp/          # UI + ViewModels (Compose Multiplatform)
│   └── src/commonMain/kotlin/com/example/kmpbackbone/
│       ├── ui/          # Screens Compose
│       ├── viewmodel/   # ViewModels MVVM
│       └── navigation/  # Navigation state
├── domain/              # Logique metier pure
│   └── src/commonMain/kotlin/com/example/domain/
│       ├── model/       # Data classes immutables
│       ├── repository/  # Interfaces
│       └── usecase/     # Use cases (1 methode execute())
├── data/                # Implementation data
│   └── src/commonMain/kotlin/com/example/data/
│       ├── repository/  # Implementations
│       ├── local/       # Room DAOs, Entities
│       └── mapper/      # Entity <-> Domain
└── resources/           # Strings, JSON seed data
```

## Conventions Obligatoires

1. **Clean Architecture**: domain ne depend de rien, data implemente, composeApp consomme
2. **Use Cases**: Une seule methode publique `suspend fun execute(...): AppResult<T>`
3. **Pas de hardcoded strings**: Tout texte utilisateur dans resources
4. **KMP-first**: Privilegier les librairies multiplatform
5. **Room KMP**: Base de donnees locale
6. **Koin**: Injection de dependances

## Modeles Existants (Reference)

```kotlin
// User
data class User(
    val id: String,
    val createdAt: Instant,
    val timezone: TimeZone,
    val coachStyle: CoachStyle,  // CHILL, HYPE, STRICT
    val premiumStatus: PremiumStatus,  // NONE, TRIAL, ACTIVE, CANCELED
    val premiumUntil: Instant?,
    val level: Int,
    val xpTotal: Long,
    val talentPointsAvailable: Int,
    val streakCurrent: Int,
    val streakBest: Int,
    val lastNightId: String?,
    val baselineSleepDurationMinutes: Int
)

// SleepPlan
data class SleepPlan(
    val id: String,
    val userId: String,
    val planStartLocalTime: LocalTime,
    val planEndLocalTime: LocalTime,
    val activeDaysMask: Int,  // Bitmask 7 jours
    val toleranceStartMin: Int,  // 15
    val toleranceEndMin: Int,    // 20
    val isActive: Boolean
)

// Night
data class Night(
    val id: String,
    val userId: String,
    val planId: String,
    val startAt: Instant,
    val endAt: Instant?,
    val status: NightStatus,  // IN_PROGRESS, SUCCESS, PARTIAL, FAIL, VOID
    val actualDurationMin: Int?,
    val planDurationMin: Int,
    val score: Int?,
    val xpEarned: Long?,
    val streakBefore: Int,
    val streakAfter: Int?
)

// Reward
data class Reward(
    val id: String,
    val type: RewardType,  // BADGE, THEME, SOUND, COUPON, RAFFLE_ENTRY
    val rarity: RewardRarity,  // COMMON, RARE, EPIC
    val name: String,
    val assetRef: String?
)

// Talent
data class Talent(
    val id: String,  // D1, D2, D3, S1, S2, S3, T1, T2, T3, I1, I2, I3
    val branch: TalentBranch,  // DISCIPLINE, STREAK, STYLE, INSIGHT
    val tier: Int,  // 1, 2, 3
    val name: String,
    val description: String,
    val costPoints: Int,  // 1, 2, 3
    val effect: TalentEffect
)
```

---

# VERSION 1.1 - RETENTION + CONFORT

## V1.1.1 - Notifications Intelligentes

### Contexte
L'utilisateur doit recevoir des notifications pour:
- Rappel de coucher (X minutes avant plan_start)
- Rappel de reveil (optionnel, a plan_end)
- "Claim morning" (resultat disponible apres stop)

### Specifications Detaillees

**Types de notifications:**
1. `BEDTIME_REMINDER`: "Time to start your sleep quest!" - 15 min avant plan_start
2. `WAKEUP_REMINDER`: "Good morning! Time to wake up." - a plan_end
3. `RESULT_READY`: "Your night result is ready! Claim your XP." - apres stop

**Parametres utilisateur (a stocker):**
- `notificationsEnabled: Boolean` (global on/off)
- `bedtimeReminderEnabled: Boolean`
- `bedtimeReminderMinutesBefore: Int` (default 15)
- `wakeupReminderEnabled: Boolean`
- `resultReminderEnabled: Boolean`

### Implementation

#### Etape 1: Modele Domain

**Fichier: `domain/src/commonMain/kotlin/com/example/domain/model/NotificationSettings.kt`**
```kotlin
data class NotificationSettings(
    val enabled: Boolean = false,
    val bedtimeReminderEnabled: Boolean = true,
    val bedtimeReminderMinutesBefore: Int = 15,
    val wakeupReminderEnabled: Boolean = true,
    val resultReminderEnabled: Boolean = true
)
```

**Fichier: `domain/src/commonMain/kotlin/com/example/domain/model/ScheduledNotification.kt`**
```kotlin
data class ScheduledNotification(
    val id: String,
    val type: NotificationType,
    val scheduledAt: Instant,
    val title: String,
    val body: String,
    val delivered: Boolean = false
)

enum class NotificationType {
    BEDTIME_REMINDER,
    WAKEUP_REMINDER,
    RESULT_READY
}
```

#### Etape 2: Repository Interface

**Fichier: `domain/src/commonMain/kotlin/com/example/domain/repository/NotificationRepository.kt`**
```kotlin
interface NotificationRepository {
    suspend fun getSettings(): AppResult<NotificationSettings>
    suspend fun updateSettings(settings: NotificationSettings): AppResult<Unit>
    suspend fun scheduleNotification(notification: ScheduledNotification): AppResult<Unit>
    suspend fun cancelNotification(id: String): AppResult<Unit>
    suspend fun cancelAllNotifications(): AppResult<Unit>
}
```

#### Etape 3: Service Platform-Specific

**Fichier: `data/src/commonMain/kotlin/com/example/data/service/NotificationService.kt`**
```kotlin
expect class NotificationService {
    fun requestPermission(): Boolean
    fun scheduleNotification(
        id: String,
        title: String,
        body: String,
        triggerAt: Instant
    )
    fun cancelNotification(id: String)
    fun cancelAllNotifications()
}
```

**Fichier: `data/src/androidMain/kotlin/com/example/data/service/NotificationService.android.kt`**
```kotlin
actual class NotificationService(private val context: Context) {

    actual fun requestPermission(): Boolean {
        // Android 13+ requires POST_NOTIFICATIONS permission
        // Use NotificationManagerCompat
    }

    actual fun scheduleNotification(
        id: String,
        title: String,
        body: String,
        triggerAt: Instant
    ) {
        // Use AlarmManager + BroadcastReceiver
        // Or WorkManager for reliability
    }

    actual fun cancelNotification(id: String) {
        // Cancel via AlarmManager or NotificationManager
    }

    actual fun cancelAllNotifications() {
        // Cancel all pending alarms
    }
}
```

**Fichier: `data/src/iosMain/kotlin/com/example/data/service/NotificationService.ios.kt`**
```kotlin
actual class NotificationService {

    actual fun requestPermission(): Boolean {
        // UNUserNotificationCenter.requestAuthorization
    }

    actual fun scheduleNotification(
        id: String,
        title: String,
        body: String,
        triggerAt: Instant
    ) {
        // UNMutableNotificationContent + UNTimeIntervalNotificationTrigger
    }

    actual fun cancelNotification(id: String) {
        // UNUserNotificationCenter.removePendingNotificationRequests
    }

    actual fun cancelAllNotifications() {
        // UNUserNotificationCenter.removeAllPendingNotificationRequests
    }
}
```

#### Etape 4: Use Cases

**Fichier: `domain/src/commonMain/kotlin/com/example/domain/usecase/ScheduleBedtimeReminderUseCase.kt`**
```kotlin
class ScheduleBedtimeReminderUseCase(
    private val notificationRepository: NotificationRepository,
    private val sleepPlanRepository: SleepPlanRepository,
    private val clock: Clock
) {
    suspend fun execute(): AppResult<Unit> {
        return try {
            val settings = notificationRepository.getSettings().getOrThrow()
            if (!settings.enabled || !settings.bedtimeReminderEnabled) {
                return AppResult.Success(Unit)
            }

            val plan = sleepPlanRepository.getActivePlan().getOrThrow()
            val today = clock.now().toLocalDateTime(plan.timezone).date

            // Calculate next bedtime
            val nextBedtime = calculateNextBedtime(plan, today)
            val reminderTime = nextBedtime.minus(
                settings.bedtimeReminderMinutesBefore.minutes
            )

            val notification = ScheduledNotification(
                id = "bedtime_${today}",
                type = NotificationType.BEDTIME_REMINDER,
                scheduledAt = reminderTime,
                title = "Time for sleep",  // From resources
                body = "Start your sleep quest now!"
            )

            notificationRepository.scheduleNotification(notification).getOrThrow()
            AppResult.Success(Unit)
        } catch (e: DomainException) {
            AppResult.Error(e.error)
        }
    }
}
```

#### Etape 5: UI Settings

**Modifier: `composeApp/src/commonMain/kotlin/com/example/kmpbackbone/ui/settings/SettingsScreen.kt`**

```kotlin
// Remplacer le placeholder par des vrais toggles:

@Composable
fun NotificationSettingsSection(
    settings: NotificationSettings,
    onToggleEnabled: (Boolean) -> Unit,
    onToggleBedtime: (Boolean) -> Unit,
    onToggleWakeup: (Boolean) -> Unit,
    onToggleResult: (Boolean) -> Unit
) {
    Column {
        Text(
            text = stringResource(Res.string.settings_notifications),
            style = MaterialTheme.typography.titleMedium
        )

        SwitchRow(
            label = stringResource(Res.string.settings_notifications_enabled),
            checked = settings.enabled,
            onCheckedChange = onToggleEnabled
        )

        if (settings.enabled) {
            SwitchRow(
                label = stringResource(Res.string.settings_bedtime_reminder),
                checked = settings.bedtimeReminderEnabled,
                onCheckedChange = onToggleBedtime
            )
            SwitchRow(
                label = stringResource(Res.string.settings_wakeup_reminder),
                checked = settings.wakeupReminderEnabled,
                onCheckedChange = onToggleWakeup
            )
            SwitchRow(
                label = stringResource(Res.string.settings_result_reminder),
                checked = settings.resultReminderEnabled,
                onCheckedChange = onToggleResult
            )
        }
    }
}
```

#### Etape 6: Strings

```xml
<string name="settings_notifications_enabled">Enable notifications</string>
<string name="settings_bedtime_reminder">Bedtime reminder</string>
<string name="settings_wakeup_reminder">Wake-up reminder</string>
<string name="settings_result_reminder">Result ready reminder</string>
<string name="notification_bedtime_title">Time for sleep</string>
<string name="notification_bedtime_body">Start your sleep quest now!</string>
<string name="notification_wakeup_title">Good morning!</string>
<string name="notification_wakeup_body">Time to wake up and claim your rewards.</string>
<string name="notification_result_title">Night result ready</string>
<string name="notification_result_body">See how you did and claim your XP!</string>
```

#### Etape 7: Integration

- Appeler `ScheduleBedtimeReminderUseCase` apres:
  - Onboarding complete
  - Plan modifie
  - Chaque jour a minuit (WorkManager/Background task)
- Appeler `ScheduleResultReminderUseCase` apres StopNight

### Tests V1.1.1
- [ ] Permission demandee au premier enable
- [ ] Notification bedtime recue au bon moment
- [ ] Notification result recue apres stop
- [ ] Annulation quand settings disabled
- [ ] Fonctionne sur Android et iOS

---

## V1.1.2 - Share Card Active

### Contexte
Permettre a l'utilisateur de partager son resultat de nuit sous forme d'image pour Instagram/TikTok stories.

### Specifications

**Contenu de la Share Card:**
- Verdict (SUCCESS/PARTIAL/FAIL)
- Score (0-100)
- Streak actuel
- XP gagnee
- Message coach (court)
- Branding SleepTok

**Format:**
- Image PNG/JPEG
- Ratio 9:16 (story) ou 1:1 (post)
- Fond selon theme actif

### Implementation

#### Etape 1: Share Service

**Fichier: `data/src/commonMain/kotlin/com/example/data/service/ShareService.kt`**
```kotlin
expect class ShareService {
    suspend fun shareImage(
        imageBytes: ByteArray,
        mimeType: String,
        title: String
    ): Boolean
}
```

**Android implementation:**
```kotlin
actual class ShareService(private val context: Context) {
    actual suspend fun shareImage(
        imageBytes: ByteArray,
        mimeType: String,
        title: String
    ): Boolean {
        // Save to cache file
        // Create share intent with FileProvider
        // context.startActivity(Intent.createChooser(...))
    }
}
```

**iOS implementation:**
```kotlin
actual class ShareService {
    actual suspend fun shareImage(
        imageBytes: ByteArray,
        mimeType: String,
        title: String
    ): Boolean {
        // UIActivityViewController
    }
}
```

#### Etape 2: Card Generator

**Fichier: `composeApp/src/commonMain/kotlin/com/example/kmpbackbone/ui/share/ShareCardGenerator.kt`**
```kotlin
@Composable
fun ShareCardContent(
    result: NightResult,
    theme: AppTheme
) {
    Box(
        modifier = Modifier
            .size(width = 1080.dp, height = 1920.dp)  // 9:16
            .background(theme.backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            // Logo
            Image(
                painter = painterResource(Res.drawable.logo),
                contentDescription = null,
                modifier = Modifier.size(120.dp)
            )

            // Verdict
            Text(
                text = result.status.name,
                style = MaterialTheme.typography.displayLarge,
                color = when (result.status) {
                    NightStatus.SUCCESS -> Color.Green
                    NightStatus.PARTIAL -> Color.Yellow
                    NightStatus.FAIL -> Color.Red
                    else -> Color.White
                }
            )

            // Score Ring
            ScoreRing(
                score = result.score,
                modifier = Modifier.size(200.dp)
            )

            // Stats Row
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                StatItem(
                    label = "XP",
                    value = "+${result.xpEarned}"
                )
                StatItem(
                    label = "Streak",
                    value = "${result.streakAfter}"
                )
            }

            // Coach Message
            Text(
                text = result.coachMessage,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )

            // Branding
            Text(
                text = "sleeptok.app",
                style = MaterialTheme.typography.labelMedium,
                color = Color.White.copy(alpha = 0.7f)
            )
        }
    }
}
```

#### Etape 3: Capture to Bitmap

```kotlin
// Utiliser une librairie comme:
// - compose-screenshot (pour capture composable)
// - ou draw to Canvas manuellement

suspend fun captureShareCard(
    result: NightResult,
    theme: AppTheme
): ByteArray {
    // Render ShareCardContent to bitmap
    // Encode to PNG bytes
}
```

#### Etape 4: UI Integration

**Modifier NightResultScreen:**
```kotlin
// Activer le bouton Share (etait disabled)
Button(
    onClick = {
        viewModel.onShareClicked()
    },
    enabled = true  // Maintenant enabled
) {
    Icon(Icons.Default.Share, null)
    Text(stringResource(Res.string.night_result_share))
}
```

**Dans ViewModel:**
```kotlin
fun onShareClicked() {
    viewModelScope.launch {
        _state.update { it.copy(isGeneratingShare = true) }

        val imageBytes = generateShareCardUseCase.execute(
            result = state.value.result,
            theme = state.value.currentTheme
        )

        shareService.shareImage(
            imageBytes = imageBytes,
            mimeType = "image/png",
            title = "My SleepTok Result"
        )

        _state.update { it.copy(isGeneratingShare = false) }
    }
}
```

### Tests V1.1.2
- [ ] Bouton Share enabled
- [ ] Image generee correctement
- [ ] Share intent s'ouvre (Android)
- [ ] Share sheet s'ouvre (iOS)
- [ ] Image lisible sur Instagram story

---

## V1.1.3 - Daily Chest Enrichi

### Contexte
Ameliorer le Daily Chest avec:
- Drops aleatoires basees sur rarete
- Animation d'ouverture

### Specifications

**Probabilites de drop:**
- COMMON: 70%
- RARE: 25%
- EPIC: 5%

**Bonus Talent T3 (Rare Cosmetics):**
- +20% luck = probabilites modifiees:
  - COMMON: 50%
  - RARE: 40%
  - EPIC: 10%

### Implementation

#### Etape 1: Modifier ClaimDailyChestUseCase

```kotlin
class ClaimDailyChestUseCase(
    private val rewardRepository: RewardRepository,
    private val userRepository: UserRepository,
    private val talentRepository: TalentRepository,
    private val random: Random = Random.Default
) {
    suspend fun execute(): AppResult<Reward> {
        return try {
            val user = userRepository.getActiveUser().getOrThrow()

            // Check if already claimed today
            val lastClaim = rewardRepository.getLastChestClaimDate(user.id)
            val today = Clock.System.now().toLocalDateTime(user.timezone).date
            if (lastClaim == today) {
                return AppResult.Error(DomainError.AlreadyClaimed)
            }

            // Get all rewards
            val rewards = rewardRepository.getAllRewards().getOrThrow()

            // Check for T3 talent (Rare Cosmetics luck bonus)
            val hasRareLuck = talentRepository
                .getUserTalents(user.id)
                .getOrThrow()
                .any { it.id == "T3" }

            // Calculate probabilities
            val probabilities = if (hasRareLuck) {
                mapOf(
                    RewardRarity.COMMON to 0.50,
                    RewardRarity.RARE to 0.40,
                    RewardRarity.EPIC to 0.10
                )
            } else {
                mapOf(
                    RewardRarity.COMMON to 0.70,
                    RewardRarity.RARE to 0.25,
                    RewardRarity.EPIC to 0.05
                )
            }

            // Roll for rarity
            val roll = random.nextDouble()
            val targetRarity = when {
                roll < probabilities[RewardRarity.EPIC]!! -> RewardRarity.EPIC
                roll < probabilities[RewardRarity.EPIC]!! + probabilities[RewardRarity.RARE]!! -> RewardRarity.RARE
                else -> RewardRarity.COMMON
            }

            // Filter by rarity, exclude already owned
            val ownedIds = rewardRepository
                .getUserRewards(user.id)
                .getOrThrow()
                .map { it.rewardId }
                .toSet()

            val eligibleRewards = rewards
                .filter { it.rarity == targetRarity && it.id !in ownedIds }

            // Fallback to any unowned if none at target rarity
            val rewardToGrant = eligibleRewards.randomOrNull()
                ?: rewards.filter { it.id !in ownedIds }.randomOrNull()
                ?: rewards.random()  // Cycle if all owned

            // Grant reward
            rewardRepository.grantReward(
                userId = user.id,
                rewardId = rewardToGrant.id,
                source = RewardSource.DAILY_CHEST
            ).getOrThrow()

            AppResult.Success(rewardToGrant)
        } catch (e: DomainException) {
            AppResult.Error(e.error)
        }
    }
}
```

#### Etape 2: Animation d'Ouverture

**Fichier: `composeApp/src/commonMain/kotlin/com/example/kmpbackbone/ui/dailychest/ChestOpenAnimation.kt`**

```kotlin
@Composable
fun ChestOpenAnimation(
    isOpen: Boolean,
    reward: Reward?,
    onAnimationComplete: () -> Unit
) {
    var animationPhase by remember { mutableStateOf(0) }

    LaunchedEffect(isOpen) {
        if (isOpen) {
            // Phase 1: Chest shaking
            delay(500)
            animationPhase = 1

            // Phase 2: Chest opening
            delay(500)
            animationPhase = 2

            // Phase 3: Reward reveal
            delay(500)
            animationPhase = 3

            // Phase 4: Complete
            delay(1000)
            onAnimationComplete()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (animationPhase) {
            0 -> {
                // Closed chest
                Image(
                    painter = painterResource(Res.drawable.chest_closed),
                    contentDescription = null,
                    modifier = Modifier.size(200.dp)
                )
            }
            1 -> {
                // Shaking chest
                val shakeOffset by animateFloatAsState(
                    targetValue = if (animationPhase == 1) 10f else 0f,
                    animationSpec = repeatable(
                        iterations = 5,
                        animation = tween(100),
                        repeatMode = RepeatMode.Reverse
                    )
                )
                Image(
                    painter = painterResource(Res.drawable.chest_closed),
                    contentDescription = null,
                    modifier = Modifier
                        .size(200.dp)
                        .offset(x = shakeOffset.dp)
                )
            }
            2 -> {
                // Opening chest with glow
                Image(
                    painter = painterResource(Res.drawable.chest_open),
                    contentDescription = null,
                    modifier = Modifier.size(200.dp)
                )
                // Light rays effect
                Box(
                    modifier = Modifier
                        .size(300.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color.Yellow.copy(alpha = 0.5f),
                                    Color.Transparent
                                )
                            )
                        )
                )
            }
            3 -> {
                // Reward reveal
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    RewardCard(
                        reward = reward!!,
                        modifier = Modifier
                            .scale(animateFloatAsState(
                                targetValue = 1f,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy
                                )
                            ).value)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = when (reward.rarity) {
                            RewardRarity.EPIC -> "EPIC DROP!"
                            RewardRarity.RARE -> "Rare find!"
                            RewardRarity.COMMON -> "Nice!"
                        },
                        style = MaterialTheme.typography.headlineMedium,
                        color = when (reward.rarity) {
                            RewardRarity.EPIC -> Color(0xFFFFD700)
                            RewardRarity.RARE -> Color(0xFF9B59B6)
                            RewardRarity.COMMON -> Color.White
                        }
                    )
                }
            }
        }
    }
}
```

### Tests V1.1.3
- [ ] Drop aleatoire fonctionne
- [ ] Probabilites respectees (tester sur 100 drops)
- [ ] Bonus T3 modifie les probabilites
- [ ] Animation complete avant reveal
- [ ] Reward correctement persiste

---

## V1.1.4 - Challenges Simples (1 Slot)

### Contexte
Introduire un systeme de defis avec 1 slot actif pour augmenter l'engagement.

### Specifications

**Types de challenges:**
1. `STREAK_3`: Atteindre un streak de 3
2. `STREAK_7`: Atteindre un streak de 7
3. `PERFECT_NIGHT`: Obtenir un score >= 90
4. `ON_TIME_START`: 3 nuits avec start dans la tolerance
5. `EARLY_BIRD`: 3 reveils dans +/- 10 min
6. `CONSISTENCY`: 5 SUCCESS sur 7 jours
7. `COMEBACK`: SUCCESS apres un FAIL

**Recompenses:**
- XP bonus: +50 a +150 selon difficulte
- Badge specifique (optionnel)

**Rotation:**
- Nouveau challenge propose chaque jour si slot vide
- L'utilisateur peut accepter ou ignorer
- Expiration apres 7 jours si non complete

### Implementation

#### Etape 1: Modeles Domain

**Fichier: `domain/src/commonMain/kotlin/com/example/domain/model/Challenge.kt`**
```kotlin
data class Challenge(
    val id: String,
    val type: ChallengeType,
    val title: String,
    val description: String,
    val targetValue: Int,
    val xpReward: Long,
    val badgeId: String?,  // Optional badge reward
    val expiresInDays: Int = 7
)

enum class ChallengeType {
    STREAK_3,
    STREAK_7,
    PERFECT_NIGHT,
    ON_TIME_START,
    EARLY_BIRD,
    CONSISTENCY,
    COMEBACK
}

data class UserChallenge(
    val id: String,
    val challengeId: String,
    val userId: String,
    val acceptedAt: Instant,
    val expiresAt: Instant,
    val currentProgress: Int,
    val targetProgress: Int,
    val status: ChallengeStatus
)

enum class ChallengeStatus {
    ACTIVE,
    COMPLETED,
    EXPIRED,
    CLAIMED
}
```

#### Etape 2: Entities Room

**Fichier: `data/src/commonMain/kotlin/com/example/data/local/entity/ChallengeEntity.kt`**
```kotlin
@Entity(tableName = "challenges")
data class ChallengeEntity(
    @PrimaryKey val id: String,
    val type: String,
    val title: String,
    val description: String,
    val targetValue: Int,
    val xpReward: Long,
    val badgeId: String?,
    val expiresInDays: Int
)

@Entity(
    tableName = "user_challenges",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ChallengeEntity::class,
            parentColumns = ["id"],
            childColumns = ["challengeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId"), Index("challengeId")]
)
data class UserChallengeEntity(
    @PrimaryKey val id: String,
    val challengeId: String,
    val userId: String,
    val acceptedAt: Long,  // Epoch millis
    val expiresAt: Long,
    val currentProgress: Int,
    val targetProgress: Int,
    val status: String
)
```

#### Etape 3: DAO

**Fichier: `data/src/commonMain/kotlin/com/example/data/local/dao/ChallengeDao.kt`**
```kotlin
@Dao
interface ChallengeDao {
    @Query("SELECT * FROM challenges")
    suspend fun getAllChallenges(): List<ChallengeEntity>

    @Query("SELECT * FROM challenges WHERE id = :id")
    suspend fun getChallengeById(id: String): ChallengeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChallenge(challenge: ChallengeEntity)

    @Query("""
        SELECT * FROM user_challenges
        WHERE userId = :userId AND status = 'ACTIVE'
        LIMIT 1
    """)
    suspend fun getActiveChallenge(userId: String): UserChallengeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserChallenge(userChallenge: UserChallengeEntity)

    @Update
    suspend fun updateUserChallenge(userChallenge: UserChallengeEntity)
}
```

#### Etape 4: Repository

**Fichier: `domain/src/commonMain/kotlin/com/example/domain/repository/ChallengeRepository.kt`**
```kotlin
interface ChallengeRepository {
    suspend fun getAllChallenges(): AppResult<List<Challenge>>
    suspend fun getActiveChallenge(userId: String): AppResult<UserChallenge?>
    suspend fun acceptChallenge(userId: String, challengeId: String): AppResult<UserChallenge>
    suspend fun updateProgress(userChallengeId: String, progress: Int): AppResult<Unit>
    suspend fun completeChallenge(userChallengeId: String): AppResult<Unit>
    suspend fun claimChallenge(userChallengeId: String): AppResult<Long>  // Returns XP
}
```

#### Etape 5: Use Cases

**Fichier: `domain/src/commonMain/kotlin/com/example/domain/usecase/GetActiveChallengeUseCase.kt`**
```kotlin
class GetActiveChallengeUseCase(
    private val challengeRepository: ChallengeRepository,
    private val userRepository: UserRepository
) {
    suspend fun execute(): AppResult<UserChallenge?> {
        return try {
            val user = userRepository.getActiveUser().getOrThrow()
            challengeRepository.getActiveChallenge(user.id)
        } catch (e: DomainException) {
            AppResult.Error(e.error)
        }
    }
}
```

**Fichier: `domain/src/commonMain/kotlin/com/example/domain/usecase/AcceptChallengeUseCase.kt`**
```kotlin
class AcceptChallengeUseCase(
    private val challengeRepository: ChallengeRepository,
    private val userRepository: UserRepository,
    private val clock: Clock
) {
    suspend fun execute(challengeId: String): AppResult<UserChallenge> {
        return try {
            val user = userRepository.getActiveUser().getOrThrow()

            // Check if already has active challenge
            val existing = challengeRepository.getActiveChallenge(user.id).getOrThrow()
            if (existing != null) {
                return AppResult.Error(DomainError.ChallengeSlotFull)
            }

            challengeRepository.acceptChallenge(user.id, challengeId)
        } catch (e: DomainException) {
            AppResult.Error(e.error)
        }
    }
}
```

**Fichier: `domain/src/commonMain/kotlin/com/example/domain/usecase/UpdateChallengeProgressUseCase.kt`**
```kotlin
class UpdateChallengeProgressUseCase(
    private val challengeRepository: ChallengeRepository,
    private val userRepository: UserRepository
) {
    suspend fun execute(nightResult: NightResult): AppResult<Unit> {
        return try {
            val user = userRepository.getActiveUser().getOrThrow()
            val activeChallenge = challengeRepository
                .getActiveChallenge(user.id)
                .getOrThrow() ?: return AppResult.Success(Unit)

            val challenge = challengeRepository
                .getAllChallenges()
                .getOrThrow()
                .first { it.id == activeChallenge.challengeId }

            val newProgress = when (challenge.type) {
                ChallengeType.STREAK_3 -> {
                    if (nightResult.streakAfter >= 3) challenge.targetValue else nightResult.streakAfter
                }
                ChallengeType.STREAK_7 -> {
                    if (nightResult.streakAfter >= 7) challenge.targetValue else nightResult.streakAfter
                }
                ChallengeType.PERFECT_NIGHT -> {
                    if (nightResult.score >= 90 && nightResult.status == NightStatus.SUCCESS) {
                        activeChallenge.currentProgress + 1
                    } else activeChallenge.currentProgress
                }
                ChallengeType.ON_TIME_START -> {
                    if (nightResult.startInTolerance) {
                        activeChallenge.currentProgress + 1
                    } else activeChallenge.currentProgress
                }
                ChallengeType.EARLY_BIRD -> {
                    if (nightResult.endDeltaMinutes <= 10) {
                        activeChallenge.currentProgress + 1
                    } else activeChallenge.currentProgress
                }
                ChallengeType.CONSISTENCY -> {
                    if (nightResult.status == NightStatus.SUCCESS) {
                        activeChallenge.currentProgress + 1
                    } else activeChallenge.currentProgress
                }
                ChallengeType.COMEBACK -> {
                    if (nightResult.status == NightStatus.SUCCESS &&
                        nightResult.streakBefore == 0) {
                        1
                    } else activeChallenge.currentProgress
                }
            }

            challengeRepository.updateProgress(activeChallenge.id, newProgress)

            // Check completion
            if (newProgress >= activeChallenge.targetProgress) {
                challengeRepository.completeChallenge(activeChallenge.id)
            }

            AppResult.Success(Unit)
        } catch (e: DomainException) {
            AppResult.Error(e.error)
        }
    }
}
```

#### Etape 6: Seed Data

**Fichier: `composeApp/src/commonMain/composeResources/files/seed_challenges.json`**
```json
[
  {
    "id": "challenge_streak_3",
    "type": "STREAK_3",
    "title": "Streak Starter",
    "description": "Reach a streak of 3",
    "targetValue": 3,
    "xpReward": 50,
    "badgeId": null,
    "expiresInDays": 7
  },
  {
    "id": "challenge_streak_7",
    "type": "STREAK_7",
    "title": "Week Warrior",
    "description": "Reach a streak of 7",
    "targetValue": 7,
    "xpReward": 100,
    "badgeId": null,
    "expiresInDays": 14
  },
  {
    "id": "challenge_perfect",
    "type": "PERFECT_NIGHT",
    "title": "Perfectionist",
    "description": "Get a score of 90+ on a SUCCESS night",
    "targetValue": 1,
    "xpReward": 75,
    "badgeId": null,
    "expiresInDays": 7
  },
  {
    "id": "challenge_on_time",
    "type": "ON_TIME_START",
    "title": "Punctual",
    "description": "Start 3 nights within the tolerance window",
    "targetValue": 3,
    "xpReward": 60,
    "badgeId": null,
    "expiresInDays": 7
  },
  {
    "id": "challenge_early_bird",
    "type": "EARLY_BIRD",
    "title": "Early Riser",
    "description": "Wake up within 10 minutes of plan 3 times",
    "targetValue": 3,
    "xpReward": 60,
    "badgeId": null,
    "expiresInDays": 7
  },
  {
    "id": "challenge_consistency",
    "type": "CONSISTENCY",
    "title": "Consistent",
    "description": "Get 5 SUCCESS nights in 7 days",
    "targetValue": 5,
    "xpReward": 100,
    "badgeId": null,
    "expiresInDays": 7
  },
  {
    "id": "challenge_comeback",
    "type": "COMEBACK",
    "title": "Comeback Kid",
    "description": "Get a SUCCESS right after a FAIL",
    "targetValue": 1,
    "xpReward": 75,
    "badgeId": "badge_comeback",
    "expiresInDays": 7
  }
]
```

#### Etape 7: UI - Challenge Card sur Home

**Fichier: `composeApp/src/commonMain/kotlin/com/example/kmpbackbone/ui/home/ChallengeCard.kt`**
```kotlin
@Composable
fun ChallengeCard(
    challenge: UserChallenge?,
    challengeDetails: Challenge?,
    onAcceptNew: () -> Unit,
    onClaim: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        when {
            challenge == null -> {
                // No active challenge - offer new one
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(Res.string.challenge_available),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = onAcceptNew) {
                        Text(stringResource(Res.string.challenge_view))
                    }
                }
            }
            challenge.status == ChallengeStatus.COMPLETED -> {
                // Completed - claim rewards
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(Res.string.challenge_completed),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(text = challengeDetails?.title ?: "")
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = onClaim) {
                        Text(stringResource(Res.string.challenge_claim, challengeDetails?.xpReward ?: 0))
                    }
                }
            }
            else -> {
                // Active challenge - show progress
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = challengeDetails?.title ?: "",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = challengeDetails?.description ?: "",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = challenge.currentProgress.toFloat() / challenge.targetProgress,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "${challenge.currentProgress}/${challenge.targetProgress}",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}
```

### Tests V1.1.4
- [ ] Seed challenges charges au premier lancement
- [ ] Accepter un challenge le rend actif
- [ ] Progress mis a jour apres chaque nuit
- [ ] Challenge complete quand target atteint
- [ ] XP accordee au claim
- [ ] Nouveau challenge propose apres claim
- [ ] Challenge expire apres 7 jours

---

## V1.1.5 - Streak Shield Refresh Hebdomadaire

### Contexte
Le talent S2 "Streak Shield" donne +1 charge/semaine. Il faut implementer le refresh automatique.

### Implementation

#### Etape 1: Modifier StreakShieldEntity

```kotlin
@Entity(tableName = "streak_shields")
data class StreakShieldEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val chargesAvailable: Int,
    val maxCharges: Int,  // Defini par talent
    val lastRefreshAt: Long,  // Epoch millis
    val nextRefreshAt: Long,
    val source: String  // TALENT, PREMIUM, STORE, AD
)
```

#### Etape 2: Use Case de Refresh

**Fichier: `domain/src/commonMain/kotlin/com/example/domain/usecase/RefreshStreakShieldsUseCase.kt`**
```kotlin
class RefreshStreakShieldsUseCase(
    private val shieldRepository: StreakShieldRepository,
    private val userRepository: UserRepository,
    private val talentRepository: TalentRepository,
    private val clock: Clock
) {
    suspend fun execute(): AppResult<Unit> {
        return try {
            val user = userRepository.getActiveUser().getOrThrow()
            val now = clock.now()

            // Check if S2 talent is unlocked
            val hasS2 = talentRepository
                .getUserTalents(user.id)
                .getOrThrow()
                .any { it.id == "S2" }

            if (!hasS2) return AppResult.Success(Unit)

            // Get current shield
            val shield = shieldRepository.getShield(user.id).getOrThrow()

            if (shield == null) {
                // Create new shield
                shieldRepository.createShield(
                    userId = user.id,
                    maxCharges = 1,
                    source = "TALENT"
                )
            } else if (now >= shield.nextRefreshAt) {
                // Refresh charges
                shieldRepository.refreshCharges(
                    shieldId = shield.id,
                    newCharges = shield.maxCharges,
                    nextRefresh = now.plus(7.days)
                )
            }

            AppResult.Success(Unit)
        } catch (e: DomainException) {
            AppResult.Error(e.error)
        }
    }
}
```

#### Etape 3: Appeler au Demarrage

Dans `HomeViewModel.init()`:
```kotlin
init {
    viewModelScope.launch {
        refreshStreakShieldsUseCase.execute()
        loadHomeData()
    }
}
```

### Tests V1.1.5
- [ ] Shield cree quand S2 debloque
- [ ] Charges refreshed apres 7 jours
- [ ] Shield visible dans Home/NightResult
- [ ] Charge consommee correctement

---

## V1.1.6 - Sons Pack #1 (Playback)

### Contexte
Permettre le playback reel des sons debloques via talent T2.

### Specifications

**Sons disponibles (de seed_rewards.json):**
- Sleep:
  - `sound_rain_lofi` - T2 + niveau 3
  - `sound_ocean_hush` - T2 + 5 SUCCESS
  - `sound_space_drone` - T2 + badge Moonrunner
- Wake:
  - `sound_soft_chimes` - T2 + niveau 3
  - `sound_hype_alarm_cute` - T2 + streak 3
  - `sound_retro_arcade_ping` - T2 + badge Level 5

### Implementation

#### Etape 1: Audio Service

**Fichier: `data/src/commonMain/kotlin/com/example/data/service/AudioService.kt`**
```kotlin
expect class AudioService {
    fun play(assetPath: String, loop: Boolean = false)
    fun stop()
    fun setVolume(volume: Float)  // 0.0 to 1.0
    fun isPlaying(): Boolean
}
```

**Android:**
```kotlin
actual class AudioService(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null

    actual fun play(assetPath: String, loop: Boolean) {
        stop()
        mediaPlayer = MediaPlayer().apply {
            val afd = context.assets.openFd(assetPath)
            setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            isLooping = loop
            prepare()
            start()
        }
    }

    actual fun stop() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    actual fun setVolume(volume: Float) {
        mediaPlayer?.setVolume(volume, volume)
    }

    actual fun isPlaying(): Boolean = mediaPlayer?.isPlaying == true
}
```

**iOS:**
```kotlin
actual class AudioService {
    private var audioPlayer: AVAudioPlayer? = null

    actual fun play(assetPath: String, loop: Boolean) {
        // Load from bundle, play with AVAudioPlayer
    }

    actual fun stop() {
        audioPlayer?.stop()
        audioPlayer = null
    }

    actual fun setVolume(volume: Float) {
        audioPlayer?.volume = volume
    }

    actual fun isPlaying(): Boolean = audioPlayer?.isPlaying == true
}
```

#### Etape 2: Ajouter les Fichiers Audio

Placer les fichiers audio dans:
- Android: `composeApp/src/androidMain/assets/sounds/`
- iOS: Ajouter au bundle Xcode

Fichiers necessaires:
- `rain_lofi.mp3`
- `ocean_hush.mp3`
- `space_drone.mp3`
- `soft_chimes.mp3`
- `hype_alarm_cute.mp3`
- `retro_arcade_ping.mp3`

#### Etape 3: Sound Settings

**Fichier: `domain/src/commonMain/kotlin/com/example/domain/model/SoundSettings.kt`**
```kotlin
data class SoundSettings(
    val selectedSleepSound: String? = null,
    val selectedWakeSound: String? = null,
    val volume: Float = 0.7f
)
```

#### Etape 4: UI Sound Picker

**Fichier: `composeApp/src/commonMain/kotlin/com/example/kmpbackbone/ui/settings/SoundPickerScreen.kt`**
```kotlin
@Composable
fun SoundPickerScreen(
    sounds: List<Reward>,
    unlockedSoundIds: Set<String>,
    selectedSoundId: String?,
    onSelect: (String) -> Unit,
    onPreview: (String) -> Unit
) {
    LazyColumn {
        items(sounds) { sound ->
            val isUnlocked = sound.id in unlockedSoundIds

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = isUnlocked) { onSelect(sound.id) }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = sound.id == selectedSoundId,
                    onClick = { if (isUnlocked) onSelect(sound.id) },
                    enabled = isUnlocked
                )

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = sound.name,
                        color = if (isUnlocked) Color.Unspecified
                               else Color.Gray
                    )
                    if (!isUnlocked) {
                        Text(
                            text = stringResource(Res.string.sound_locked),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }

                if (isUnlocked) {
                    IconButton(onClick = { onPreview(sound.id) }) {
                        Icon(Icons.Default.PlayArrow, "Preview")
                    }
                }
            }
        }
    }
}
```

#### Etape 5: Integration Night Mode

Dans `HomeViewModel` / Night Mode:
```kotlin
fun onNightStart() {
    // Play sleep sound if selected
    val sleepSound = soundSettings.selectedSleepSound
    if (sleepSound != null) {
        audioService.play("sounds/${sleepSound}.mp3", loop = true)
    }
}

fun onNightStop() {
    audioService.stop()

    // Play wake sound if selected
    val wakeSound = soundSettings.selectedWakeSound
    if (wakeSound != null) {
        audioService.play("sounds/${wakeSound}.mp3", loop = false)
    }
}
```

### Tests V1.1.6
- [ ] Sons charges depuis assets
- [ ] Preview fonctionne dans settings
- [ ] Sleep sound joue en boucle pendant night mode
- [ ] Wake sound joue au stop
- [ ] Volume adjustable
- [ ] Sons verrouilles si T2 non debloque

---

# VERSION 1.2 - VIRAL + ENGAGEMENT

## V1.2.1 - Templates de Partage (3 Styles) + Stickers

### Specifications

**3 Templates:**
1. **Minimal**: Fond uni, texte simple, branding discret
2. **Neon**: Fond sombre, effets neon, style gaming
3. **Gradient**: Degrade colore, style moderne

**Stickers (optionnels):**
- Flamme (streak)
- Etoile (perfect)
- Trophee (milestone)
- Emojis sommeil

### Implementation

Etendre `ShareCardGenerator` avec parametre `template: ShareTemplate`:
```kotlin
enum class ShareTemplate {
    MINIMAL,
    NEON,
    GRADIENT
}

@Composable
fun ShareCardContent(
    result: NightResult,
    template: ShareTemplate,
    stickers: List<Sticker> = emptyList()
) {
    val backgroundColor = when (template) {
        ShareTemplate.MINIMAL -> Color.White
        ShareTemplate.NEON -> Color(0xFF0D0D0D)
        ShareTemplate.GRADIENT -> Brush.verticalGradient(...)
    }
    // ... render based on template
}
```

---

## V1.2.2 - Rare Drops Actifs (7/14/30)

### Specifications

**Milestones de streak:**
- Streak 7: Drop rare garanti
- Streak 14: Drop rare garanti + chance epic (20%)
- Streak 30: Drop epic garanti

**Trigger:**
Apres le calcul du resultat de nuit, si nouveau milestone atteint.

### Implementation

#### Etape 1: Use Case

**Fichier: `domain/src/commonMain/kotlin/com/example/domain/usecase/CheckMilestoneDropUseCase.kt`**
```kotlin
class CheckMilestoneDropUseCase(
    private val rewardRepository: RewardRepository,
    private val talentRepository: TalentRepository,
    private val random: Random = Random.Default
) {
    suspend fun execute(
        streakBefore: Int,
        streakAfter: Int,
        userId: String
    ): AppResult<Reward?> {
        return try {
            val milestones = listOf(7, 14, 30)
            val crossedMilestone = milestones.firstOrNull {
                streakBefore < it && streakAfter >= it
            }

            if (crossedMilestone == null) {
                return AppResult.Success(null)
            }

            // Check T3 luck bonus
            val hasRareLuck = talentRepository
                .getUserTalents(userId)
                .getOrThrow()
                .any { it.id == "T3" }
            val luckBonus = if (hasRareLuck) 0.20 else 0.0

            // Determine rarity
            val rarity = when (crossedMilestone) {
                7 -> RewardRarity.RARE
                14 -> {
                    val epicChance = 0.20 + luckBonus
                    if (random.nextDouble() < epicChance) RewardRarity.EPIC
                    else RewardRarity.RARE
                }
                30 -> RewardRarity.EPIC
                else -> RewardRarity.RARE
            }

            // Get eligible reward
            val rewards = rewardRepository.getAllRewards().getOrThrow()
            val ownedIds = rewardRepository
                .getUserRewards(userId)
                .getOrThrow()
                .map { it.rewardId }
                .toSet()

            val eligible = rewards.filter {
                it.rarity == rarity && it.id !in ownedIds
            }

            val reward = eligible.randomOrNull()
                ?: rewards.filter { it.id !in ownedIds }.randomOrNull()

            if (reward != null) {
                rewardRepository.grantReward(
                    userId = userId,
                    rewardId = reward.id,
                    source = RewardSource.valueOf("STREAK_$crossedMilestone")
                )
            }

            AppResult.Success(reward)
        } catch (e: DomainException) {
            AppResult.Error(e.error)
        }
    }
}
```

#### Etape 2: Integration dans ApplyNightResult

```kotlin
// Dans ApplyNightResultUseCase, apres update streak:
val milestoneReward = checkMilestoneDropUseCase.execute(
    streakBefore = user.streakCurrent,
    streakAfter = newStreak,
    userId = user.id
).getOrNull()

// Retourner dans le resultat pour afficher dans UI
```

#### Etape 3: UI Milestone Drop

Dans NightResultScreen, si `milestoneReward != null`:
```kotlin
// Afficher animation de drop rare
if (state.milestoneReward != null) {
    RareDropReveal(
        reward = state.milestoneReward,
        milestone = state.milestoneReached
    )
}
```

---

## V1.2.3 - Weekly Recap Auto + Shareable

### Specifications

**Generation automatique:**
- Chaque lundi a 9h (notification)
- Ou a l'ouverture de l'app si lundi

**Contenu:**
- Total slept vs target
- Sleep gained vs baseline
- Consistency (X/7 SUCCESS)
- Best streak this week
- Average score
- Highlight phrase
- Next objective

**Shareable:**
- Bouton "Share" sur le recap
- Genere une image comme Night Result

### Implementation

#### Use Case

```kotlin
class GenerateWeeklyRecapUseCase(
    private val nightRepository: NightRepository,
    private val userRepository: UserRepository,
    private val clock: Clock
) {
    suspend fun execute(): AppResult<WeeklyRecap> {
        // Get nights from last 7 days
        // Calculate stats
        // Generate phrases from templates
    }
}

data class WeeklyRecap(
    val weekStart: LocalDate,
    val weekEnd: LocalDate,
    val totalSleptMinutes: Int,
    val targetMinutes: Int,
    val sleepGainedMinutes: Int,
    val successCount: Int,
    val bestStreak: Int,
    val averageScore: Int,
    val highlightPhrase: String,
    val nextObjective: String
)
```

---

## V1.2.4 - Defis Hebdos (2 Slots via Talent)

### Specifications

**Nouveau Talent V1.5:**
- `CHALLENGE_SLOTS`: Debloque 2 slots de defis (au lieu de 1)

**Implementation:**
- Modifier `getActiveChallenge` -> `getActiveChallenges` (liste)
- Modifier logique de slot verification
- UI pour afficher 2 challenges

---

# VERSION 2 - MONETISATION + REWARDS PHYSIQUES + EVENTS

## V2.1 - Premium Complet

### Specifications

**Prix:** 4,99 EUR/mois

**Features incluses:**
- Stats avancees sans talents requis
- 1 streak shield/semaine supplementaire
- +10% XP boost permanent
- Themes exclusifs (2 nouveaux)
- Sons exclusifs (2 nouveaux)
- Suppression pubs

### Implementation

#### Etape 1: Payment Service

```kotlin
expect class PaymentService {
    suspend fun getProducts(): List<Product>
    suspend fun purchase(productId: String): PurchaseResult
    suspend fun restorePurchases(): List<Purchase>
    fun isSubscribed(): Boolean
}
```

#### Etape 2: Premium Repository

```kotlin
interface PremiumRepository {
    suspend fun getPremiumStatus(): AppResult<PremiumStatus>
    suspend fun activatePremium(until: Instant): AppResult<Unit>
    suspend fun cancelPremium(): AppResult<Unit>
}
```

#### Etape 3: Feature Gating

```kotlin
class CheckPremiumFeatureUseCase(
    private val userRepository: UserRepository
) {
    suspend fun execute(feature: PremiumFeature): Boolean {
        val user = userRepository.getActiveUser().getOrNull() ?: return false
        return user.premiumStatus == PremiumStatus.ACTIVE
    }
}

enum class PremiumFeature {
    ADVANCED_STATS,
    EXTRA_SHIELD,
    XP_BOOST,
    PREMIUM_THEMES,
    PREMIUM_SOUNDS,
    NO_ADS
}
```

#### Etape 4: Paywall UI

```kotlin
@Composable
fun PaywallScreen(
    onPurchase: () -> Unit,
    onRestore: () -> Unit,
    onDismiss: () -> Unit
) {
    // Full-screen paywall with:
    // - Feature list
    // - Price
    // - Purchase button
    // - Restore purchases link
    // - Terms of service link
}
```

---

## V2.2 - Boutique Cosmetique

### Specifications

**Items disponibles:**
- Themes: 1-3 EUR
- Sons: 0,99 EUR
- XP Boost (24h): 0,99 EUR
- Streak Shield: 0,99 EUR

### Implementation

```kotlin
data class ShopItem(
    val id: String,
    val type: ShopItemType,
    val priceEurCents: Int,
    val name: String,
    val description: String,
    val assetRef: String?
)

enum class ShopItemType {
    THEME,
    SOUND,
    XP_BOOST,
    STREAK_SHIELD
}
```

---

## V2.3 - Tirages au Sort (Raffle)

### Specifications

**Entries gagnees par:**
- Streak milestones
- Challenges completes
- Achat premium

**Tirages:**
- Periodiques (hebdo/mensuel)
- Recompenses physiques (produits sommeil, codes promo)

### Implementation

```kotlin
data class RaffleEntry(
    val id: String,
    val userId: String,
    val earnedAt: Instant,
    val source: RaffleEntrySource,
    val usedInRaffleId: String?
)

data class Raffle(
    val id: String,
    val title: String,
    val prize: String,
    val startAt: Instant,
    val endAt: Instant,
    val winnerId: String?
)
```

---

## V2.4 - Battle Pass Saisonnier

### Specifications

**Duree:** 4 semaines

**Structure:**
- 30 niveaux
- Track gratuit (recompenses basiques)
- Track premium (recompenses exclusives)

**Progression:**
- XP de saison gagnee a chaque nuit
- Bonus pour challenges saisonniers

### Implementation

```kotlin
data class Season(
    val id: String,
    val name: String,
    val startAt: Instant,
    val endAt: Instant,
    val tiers: List<SeasonTier>
)

data class SeasonTier(
    val level: Int,
    val xpRequired: Long,
    val freeReward: Reward?,
    val premiumReward: Reward?
)

data class UserSeasonProgress(
    val seasonId: String,
    val userId: String,
    val currentXp: Long,
    val currentTier: Int,
    val hasPremiumPass: Boolean,
    val claimedFreeTiers: Set<Int>,
    val claimedPremiumTiers: Set<Int>
)
```

---

## V2.5 - Publicites Optionnelles

### Specifications

**Placements:**
- Doubler XP apres night result
- Sauver streak (alternative au shield)
- Debloquer animation

### Implementation

```kotlin
expect class AdService {
    fun loadRewardedAd(adUnitId: String)
    fun showRewardedAd(onReward: () -> Unit, onDismiss: () -> Unit)
    fun isAdReady(): Boolean
}
```

---

## V2.6 - Events Temporaires

### Specifications

**Types d'events:**
- Double XP weekend
- Challenge special Halloween/Noel
- Competition communautaire

### Implementation

```kotlin
data class Event(
    val id: String,
    val type: EventType,
    val title: String,
    val startAt: Instant,
    val endAt: Instant,
    val config: EventConfig  // JSON with event-specific data
)

sealed class EventConfig {
    data class DoubleXp(val multiplier: Float = 2.0f) : EventConfig()
    data class SpecialChallenge(val challengeId: String) : EventConfig()
    data class CommunityGoal(val target: Long, val current: Long) : EventConfig()
}
```

---

## V2.7 - Classements

### Specifications

**Types:**
- Streak classement (best streak this week/month)
- Score classement (average score)
- XP classement (total XP gagnee)

**Scope:**
- Global
- Amis (V3)
- Guilde (V3)

### Implementation

**Necessite backend/server:**
```kotlin
interface LeaderboardRepository {
    suspend fun getGlobalLeaderboard(
        type: LeaderboardType,
        period: LeaderboardPeriod,
        limit: Int
    ): AppResult<List<LeaderboardEntry>>

    suspend fun getUserRank(
        userId: String,
        type: LeaderboardType,
        period: LeaderboardPeriod
    ): AppResult<Int>
}

data class LeaderboardEntry(
    val rank: Int,
    val userId: String,
    val username: String,
    val avatarUrl: String?,
    val value: Long  // Streak count, score, or XP
)
```

---

# VERSION 3 - SOCIAL

## V3.1 - Systeme d'Amis

### Specifications

**Fonctionnalites:**
- Recherche d'utilisateurs
- Envoi/acceptation de demandes
- Liste d'amis
- Voir profil d'un ami
- Comparaison de stats

### Implementation

**Necessite backend:**
```kotlin
interface FriendRepository {
    suspend fun searchUsers(query: String): AppResult<List<UserProfile>>
    suspend fun sendFriendRequest(toUserId: String): AppResult<Unit>
    suspend fun acceptFriendRequest(requestId: String): AppResult<Unit>
    suspend fun rejectFriendRequest(requestId: String): AppResult<Unit>
    suspend fun getFriends(): AppResult<List<Friend>>
    suspend fun removeFriend(friendId: String): AppResult<Unit>
    suspend fun getFriendProfile(friendId: String): AppResult<FriendProfile>
}

data class Friend(
    val userId: String,
    val username: String,
    val avatarUrl: String?,
    val level: Int,
    val currentStreak: Int,
    val isOnline: Boolean
)

data class FriendProfile(
    val userId: String,
    val username: String,
    val avatarUrl: String?,
    val level: Int,
    val xpTotal: Long,
    val currentStreak: Int,
    val bestStreak: Int,
    val totalNights: Int,
    val averageScore: Int,
    val badges: List<Reward>
)
```

---

## V3.2 - Guildes / Teams

### Specifications

**Fonctionnalites:**
- Creer une guilde (nom, description, logo)
- Rejoindre une guilde (recherche ou invitation)
- Chat de guilde
- Objectifs collectifs
- Classement interne

### Implementation

```kotlin
data class Guild(
    val id: String,
    val name: String,
    val description: String,
    val logoUrl: String?,
    val createdAt: Instant,
    val memberCount: Int,
    val totalXp: Long,
    val weeklyXp: Long
)

data class GuildMember(
    val userId: String,
    val username: String,
    val role: GuildRole,  // LEADER, OFFICER, MEMBER
    val joinedAt: Instant,
    val contributedXp: Long
)

data class GuildChallenge(
    val id: String,
    val guildId: String,
    val title: String,
    val description: String,
    val targetValue: Long,
    val currentValue: Long,
    val reward: Reward,
    val expiresAt: Instant
)
```

---

## V3.3 - Defis Entre Amis

### Specifications

**Fonctionnalites:**
- Creer un defi et inviter des amis
- Defi 1v1 ou groupe
- Types: streak, score, consistency
- Mise (XP, badge special)

### Implementation

```kotlin
data class FriendChallenge(
    val id: String,
    val creatorId: String,
    val type: FriendChallengeType,
    val title: String,
    val duration: Duration,
    val startAt: Instant,
    val endAt: Instant,
    val participants: List<ChallengeParticipant>,
    val winnerId: String?,
    val reward: FriendChallengeReward
)

data class ChallengeParticipant(
    val userId: String,
    val username: String,
    val currentValue: Int,
    val accepted: Boolean
)

sealed class FriendChallengeReward {
    data class XpStake(val amount: Long) : FriendChallengeReward()
    data class Badge(val badgeId: String) : FriendChallengeReward()
    object Bragging : FriendChallengeReward()  // Just for fun
}
```

---

## V3.4 - Social Boost (Talent)

### Specifications

**Nouveau Talent:**
- `SOCIAL_BOOST`: +5% XP par ami actif (max +25%)

**Definition "ami actif":**
- A complete une nuit dans les derniers 7 jours

### Implementation

```kotlin
class CalculateSocialBoostUseCase(
    private val friendRepository: FriendRepository,
    private val talentRepository: TalentRepository
) {
    suspend fun execute(userId: String): Float {
        val hasTalent = talentRepository
            .getUserTalents(userId)
            .getOrThrow()
            .any { it.id == "SOCIAL_BOOST" }

        if (!hasTalent) return 0f

        val friends = friendRepository.getFriends().getOrThrow()
        val activeFriends = friends.count { it.isActive }
        val bonus = (activeFriends * 0.05f).coerceAtMost(0.25f)

        return bonus
    }
}
```

---

# ANNEXES

## A1 - Textes de Coaching Complets

Tous les textes de coaching sont dans `seed_coach_messages.json`.
Structure:
```json
{
  "success": {
    "chill": ["msg1", "msg2", ...],  // 12 variantes
    "hype": ["msg1", "msg2", ...],
    "strict": ["msg1", "msg2", ...]
  },
  "partial": { ... },
  "fail": { ... },
  "feedback_success": ["..."],  // 6 variantes
  "feedback_partial": ["..."],  // 8 variantes
  "feedback_fail": ["..."],  // 8 variantes
  "advice_success": ["..."],  // 6 variantes
  "advice_partial": ["..."],  // 8 variantes
  "advice_fail": ["..."],  // 10 variantes
  "weekly_highlight": ["..."],  // 12 variantes
  "weekly_objective": ["..."]  // 8 variantes
}
```

## A2 - Assets Requis par Version

| Version | Assets |
|---------|--------|
| V1.1 | 6 fichiers audio (sons), icones notifications |
| V1.2 | 3 templates share card, stickers |
| V2 | Themes premium (2), sons premium (2), icones boutique |
| V3 | Icones social, avatars, badges guilde |

## A3 - Backend Requis

| Feature | Endpoints Necessaires |
|---------|----------------------|
| Classements (V2) | GET /leaderboard, GET /user/rank |
| Amis (V3) | /friends/*, /requests/* |
| Guildes (V3) | /guilds/*, /guild-chat/* |
| Events (V2) | GET /events/active |
| Raffle (V2) | /raffle/*, POST /raffle/enter |

## A4 - Priorite d'Implementation Recommandee

1. **V1.1.1** Notifications - Impact retention eleve
2. **V1.1.4** Challenges - Engagement quotidien
3. **V1.1.2** Share Card - Viralite
4. **V1.1.6** Sons - Experience utilisateur
5. **V1.2.2** Rare Drops - Motivation long terme
6. **V2.1** Premium - Monetisation
7. **V2.4** Battle Pass - Retention + monetisation
8. **V3.1** Amis - Social loop

---

*Document genere le 2026-01-29*
*Version: 1.0*
*Derniere mise a jour du code: M12.2 complete*
