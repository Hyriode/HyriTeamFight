package fr.hyriode.teamfight.game;

import fr.hyriode.hyrame.game.HyriGameType;

/**
 * Created by AstFaster
 * on 16/05/2023 at 18:53
 */
public enum TFGameType implements HyriGameType {

    FIVE_FIVE("5v5", 8, 10),

    ;

    private final String displayName;
    private final int minPlayers;
    private final int maxPlayers;

    TFGameType(String displayName, int minPlayers, int maxPlayers) {
        this.displayName = displayName;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
    }

    @Override
    public String getName() {
        return this.name();
    }

    @Override
    public String getDisplayName() {
        return this.displayName;
    }

    @Override
    public int getMinPlayers() {
        return this.minPlayers;
    }

    @Override
    public int getMaxPlayers() {
        return this.maxPlayers;
    }

}
