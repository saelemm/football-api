package usecase;

import entity.PlayerId;
import Errors.PlayerNotFoundException;

/**
 * Use Case pour mettre à jour la performance d'un joueur
 *
 * Respecte l'architecture hexagonale:
 * - Orchestre l'entité Player du domaine
 * - Valide que la note est dans la plage valide
 * - Persiste les changements
 */
public interface UpdatePlayerPerformanceUseCase {

    /**
     * Met à jour la note de performance d'un joueur
     *
     * Orchestration:
     * 1. Récupère le joueur
     * 2. Crée une nouvelle note validée
     * 3. Appelle Player.updatePerformance()
     * 4. Persiste le joueur mis à jour
     *
     * @param playerId ID du joueur
     * @param newPerformance Nouvelle note de performance (0-10)
     *
     * @throws PlayerNotFoundException si le joueur n'existe pas
     * @throws IllegalArgumentException si la note n'est pas valide
     */
    void execute(PlayerId playerId, Float newPerformance);
}

