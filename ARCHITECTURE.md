# Architecture

Le projet suit une architecture hexagonale simple: le metier au centre, les details techniques autour.

Le coeur gère les regles metier, les adapters REST et JPA ne font que brancher l'exterieur.

## Vue rapide

- `football-domain` contient le coeur metier (entites, regles, ports, interfaces usecase).
- `football-application` implemente les usecases et orchestre les ports.
- `football-adapter-rest` expose les usecases en HTTP.
- `football-adapter-persistence-jpa` branche les ports de persistence sur PostgreSQL/JPA.
- `football-bootstrap` assemble le tout et demarre Spring Boot.

## Fonctionnalites principales

- gestion des equipes et des joueurs
- transfert de joueurs avec retour du detail du transfert
- recrutement et changement titulaire / remplaçant
- liste des équipes et des joueurs avec pagination et tri
- historique des transferts d'une équipe, en entrée et en sortie

## Sens des dépendances

- `domain` ne dépend de personne.
- `application` dépend de `domain`.
- `adapter-rest` dépend de `domain` (et est activé via Spring dans le bootstrap).
- `adapter-persistence-jpa` dépend de `domain`.
- `bootstrap` dépend de tous les modules pour construire l'application runnable.

En pratique: les adapters dépendent du métier, jamais l'inverse.

Les valeurs de tri et de pagination sont valides au niveau REST puis traduites vers la persistence.

