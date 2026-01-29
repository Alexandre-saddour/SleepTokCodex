# Finaliser le MVP - Liste Exhaustive

**Objectif:** Completer toutes les taches restantes pour avoir un MVP 100% fonctionnel selon les specs originales.

**Etat actuel:** M12.2 complete, M13 (tests) en attente

---

## 1. Share Card - Bouton Teaser (UI Prete, Desactivee)

### 1.1 Contexte
La spec MVP indique: "Share Card (desactive mais UI prete) (teaser)"
Actuellement, le bouton n'existe pas dans NightResultScreen.

### 1.2 Fichiers a Modifier

**composeApp/src/commonMain/kotlin/com/example/kmpbackbone/ui/nightresult/NightResultScreen.kt**

### 1.3 Implementation

#### A. Ajouter la string dans les resources
Fichier: `composeApp/src/commonMain/composeResources/values/strings.xml`
```xml
<string name="night_result_share">Share</string>
<string name="night_result_share_coming_soon">Coming soon</string>
```

#### B. Modifier NightResultScreen.kt
Apres le bouton "Continue", ajouter un bouton "Share" desactive:

```kotlin
// Apres le bouton Continue, ajouter:
Spacer(modifier = Modifier.height(12.dp))

// Share button (disabled teaser)
OutlinedButton(
    onClick = { /* No-op - disabled for MVP */ },
    enabled = false,
    modifier = Modifier.fillMaxWidth(),
    colors = ButtonDefaults.outlinedButtonColors(
        disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
    )
) {
    Icon(
        imageVector = Icons.Default.Share,
        contentDescription = null,
        modifier = Modifier.size(18.dp)
    )
    Spacer(modifier = Modifier.width(8.dp))
    Text(stringResource(Res.string.night_result_share))
}

// Helper text
Text(
    text = stringResource(Res.string.night_result_share_coming_soon),
    style = MaterialTheme.typography.bodySmall,
    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
    modifier = Modifier.padding(top = 4.dp)
)
```

#### C. Import necessaire
```kotlin
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
```

### 1.4 Verification
- [ ] Le bouton "Share" apparait sous le bouton "Continue"
- [ ] Le bouton est visuellement desactive (grise)
- [ ] Le texte "Coming soon" apparait en dessous
- [ ] Cliquer sur le bouton ne fait rien

---

## 2. Loot Drop Milestone - Teaser UI

### 2.1 Contexte
La spec MVP indique: "Loot Drop milestone (7/14/30) (teaser UI)"
Verifier que l'UI affiche un teaser pour les prochains drops aux milestones de streak.

### 2.2 Verification de l'Existant
Verifier dans NightResultScreen si le "next milestone" est affiche.
Si non present, ajouter:

### 2.3 Implementation (si manquant)

#### A. Strings a ajouter
```xml
<string name="night_result_next_milestone">Next rare drop at streak %1$d</string>
<string name="night_result_milestone_reached">Rare drop available!</string>
```

#### B. Logique dans NightResultViewModel
Le ViewModel doit calculer le prochain milestone:
```kotlin
private fun getNextMilestone(currentStreak: Int): Int? {
    val milestones = listOf(7, 14, 30)
    return milestones.firstOrNull { it > currentStreak }
}
```

#### C. Affichage dans NightResultScreen
```kotlin
// Afficher le teaser milestone si applicable
state.nextMilestone?.let { milestone ->
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.tertiary
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = stringResource(Res.string.night_result_next_milestone, milestone),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
```

### 2.4 Verification
- [ ] Le prochain milestone (7, 14, ou 30) est affiche si streak < milestone
- [ ] Le style visuel est distinct (teaser)
- [ ] Rien n'est affiche si streak >= 30

---

## 3. Tests Domaine (M13.1)

### 3.1 Structure des Tests
Creer: `domain/src/commonTest/kotlin/com/example/domain/`

### 3.2 Tests XP Formula

#### Fichier: `domain/src/commonTest/kotlin/com/example/domain/usecase/ComputeNightResultUseCaseTest.kt`

