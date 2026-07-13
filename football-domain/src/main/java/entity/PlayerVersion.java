package entity;

import java.util.Date;

/**
 * Entity permettant de représenter la version d'un joueur, incluant les informations de version et les dates de création et de mise à jour.
 *
 * La couche domaine incrémente la version et met a jour updatedAt a chaque modification metier.
 * La couche persistence utilise ensuite cette version pour verifier l'optimistic locking.
 *
 * @param version Integer de version du joueur.
 * @param createdAt Date d'ajout dans le système du joueur.
 * @param updatedAt Date de dernière mise à jour du joueur.
 */
public record PlayerVersion(Integer version,
                            Date createdAt,
                            Date updatedAt) {

    public PlayerVersion incrementVersion() {
        Date now = new Date();
        int nextVersion = version == null ? 1 : version + 1;
        return new PlayerVersion(nextVersion, createdAt, now);
    }
}
