package Errors;

public class ErrorMessages {

    // Private constants
    private static final String NE_PEUX_PAS_ETRE_NULL = "ne peux pas être null";
    private static final String D_UN_JOUEUR = "d'un joueur";

    // Public constants
    public static final String ID_NULL = "L'identifiant " + NE_PEUX_PAS_ETRE_NULL;
    public static final String POSITION_NULL = "La position " + D_UN_JOUEUR + " " + NE_PEUX_PAS_ETRE_NULL;
    public static final String NOTE_NULL = "La note " + D_UN_JOUEUR + " " + NE_PEUX_PAS_ETRE_NULL;
    public static final String PRIX_NULL = "Le prix " + D_UN_JOUEUR + " " + NE_PEUX_PAS_ETRE_NULL;
    public static final String PRIX_ZERO = "Le prix " + D_UN_JOUEUR + " ne peux pas être inférieur à 0";
    public static final String POURCENTAGE_NEGATIF = "Le pourcentage ne peux pas être négatif";
    public static final String NOTE_OUTBOUND = "La note doit être entre 0 et 10";
    public static final String BUDGET_INSUFFISANT_POUR_CE_JOUEUR = "Budget insuffisant pour ce joueur";
    public static final String BUDGET_NE_PEUT_PAS_ÊTRE_NULL = "Le budget ne peut pas être null";
    public static final String NOM_TEAM_NON_NULL = "Le nom de l'équipe ne peut pas être null";
    public static final String BUDGET_POSITIF = "Le budget doit être positif";
    public static final String TITULAIRE_NE_PEUT_PAS_ETRE_TRANSFERE = "Ce joueur titulaire ne peut pas être transféré";
    public static final String LES_DEUX_JOUEURS_DOIVENT_ETRE_DIFFERENTS = "Les deux joueurs doivent être différents";
    public static final String LES_DEUX_JOUEURS_DOIVENT_APPARTENIR_A_LA_MEME_EQUIPE = "Les deux joueurs doivent appartenir à la même équipe";
    public static final String LE_JOUEUR_SORTANT_DOIT_ETRE_TITULAIRE = "Le joueur sortant doit être titulaire";
    public static final String LE_JOUEUR_ENTRANT_DOIT_ETRE_NON_TITULAIRE = "Le joueur entrant doit être non titulaire";

}
