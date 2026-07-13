package usecase;

import Errors.PlayerNotFoundException;
import entity.PlayerId;

import java.math.BigDecimal;

/**
 * Use case pour mettre à jour le prix d'un joueur
 *
 * - Orchestres l'entité Player du domaine
 * - Valide que le nouveau prix n'est pas négatif
 * - Persiste les changements
 *
 */
public interface UpdatePlayerPriceUseCase {


    /**
     * Met à jour le prix d'un joueur en additionnant le prix fournis
     *
     * @param playerId ID du joueur
     * @param addedValue Valeure ajoutée ou retirée du joueur
     *
     * @throws PlayerNotFoundException si le joueur n'existe pas
     * @throws IllegalArgumentException si le nouveau prix est négatif
     */
    void execute(PlayerId playerId, BigDecimal addedValue);
}
