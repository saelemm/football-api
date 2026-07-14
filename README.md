# football-api

API de gestion d'equipes, joueurs et transferts, structuree en architecture hexagonale (multi-modules Maven).

## Modules

- `football-domain` : modeles metier, regles, ports, interfaces usecase.
- `football-application` : implementations des usecases (orchestration metier).
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
- `POST /api/teams` : creation d'une equipe
- `POST /api/players/recruit` : recrutement d'un joueur
- `POST /api/players/transfer` : transfert d'un joueur
- `PATCH /api/teams/{teamId}/players/titularisation/swap` : permutation titulaire/remplaçant
- `PATCH /api/players/{playerId}/performance` : mise a jour performance
- `PATCH /api/players/{playerId}/price` : mise a jour prix

## Prerequis

- Java 21
- Docker (pour les tests Testcontainers JPA)
- PostgreSQL (pour execution locale applicative)

## Commandes utiles

```bash
./mvnw test
```

```bash
./mvnw -pl football-bootstrap -am spring-boot:run
```

## Demarrage rapide Docker Compose

### Stack complete en Docker

Le `docker compose up --build -d` suffit pour demarrer :

- PostgreSQL
- l'application `football-bootstrap`
- l'execution automatique des migrations Flyway
- le schema et les donnees de base
- l'exposition HTTP sur `8080`

```bash
docker compose down -v
docker compose up --build -d
```

Swagger UI sera disponible sur:

- `http://localhost:8080/swagger-ui/index.html`

Arret:

```bash
docker compose down
```

Arret + suppression volume PostgreSQL (reset complet des donnees):

```bash
docker compose down -v
```

Voir aussi `ARCHITECTURE.md` pour la vue d'ensemble.