```kotlin
class ComputeNightResultUseCaseTest {

    // Test 1: SUCCESS status
    @Test
    fun `night with full duration and on-time start and end returns SUCCESS`() {
        // Given: plan 23:30 -> 07:30 (8h)
        // actual: 23:25 -> 07:35 (8h10)
        // Expected: SUCCESS
    }

    // Test 2: PARTIAL status
    @Test
    fun `night with duration minus 30min returns PARTIAL`() {
        // Given: plan 8h, actual 7h30
        // Expected: PARTIAL
    }

    // Test 3: FAIL status
    @Test
    fun `night with duration minus 50min returns FAIL`() {
        // Given: plan 8h, actual 7h10
        // Expected: FAIL
    }

    // Test 4: Score calculation - duration component
    @Test
    fun `score duration component is 60 when duration equals plan`() {
        // duration_ratio = 1.0 -> 60 points
    }

    // Test 5: Score calculation - start punctuality
    @Test
    fun `score start component is 20 when exactly on time`() {
        // delta_start = 0 -> 20 points
    }

    @Test
    fun `score start component is 0 when 30min late`() {
        // delta_start = 30 -> 0 points
    }

    // Test 6: Score calculation - end punctuality
    @Test
    fun `score end component is 20 when exactly on time`() {
        // delta_end = 0 -> 20 points
    }

    @Test
    fun `score end component is 0 when 40min late`() {
        // delta_end = 40 -> 0 points
    }

    // Test 7: XP base calculation
    @Test
    fun `SUCCESS returns 100 base XP`() {
        // status = SUCCESS -> base = 100
    }

    @Test
    fun `PARTIAL returns 40 base XP`() {
        // status = PARTIAL -> base = 40
    }

    @Test
    fun `FAIL returns 10 base XP`() {
        // status = FAIL -> base = 10
    }

    // Test 8: Score bonus
    @Test
    fun `score bonus is floor(score div 10) times 2`() {
        // score = 92 -> bonus = 18
        // score = 75 -> bonus = 14
    }

    // Test 9: Perfect bonus
    @Test
    fun `perfect bonus is 25 when SUCCESS and score >= 90`() {
        // SUCCESS + score 92 -> +25 XP
    }

    @Test
    fun `no perfect bonus when score < 90`() {
        // SUCCESS + score 85 -> no bonus
    }

    // Test 10: Streak multiplier
    @Test
    fun `streak 0-2 has multiplier 1_0`() { }

    @Test
    fun `streak 3-6 has multiplier 1_1`() { }

    @Test
    fun `streak 7-13 has multiplier 1_2`() { }

    @Test
    fun `streak 14-29 has multiplier 1_3`() { }

    @Test
    fun `streak 30+ has multiplier 1_4`() { }

    // Test 11: XP formula complete
    @Test
    fun `xp_final equals floor of xp_raw times streak_multiplier times talent_multiplier`() {
        // xp_raw = 100 + 18 + 25 = 143
        // streak = 5 -> x1.1
        // talent S1 active -> x1.05
        // xp_final = floor(143 * 1.1 * 1.05) = floor(165.165) = 165
    }
}
```

### 3.3 Tests Streak

#### Fichier: `domain/src/commonTest/kotlin/com/example/domain/usecase/ApplyNightResultUseCaseTest.kt`

```kotlin
class ApplyNightResultUseCaseTest {

    @Test
    fun `SUCCESS increments streak by 1`() {
        // streak_before = 4, status = SUCCESS -> streak_after = 5
    }

    @Test
    fun `PARTIAL preserves streak if duration within 45min of plan`() {
        // Depends on partial rules
    }

    @Test
    fun `FAIL resets streak to 0`() {
        // streak_before = 10, status = FAIL -> streak_after = 0
    }

    @Test
    fun `FAIL with shield preserves streak`() {
        // streak_before = 10, status = FAIL, shield used -> streak_after = 10
    }

    @Test
    fun `shield is consumed after use`() {
        // charges_available = 1 -> charges_available = 0
    }

    @Test
    fun `level increases when xp_total reaches threshold`() {
        // xp_total = 650, xp_earned = 100 -> xp_total = 750 -> level 3
    }

    @Test
    fun `talent points increase with level`() {
        // level 2 -> 3 grants +1 talent point
    }
}
```

### 3.4 Tests Level Thresholds

```kotlin
class LevelCalculatorTest {

    @Test
    fun `level 1 at 0 XP`() { }

    @Test
    fun `level 2 at 300 XP`() { }

    @Test
    fun `level 3 at 700 XP`() { }

    @Test
    fun `level 5 at 2000 XP`() { }

    @Test
    fun `level 10 at 7000 XP`() { }

    @Test
    fun `level 20 at 25000 XP`() { }
}
```

### 3.5 Tests Tolerances

