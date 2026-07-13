package usecase;

import Errors.PlayerNotFoundException;
import Errors.TitularisationNotAllowedException;
import entity.PlayerId;
import entity.TeamId;

/**
 * Use Case pour échanger la titularisation entre deux joueurs d'une même équipe.
 *
 * Le joueur sortant perd sa titularisation et devient transférable,
 * le joueur entrant devient titulaire.
 */
public interface SwapPlayerTitularisationUseCase {

    /**
     * Échange la titularisation entre un joueur titulaire et un joueur non titulaire.
     *
     * @param teamId ID de l'équipe concernée
     * @param titulairePlayerId ID du joueur actuellement titulaire
     * @param replacementPlayerId ID du joueur non titulaire qui prend sa place
     *
     * @throws PlayerNotFoundException si un des joueurs n'existe pas dans l'équipe concernée
     * @throws TitularisationNotAllowedException si les deux joueurs ne peuvent pas échanger leur statut
     */
    void execute(TeamId teamId, PlayerId titulairePlayerId, PlayerId replacementPlayerId);
}

