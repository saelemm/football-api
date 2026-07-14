# football-api

API de gestion d'équipes, joueurs et transferts, structurée en architecture hexagonale (multi-modules Maven).

## Prérequis

- Java 21
- Docker (pour les tests Testcontainers JPA), Docker Desktop recommandé : https://www.docker.com/products/docker-desktop/
- PostgreSQL (pour exécution locale applicative)

## Démarrage rapide Docker Compose

### Stack complète en Docker

Le `docker compose up --build -d` suffit pour démarrer :

- PostgreSQL
- l'application `football-bootstrap`
- l'exécution automatique des migrations Flyway
- le schéma et les données de base
- l'exposition HTTP sur `8080`

```bash
docker compose down -v
docker compose up --build -d
```

Swagger UI sera disponible pour tester l'application via l'URL suivante :

- `http://localhost:8080/swagger-ui/index.html`

Arrêt:

```bash
docker compose down
```

Arrêt + suppression volume PostgreSQL (reset complet des données):

```bash
docker compose down -v
```

## Modules

- `football-domain` : modèles métier, règles, ports, interfaces usecase.
- `football-application` : implémentations des usecases (orchestration métier).
- `football-adapter-persistence-jpa` : implementation des ports via Spring Data JPA.
- `football-adapter-rest` : exposition HTTP des usecases.
- `football-bootstrap` : point d'entree Spring Boot.

## Fonctionnalites

- creation / consultation d'equipes et de joueurs
- transfert de joueurs avec retour des details du transfert
- changement de titulaire / remplaçant
- historique des transferts par equipe, entree / sortie
- pagination et tri sur les listes d'equipes, joueurs et transferts
- API documentee via Swagger UI

## Endpoints

- `GET /api/teams` : liste des equipes
- `GET /api/teams/{teamId}` : detail d'une equipe
- `GET /api/teams/{teamId}/players` : joueurs de l'equipe
- `GET /api/teams/{teamId}/players/starters` : titulaires
- `GET /api/teams/{teamId}/players/substitutes` : remplaçants
- `GET /api/teams/{teamId}/transfers` : historique complet
- `GET /api/teams/{teamId}/transfers/incoming` : transferts entrants
- `GET /api/teams/{teamId}/transfers/outgoing` : transferts sortants
- `POST /api/teams` : création d'une équipe
- `POST /api/players/recruit` : recrutement d'un joueur
- `POST /api/players/transfer` : transfert d'un joueur
- `PATCH /api/teams/{teamId}/players/titularisation/swap` : permutation titulaire/remplaçant
- `PATCH /api/players/{playerId}/performance` : mise à jour performance
- `PATCH /api/players/{playerId}/price` : mise à jour prix

## Load des collections postgres

Au démarrage de l'application, les collections PostgreSQL sont initialisées via Flyway avec un jeu de données de base (équipes et joueurs).
On injecte les données via le versioning flyway en SQL dans le répertoire `src/main/resources/db/migration`.
5 équipes sont créées avec 15 joueurs pour un total de 75 joueurs. Les transferts sont injectés dans la V3.
Le script V4 modifie les tables pour rajouter la gestion de version des joueurs et des transferts.

## Compilation maven

La compilation Maven n'est pas nécessaire pour démarrer l'application, 
elle sert uniquement à s'assurer que l'ensemble des tests passent et que l'application compile sans erreurs.

```bash
./mvnw test
```

```bash
./mvnw -pl football-bootstrap -am spring-boot:run
```

## Temps passé sur le projet

Première soirée : 2h30 (vendredi 10 juillet 2026)
- Etudes des technologues nécessaires pour le projet.
- Etudes de l'architecture hexagonale, mise en place du projet, création des modules, configuration Maven, configuration Spring Boot.

Première journée : 5h (samedi 11 juillet 2026)
- Création des tables SQL sans Flyway dans un premier temps pour valider la structure des entités.
- Implémentation des domain entity + tests unitaires
- Création des usecases + ports
- Création des entity JPA + tests unitaires
- Design + implémentation des endpoints REST
- Mise en place de l'image docker + docker compose pour PostgreSQL avec le bootstrap de l'application

Deuxième journée : 4h (dimanche 12 juillet 2026)
- Implémentation des usecases + tests unitaires
- Implémentation des ports dans la couche de persistence + tests unitaires
- Premier déploiement de l'application avec docker compose, tests avec le swagger

Troisième journée : 5h (lundi 13 juillet 2026)
- Ajout des données dans flyway
- Injection de jeux de données de test dans flyway
- Ajustement des endpoints et cas d'usages
- Tests d'intégration, corrections de bugs, tests de bout en bout.
- Beaucoup de refactoring pour améliorer la structure du code, la lisibilité et la maintenabilité.
- Ajout du versioning des entités pour gérer les transferts et les performances des joueurs.

Quatrième journée : 4h (mardi 14 juillet 2026)
- Ajout de la pagination et du tri sur les endpoints REST
- Finalisation des tests, ajustement de la documentation, correction de bugs
- Finalisation du rendu

Voir aussi `ARCHITECTURE.md` pour la vue d'ensemble.