```kotlin
class ToleranceTest {

    @Test
    fun `start within 15min tolerance is valid`() {
        // plan_start = 23:30, actual_start = 23:40 -> valid
    }

    @Test
    fun `start outside 15min tolerance is invalid`() {
        // plan_start = 23:30, actual_start = 23:50 -> invalid
    }

    @Test
    fun `end within 20min tolerance is valid`() {
        // plan_end = 07:30, actual_end = 07:45 -> valid
    }

    @Test
    fun `end outside 20min tolerance is invalid`() {
        // plan_end = 07:30, actual_end = 08:00 -> invalid
    }
}
```

### 3.6 Tests Edge Cases

```kotlin
class EdgeCasesTest {

    @Test
    fun `night crossing midnight is handled correctly`() {
        // start 23:30, end 07:30 next day = 8h
    }

    @Test
    fun `very short night (less than 1h) is FAIL`() { }

    @Test
    fun `longer than planned night caps duration ratio at 1_0`() {
        // plan 8h, actual 9h -> ratio = 1.0, not 1.125
    }

    @Test
    fun `negative delta when waking early is handled`() {
        // plan_end 07:30, actual_end 07:00 -> delta = 30 min
    }
}
```

### 3.7 Verification M13.1
- [ ] Tous les tests de formule XP passent
- [ ] Tous les tests de score passent
- [ ] Tous les tests de streak passent
- [ ] Tous les tests de level passent
- [ ] Tous les edge cases passent
- [ ] Coverage > 80% sur les use cases critiques

---

## 4. Tests Data (M13.2)

### 4.1 Structure des Tests
Creer: `data/src/commonTest/kotlin/com/example/data/`

### 4.2 Tests Repository

#### Fichier: `data/src/commonTest/kotlin/com/example/data/repository/NightRepositoryImplTest.kt`

```kotlin
class NightRepositoryImplTest {

    @Test
    fun `saveNight persists night to database`() { }

    @Test
    fun `getActiveNight returns in_progress night`() { }

    @Test
    fun `getActiveNight returns null when no active night`() { }

    @Test
    fun `getNightsForMonth returns correct nights`() { }

    @Test
    fun `only one in_progress night allowed per user`() { }
}
```

#### Fichier: `data/src/commonTest/kotlin/com/example/data/repository/UserRepositoryImplTest.kt`

```kotlin
class UserRepositoryImplTest {

    @Test
    fun `createUser persists user with initial values`() { }

    @Test
    fun `updateXp adds to xp_total`() { }

    @Test
    fun `updateLevel sets correct level`() { }

    @Test
    fun `updateStreak sets streak_current and streak_best`() { }

    @Test
    fun `streak_best is updated only when current exceeds it`() { }
}
```

### 4.3 Tests Seed Data

#### Fichier: `data/src/commonTest/kotlin/com/example/data/seed/SeedDataTest.kt`

```kotlin
class SeedDataTest {

    @Test
    fun `seed_talents_json has 12 talents`() { }

    @Test
    fun `each talent has required fields`() {
        // id, branch, tier, name, description, cost, effect
    }

    @Test
    fun `talent costs match spec (tier1=1, tier2=2, tier3=3)`() { }

    @Test
    fun `seed_rewards_json has 12 badges`() { }

    @Test
    fun `seed_rewards_json has 6 themes`() { }

    @Test
    fun `seed_rewards_json has 6 sounds`() { }

    @Test
    fun `each reward has required fields`() {
        // id, type, rarity, name
    }

    @Test
    fun `coach_messages_json has all three styles`() {
        // chill, hype, strict
    }

    @Test
    fun `each style has SUCCESS, PARTIAL, FAIL messages`() { }
}
```

### 4.4 Tests Mappers

```kotlin
class MapperTest {

    @Test
    fun `UserEntity maps to User correctly`() { }

    @Test
    fun `User maps to UserEntity correctly`() { }

    @Test
    fun `NightEntity maps to Night correctly`() { }

    @Test
    fun `SleepPlanEntity maps to SleepPlan correctly`() { }

    @Test
    fun `time conversions are correct across timezones`() { }
}
```

### 4.5 Verification M13.2
- [ ] Tous les tests repository passent
- [ ] Tous les tests seed data passent
- [ ] Tous les tests mappers passent
- [ ] Base de donnees s'initialise correctement sur Android et iOS

---

## 5. Tests UI / QA Manuel (M13.3)

### 5.1 Checklist Onboarding

#### O1 - Welcome
- [ ] Logo et tagline s'affichent
- [ ] Bouton "Start" fonctionne
- [ ] Navigation vers O2

