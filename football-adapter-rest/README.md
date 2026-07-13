# football-adapter-rest

Adapter REST Spring MVC qui expose les use cases du domaine via `/api`.

## Endpoints par controller

### `TeamController`

- `GET /api/teams`
- `POST /api/teams`
- `GET /api/teams/{teamId}`
- `GET /api/teams/{teamId}/players`
- `GET /api/teams/{teamId}/players/starters`
- `GET /api/teams/{teamId}/players/substitutes`
- `PATCH /api/teams/{teamId}/players/titularisation/swap`

### `PlayerController`

- `GET /api/players/{playerId}`
- `POST /api/players/recruit`
- `POST /api/players/transfer`
- `PATCH /api/players/{playerId}/performance`

### `TransferController`

- `GET /api/teams/{teamId}/transfers`
- `GET /api/teams/{teamId}/transfers/incoming`
- `GET /api/teams/{teamId}/transfers/outgoing`

## Notes

- Les erreurs métier/validation sont renvoyées en `400` avec un payload JSON standard (`timestamp`, `status`, `message`, `path`).
- Ce module contient les controllers, DTOs, mapper REST et gestionnaire d'erreurs.
