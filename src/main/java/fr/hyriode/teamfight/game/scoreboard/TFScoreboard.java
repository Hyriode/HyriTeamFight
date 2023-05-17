package fr.hyriode.teamfight.game.scoreboard;

import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.hyrame.game.scoreboard.HyriGameScoreboard;
import fr.hyriode.hyrame.utils.Symbols;
import fr.hyriode.teamfight.HyriTeamFight;
import fr.hyriode.teamfight.game.TFGame;
import fr.hyriode.teamfight.game.TFPlayer;
import fr.hyriode.teamfight.game.team.TFTeam;
import fr.hyriode.teamfight.util.TFValues;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by AstFaster
 * on 16/05/2023 at 18:35
 */
public class TFScoreboard extends HyriGameScoreboard<TFGame> {

    private final TFPlayer gamePlayer;
    private final HyriTeamFight plugin;

    public TFScoreboard(HyriTeamFight plugin, Player player) {
        super(plugin, plugin.getGame(), player, "teamfight");
        this.plugin = plugin;
        this.gamePlayer = this.plugin.getGame().getPlayer(this.player.getUniqueId());

        this.addLines();

        this.addCurrentDateLine(0);
        this.addBlankLine(1);
        this.addBlankLine(4);

        if (this.gamePlayer != null) {
            this.addBlankLine(7);
            this.addGameTimeLine(8, this.getLinePrefix("time"));
            this.addBlankLine(9);
        } else { // Spectator scoreboard
            this.addGameTimeLine(5, this.getLinePrefix("time"));
            this.addBlankLine(6);
        }

        this.addHostnameLine();
    }

    private void addLines() {
        this.setLine(2, this.getTeamLine(this.plugin.getGame().getFirstTeam()));
        this.setLine(3, this.getTeamLine(this.plugin.getGame().getSecondTeam()));

        if (this.gamePlayer != null) {
            this.setLine(5, this.getKillsLine());
            this.setLine(6, this.getDeathsLine());
        }
    }

    public void update() {
        this.addLines();
        this.updateLines();
    }

    private String getKillsLine() {
        return this.getLinePrefix("kills") + ChatColor.AQUA + (this.gamePlayer != null ? this.gamePlayer.getKills() : ChatColor.RED + Symbols.CROSS_STYLIZED_BOLD);
    }

    private String getDeathsLine() {
        return this.getLinePrefix("deaths") + ChatColor.AQUA + (this.gamePlayer != null ? this.gamePlayer.getDeaths() : ChatColor.RED + Symbols.CROSS_STYLIZED_BOLD);
    }

    private String getTeamLine(TFTeam team) {
        return team.getFormattedDisplayName(this.player) + ChatColor.RESET + ": "
                + ChatColor.AQUA + team.getPoints() + "/" + TFValues.WIN_POINTS.get()
                + (team.contains(this.gamePlayer) ? this.getLinePrefix("you") : "");
    }

    private String getLinePrefix(String prefix) {
        return HyriLanguageMessage.get("scoreboard." + prefix + ".display").getValue(this.player);
    }

}
