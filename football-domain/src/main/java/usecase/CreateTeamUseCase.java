package usecase;

import Errors.DuplicateTeamException;

import java.math.BigDecimal;

/**
 * Use Case pour créer une nouvelle équipe
 *
 * - Orchestre la création d'une entité Team du domaine
 * - Valide les données d'entrée
 * - Génère l'ID
 * - Persiste
 */
public interface CreateTeamUseCase {

    /**
     * Crée une nouvelle équipe
     *
     * Orchestration:
     * 1. Valide que le nom et l'acronyme sont uniques
     * 2. Génère un nouvel ID
     * 3. Crée une nouvelle entité Team avec les paramètres validés
     * 4. Persiste l'équipe
     * 5. Retourne l'ID de l'équipe créée
     *
     * @param name Nom de l'équipe (doit être unique)
     * @param acronym Acronyme de l'équipe (doit être unique)
     * @param initialBudget Budget initial de l'équipe
     *
     * @return ID de l'équipe créée
     *
     * @throws IllegalArgumentException si les paramètres ne sont pas valides
     * @throws DuplicateTeamException si le nom ou l'acronyme existe déjà
     */
    Long execute(String name, String acronym, BigDecimal initialBudget);
}

