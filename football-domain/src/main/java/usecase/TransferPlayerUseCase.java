package usecase;

import entity.PlayerId;
import entity.TeamId;
import Errors.InsufficientBudgetException;
import Errors.PlayerNotFoundException;
import Errors.TeamNotFoundException;
import Errors.TransferNotAllowedException;

import java.math.BigDecimal;
import entity.Transfer;

/**
 * Use Case pour orchstrer un transfert de joueur entre deux équipes
 *
 * Respecte l'architecture hexagonale:
 * - Orchestre les entités du domaine (Player, Team, Transfer)
 * - Valide les règles métier
 * - Laisse le domaine pur (pas d'orchestration dedans)
 */
public interface TransferPlayerUseCase {

    /**
     * Transfère un joueur d'une équipe source à une équipe cible
     *
     * Orchestration:
     * 1. Récupère le joueur et valide qu'il peut être transféré
     * 2. Récupère l'équipe source et l'équipe cible
     * 3. Valide que l'équipe cible a assez de budget
     * 4. Crée un Transfer
     * 5. Met à jour le joueur (changement d'équipe)
     * 6. Met à jour les deux équipes (budget + historique)
     * 7. Persiste tout
     *
     * @param playerId ID du joueur à transférer
     * @param sourceTeamId ID de l'équipe source (null si recrutement)
     * @param targetTeamId ID de l'équipe cible
     * @param transferPrice Prix du transfert
     *
     * @return L'objet Transfer créé avec tous ses détails
     * @throws PlayerNotFoundException si le joueur n'existe pas
     * @throws TeamNotFoundException si une équipe n'existe pas
     * @throws TransferNotAllowedException si le joueur ne peut pas être transféré
     * @throws InsufficientBudgetException si l'équipe cible n'a pas assez de budget
     */
    Transfer execute(PlayerId playerId,
                     TeamId sourceTeamId,
                     TeamId targetTeamId,
                     BigDecimal transferPrice);
}

