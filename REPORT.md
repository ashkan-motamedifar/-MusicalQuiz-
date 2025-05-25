# MusicalQuiz – Rapport Final de l'Application Android

## 1. Vue d'ensemble fonctionnelle

MusicalQuiz est une application Android qui allie la découverte musicale à l’apprentissage ludique. Développée avec Kotlin et les bibliothèques Jetpack, elle permet aux utilisateurs de :

* Rechercher des morceaux via l’API Deezer
* Consulter les détails d’un morceau, incluant une image haute résolution et un extrait audio
* Créer et gérer plusieurs playlists personnalisées localement via Room DB
* Jouer à des quiz musicaux générés à partir des morceaux des playlists (titre, album ou pochette)

### 1.1 Fonctionnalités principales

* **Recherche** : Entrée d’une requête textuelle, récupération des résultats via Retrofit, affichage dans une grille avec images et titres.
* **Détails** : Affichage des détails complets du morceau (titre, artiste, couverture), lecture d’un extrait via MediaPlayer.
* **Playlists multiples** :

  * Bouton "Créer une playlist" accessible en haut de l’écran Playlist.
  * Sélection ou création d’une playlist lors du long-appui sur un morceau dans l’écran Recherche.
  * Consultation d'une playlist avec les morceaux ajoutés, possibilité de les lire ou de les supprimer individuellement.
* **Quiz musical** :

  * Choix automatique de playlists contenant au moins 5 morceaux.
  * Quiz généré selon le **titre**, **album** ou **pochette de couverture** du morceau.

### 1.2 Captures d'écran

À inclure dans le dépôt Git sous `/screenshots` :

* Écran de recherche
* Écran de détails
* Liste des playlists
* Vue d’une playlist avec options de lecture/suppression
* Écran de quiz en cours

## 2. Architecture technique et implémentation

### 2.1 Vue d'ensemble

Architecture MVVM avec une seule activité et navigation via le `NavigationComponent`.

* **Activity principale** : MainActivity héberge un `NavHostFragment`.
* **Fragments** : SearchFragment, DetailsFragment, PlaylistFragment, QuizFragment.
* **Navigation** : Menu de navigation en bas avec 4 sections.

### 2.2 Technologies utilisées

* **Langage** : Kotlin
* **Frameworks** : Android Jetpack (ViewModel, LiveData, Navigation, Room)
* **API** : Deezer avec Retrofit + Gson
* **Multimédia** : MediaPlayer
* **UI** : Glide, RecyclerView, ConstraintLayout

### 2.3 Diagramme de flux

```
[Entrée utilisateur] -> [SearchFragment] -> [ViewModel] -> [Repository] -> [API Deezer]
                                                           ↓
                                               [LiveData] -> [RecyclerView UI]

Long-press → [Popup choix de playlist] → [Insertion en BD Room]
PlaylistFragment → [Room DB] → [PlaylistWithTracks]

Quiz → [QuizViewModel] → [Tracks DB] → [Questions aléatoires (titre / album / pochette)]
```

### 2.4 Modélisation base de données (Room)

```plaintext
@Database(entities = [Playlist, TrackEntity, PlaylistTrackCrossRef], ...)

Entity: Playlist
Entity: TrackEntity
Entity: PlaylistTrackCrossRef (relation many-to-many)
Data Class: PlaylistWithTracks (@Relation)
```

### 2.5 Étapes clés de l'implémentation

#### Recherche

* Saisie texte + bouton de recherche
* Appel à l'API Deezer via Retrofit
* Affichage via RecyclerView
* Long-appui ouvre une boîte de dialogue pour choisir/nommer une playlist

#### Détails

* Passage des données via Bundle
* Chargement d’image via Glide
* Lecture audio via MediaPlayer et `prepareAsync()`

#### Playlists

* Création de playlists via bouton + boîte de dialogue
* Liaison morceau-playlist via entité croisée
* RecyclerView imbriquée : playlists + morceaux
* Lecture et suppression disponibles pour chaque piste

#### Quiz

* Lancement uniquement si playlist ≥ 5 morceaux
* Choix aléatoire des questions (titre, album, couverture)
* Suivi du score et affichage des résultats finaux

## 3. Problèmes rencontrés et limitations

### 3.1 Résolution de bugs

* Blocage ANR : `MediaPlayer.prepare()` bloquait l’UI. Résolu avec `prepareAsync()`.
* Crashes LiveData : Observations sur des données nulles corrigées avec vérifications et initialisation.

### 3.2 Limitations actuelles

* Pas encore de résumé de quiz ou de retour après le jeu
* Pas de tri manuel ni d’édition de morceaux
* L'UI pourrait être améliorée pour différents formats d'écran

## 4. Conclusion et perspectives

L’application MusicalQuiz démontre une intégration réussie entre une API musicale, une base de données locale et une interface utilisateur interactive. Elle permet une expérience complète de découverte musicale, de personnalisation et d’apprentissage.

### Prochaines améliorations

* Résumé de quiz + retour utilisateur
* Réorganisation des pistes dans les playlists
* Amélioration de l’interface et support multi-langue
* Notifications et sauvegarde dans le cloud

---
Université de Strasbourg – Programmation Mobile – Mai 2025
