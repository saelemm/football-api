/**
 * Package des Use Cases (Application Services) - Couche Application
 *
 * Ces interfaces définissent les cas d'usage du domaine football en respectant l'architecture hexagonale et le DDD.
 *
 * ============================================================================
 * RESPONSABILITÉS DES USE CASES
 * ============================================================================
 *
 * Les use cases sont responsables de:
 * 1. ORCHESTRATION: Coordonner les entités du domaine pour réaliser un cas d'usage
 * 2. VALIDATION MÉTIER: Vérifier les règles métier avant d'appeler le domaine
 * 3. PERSISTANCE: Utiliser les repositories (ports) pour charger/sauvegarder
 * 4. TRANSACTIONS: Gérer les transactions si nécessaire
 * 5. QUERIES: Récupérer et filtrer les données (role du USE CASE, pas du domaine)
 *
 * ============================================================================
 * EXEMPLES DE USE CASES
 * ============================================================================
 *
 * Cas d'usage COMMANDS (modification):
 * - TransferPlayerUseCase: Transfert d'un joueur entre deux équipes
 * - RecruitPlayerUseCase: Recrutement d'un nouveau joueur
 * - CreateTeamUseCase: Création d'une nouvelle équipe
 * - UpdatePlayerPerformanceUseCase: Mise à jour de la note de performance
 *
 * Cas d'usage QUERIES (lecture):
 * - GetTeamDetailsUseCase: Récupérer les détails d'une équipe
 * - GetPlayerDetailsUseCase: Récupérer les détails d'un joueur
 * - GetTeamTransferHistoryUseCase: Récupérer l'historique des transferts
 *
 * ============================================================================
 * FLUX D'EXÉCUTION D'UN USE CASE
 * ============================================================================
 *
 * Exemple avec TransferPlayerUseCase:
 *
 * 1. Controller reçoit une requête HTTP
 * 2. Controller appelle TransferPlayerUseCase.execute()
 * 3. Use Case charge les entités via les repositories
 * 4. Use Case valide les règles métier
 * 5. Use Case appelle les méthodes du domaine:
 *    - Player.canBeTransferred()
 *    - Team1.hasEnoughBudget()
 *    - Team2.hasEnoughBudget()
 * 6. Use Case crée un Transfer (l'orchestration)
 * 7. Use Case appelle:
 *    - Team1.removePlayer(playerId, price, transfer)
 *    - Team2.addPlayer(playerId, price, transfer)
 * 8. Use Case appelle les repositories pour sauvegarder
 * 9. Controller retourne la réponse HTTP
 *
 * ============================================================================
 * RESPECT DE L'ARCHITECTURE
 * ============================================================================
 *
 * Domaine (entity/):
 * - AUCUNE orchestration
 * - AUCUNE dépendance vers les repositories
 * - AUCUNE query/filtrage (les getters filtrants sont retirés)
 * - Logique métier pure encapsulée
 * - Comportements métier: addPlayer(), removePlayer(), canBeTransferred(), etc.
 *
 * Use Cases (usecase/):
 * - Orchestration des entités du domaine
 * - Dépendances vers les repositories (ports)
 * - Crée les Transfer et Event Domain si nécessaire
 * - FAIT les queries via les repositories, pas via les entités
 *
 * Adapters (adapter-rest/, adapter-persistence-jpa/):
 * - Implémentations des use cases
 * - Implémentations des repositories
 * - Traduction entre domaine et technologie
 */
package usecase;

