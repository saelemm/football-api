package entity;

/**
 * Entity représentant les statistiques métiers liées au football d'un joueur.
 *
 * @param position Position du joueur sur le terrein.
 * @param performanceNote Note de performance du joueur.
 * @param marketPrice Prix du joueur sur le marché.
 * @param isTitulaire Indique si le joueur est titulaire.
 */
public record PlayerStat(PositionEnum position,
                         Note performanceNote,
                         Price marketPrice,
                         boolean isTitulaire) {
}
