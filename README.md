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

Voir aussi `ARCHITECTURE.md` pour la vue d'ensemble.

