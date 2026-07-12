# football-bootstrap

Point d'entrée Spring Boot de l'application (`com.foot.bootstrap.FootballBootstrapApplication`).

## Rôle

Ce module assemble :

- `football-domain`
- `football-application`
- `football-adapter-persistence-jpa`
- `football-adapter-rest`

## Configuration

Variables d'environnement supportées :

- `DB_URL` (défaut: `jdbc:postgresql://localhost:5432/football`)
- `DB_USERNAME` (défaut: `postgres`)
- `DB_PASSWORD` (défaut: `postgres`)
- `SERVER_PORT` (défaut: `8080`)

## Lancement

```bash
./mvnw -pl football-bootstrap -am spring-boot:run
```

```bash
./mvnw -pl football-bootstrap -am package -DskipTests
java -jar football-bootstrap/target/football-bootstrap-0.0.1-SNAPSHOT.jar
```
