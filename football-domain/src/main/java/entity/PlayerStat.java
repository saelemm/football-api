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

    public PlayerStat updatePrice(Price price) {
        return new PlayerStat(
                this.position(),
                this.performanceNote(),
                price,
                this.isTitulaire()
        );
    }

    public PlayerStat updatePerformance(Note newNote) {
        return new PlayerStat(
                this.position(),
                newNote,
                this.marketPrice(),
                this.isTitulaire()
        );
    }

    public PlayerStat removeTitularisation() {
                return new PlayerStat(
                        this.position(),
                        this.performanceNote(),
                        this.marketPrice(),
                        false
                );
    }

    public PlayerStat assignTitularisation() {
        return new PlayerStat(
            this.position(),
            this.performanceNote(),
            this.marketPrice(),
                   true
            );
    }
}
