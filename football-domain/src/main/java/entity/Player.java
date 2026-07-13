package entity;

import Errors.TransferNotAllowedException;
import jakarta.validation.constraints.NotNull;

import static Errors.ErrorMessages.TITULAIRE_NE_PEUT_PAS_ETRE_TRANSFERE;

/**
 * Entitee principale représentant un joueur.
 * Trois éléments principaux : l'identifiant, les statistiques et la version du joueur.
 * Limiter à 3 éléments uniquement afin de limiter le nombres d'arguments sur le constructeur et segmenter les
 * responsabilitées, principe SRP.
 *
 * @param identifier Entity contenant tout les identifiants.
 * @param stats Entity contenant toutes les statisques.
 * @param version Entity contenant les informations de version et les dates de création et de mise à jour.
 */
@NotNull
public record Player(PlayerIdentifier identifier,
                     PlayerStat stats,
                     PlayerVersion version) {
    
    public Player updatePerformance(Note newNote) {
        PlayerStat updatedStats = new PlayerStat(
                stats.position(),
                newNote,
                stats.marketPrice(),
                stats.isTitulaire()
        );
        return new Player(identifier, updatedStats, version);
    }

    public Player removeTitularisation() {
        return new Player(
                identifier,
                new PlayerStat(
                        stats.position(),
                        stats.performanceNote(),
                        stats.marketPrice(),
                        false
                ),
                version
        );
    }

    public Player assignTitularisation() {
        return new Player(
                identifier,
                new PlayerStat(
                        stats.position(),
                        stats.performanceNote(),
                        stats.marketPrice(),
                        true
                ),
                version
        );
    }

    // Retourne un nouveau Player après transfert
    public Player transferTo(TeamId newTeamId) {
        validateTransfer(newTeamId);

        PlayerIdentifier updatedIdentifier = new PlayerIdentifier(
                identifier.id(),
                identifier.firstName(),
                identifier.lastName(),
                identifier.acronym(),
                newTeamId
        );
        return new Player(updatedIdentifier, stats, version);
    }

    public boolean canBeTransferred() {
        return !stats.isTitulaire();
    }

    private void validateTransfer(TeamId newTeamId) {
        if (!canBeTransferred()) {
            throw new TransferNotAllowedException(TITULAIRE_NE_PEUT_PAS_ETRE_TRANSFERE);
        }
    }
}
