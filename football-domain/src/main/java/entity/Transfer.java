package entity;

import Errors.TransferNotAllowedException;
import Validator.NullValidator;

import java.util.Date;

import static Errors.ErrorMessages.*;

/**
 * Transfer - Entité du domaine représentant l'historique d'un transfert de joueur
 *
 * Comportement métier encapsulé:
 * - Validation de création: équipes différentes, champs requis
 */
public record Transfer(TransferId transferId,
                       PlayerId playerId,
                       TeamId sourceTeamId,
                       TeamId targetTeamId,
                       Price transferPrice,
                       Date transferDate) {

    public Transfer {
        NullValidator.requireNonNull(playerId, ID_NULL);
        NullValidator.requireNonNull(targetTeamId, ID_NULL);
        NullValidator.requireNonNull(transferPrice, PRIX_NULL);

        // Les équipes source et cible doivent être différentes
        if (sourceTeamId != null &&
            sourceTeamId.value().equals(targetTeamId.value())) {
            throw new TransferNotAllowedException("L'équipe source et cible doivent être différentes");
        }
    }
}
