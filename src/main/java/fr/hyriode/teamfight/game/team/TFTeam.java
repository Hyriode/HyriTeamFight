package fr.hyriode.teamfight.game.team;

import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.game.team.HyriGameTeam;
import fr.hyriode.teamfight.HyriTeamFight;
import fr.hyriode.teamfight.config.TFConfig;
import fr.hyriode.teamfight.game.TFPlayer;
import fr.hyriode.teamfight.util.TFValues;
import org.bukkit.Location;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by AstFaster
 * on 16/05/2023 at 19:00
 */
public class TFTeam extends HyriGameTeam {

    private int points;

    private final TFConfig.Team config;

    public TFTeam(ETFTeam model, TFConfig.Team config) {
        super(model.getName(), model.getDisplayName(), model.getColor(), 5);
        this.config = config;
    }

    public void spawnPlayers() {
        final Location spawn = this.config.getSpawn().asBukkit();

        for (HyriGamePlayer gamePlayer : this.players.values()) {
            if (!gamePlayer.isOnline()) {
                continue;
            }

            gamePlayer.getPlayer().teleport(spawn);
        }
    }

    public List<TFPlayer> getRemainingPlayers() {
        return HyriTeamFight.get().getGame().getPlayers()
                .stream()
                .filter(player -> player.isOnline() && !player.isDead())
                .collect(Collectors.toList());
    }

    public void addPoint() {
        this.points++;
    }

    public int getPoints() {
        return this.points;
    }

    public boolean hasWon() {
        return this.points >= TFValues.WIN_POINTS.get();
    }

    public TFConfig.Team getConfig() {
        return this.config;
    }

}