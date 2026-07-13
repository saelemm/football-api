package usecase;

import entity.PositionEnum;
import java.math.BigDecimal;

/**
 * Use Case pour recruter un nouveau joueur
 *
 * - Orchestre la création d'une entité Player du domaine
 * - Valide les données d'un joueur nouveau
 * - Met à jour l'équipe (budget - prix, ajout du joueur)
 * - Persiste
 */
public interface RecruitPlayerUseCase {

    /**
     * Recrute un nouveau joueur pour une équipe
     *
     * Orchestration:
     * 1. Valide que l'équipe existe et a un budget suffisant
     * 2. Génère un nouvel ID pour le joueur
     * 3. Crée une nouvelle entité Player
     * 4. Crée un Transfer (cas initial: sourceTeamId = null)
     * 5. Met à jour l'équipe (Player.addPlayer avec le Transfer)
     * 6. Persiste le joueur et l'équipe
     * 7. Retourne l'ID du joueur créé
     *
     * @param firstName Prénom du joueur
     * @param lastName Nom du joueur
     * @param acronym Acronyme du joueur
     * @param position Position du joueur
     * @param performance Note de performance initiale (0-10)
     * @param marketPrice Prix d'achat du joueur
     * @param teamId ID de l'équipe qui recrute
     *
     * @return ID du joueur recruté
     *
     * @throws TeamNotFoundException si l'équipe n'existe pas
     * @throws InsufficientBudgetException si l'équipe n'a pas assez de budget
     * @throws IllegalArgumentException si les paramètres ne sont pas valides
     */
    Long execute(String firstName,
                 String lastName,
                 String acronym,
                 PositionEnum position,
                 Float performance,
                 BigDecimal marketPrice,
                 Long teamId);
}

