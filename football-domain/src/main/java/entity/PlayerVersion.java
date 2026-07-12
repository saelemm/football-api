package entity;

import java.util.Date;

/**
 * Entity permettant de représenter la version d'un joueur, incluant les informations de version et les dates de création et de mise à jour.
 *
 * @param version Integer de version du joueur.
 * @param createdAt Date d'ajout dans le système du joueur.
 * @param updatedAt Date de dernière mise à jour du joueur.
 */
public record PlayerVersion(Integer version,
                            Date createdAt,
                            Date updatedAt) {
}
