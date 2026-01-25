# Implementation Plan - SleepTokCodex (KMP + Compose)

This file is the single source of truth for step-by-step delivery. Each step has an ID. Do not skip steps. Mark a step done only when it is fully complete and verified.

Last completed step: M9.2
Next step to run: M9.3

--------------------------------------------------------------------------------
0) How to use this plan
--------------------------------------------------------------------------------
- At the start of each session, open this file and resume from "Next step to run".
- Update "Last completed step" and "Next step to run" as you go.
- If a step is blocked, write a short note in this file under "Open blockers".
- Keep changes small and verifiable; avoid large refactors in a single step.

Open blockers:
- None

--------------------------------------------------------------------------------
1) Scope (MVP = V1 as described in docs)
--------------------------------------------------------------------------------
MVP includes:
- Onboarding flow (6 screens)
- Home (before night, night mode, post-stop claim)
- Night result (success/partial/fail)
- Progress (monthly calendar + weekly recap + night detail)
- Talents (4 branches, 12 talents)
- Profile + Settings (plan edit, coach style, premium teaser)
- Rewards (badges, themes, sounds), Daily Chest (basic)
- XP + level + streak system, talent points
- Local-only storage (Room KMP)
- Koin DI
- Compose Multiplatform UI

Not in MVP (but planned for later):
- Share card activation (button only in MVP, disabled)
- Notifications (placeholder only)
- Premium and ads logic (teaser only)
- Social, challenges beyond MVP

--------------------------------------------------------------------------------
2) Global constraints (must never be violated)
--------------------------------------------------------------------------------
- Clean Architecture: domain is pure, data implements, composeApp is UI + ViewModel only.
- Use cases expose exactly one public method: suspend fun execute(...)
- No hardcoded user-visible strings anywhere (UI/domain/data). All texts come from resources or local seed data files.
- Prefer KMP libraries; avoid platform-specific APIs in common code.
- Room must be multiplatform and configured for iOS.
- Koin modules per layer (domain, data, composeApp).

--------------------------------------------------------------------------------
3) Milestones and steps
--------------------------------------------------------------------------------

M0) Repo cleanup and structure
--------------------------------
- [ ] M0.1 Remove template/sample code (Greeting, WelcomeMessage, sample Home screen).
  - Delete: composeApp/src/commonMain/kotlin/com/example/kmpbackbone/Greeting.kt
  - Delete: domain/.../WelcomeMessageRepository + GetWelcomeMessageUseCase
  - Delete: data/.../WelcomeMessageRepositoryImpl + Platform.* if unused after cleanup
  - Delete: sample UI in composeApp/home or move to new structure
  - Done when: app builds without sample references

- [ ] M0.2 Create required package structure in composeApp
  - Add: composeApp/src/commonMain/kotlin/.../ui
  - Add: composeApp/src/commonMain/kotlin/.../navigation
  - Add: composeApp/src/commonMain/kotlin/.../viewmodel
  - Add: composeApp/src/commonMain/kotlin/.../resources (code helpers only, not assets)
  - Done when: new packages exist and are used by new root screen

- [ ] M0.3 Create AppRoot and update entry points
  - Add: AppRoot composable in composeApp/ui
  - Update MainActivity and MainViewController to use AppRoot
  - Done when: Android and iOS show AppRoot composable

M1) Dependencies and build configuration
----------------------------------------
- [ ] M1.1 Add KMP dependencies (latest stable at implementation time)
  - Room KMP (runtime + compiler)
  - Kotlinx datetime
  - Kotlinx serialization (for seed data)
  - Optional: a KMP navigation lib if custom nav is not desired
  - Update gradle/libs.versions.toml with versions and library aliases
  - Done when: Gradle sync succeeds and builds compile

- [ ] M1.2 Configure Room for KMP
  - Apply KSP plugin to data module
  - Add Room schema export path
  - Ensure iOS driver config follows Room KMP docs
  - Done when: Room classes are generated and iOS build does not fail

M2) Domain layer (models, errors, use cases)
--------------------------------------------
- [ ] M2.1 Create domain models (immutable data classes)
  - User, SleepPlan, Night, XpEvent
  - Talent, TalentBranch, TalentTier
  - Reward, RewardType, RewardRarity
  - StreakShield, CoachStyle, NightStatus
  - Time types should use kotlinx.datetime
  - Done when: models compile and are used in interfaces

- [ ] M2.2 Define domain result and error types
  - sealed class AppResult<out T> (Success, Error)
  - sealed class DomainError (NotFound, Validation, Storage, Conflict, Unknown)
  - Done when: repositories and use cases return AppResult

- [ ] M2.3 Create repository interfaces in domain/repository
  - UserRepository
  - SleepPlanRepository
  - NightRepository
  - TalentRepository
  - RewardRepository
  - StreakShieldRepository
  - Done when: all required data access is represented

- [ ] M2.4 Create use cases (each with only execute)
  - Onboarding: GetOnboardingState, CompleteOnboarding
  - Plan: GetActivePlan, UpdatePlan
  - Home: GetHomeSummary, GetActiveNight
  - Night: StartNight, StopNight, ComputeNightResult, ApplyNightResult
  - Progress: GetCalendarMonth, GetWeeklyRecap, GetNightDetail
  - Talents: GetTalentTree, UnlockTalent
  - Profile: GetProfileSummary
  - Rewards: ClaimDailyChest, GetBadgesAndCosmetics
  - Done when: each use case compiles and is testable

- [ ] M2.5 Implement core scoring/xp logic in domain (pure functions)
  - Night status (SUCCESS/PARTIAL/FAIL) based on tolerances
  - Score (0-100) with duration + punctuality components
  - XP formula (base + bonus + perfect + talents) then streak multiplier
  - Level thresholds and talent points (+1 per level)
  - Done when: unit tests cover expected cases

M3) Data layer (Room KMP + repositories)
----------------------------------------
- [ ] M3.1 Define Room entities and DAOs
  - Entities: UserEntity, SleepPlanEntity, NightEntity, XpEventEntity,
    TalentEntity, UserTalentEntity, RewardEntity, UserRewardEntity,
    StreakShieldEntity
  - DAOs: UserDao, SleepPlanDao, NightDao, TalentDao, RewardDao, ShieldDao
  - Include indexes and unique constraints as per schema
  - Done when: Room schema builds with version 1

