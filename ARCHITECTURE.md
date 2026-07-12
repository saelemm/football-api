# Architecture

Le projet suit une architecture hexagonale simple: le metier au centre, les details techniques autour.

## Vue rapide

- `football-domain` contient le coeur metier (entites, regles, ports, interfaces usecase).
- `football-application` implemente les usecases et orchestre les ports.
- `football-adapter-rest` expose les usecases en HTTP.
- `football-adapter-persistence-jpa` branche les ports de persistence sur PostgreSQL/JPA.
- `football-bootstrap` assemble le tout et demarre Spring Boot.

## Sens des dependances

- `domain` ne depend de personne.
- `application` depend de `domain`.
- `adapter-rest` depend de `domain` (et est active via Spring dans le bootstrap).
- `adapter-persistence-jpa` depend de `domain`.
- `bootstrap` depend de tous les modules pour construire l'application runnable.

En pratique: les adapters dependent du metier, jamais l'inverse.

