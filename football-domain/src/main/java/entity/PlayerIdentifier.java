package entity;

/**
 * Entity contenant la sous-responsabilities des identifiants joueur,
 * regroupant les ID de team et les informations personnelles du joueur.
 *
 * @param id Identifiant du joueur.
 * @param firstName Prénom du joueur.
 * @param lastName Nom de famille du joueur.
 * @param acronym Acronyme du joueur.
 * @param teamId Identifiant de l'équipe à laquelle le joueur appartient.
 */
public record PlayerIdentifier (PlayerId id,
                                String firstName,
                                String lastName,
                                String acronym,
                                TeamId teamId) {
}