- [ ] M3.2 Add Room database and type converters
  - Database class with all DAOs
  - Type converters for LocalDate, LocalDateTime, LocalTime, enums
  - Done when: database opens on Android and iOS

- [ ] M3.3 Create data mappers (entity <-> domain)
  - One mapper per entity pair
  - Keep mapping logic in data/mapper
  - Done when: repositories can map all data

- [ ] M3.4 Implement repositories
  - Use DAOs and mappers
  - Convert storage errors to DomainError
  - Done when: domain use cases can use repositories

- [x] M3.5 Seed static data (talents, rewards, coach texts)
  - Create JSON files under composeApp resources (raw) for:
    - talents list (12)
    - rewards list (badges, themes, sounds)
    - coach messages (success/partial/fail, 3 tones)
  - Create a seeding use case or data initializer in data layer
  - Seed once on first launch
  - Done when: DB has initial talent and reward data

M4) DI and app shell
-------------------
- [ ] M4.1 Create Koin modules per layer
  - domain module: use cases
  - data module: database, daos, repositories, seeders
  - composeApp module: viewmodels
  - Done when: initKoin wires all modules

- [x] M4.2 Build AppRoot + navigation state
  - Custom navigation state or KMP nav lib
  - Support onboarding flow and main tabs (Home, Progress, Talents, Profile)
  - Done when: navigation works in common code

- [ ] M4.3 Theme + resources plumbing
  - Add fonts and color palette (non-default)
  - Define typography and color scheme in AppTheme
  - Ensure all UI texts use resources (Res.string.*)
  - Done when: AppRoot uses AppTheme

M5) Onboarding flow (O1-O6)
---------------------------
- [x] M5.1 Onboarding ViewModel and state
  - UiState includes step index and selections
  - UiEvent for completion
  - Use GetOnboardingState and CompleteOnboarding
  - Done when: onboarding state drives UI

- [x] M5.2 Screens O1-O6
  - O1 Welcome (logo + CTA)
  - O2 Goal picker (4 cards)
  - O3 Coach style (3 cards with sample text)
  - O4 Plan (bedtime, wake time, active days)
  - O5 Gamification intro (3 slides)
  - O6 Ready (summary + CTA)
  - Done when: onboarding completes and navigates to Home

- [x] M5.3 Persist onboarding outputs
  - Create User with baseline sleep duration
  - Create active SleepPlan with tolerances
  - Store coach style
  - Done when: Home reads plan and user data from DB

M6) Home (before night, night mode, post-stop claim)
----------------------------------------------------
- [x] M6.1 Home ViewModel and state
  - UiState for before-night, night-mode, post-stop
  - Use GetHomeSummary and GetActiveNight
  - Done when: state updates from DB

- [x] M6.2 Home UI (before night)
  - Hero card with plan time and duration
  - Streak chip, level + XP bar
  - Primary CTA: Play
  - Secondary CTA: Edit plan
  - Teaser card for next reward
  - Done when: before-night screen matches spec

- [x] M6.3 Night mode UI
  - Full-screen calm animation
  - Timer from start
  - Hold-to-stop interaction
  - Done when: hold-to-stop requires sustained press

- [x] M6.4 Post-stop claim UI
  - Show "result ready" card if user returns
  - CTA to open Night Result
  - Done when: pending result is tracked

M7) Night result (SUCCESS/PARTIAL/FAIL)
---------------------------------------
- [x] M7.1 ComputeNightResult use case (pure)
  - Input: plan, start, end, tolerances
  - Output: status, score, xp breakdown, streak change proposal
  - Done when: tests cover all branches

- [x] M7.2 ApplyNightResult use case (persist)
  - Persist night record
  - Update user xp_total, level, streak
  - Add xp_event entries
  - Done when: DB reflects results after stop

- [x] M7.3 Night Result UI
  - Verdict + score ring
  - XP breakdown (collapsible)
  - Streak update
  - Coach message
  - CTA: Continue
  - Done when: each status renders correct content

- [x] M7.4 Streak shield handling
  - If FAIL and shield available -> prompt use
  - If used -> preserve streak, consume charge
  - Done when: shield logic is persisted

M8) Progress (calendar + weekly recap + night detail)
-----------------------------------------------------
- [x] M8.1 Progress ViewModel and state
  - Month data grid (status per day)
  - Weekly recap card data
  - Night detail modal data
  - Done when: UI uses state only

- [x] M8.2 Calendar UI
  - Month selector
  - Grid with status colors
  - Legend
  - Done when: month changes update grid

- [x] M8.3 Weekly recap UI
  - Slept vs target
  - Sleep gained vs baseline
  - Best streak this week
  - Done when: recap appears if talent I1 unlocked

- [x] M8.4 Night detail UI
  - Plan vs actual
  - Score and XP
  - Status and coach note
  - Done when: tap on day opens detail

M9) Talents (tree)
------------------
- [x] M9.1 Talents ViewModel and state
  - Points available
  - Branch sections with tiers
  - Locked/unlocked status
  - Done when: state reflects DB

- [x] M9.2 Talents UI
  - Tree or section layout
  - Talent cards with cost + effect
  - Unlock button
  - Done when: unlock updates DB and UI

- [ ] M9.3 Talent effects integration
  - XP bonuses, streak shield, cosmetic unlocks, recap visibility
  - Done when: system behavior changes based on unlocks

M10) Profile + Settings
-----------------------
- [ ] M10.1 Profile ViewModel and state
  - Level, XP, total nights, best streak
  - Badges grid
  - Done when: profile uses data only

- [ ] M10.2 Profile UI
  - Avatar preset
  - XP bar
  - Badges grid
  - Quick stats
  - Done when: layout matches spec

- [ ] M10.3 Settings UI
  - Edit plan
  - Coach style
  - Notifications (placeholder)
  - Premium teaser
  - Done when: edits persist to DB

M11) Rewards (daily chest, milestones, cosmetics)
-------------------------------------------------
- [ ] M11.1 Reward claim flow
  - Daily chest (morning)
  - Animation + reward reveal
  - Equip or OK
  - Done when: reward is stored as user_reward

