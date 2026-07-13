# football-api

API de gestion d'equipes, joueurs et transferts, structuree en architecture hexagonale (multi-modules Maven).

## Modules

- `football-domain` : modeles metier, regles, ports, interfaces usecase.
- `football-application` : implementations des usecases (orchestration metier).
- `football-adapter-persistence-jpa` : implementation des ports via Spring Data JPA.
- `football-adapter-rest` : exposition HTTP des usecases.
- `football-bootstrap` : point d'entree Spring Boot.

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
- l'execution automatique des migrations Flyway `V1`, `V2`, `V3`
- l'injection des donnees de base
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

