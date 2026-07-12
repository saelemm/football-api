/**
 * Package des Ports (Interfaces des Repositories) - Couche Domaine
 *
 * Ces interfaces définissent les contrats pour accéder à la persistance,
 * sans dépendre d'une technologie particulière (JPA, SQL, MongoDB, etc).
 *
 * ============================================================================
 * RESPONSABILITÉS DES PORTS
 * ============================================================================
 *
 * Les ports sont responsables de:
 * 1. CONTRAT: Définir le comportement attendu de la persistance
 * 2. ABSTRACTION: Isoler le domaine de la technologie de persistance
 * 3. TESTABILITÉ: Permettre des implémentations mock pour les tests
 *
 * ============================================================================
 * PRINCIPES
 * ============================================================================
 *
 * - Les ports sont des interfaces
 * - Pas d'implémentation dans le domaine
 * - Les implémentations sont dans les adapters (adapter-persistence-jpa/)
 * - Les use cases dépendent des ports, pas des implémentations
 *
 * ============================================================================
 * EXEMPLE D'UTILISATION
 * ============================================================================
 *
 * Dans TransferPlayerUseCase:
 *
 * public class TransferPlayerUseCaseImpl implements TransferPlayerUseCase {
 *     private final IPlayerRepository playerRepository;
 *     private final ITeamRepository teamRepository;
 *
 *     @Override
 *     public void execute(Id<Long> playerId, ...) {
 *         Player player = playerRepository.findById(playerId);  // Port
 *         Team sourceTeam = teamRepository.findById(sourceTeamId);  // Port
 *         Team targetTeam = teamRepository.findById(targetTeamId);  // Port
 *
 *         // Orchestration
 *         // ...
 *
 *         playerRepository.save(updatedPlayer);  // Port
 *         teamRepository.save(updatedSourceTeam);  // Port
 *         teamRepository.save(updatedTargetTeam);  // Port
 *     }
 * }
 */
package port;