- [ ] M11.2 Milestone rewards (7/14/30)
  - Trigger on streak milestones
  - Teaser UI in MVP (actual drop optional)
  - Done when: milestones display in Night Result

M12) Resources and text
-----------------------
- [ ] M12.1 Add all user-visible strings to resources
  - All screen titles, buttons, descriptions, helper texts
  - Coach messages and recap templates via JSON resource
  - No hardcoded strings in code
  - Done when: strings lint check passes

- [ ] M12.2 Add icons and art placeholders
  - Badges, themes, sound icons
  - Minimal assets for MVP
  - Done when: UI renders without missing resources

M13) Testing and QA
------------------
- [ ] M13.1 Domain tests
  - XP formula, score computation, streak updates
  - Edge cases (late start, early end, short duration)
  - Done when: tests pass

- [ ] M13.2 Data tests
  - Repository behavior with seed data
  - Migrations (if any)
  - Done when: tests pass

- [ ] M13.3 UI sanity checks
  - Onboarding flow end-to-end
  - Start/stop night and result
  - Progress + talents + profile
  - Done when: manual QA checklist complete

--------------------------------------------------------------------------------
4) Reference data (to be implemented as seed JSON)
--------------------------------------------------------------------------------
Talents (12):
- Discipline: D1 Warm-up XP, D2 Clean Start, D3 Perfect Night Bonus
- Streak: S1 Streak Booster I, S2 Streak Shield, S3 Streak Booster II
- Style: T1 Theme Slot, T2 Sound Pack, T3 Rare Cosmetics
- Insight: I1 Weekly Recap, I2 Advanced Calendar, I3 Trendline

Rewards (badges/themes/sounds) and coach texts:
- Use the provided doc lists exactly as source of truth.
- Store text in resource JSON and load into DB on first launch.

--------------------------------------------------------------------------------
5) Final MVP checklist (must be true before MVP is "done")
--------------------------------------------------------------------------------
- Onboarding creates user + plan + coach style.
- Night start/stop creates a night record and computes result.
- XP, level, streak, talent points update correctly.
- Progress calendar and weekly recap show real data.
- Talents unlock and affect behavior.
- Profile shows correct stats and badges.
- No hardcoded user strings anywhere.
- Works on Android and iOS with Room KMP.

--------------------------------------------------------------------------------
6) Exhaustive requirements (verbatim + structured source)
--------------------------------------------------------------------------------
This section preserves all user-provided details to prevent loss across sessions.
All user-visible copy below must be stored in resources or seed JSON, never hardcoded.

6.1 Vision produit claire
- Objectif: Creer une application addictive mais saine, qui transforme le sommeil en jeu de progression personnelle.
- Cible:
  - 16-30 ans
  - Habitues aux mecaniques: XP, streak, battle pass, daily rewards
  - Faible discipline -> besoin de feedback immediat
- Promesse utilisateur: "Tu ne dors pas juste mieux. Tu progresses."

6.2 Core loop (boucle principale)
- Planifier -> Dormir -> Resultat -> Recompense -> Progression -> Motivation -> Rejouer
- Details:
  - L'utilisateur definit une nuit
  - Il lance le mode nuit
  - Il se reveille -> validation
  - XP + streak + feedback
  - Deblocage / progression
  - Desir de continuer pour ne pas perdre

6.3 MVP - Features detaillees
6.3.1 Onboarding (ultra important)
- Choix objectif principal:
  - Mieux dormir
  - Avoir une routine
  - Etre plus productif
  - Se lever plus tot
- Parametres sommeil:
  - Heure coucher
  - Heure reveil
  - Jours actifs
- Ton de l'app:
  - Chill
  - Motivant
  - Strict
- Explication rapide:
  - XP
  - Streak
  - Recompenses
- Duree cible: 60-90 secondes

6.3.2 Ecran principal - Night Screen
- Contenu:
  - Heure actuelle
  - Bouton PLAY
  - Objectif de la nuit
  - Streak actuel
  - XP du niveau
- Pendant la nuit:
  - Animation calme
  - Message du type: "Tu construis ta meilleure version."
- Bouton STOP au reveil

6.3.3 Resultat du matin (ecran cle)
- Si reussite:
  - Animation positive
  - XP gagnee
  - Streak +1
  - Message personnalise
  - Exemples:
    - "+42 min de sommeil cette semaine"
    - "Tu es plus regulier que 78% des utilisateurs"
    - "3 nuits avant un nouveau pouvoir"
- Si echec:
  - Message bienveillant
  - Pas de culpabilisation
  - Proposition: Reessayer ou utiliser un protecteur de streak

6.4 Systeme d'XP (precis)
- XP de base:
  - Nuit reussie: 100
  - + respect horaire: +20
  - Streak (x2, x3...): multiplicateur
  - Defi reussi: +50-150
  - Perfect night: +50
- Multiplicateurs:
  - Streak x2 -> apres 3 jours
  - Streak x3 -> apres 7 jours
  - Talent actif -> +10 a 25%

6.5 Niveaux
- Niveau -> XP cumulee:
  - 1 -> 0
  - 2 -> 300
  - 3 -> 700
  - 5 -> 2 000
  - 10 -> 7 000
  - 20 -> 25 000
- Chaque niveau:
  - +1 point de talent
  - parfois recompense cosmetique

6.6 Arbre de talents (cle du produit)
- Branche Discipline:
  - +5% XP
  - +10% XP
  - Streak +1 jour de tolerance
  - Bonus matin
- Branche Streak:
  - Protection 1x/semaine
  - Bonus XP streak
  - Streak ne casse pas si 1h de retard
- Branche Esthetique:
  - Themes
  - Animations
  - Sons exclusifs
- Branche Insight:
  - Stats avancees
  - Historique detaille
  - Comparaisons hebdo
- Branche Reward:
  - Acces tirages
  - de chances de gagner
  - Rewards exclusives

6.7 Streak system (ultra important)
- Regles:
  - 1 nuit ratee = streak cassee
  - Sauf si: talent actif, item utilise, premium
- Bonus streak:
  - 3 jours: +10% XP
  - 7 jours: badge
  - 14 jours: recompense rare
  - 30 jours: reward physique

6.8 Recompenses
- Virtuelles: Badges, Themes, Sons, Animations, Titres
- Psychologiques: Messages dynamiques, Graphiques de progres, "Tu dors mieux que X%"
- Physiques: Codes promo, Produits sommeil, Abonnements partenaires

