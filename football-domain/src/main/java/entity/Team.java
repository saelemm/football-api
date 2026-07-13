package entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import Errors.InsufficientBudgetException;
import Validator.NullValidator;
import Validator.NumberValidator;

import static Errors.ErrorMessages.*;

/**
 * Main entity représentant une équipe. Comme pour l'entity 'Player', les responsabilitées sont segmenté en
 * trois sous entitées, l'identifiant, les statisques, la listes d'identifiants des joueurs et la liste de transfers.
 *
 * Comportement métier encapsulé:
 * - Gestion des joueurs et du budget
 * - Enregistrement des transferts dans l'historique
 * - Validation du budget suffisant
 *
 * Immuabilité: chaque méthode retourne une nouvelle instance
 */
public record Team(TeamIdentifier teamId,
                   TeamStat teamStat,
                   List<PlayerId> playerIds,
                   List<Transfer> transferHistory) {

    public Team {
        NullValidator.requireNonNull(teamId.name(), NOM_TEAM_NON_NULL);
        NumberValidator.requirePositive(teamStat.budget(), BUDGET_POSITIF);
    }

    /**
     * Retourne une nouvelle Team entity après l'ajout d'un joueur via transfert
     *
     * Comportement métier:
     * - Valide que l'équipe a assez de budget
     * - Ajoute le joueur
     * - Enregistre le transfert dans l'historique
     *
     * @param playerId ID du joueur à ajouter
     * @param playerPrice Prix du joueur
     * @param transfer Enregistrement du transfert
     * @return Nouvelle Team avec le joueur ajouté et le budget réduit
     */
    public Team addPlayer(PlayerId playerId, Price playerPrice, Transfer transfer) {
        if (!hasEnoughBudget(playerPrice)) {
            throw new InsufficientBudgetException(BUDGET_INSUFFISANT_POUR_CE_JOUEUR);
        }

        List<PlayerId> updatedPlayerIds = new ArrayList<>(playerIds);
        updatedPlayerIds.add(playerId);

        List<Transfer> updatedTransfers = new ArrayList<>(transferHistory);
        updatedTransfers.add(transfer);

        return new Team(
                teamId,
                teamStat.incrementVersionWithBudget(teamStat.budget().subtract(playerPrice.value())),
                updatedPlayerIds,
                updatedTransfers
        );
    }

    /**
     * Retourne une nouvelle Team après la suppression d'un joueur via transfert
     *
     * Comportement métier:
     * - Retire le joueur de l'équipe
     * - Augmente le budget
     * - Enregistre le transfert dans l'historique
     *
     * @param playerId ID du joueur à supprimer
     * @param playerRefund Prix de remboursement du joueur
     * @param transfer Enregistrement du transfert
     * @return Nouvelle Team avec le joueur retiré et le budget augmenté
     */
    public Team removePlayer(PlayerId playerId, Price playerRefund, Transfer transfer) {
        List<PlayerId> updatedPlayerIds = new ArrayList<>(playerIds);
        updatedPlayerIds.remove(playerId);

        List<Transfer> updatedTransfers = new ArrayList<>(transferHistory);
        updatedTransfers.add(transfer);

        return new Team(
                teamId,
                teamStat.incrementVersionWithBudget(teamStat.budget().add(playerRefund.value())),
                updatedPlayerIds,
                updatedTransfers
        );
    }

    /**
     * Vérification métier: l'équipe a-t-elle assez de budget?
     */
    public boolean hasEnoughBudget(Price price) {
        return teamStat.budget().compareTo(price.value()) >= 0;
    }

    /**
     * Calcul métier: quel budget reste après une dépense?
     */
    public BigDecimal getAvailableBudget(Price price) {
        return teamStat.budget().subtract(price.value());
    }
}