#### O2 - Goal Picker
- [ ] 4 cartes s'affichent (Routine, Energie, Productivite, Lever tot)
- [ ] Selection d'une carte la met en surbrillance
- [ ] Impossible de continuer sans selection
- [ ] Navigation vers O3

#### O3 - Coach Style
- [ ] 3 options s'affichent (Chill, Hype, Strict)
- [ ] Exemple de phrase sous chaque choix
- [ ] Selection fonctionne
- [ ] Navigation vers O4

#### O4 - Plan
- [ ] Time picker pour bedtime fonctionne
- [ ] Time picker pour wake time fonctionne
- [ ] Chips jours de la semaine fonctionnent
- [ ] Info tolerance affichee (15 min coucher / 20 min reveil)
- [ ] Navigation vers O5

#### O5 - Gamification Intro
- [ ] 3 slides s'affichent
- [ ] Slide 1: XP / Level / Talent points
- [ ] Slide 2: Streak / bonus / rewards
- [ ] Slide 3: Talents = pouvoirs + features
- [ ] Navigation entre slides
- [ ] Navigation vers O6

#### O6 - Ready
- [ ] Resume du plan s'affiche
- [ ] Bouton "Go Home" fonctionne
- [ ] Navigation vers Home
- [ ] Donnees persistees en base

### 5.2 Checklist Home

#### H1 - Before Night
- [ ] Hero card affiche heures et duree
- [ ] Streak chip affiche streak actuel
- [ ] Level + XP bar affiche progression
- [ ] Bouton PLAY fonctionne
- [ ] Bouton "Edit plan" fonctionne
- [ ] Card "Next reward" affiche prochain badge
- [ ] Card "Daily chest" apparait le matin (si non reclame)

#### H2 - Night Mode
- [ ] Animation calme s'affiche
- [ ] Timer fonctionne
- [ ] Hold-to-stop necessite pression prolongee
- [ ] Stop trop court ne stoppe pas
- [ ] Night est persistee avec status IN_PROGRESS

#### H3 - Post-stop
- [ ] Card "Morning result ready" s'affiche
- [ ] Bouton "View result" fonctionne
- [ ] Navigation vers Night Result

### 5.3 Checklist Night Result

#### R1 - Success
- [ ] Verdict "SUCCESS" s'affiche
- [ ] Score ring affiche score (0-100)
- [ ] XP breakdown s'affiche
- [ ] Streak update s'affiche
- [ ] Coach message personnalise s'affiche
- [ ] Bouton "Continue" fonctionne
- [ ] Bouton "Share" desactive visible (MVP teaser)
- [ ] XP et streak sont persistes

#### R2 - Partial
- [ ] Verdict "PARTIAL" s'affiche
- [ ] Score et XP reduits
- [ ] Message de suggestion s'affiche

#### R3 - Fail
- [ ] Verdict "FAIL" s'affiche
- [ ] XP minimal (10)
- [ ] Streak broken message
- [ ] Si shield disponible: option d'utilisation
- [ ] Shield consomme si utilise

### 5.4 Checklist Progress

#### P1 - Calendar
- [ ] Selecteur de mois fonctionne
- [ ] Grille calendrier s'affiche
- [ ] Couleurs correctes (vert/jaune/rouge)
- [ ] Legende visible
- [ ] Tap sur jour ouvre detail

#### P2 - Night Detail
- [ ] Plan vs actual s'affiche
- [ ] Score s'affiche
- [ ] XP earned s'affiche
- [ ] Status s'affiche
- [ ] Coach note s'affiche

#### P3 - Weekly Recap (si talent I1)
- [ ] Card recap s'affiche si I1 debloque
- [ ] "Total slept vs target" correct
- [ ] "Sleep gained vs baseline" correct
- [ ] Card masquee si I1 non debloque

### 5.5 Checklist Talents

- [ ] Points disponibles s'affichent
- [ ] 4 branches visibles (Discipline, Streak, Style, Insight)
- [ ] 3 tiers par branche
- [ ] Talent node affiche icone, nom, effet, cout
- [ ] Bouton "Unlock" fonctionne si points disponibles
- [ ] Prerequis respecte (tier precedent requis)
- [ ] Talent debloque change d'etat visuel
- [ ] Points deduits apres unlock
- [ ] Effets des talents appliques:
  - [ ] D1: +5 XP si start <= plan_start +10 min
  - [ ] D2: +10 XP si start dans +/- 15 min
  - [ ] D3: +25 XP si SUCCESS + score >= 90
  - [ ] S1: +5% XP si streak >= 3
  - [ ] S2: +1 shield/semaine
  - [ ] S3: +10% XP si streak >= 7
  - [ ] T1: Debloque 1 theme
  - [ ] T2: Debloque 3 sons
  - [ ] T3: (teaser - rare drops)
  - [ ] I1: Active weekly recap
  - [ ] I2: (avance calendar tags)
  - [ ] I3: (trendline graph)