6.9 Monetisation (cle)
- Premium (4,99EUR/mois) inclut:
  - Stats avancees
  - Protection de streak
  - XP boost
  - Themes exclusifs
  - Recompenses premium
  - Suppression pubs
- Microtransactions:
  - Sauver un streak: 0,99EUR
  - Skin: 1-3EUR
  - Sons: 0,99EUR
  - XP boost
- Ads optionnelles:
  - Doubler XP
  - Sauver un streak
  - Debloquer animation

6.10 Roadmap (haute niveau)
- V1 - MVP: Core sleep, XP, Streak, Talents simples
- V1.5: Defis, Sons, Stats, Partage social
- V2: Recompenses physiques, Partenariats, Events, Classements
- V3: Social, Guildes, Defis entre amis, Saison/battle pass

6.11 Flow UX ecran par ecran (Doc2)
6.11.1 Onboarding (premiere ouverture)
- Splash / Brand: Logo anime + tagline ("Level up your sleep")
- Consent & disclaimers:
  - "Pas un dispositif medical"
  - Permissions (notifications) -> "Plus tard" possible
- Choix du "Why": Cartes "Routine", "Energie", "Productivite", "Reduire fatigue"
- Choix du style: "Chill coach" / "Hype coach" / "Strict coach"
- Plan sommeil:
  - Heure coucher (slider/clock)
  - Heure reveil
  - Jours actifs (lun-dim)
  - Option: "Tolerance" (MVP: 15 min fixe, non modifiable)
- Mini-tutoriel gamification (3 slides):
  - XP -> Level -> Talent points
  - Streak -> bonus XP + rewards
  - Talents -> deverrouillent features + pouvoirs
- Ecran "Ready":
  - CTA: "Let's go" -> Home

6.11.2 Navigation (MVP)
- Barre 4 onglets: Home, Progress, Talents, Profile

6.11.3 Home (hub du quotidien)
- Etats: Avant nuit / Pendant nuit / Apres nuit (si pas vu le resultat)
- Home - avant nuit:
  - Hero card: "Ce soir : 23:30 -> 07:30 (8h)"
  - Streak chip: "Streak 4 🔥"
  - Niveau + XP barre
  - CTA principal: PLAY
  - CTA secondaire: "Modifier plan"
  - Bloc "Tonight Bonus": "+10% XP si tu lances avant 00:00"
  - Teaser reward: "3 nuits avant: Badge 'Moonrunner'"
- Home - pendant la nuit:
  - Full-screen "Night Mode"
  - Timer depuis start
  - Rappel doux: "Ecran minimal"
  - CTA discret: STOP (hold-to-stop)
  - Option (MVP): "SOS stop" (confirmation)
- Home - matin (post-stop):
  - Affiche "Voir ton resultat" si l'utilisateur ferme trop vite
  - CTA: "Claim rewards"

6.11.4 Night Result (ecran le plus important)
- Entrees: start_time, end_time, plan_start, plan_end
- Affichage:
  - Verdict: "SUCCESS" (animation + confettis minimal) / "PARTIAL" / "FAIL"
  - Score visuel (0-100)
  - XP breakdown (foldable)
  - Streak update
  - Message psychologique
  - CTA: "Continue" (retour Home), "Share" (V1.2, bouton desactive)
- Actions si FAIL:
  - Proposer "Streak Shield" si possede
  - Sinon: "Try again tonight"
  - Option premium teaser (soft): "Premium: 1 shield / semaine"

6.11.5 Progress (historique)
- MVP:
  - Calendrier mensuel (cases): vert=success, jaune=partial, rouge=fail
  - Semaine en cours: barres "heures dormies vs objectif"
  - "Sleep gained": difference entre duree reelle moyenne semaine vs baseline initiale
- Actions:
  - Tap sur une nuit -> detail (duree, plan, XP, statut)

6.11.6 Talents (arbre)
- MVP: 4 branches, 3 tiers chacune, UI tree simple
- Header: points disponibles
- Branches: Discipline / Streak / Style / Insight
- Chaque talent:
  - icone + titre
  - effet
  - cout
  - "Unlock"

6.11.7 Profile
- Avatar (MVP: preset)
- Niveau, XP, total nights, best streak
- Badges (MVP: 6-12)
- Settings:
  - Plan sommeil
  - Notifications (V1.1)
  - Ton du coach

6.11.8 Settings (MVP minimal)
- Edit plan
- Export / delete account (si besoin legal plus tard)
- Support / feedback

6.12 Talents + couts (MVP -> V1.5)
- Regles:
  - Talent points: +1 a chaque niveau
  - Couts: Tier 1 = 1, Tier 2 = 2, Tier 3 = 3
  - Prerequis: tier precedent dans la meme branche
- Branche 1 - Discipline (XP & consistance):
  - D1 Tier1 "Warm-up XP" (1): +5 XP si tu lances avant l'heure de coucher + 10 min
  - D2 Tier2 "Clean Start" (2): +10 XP si tu lances dans la fenetre cible
  - D3 Tier3 "Perfect Night Bonus" (3): +25 XP si "SUCCESS" + score >= 90
- Branche 2 - Streak (protection & multiplicateurs):
  - S1 Tier1 "Streak Booster I" (1): +5% XP tant que streak >= 3
  - S2 Tier2 "Streak Shield" (2): 1 sauvegarde de streak / semaine (charge hebdo)
  - S3 Tier3 "Streak Booster II" (3): +10% XP tant que streak >= 7
- Branche 3 - Style (cosmetique & motivation):
  - T1 Tier1 "Theme Slot" (1): Debloque 1 theme (palette + background)
  - T2 Tier2 "Sound Pack" (2): Debloque 3 sons (sleep/wake)
  - T3 Tier3 "Rare Cosmetics" (3): Acces "rare drops" sur milestones (7/14/30)
- Branche 4 - Insight (data & retention):
  - I1 Tier1 "Weekly Recap" (1): Resume semaine (heures, regularite, gain)
  - I2 Tier2 "Advanced Calendar" (2): Affiche score / nuit + tags "late/early"
  - I3 Tier3 "Trendline" (3): Graph simple "duree vs objectif" sur 30 jours
