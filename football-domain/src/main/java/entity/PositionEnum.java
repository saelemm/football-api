package entity;

/**
 * Enum de positions des joueurs
 *
 * Source : https://fifafootballvideogames.fandom.com/wiki/Soccer_positions
 */
public enum PositionEnum {
    GK("Goalkeeper"),
    SW("Sweeper"),
    RWB("Right Wingback"),
    LWB("Left Wingback"),
    RB("Right Back"),
    LB("Left Back"),
    CB("Centre Back"),
    DM("Defensive Midfielder"),
    RW("Right Winger"),
    LW("Left Winger"),
    LM("Left Midfielder"),
    RM("Right Midfielder"),
    CM("Centre Midfielder"),
    AM("Attacking Midfielder"),
    CF("Centre Forward"),
    RF("Right Forward"),
    LF("Left Forward"),
    ST("Striker");

    private final String position;

    PositionEnum(String position) {
        this.position = position;
    }

    public String position() {
        return position;
    }
}
