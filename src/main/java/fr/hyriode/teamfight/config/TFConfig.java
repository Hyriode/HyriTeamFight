package fr.hyriode.teamfight.config;

import fr.hyriode.api.config.IHyriConfig;
import fr.hyriode.hyrame.game.waitingroom.HyriWaitingRoom;
import fr.hyriode.hyrame.utils.AreaWrapper;
import fr.hyriode.hyrame.utils.LocationWrapper;

/**
 * Created by AstFaster
 * on 16/05/2023 at 18:49
 */
public class TFConfig implements IHyriConfig {

    private final HyriWaitingRoom.Config waitingRoom;

    private final AreaWrapper gameArea;
    private final LocationWrapper center;

    private final Team firstTeam;
    private final Team secondTeam;

    public TFConfig(HyriWaitingRoom.Config waitingRoom, AreaWrapper gameArea, LocationWrapper center, Team firstTeam, Team secondTeam) {
        this.waitingRoom = waitingRoom;
        this.gameArea = gameArea;
        this.center = center;
        this.firstTeam = firstTeam;
        this.secondTeam = secondTeam;
    }

    public HyriWaitingRoom.Config getWaitingRoom() {
        return this.waitingRoom;
    }

    public AreaWrapper getGameArea() {
        return this.gameArea;
    }

    public LocationWrapper getCenter() {
        return this.center;
    }

    public Team getFirstTeam() {
        return this.firstTeam;
    }

    public Team getSecondTeam() {
        return this.secondTeam;
    }

    public static class Team {

        private final LocationWrapper spawn;

        public Team(LocationWrapper spawn) {
            this.spawn = spawn;
        }

        public LocationWrapper getSpawn() {
            return this.spawn;
        }

    }

}
