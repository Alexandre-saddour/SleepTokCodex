# Fonctionnalites Manquantes - SleepTokCodex

**Genere le:** 2026-01-29
**Version actuelle du code:** V1-MVP (en cours de finalisation)
**Derniere etape completee:** M13.2 (Domain and Data tests)
**Prochaine etape:** M13.3 (UI sanity checks)

---

## Resume de la Version Actuelle

L'application est actuellement au niveau **V1-MVP**. La quasi-totalite des fonctionnalites MVP sont implementees:
- Core loop complet (planifier, dormir, resultat, recompense)
- Systeme XP, niveaux, streak
- Arbre de talents (4 branches, 12 talents)
- Calendrier de progression + recap hebdo
- Profil avec badges et stats
- Daily Chest (coffre quotidien)

Il reste les tests (M13) avant de pouvoir considerer le MVP comme termine.

---

## 1. Fonctionnalites MVP Non Completees

### 1.1 Share Card (Teaser)
**Statut:** PARTIELLEMENT IMPLEMENTE (TEASER UNIQUEMENT)
**Spec:** "Share Card (desactive mais UI prete)"

Le bouton "Share" est present et desactive dans l'ecran Night Result, avec un message "Coming soon".

**Reste a faire (V1.1+):**
- Generation d'image de la carte de resultat
- Integration partage natif (Android/iOS)

### 1.2 Tests (M13)
**Statut:** EN COURS
**Steps completes:**
- M13.1: Tests domaine (formules XP, score, streak) ✓
- M13.2: Tests data (seed mappers, seed models parsing) ✓
**Steps manquants:**
- M13.3: Tests UI (QA manuel - onboarding, night flow, etc.)

---

## 2. Fonctionnalites V1.1 (Non Implementees)

### 2.1 Notifications Intelligentes
**Statut:** PLACEHOLDER UNIQUEMENT
**Spec:**
- Notification de coucher
- Notification de reveil
- Notification "claim morning" (resultat pret)

**Actuel:**
- Le switch dans Settings est desactive
- Aucune logique de notification implementee
- Pas de service de notification cote Android/iOS

**A implementer:**
- Service de notifications multiplateforme
- Planification des notifications basee sur le plan sommeil
- Gestion des permissions notifications
- Notification push "Resultat du matin disponible"

### 2.2 Share Card Active
**Statut:** NON IMPLEMENTE
**Spec:**
- Share Card story-ready
- Partage vers Instagram/TikTok stories
- Template visuel de la carte de resultat

**A implementer:**
- Generation d'image de la carte de resultat
- Integration partage natif (Android/iOS)
- 3 styles de templates (V1.2)

### 2.3 Daily Chest Enrichi
**Statut:** PARTIELLEMENT IMPLEMENTE
**Spec:**
- Loot cosmetique leger
- Animation d'ouverture

**Actuel:**
- Logique de claim basique implementee
- Attribution deterministe (premiere recompense non possedee)

**Manquant:**
- Systeme de drop aleatoire avec rarete
- Animation d'ouverture du coffre
- Variete dans les recompenses (tokens, etc.)

### 2.4 Challenges/Defis Simples (1 slot)
**Statut:** NON IMPLEMENTE
**Spec:**
- 1 slot de defi actif
- Defis quotidiens/hebdomadaires
- Bonus XP pour defis reussis (+50-150 XP)

**A implementer:**
- Modele domaine: Challenge, UserChallenge
- Entites Room: ChallengeEntity, UserChallengeEntity
- ChallengeRepository
- Use cases: GetActiveChallenge, CompleteChallenge
- UI: Affichage du defi actif sur Home
- Seed data: Liste de defis

### 2.5 Streak Shield Charge Hebdomadaire
**Statut:** PARTIELLEMENT IMPLEMENTE
**Spec:**
- Si talent S2 debloque: +1 charge/semaine
- Refresh automatique hebdomadaire

**Actuel:**
- Le talent S2 "Streak Shield" existe
- StreakShieldRepository existe
- Logique de base pour utiliser un shield

**Manquant:**
- Refresh automatique des charges chaque semaine
- Logique de detection du debut de semaine
- UI pour voir les charges disponibles

### 2.6 Sons Pack #1 (Playback)
**Statut:** DONNEES UNIQUEMENT
**Spec:**
- 3 sons sleep + 3 sons wake
- Playback reel des sons

**Actuel:**
- 6 sons definis dans seed_rewards.json
- RewardType.SOUND existe
- Talent T2 "Sound Pack" debloque des sons

**Manquant:**
- Fichiers audio reels
- Service audio multiplateforme
- UI de selection/preview des sons
- Integration avec Night Mode (son de reveil)

---

## 3. Fonctionnalites V1.2 (Non Implementees)

### 3.1 Templates de Partage (3 styles) + Stickers
- 3 styles visuels differents pour les share cards
- Stickers/decorations personnalisables
- Export haute qualite pour stories

### 3.2 Rare Drops Actives (7/14/30)
**Statut:** TEASER/DATA UNIQUEMENT
**Spec:**
- Drops rares automatiques aux milestones de streak
- Probabilite augmentee par talent T3 (+20% luck)

**Actuel:**
- TalentEffect.RareDropLuck defini mais jamais utilise
- RewardRarity (COMMON/RARE/EPIC) existe
- Aucune logique de drop aleatoire

**A implementer:**
- Algorithme de selection aleatoire basee sur rarete
- Integration du bonus luck du talent T3
- Triggers aux milestones (streak 7/14/30)
- Animation de drop rare
- UI de revelation du loot

### 3.3 Weekly Recap Auto + Shareable
- Generation automatique du recap chaque lundi
- Notification de recap disponible
- Export shareable du recap

