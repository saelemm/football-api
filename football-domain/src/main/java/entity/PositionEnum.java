package entity;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Arrays;

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

    @JsonCreator
    public static PositionEnum from(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("La position du joueur est obligatoire");
        }

        return Arrays.stream(values())
            .filter(positionEnum -> positionEnum.name().equalsIgnoreCase(value.trim())
                || positionEnum.position.equalsIgnoreCase(value.trim()))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(
                "Position invalide : " + value + ". Valeurs autorisées : "
                    + Arrays.stream(values()).map(PositionEnum::name).reduce((a, b) -> a + ", " + b).orElse("")
            ));
    }
}
