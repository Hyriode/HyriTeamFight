package fr.hyriode.teamfight.config;

import fr.hyriode.api.config.IHyriConfig;
import fr.hyriode.hyrame.game.waitingroom.HyriWaitingRoom;
import fr.hyriode.hyrame.utils.LocationWrapper;

import java.util.List;

/**
 * Created by AstFaster
 * on 16/05/2023 at 18:49
 */
public class TFConfig implements IHyriConfig {

    private final HyriWaitingRoom.Config waitingRoom;
    private final List<Team> teams;

    public TFConfig(HyriWaitingRoom.Config waitingRoom, List<Team> teams) {
        this.waitingRoom = waitingRoom;
        this.teams = teams;
    }

    public HyriWaitingRoom.Config getWaitingRoom() {
        return this.waitingRoom;
    }

    public List<Team> getTeams() {
        return this.teams;
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