### 3.4 Defis Hebdos (2 slots via talent)
- Extension du systeme de challenges
- Talent additionnel pour debloquer 2 slots
- Rotation hebdomadaire des defis

---

## 4. Fonctionnalites V2 (Non Implementees)

### 4.1 Premium Complet
**Statut:** MODELE DE DONNEES UNIQUEMENT
**Spec:**
- 4,99EUR/mois
- Stats avancees
- Protection de streak
- XP boost
- Themes exclusifs
- Recompenses premium
- Suppression pubs

**Actuel:**
- PremiumStatus enum existe (NONE, TRIAL, ACTIVE, CANCELED)
- User.premiumStatus et User.premiumUntil existent

**A implementer:**
- Integration paiement (Google Play / App Store)
- Validation des achats
- Feature gating basee sur premium
- Restauration des achats
- UI du paywall
- Gestion des abonnements

### 4.2 Boutique Cosmetique
- Skins (1-3EUR)
- Sons (0,99EUR)
- Themes exclusifs
- XP boost payants
- UI de boutique
- Monnaie virtuelle optionnelle

### 4.3 Microtransactions
- Sauver un streak: 0,99EUR
- Achats in-app ponctuels
- Integration paiement

### 4.4 Tirages au Sort (Raffle Entries)
- Systeme d'entrees pour tirages
- Tirages periodiques avec recompenses physiques
- UI de participation

### 4.5 Challenges Sponsorises
- Defis de marques partenaires
- Recompenses speciales
- Tracking et analytics

### 4.6 Battle Pass Saisonnier (4 semaines)
- Systeme de saison avec duree limitee
- Recompenses exclusives par tier
- Track gratuit vs premium
- UI de progression saisonniere
- Reset entre saisons

### 4.7 Publicites Optionnelles
- Doubler XP via ad
- Sauver streak via ad
- Debloquer animation via ad
- Integration ad SDK (AdMob, etc.)

### 4.8 Recompenses Physiques
- Codes promo
- Produits sommeil
- Abonnements partenaires
- Integration avec partenaires

### 4.9 Events
- Events temporaires
- Recompenses limitees dans le temps
- Calendrier d'events

### 4.10 Classements
- Leaderboard streak
- Leaderboard score
- Classement hebdomadaire/mensuel

---

## 5. Fonctionnalites V3 (Non Implementees)

### 5.1 Amis
- Ajout d'amis
- Liste d'amis
- Voir les stats des amis
- Comparaison de progression

### 5.2 Guildes / Teams
- Creation de guildes
- Rejoindre une guilde
- Objectifs collectifs
- Chat de guilde

### 5.3 Defis Entre Amis
- Creer un defi
- Inviter des amis
- Tracking du defi
- Recompenses de groupe

### 5.4 Social Boost
- Bonus XP si ami actif (+5% par ami actif)
- Talent "Social Boost" (V1.5+)

---

## 6. Fonctionnalites Mineures Manquantes

### 6.1 Comparaisons Utilisateurs
**Spec:** "Tu es plus regulier que 78% des utilisateurs"
**Statut:** NON IMPLEMENTE
- Necessite backend/analytics
- Comparaison anonymisee

### 6.2 Export / Delete Account
**Spec:** "Export / delete account (si besoin legal)"
**Statut:** NON IMPLEMENTE
- Export des donnees utilisateur
- Suppression complete du compte (GDPR)

### 6.3 Support / Feedback
**Spec:** Dans Settings
**Statut:** NON IMPLEMENTE
- Lien vers support
- Formulaire de feedback
- Integration email/ticketing

### 6.4 "Tonight Bonus" Dynamique
**Spec:** "+10% XP si tu lances avant 00:00"
**Statut:** A VERIFIER
- Bonus XP si start avant minuit
- Affichage du bonus sur Home

### 6.5 Tags "late/early" sur Calendrier
**Spec:** Talent I2 "Advanced Calendar" affiche tags
**Statut:** A VERIFIER
- Tags visuels pour nuits tardives/precoces
- Necessite talent I2 debloque

### 6.6 Graph Trendline 30 jours
**Spec:** Talent I3 "Trendline"
**Statut:** A VERIFIER
- Graphique duree vs objectif sur 30 jours
- Necessite talent I3 debloque

---

## 7. Resume par Priorite

### Haute Priorite (MVP a finaliser)
1. Tests domaine/data/UI (M13)

### Priorite Moyenne (V1.1)
1. Notifications intelligentes
2. Challenges simples (1 slot)
3. Rare drops actifs avec probabilites
4. Playback audio des sons

### Basse Priorite (V1.2+)
1. Share Card complet + templates
2. Premium et monetisation
3. Boutique
4. Battle Pass

### Future (V2/V3)
1. Social features
2. Guildes
3. Classements
4. Recompenses physiques/partenariats

---

## 8. Roadmap Reference (de specs.rtf)

| Version | Features |
|---------|----------|
| **V1-MVP** | Core sleep, XP, Streak, Talents, Daily Chest |
| **V1.1** | Notifications, Share Card, Challenges (1 slot), Sons |
| **V1.2** | Templates partage, Rare drops actifs, Weekly Recap shareable, Defis (2 slots) |
| **V2** | Premium complet, Boutique, Tirages, Battle Pass, Events, Classements |
| **V3** | Amis, Guildes, Defis entre amis |

---

## Notes Techniques

### Dependencies a Ajouter (V1.1+)
- Service de notifications multiplateforme
- SDK audio/media player
- Integration partage natif
- SDK paiement (V2)
- SDK publicite (V2)
- Backend pour classements/social (V3)

### Architecture a Etendre
- NotificationService (expect/actual)
- AudioService (expect/actual)
- ShareService (expect/actual)
- PaymentRepository (V2)
- SocialRepository (V3)
- RemoteDataSource (V2/V3)