- Talents V1.5 (plus tard):
  - "Challenge Slots" (2 defis actifs)
  - "Reward Luck +X%"
  - "Ad Skip Token" (1/jour)
  - "Social Boost" (bonus XP si ami actif)

6.13 Formules XP et scoring (precises)
6.13.1 Definitions
- Plan: plan_start, plan_end (ex: 23:30 -> 07:30)
- Nuit: start_time, end_time
- Duree plan: plan_duration = plan_end - plan_start
- Duree reelle: actual_duration = end_time - start_time
6.13.2 Tolerances (MVP)
- Tolerance start: +/- 15 min autour de plan_start
- Tolerance end: +/- 20 min autour de plan_end
6.13.3 Statut (SUCCESS / PARTIAL / FAIL)
- SUCCESS si:
  - actual_duration >= plan_duration - 10 min
  - et start dans tolerance
  - et end dans tolerance
- PARTIAL si:
  - actual_duration >= plan_duration - 45 min
  - sinon FAIL
6.13.4 Score (0-100)
- Composante duree (0-60):
  - duration_ratio = actual_duration / plan_duration (cap a 1.0)
  - duration_points = clamp(duration_ratio, 0, 1) * 60
- Ponctualite coucher (0-20):
  - delta_start = abs(start_time - plan_start) en minutes
  - start_points = max(0, 20 - (delta_start * 20 / 30))
  - 0 point a 30 min d'ecart
- Ponctualite reveil (0-20):
  - delta_end = abs(end_time - plan_end)
  - end_points = max(0, 20 - (delta_end * 20 / 40))
  - 0 point a 40 min d'ecart
- Score total = somme arrondie (max 100)
6.13.5 XP gagnee
- Base:
  - SUCCESS: 100 XP
  - PARTIAL: 40 XP
  - FAIL: 10 XP (anti-churn)
- Bonus:
  - score_bonus = floor(score / 10) * 2 (0 a 20 XP)
  - perfect_bonus = 25 XP si score >= 90 et statut SUCCESS
- Streak multiplier:
  - streak 0-2: x1.0
  - streak 3-6: x1.1
  - streak 7-13: x1.2
  - streak 14-29: x1.3
  - streak >= 30: x1.4
- Talent modifiers:
  - Ex: S1 (+5% XP) -> multiplicateur x1.05 si streak >= 3
  - D3 (+25 XP) s'ajoute avant multiplicateur ou apres (choix)
  - Recommandation: ajouts avant multiplicateur
- Formule finale:
  - xp_raw = base + score_bonus + perfect_bonus + talent_additions
  - xp_final = floor(xp_raw * streak_multiplier * talent_multiplier)

6.14 Structure BDD (schema logique)
6.14.1 Entites principales
- users:
  - id (PK)
  - created_at
  - timezone
  - coach_style (chill/hype/strict)
  - premium_status (none/trial/active/canceled)
  - premium_until (nullable)
  - level
  - xp_total
  - talent_points_available
  - streak_current
  - streak_best
  - last_night_id (nullable)
  - baseline_sleep_duration_minutes (valeur onboarding)
  - settings_json (pour iterations)
- sleep_plans:
  - id (PK)
  - user_id (FK)
  - plan_start_local_time (HH:MM)
  - plan_end_local_time (HH:MM)
  - active_days_mask (bitmask 7 jours)
  - tolerance_start_min (MVP: 15)
  - tolerance_end_min (MVP: 20)
  - created_at
  - is_active
- nights:
  - id (PK)
  - user_id (FK)
  - plan_id (FK)
  - start_at (timestamp)
  - end_at (timestamp nullable si en cours)
  - status (in_progress/success/partial/fail/void)
  - actual_duration_min
  - plan_duration_min
  - score
  - xp_earned
  - streak_before
  - streak_after
  - created_at
  - note (nullable)
- xp_events (optionnel, mais utile):
  - id
  - user_id
  - night_id (nullable)
  - type (night_success, bonus, challenge, etc.)
  - amount
  - created_at
  - meta_json
- talents:
  - id (ex: "S2")
  - branch (discipline/streak/style/insight)
  - tier (1/2/3)
  - name
  - description
  - cost_points
  - effect_json (ex: { "xp_bonus_pct": 0.05, "condition": "streak>=3" })
  - is_active (pour disable)
- user_talents:
  - user_id
  - talent_id
  - unlocked_at
- rewards:
  - id
  - type (badge/theme/sound/coupon/raffle_entry)
  - rarity (common/rare/epic)
  - name
  - asset_ref (sound/theme id)
  - meta_json
- user_rewards:
  - user_id
  - reward_id
  - earned_at
  - source (streak_7, level_up, etc.)
  - consumed_at (nullable)
- streak_shields (si item):
  - user_id
  - charges_available
  - refresh_at (timestamp)
  - source (talent/premium/store/ad)
6.14.2 Index/contraintes importantes
- nights(user_id, start_at)
- user_talents(user_id, talent_id) unique
- user_rewards(user_id, reward_id, earned_at) pour historique
- Contrainte: une seule nuit in_progress par user

6.15 Wireframes texte (ready for Figma)
6.15.1 Style guidelines (Gen Z / TikTok-friendly)
- 1 grand "hero" par ecran
- 1 CTA principal max
- animations courtes
- visuels "shareable cards" des V1.2

6.15.2 Onboarding - "Plan"
- Header: "Set your sleep quest"
- Card: "Bedtime" [time picker]
- Card: "Wake time" [time picker]
- Row: "Active days" [Mon..Sun chips]
- Helper: "We'll track your streak on active days"
- CTA sticky bottom: Continue

6.15.3 Home - Avant nuit
- Top bar: avatar + level badge / settings icon
- Hero card (large):
  - Title: "Tonight's Quest"
  - Sub: "23:30 -> 07:30 · 8h"
  - Pill: "Streak 4 🔥"
  - Progress bar XP: "Lv 6 · 420/700"
- CTA Primary (full width): PLAY
- Secondary row:
  - Button: "Edit plan"
  - Button: "View progress"
- Small card:
  - "Next reward in 3 wins: 'Moonrunner' badge"

