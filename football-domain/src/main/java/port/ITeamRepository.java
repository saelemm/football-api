package port;

import entity.Team;
import entity.TeamId;
import java.util.Optional;
import pagination.PagedResult;

/**
 * Port de persistance pour les équipes
 *
 * Cette interface définit le contrat pour accéder à la persistance des équipes.
 * L'implémentation concrète est dans l'adapter persistence-jpa.
 */
public interface ITeamRepository {

    /**
     * Récupère une équipe par son ID
     *
     * @param id ID de l'équipe
     * @return L'équipe si elle existe
     */
    Optional<Team> findById(TeamId id);

    /**
     * Récupère une équipe par son nom (unique)
     *
     * @param name Nom de l'équipe
     * @return L'équipe si elle existe
     */
    Optional<Team> findByName(String name);

    /**
     * Récupère une équipe par son acronyme (unique)
     *
     * @param acronym Acronyme de l'équipe
     * @return L'équipe si elle existe
     */
    Optional<Team> findByAcronym(String acronym);

    /**
     * Sauvegarde une équipe (création ou mise à jour)
     *
     * @param team L'équipe à sauvegarder
     * @return ID de l'équipe sauvegardée
     */
    Long save(Team team);

    /**
     * Supprime une équipe par son ID
     *
     * @param id ID de l'équipe à supprimer
     */
    void delete(TeamId id);

    /**
     * Récupère toutes les équipes
     *
     * @return Liste de toutes les équipes
     */
    PagedResult<Team> findAll(int page, int size, String sortBy, String direction);

}