### 5.6 Checklist Profile

#### PR1 - Profile Main
- [ ] Avatar preset s'affiche
- [ ] Level + XP s'affiche
- [ ] Streak current + best s'affiche
- [ ] Badges grid s'affiche (12 badges)
- [ ] Quick stats (wins, total nights)
- [ ] Bouton Settings fonctionne

#### PR2 - Settings
- [ ] Edit plan fonctionne
- [ ] Coach style modifiable
- [ ] Notifications (placeholder/desactive)
- [ ] Premium (teaser)

### 5.7 Checklist Daily Chest

- [ ] Modal s'ouvre depuis Home
- [ ] Animation d'ouverture (si implementee)
- [ ] Recompense revelee
- [ ] Bouton "OK" ou "Equip" fonctionne
- [ ] Recompense persistee dans user_rewards
- [ ] Chest non disponible jusqu'au lendemain

### 5.8 Checklist Cross-Platform

#### Android
- [ ] App demarre sans crash
- [ ] Toutes les navigations fonctionnent
- [ ] Base de donnees Room fonctionne
- [ ] Rotations d'ecran gerees

#### iOS
- [ ] App demarre sans crash
- [ ] Toutes les navigations fonctionnent
- [ ] Base de donnees Room KMP fonctionne
- [ ] Safe areas respectees

### 5.9 Verification M13.3
- [ ] Toutes les checklists completees sur Android
- [ ] Toutes les checklists completees sur iOS
- [ ] Aucun crash identifie
- [ ] Performances acceptables

---

## 6. Corrections Mineures Potentielles

### 6.1 Verifications Additionnelles
- [ ] Tous les textes viennent des resources (aucun hardcode)
- [ ] Toutes les icones/images ont des placeholders
- [ ] Les erreurs sont gerees gracieusement
- [ ] Les etats de chargement sont affiches

### 6.2 Verifier l'Implementation des Specs

| Feature | Spec | A Verifier |
|---------|------|------------|
| Tonight Bonus | "+10% XP si tu lances avant 00:00" | Affiche sur Home? |
| Teaser reward | "3 nuits avant: Badge 'Moonrunner'" | Affiche sur Home? |
| Message nuit | "Tu construis ta meilleure version." | Affiche en Night Mode? |
| Hold-to-stop | "hold-to-stop pour eviter erreurs" | Duree suffisante? |
| Feedback SUCCESS | 12 variantes par style | Toutes implementees? |
| Feedback PARTIAL | 12 variantes par style | Toutes implementees? |
| Feedback FAIL | 12 variantes par style | Toutes implementees? |
| Micro-conseils | 6-10 par status | Implementes? |

---

## 7. Definition of Done - MVP

Le MVP est considere comme **TERMINE** quand:

1. **Code Complete**
   - [ ] Share Card button (disabled) present
   - [ ] Milestone teaser UI present
   - [ ] Tous les ecrans implementes selon specs

2. **Tests Passes**
   - [ ] M13.1 Domain tests: 100% pass
   - [ ] M13.2 Data tests: 100% pass
   - [ ] M13.3 UI tests: Toutes checklists validees

3. **Cross-Platform**
   - [ ] Android: Build + Run sans erreurs
   - [ ] iOS: Build + Run sans erreurs

4. **Quality**
   - [ ] Aucun crash identifie
   - [ ] Aucun texte hardcode
   - [ ] UX fluide et coherente

5. **Documentation**
   - [ ] IMPLEMENTATION_PLAN.md mis a jour (M13 complete)
   - [ ] README a jour si necessaire

---

## Estimation

| Tache | Effort |
|-------|--------|
| Share Card teaser | 30 min |
| Milestone teaser UI | 1h |
| M13.1 Domain tests | 4-6h |
| M13.2 Data tests | 2-3h |
| M13.3 UI QA | 2-3h |
| Corrections mineures | 1-2h |
| **Total** | **10-15h** |