6.15.4 Night Mode - Pendant nuit
- Full screen gradient
- Big text: "Quest in progress"
- Time elapsed small
- Calm animation center (breathing orb)
- Bottom:
  - Hold to STOP (slide/hold)
  - microtext: "Avoid accidental stops"

6.15.5 Night Result - Success
- Hero:
  - Big: "SUCCESS"
  - Score ring: "92"
  - Text: "Perfect Night! +25 bonus"
- XP breakdown card (collapsible):
  - Base 100
  - Score bonus +18
  - Perfect +25
  - Streak x1.1
  - Total: 156 XP
- Streak card:
  - "Streak: 4 -> 5"
  - "Next milestone: 7 (Rare drop)"
- Message card:
  - "You gained 42 min of sleep this week"
- CTA:
  - Primary: "Claim & continue"
  - Secondary: "Share card" (disabled MVP)

6.15.6 Progress - Calendrier
- Header: "Your streak calendar"
- Month selector
- Calendar grid
- Legend chips (success/partial/fail)
- Weekly recap card:
  - "This week: 38h / 56h target"
  - "Sleep gained: +1h12"
- List (optional MVP): Dernieres nuits (3 items)

6.15.7 Talents - Tree
- Header: "Talent Tree"
- Points available: "3"
- Tabs (ou scroll sections): Discipline / Streak / Style / Insight
- Talent node card:
  - Icon + Name
  - Effect line
  - Cost badge "2 TP"
  - Button: "Unlock" / "Unlocked"

6.15.8 Profile
- Header card:
  - Avatar
  - "Lv 6"
  - XP bar
  - "Best streak: 12"
- Badges section: Grid 3xN
- Stats quick:
  - "Total wins"
  - "Total nights"
  - "Avg score (30d)" (si talent insight debloque)
- Settings list:
  - Plan
  - Coach style
  - Premium

6.16 Roadmap enrichie (Doc3)
- V1 - MVP:
  - Plan sommeil (heures + jours)
  - Home + Night Mode Play/Stop
  - Resultat du matin (SUCCESS/PARTIAL/FAIL) + XP + streak
  - Niveaux + points de talent
  - Talents (4 branches, 12 talents)
  - Progress: calendrier + recap semaine (simple)
  - Profile: badges + stats basiques
  - Daily Chest (matin) (recompense legere de connexion)
  - Share Card (desactive mais UI prete) (teaser)
  - Loot Drop milestone (7/14/30) (teaser UI)
- V1.1:
  - Notifications intelligentes (coucher / reveil / "claim morning")
  - Share Card active (story-ready)
  - "Daily Chest" enrichi (loot cosmetique leger)
  - Challenges simples (1 slot)
  - Streak Shield "charge hebdo" (si talent)
  - Sons pack #1
- V1.2:
  - Templates de partage (3 styles) + stickers
  - "Rare drops" actives a 7/14/30
  - "Weekly Recap" auto + shareable
  - Defis hebdos (2 slots via talent)
- V2:
  - Premium complet
  - Boutique cosmetique (skins/sons/animations)
  - Tirages au sort (raffle entries)
  - Challenges sponsorises
  - Battle Pass saisonnier (4 semaines)
- V3:
  - Amis
  - Classements (streak / score)
  - Guildes / team challenges

6.17 MVP scope exact - ecrans + etats + composants (Doc3)
6.17.1 Navigation MVP (4 onglets)
- Home, Progress, Talents, Profile
- Modals/flows: Onboarding, Edit plan, Night Result, Paywall (teaser), Reward Claim

6.17.2 Onboarding (Flow multi-ecrans)
- O1 Welcome: Logo + tagline, CTA "Start"
- O2 Goal picker: Cartes Routine / Energie / Productivite / Lever tot
- O3 Coach style: Chill / Hype / Strict + exemple phrase sous chaque choix
- O4 Plan: Bedtime (time picker), Wake time, Days active (chips),
  info: "tolerance 15 min au coucher / 20 min au reveil"
- O5 Gamification intro (3 slides): XP/Level/Talent points, Streak/bonus/rewards,
  Talents = pouvoirs + features
- O6 Ready: Resume plan, CTA "Go Home"

6.17.3 Home (3 etats)
- H1 Before Night:
  - Hero: Tonight's Quest (heure -> heure, duree)
  - Streak chip
  - Level + XP bar
  - CTA primary: PLAY
  - CTA secondary: Edit plan
  - Card: "Next reward preview" (ex: "2 wins -> Badge")
  - Card: "Daily chest available" (si matin)
- H2 Night Mode (in progress):
  - Animation calm
  - Timer
  - CTA: Hold-to-stop
  - Micro: "Do not disturb" suggestion (texte)
- H3 Post-stop "Claim":
  - Card: "Morning result ready"
  - CTA: View result

6.17.4 Night Result (ecran dedie)
- R1 Success:
  - Verdict + score ring
  - XP breakdown
  - Streak update
  - Reward claim (si milestone)
  - Coach message (personnalise)
  - CTA: Continue
- R2 Partial:
  - Verdict + score ring
  - "What happened" (ex: -22 min, coucher tard)
  - XP + streak (streak cassee ou non selon regles)
  - Suggestion (soft): "Try a smaller goal tonight"
  - CTA: Continue
- R3 Fail:
  - Verdict + score ring
  - XP minimal (anti churn)
  - Streak broken
  - Si shield dispo: "Use shield"
  - CTA: Continue

6.17.5 Progress (Historique)
- P1 Calendar month:
  - Month selector
  - Calendar grid (colors)
  - Legend
  - Tap day -> P2
- P2 Night detail (modal ou page):
  - Plan vs actual
  - Score
  - XP earned
  - Status
  - Coach note (1 phrase)
- P3 Weekly recap card (sur P1):
  - Total slept vs target
  - Sleep gained vs baseline
  - Best streak this week
  - Average score

6.17.6 Talents (Tree)
- T1 Talent Tree:
  - Points available
  - 4 sections (discipline / streak / style / insight)
  - Talent node cards (unlock)
  - Locked state (shows prereq)

6.17.7 Profile
- PR1 Profile main:
  - Avatar preset
  - Level + XP
  - Streak current + best streak
  - Badges grid (12)
  - Quick stats (wins, total nights)
  - Settings entry
- PR2 Settings:
  - Edit plan
  - Coach style
  - Notifications (MVP: toggle placeholder "coming soon" ou reel minimal)
  - Premium (teaser / paywall)
  - Support

6.17.8 Reward Claim (modal)
- RC1 Daily Chest:
  - Animation d'ouverture
  - Recompense (badge cosmetic token / sound preview)
  - CTA: Equip (si theme/sound) ou Ok
- RC2 Milestone Drop (teaser MVP, active V1.2):
  - "Rare drop incoming at 7 streak"

6.18 Talents inclus dans le MVP (Doc3)
- Discipline:
  - D1 Warm-up XP (1): +5 XP si start <= plan_start +10 min
  - D2 Clean Start (2): +10 XP si start dans +/- 15 min
  - D3 Perfect Bonus (3): +25 XP si SUCCESS + score >= 90
- Streak:
  - S1 Streak Booster I (1): +5% XP si streak >= 3
  - S2 Streak Shield (2): +1 charge/semaine (sauve une casse de streak)
  - S3 Streak Booster II (3): +10% XP si streak >= 7 (MVP = remplace S1)
- Style:
  - T1 Theme Slot (1): Debloque 1 theme (parmi 6)
  - T2 Sound Pack (2): Debloque 3 sons (sur 6)
  - T3 Rare Cosmetics (3): Augmente chances drop rare sur milestones (+20% luck)
- Insight:
  - I1 Weekly Recap (1): Active la carte "Weekly recap"
  - I2 Advanced Calendar (2): Affiche score par nuit + tags ("late/early")
  - I3 Trendline (3): Graph 30 jours (duree vs objectif)

6.19 Assets plan (Doc3)
6.19.1 Badges (12)
- #1 "First Quest" (Common): 1ere nuit terminee (peu importe statut) - ticket/quest scroll + lune
- #2 "Night Starter" (Common): 1 SUCCESS - bouton play stylise + etoiles
- #3 "On Time" (Common): 3 nuits avec start dans +/-15 min - horloge neon
- #4 "Early Bird" (Common): 3 reveils dans +/-10 min (meme si PARTIAL) - oiseau pixel + soleil pastel
- #5 "Streak 3" (Common): streak >= 3 - flamme x3
- #6 "Moonrunner" (Rare): 7 SUCCESS au total - personnage running sur croissant de lune
- #7 "Perfect Night" (Rare): 1 nuit score >= 90 (SUCCESS) - couronne + lune brillante
- #8 "Consistency" (Rare): 5 SUCCESS sur 7 derniers jours - calendrier checkmarks
- #9 "Streak 7" (Rare): streak >= 7 - flamme + halo neon
- #10 "Comeback" (Epic): SUCCESS le lendemain d'un FAIL - phenix lune
- #11 "Level 5" (Rare): atteindre niveau 5 - badge "LV5" arcade
- #12 "30 Club (Teaser)" (Epic): streak >= 30 (hors MVP) - medaillon diamant + 30

6.19.2 Themes (6)
- Theme 1 "Default Neon": debloque par defaut, noir + neon doux
- Theme 2 "Lavender Dream": T1 + niveau 2, violet/lavande dreamy
- Theme 3 "Matcha Night": T1 + niveau 4, vert matcha zen
- Theme 4 "Sunrise Pop": T1 + badge Early Bird, degrade sunrise energisant
- Theme 5 "Cyber Moon": T1 + streak 7, cyberpunk contraste
- Theme 6 "Cloudy Minimal": T1 + 10 SUCCESS, clair minimal aesthetic

6.19.3 Sons (6) (3 sleep + 3 wake)
- Sleep:
  - "Rain Lo-fi": T2 + niveau 3
  - "Ocean Hush": T2 + 5 SUCCESS
  - "Space Drone": T2 + badge Moonrunner
- Wake:
  - "Soft Chimes": T2 + niveau 3
  - "Hype Alarm (Cute)": T2 + streak 3
  - "Retro Arcade Ping": T2 + badge Level 5

6.20 Textes de coaching (bibliotheque exhaustive)
6.20.1 Regles de ton
- Chill: doux, empathique
- Hype: energique, "game"
- Strict: direct, mais pas culpabilisant
- Chaque resultat affiche:
  - Phrase principale
  - Feedback factuel
  - Prochaine action (micro conseil)
- Variables: {xp}, {streak}, {delta_start_min}, {delta_end_min}, {sleep_gained_week_min}, {next_milestone}

6.20.2 SUCCESS (12 variantes par style)
Chill:
  - "Nice. Tu t'es offert une vraie nuit."
  - "Ca, c'est du repos propre."
  - "Ton futur toi te remercie."
  - "Routine +1. Stress -1."
  - "Tu as respecte le plan. Simple et fort."
  - "Une nuit stable, c'est une victoire."
  - "Tu avances, tranquillement mais surement."
  - "Tu as garde le cap."
  - "Tu viens d'empiler du repos."
  - "Garde ce rythme, il te va bien."
  - "Ton corps adore ce genre de nuit."
  - "C'est exactement ca qu'on vise."
Hype:
  - "W. Quest cleared."
  - "Let's gooo. +{xp} XP."
  - "Streak power UP 🔥"
  - "Tu farmes du repos comme un pro."
  - "Clean run. Zero debat."
  - "GG. Niveau prochain bientot."
  - "Big win. On continue."
  - "C'est du gameplay parfait."
  - "Tu viens de securiser ta journee."
  - "Streak {streak}? Monster mode."
  - "Tu viens de debloquer de l'energie."
  - "Another one."
Strict:
  - "Objectif atteint. Bien."
  - "Plan respecte. Continue."
  - "C'est ca la constance."
  - "Bonne execution."
  - "Tu fais ce que tu dis."
  - "Resultat valide."
  - "Repete."
  - "Bonne discipline."
  - "Prochain objectif : pareil."
  - "Streak maintenu. Parfait."
  - "Nuit conforme."
  - "Tu progresses."
Feedback factuel (SUCCESS) - 6 variantes:
  - "Duree OK. Coucher dans la fenetre. Reveil dans la fenetre."
  - "Score {score}/100. Tu as respecte le timing."
  - "Tu es dans le bon rythme. Continue 3 jours pour stabiliser."
  - "{sleep_gained_week_min} min gagnees cette semaine."
  - "Streak {streak}. Prochain milestone : {next_milestone}."
  - "XP: {xp}. Points talent au prochain niveau."
Micro-conseils (SUCCESS) - 6:
  - "Demain : meme heure, meme victoire."
  - "Garde l'ecran loin 15 minutes avant."
  - "Hydrate-toi des le reveil."
  - "Si tu veux, avance le coucher de 10 minutes demain."
  - "Recompense-toi : choisis un theme/son."
  - "Ne change rien. La regularite gagne."

6.20.3 PARTIAL (12 variantes par style)
Chill:
  - "Pas parfait, mais tu avances."
  - "Tu t'es repose, meme si c'etait short."
  - "On garde l'elan."
  - "C'est une etape, pas un echec."
  - "Tu as fait une partie du travail."
  - "On ajuste, tranquille."
Hype:
  - "Almost. Next run = full clear."
  - "Tu etais proche. On optimise."
  - "Half win. On prend."
  - "Tu as limite la casse. Bien joue."
  - "Encore un effort pour le perfect."
  - "Next night: redemption arc."
Strict:
  - "Objectif partiellement atteint."
  - "Tu n'etais pas loin. Corrige."
  - "Le plan est bon. L'execution non."
  - "Ajuste l'heure. Recommence."
  - "Progression acceptable."
  - "Demain, on fait mieux."
Feedback factuel (PARTIAL) - 8 variantes:
  - "Tu as dormi {actual} / {target}. Il manque {missing}."
  - "Coucher decale de {delta_start_min} min."
  - "Reveil decale de {delta_end_min} min."
  - "Score {score}/100. Ca passe, mais pas optimal."
  - "Streak: {streak_after} (si conservee) / cassee (si cassee)."
  - "XP: {xp}. Un SUCCESS demain relance tout."
  - "Tu etais a {missing} min du SUCCESS."
  - "Grosse difference = reduire la cible ce soir de 15 min."
Micro-conseils (PARTIAL) - 8:
  - "Ce soir : vise +15 minutes seulement."
  - "Pose une 'heure ecran off'."
  - "Prepare le reveil: son doux, lumiere faible."
  - "Si tu te couches tard, garde le reveil fixe."
  - "Choisis une routine 5 minutes (eau, respiration)."
  - "Demain : start dans la fenetre +/-15 min."
  - "Gagne d'abord la regularite, puis la duree."
  - "Active un theme calme avant de lancer."

6.20.4 FAIL (12 variantes par style)
Chill:
  - "Nuit compliquee. Ca arrive."
  - "On ne juge pas. On repart."
  - "Le streak n'est pas toi."
  - "Une nuit n'annule pas tes progres."
  - "On respire. Demain est clean."
  - "Tu as encore le controle."
Hype:
  - "Reset, pas defeat."
  - "On a perdu la run, pas la game."
  - "Tomorrow: comeback."
  - "On analyse vite, on relance."
  - "Tu reviens plus fort."
  - "Next night = free win."
Strict:
  - "Objectif non atteint."
  - "Streak cassee. Repart."
  - "Pas grave. Mais corrige."
  - "Demain : execution simple."
  - "Reprends le plan."
  - "On repart a 1."
Feedback factuel (FAIL) - 8:
  - "Duree trop courte : {actual} / {target}."
  - "Decalage coucher : {delta_start_min} min."
  - "Decalage reveil : {delta_end_min} min."
  - "Score {score}/100. On est hors fenetre."
  - "Streak cassee a {streak_before}."
  - "XP minimal: {xp}. On garde la continuite d'usage."
  - "Demain : vise PARTIAL minimum."
  - "Si tu as un shield, tu peux sauver ta streak."
Micro-conseils (FAIL) - 10:
  - "Ce soir : objectif plus petit (-30 min)."
  - "Garde une seule regle : reveil fixe."
  - "Prepare ton coucher 30 minutes avant."
  - "Coupe cafe apres 14h (si tu en prends)."
  - "Pose le telephone hors du lit."
  - "Lance PLAY des que tu te poses (meme si tard)."
  - "Respiration 1 minute avant de dormir."
  - "Choisis un son 'sleep' doux."
  - "Demain : focus sur l'heure de coucher."
  - "Si c'est trop dur, on simplifie le plan."

6.20.5 Weekly Recap (templates)
- Structure:
  - Titre: "This week"
  - 3 stats:
    - "Slept: Xh / Yh target"
    - "Sleep gained: +{sleep_gained_week_min} min"
    - "Consistency: {success_count}/7 SUCCESS"
  - Highlight phrase
  - Next objective phrase
- Phrases highlight (12):
  - "Tu as gagne {sleep_gained_week_min} min cette semaine."
  - "Ton meilleur streak cette semaine: {best_streak}."
  - "Ta regularite s'installe."
  - "Tu as eu {perfect_count} nuits presque parfaites."
  - "Tu es revenu(e) apres une nuit dure. Solide."
  - "Tu as ameliore ton timing de coucher."
  - "Tu avances sur le plus important : la constance."
  - "Cette semaine etait stable. Continue."
  - "Ton reveil devient plus regulier."
  - "Tu as tenu le plan plus souvent que la semaine derniere."
  - "Le progres est reel."
  - "Tu construis une base."
- Next objective (8):
  - "Objectif : +1 SUCCESS de plus la semaine prochaine."
  - "Objectif : start dans la fenetre 3 fois."
  - "Objectif : garder le reveil fixe 5 jours."
  - "Objectif : viser une nuit score >= 85."
  - "Objectif : streak 3."
  - "Objectif : 7 jours d'affilee avec PLAY."
  - "Objectif : reduire l'ecart coucher de 15 min."
  - "Objectif : maintenir la duree, ameliorer l'heure."

6.21 MVP Paywall / Monetisation (teaser)
- MVP gratuit:
  - Core loop complete
  - 12 badges
  - 1 theme par defaut
  - Talents disponibles (cosmetique limite par T1/T2)
  - Recap hebdo accessible via talent I1
- Premium (teaser V1.2/V2):
  - 1 streak shield / semaine (en plus)
  - Stats avancees sans talent (ou talent booste)
  - Themes exclusifs + sons exclusifs
  - Suppression ads (si ajoutees)
  - "Share card premium frames"
