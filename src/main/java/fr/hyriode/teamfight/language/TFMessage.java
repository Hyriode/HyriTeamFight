package fr.hyriode.teamfight.language;

import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.api.player.IHyriPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;

/**
 * Created by AstFaster
 * on 23/07/2022 at 09:51
 */
public enum TFMessage {

    GAME_DESCRIPTION("game.description"),

    PLAYER_KILL("player.kill"),
    PLAYER_KILL_PLAYER("player.kill-player"),
    PLAYER_FINAL_KILL("player.final-kill"),

    SCOREBOARD_TIME("scoreboard.time"),
    SCOREBOARD_KILLS("scoreboard.kills"),

    ;

    private HyriLanguageMessage languageMessage;

    private final String key;
    private final BiFunction<Player, String, String> formatter;

    TFMessage(String key, BiFunction<Player, String, String> formatter) {
        this.key = key;
        this.formatter = formatter;
    }

    TFMessage(String key, TFMessage prefix) {
        this.key = key;
        this.formatter = (target, input) -> prefix.asString(target) + input;
    }

    TFMessage(String key) {
        this(key, (target, input) -> input);
    }

    public HyriLanguageMessage asLang() {
        return this.languageMessage == null ? this.languageMessage = HyriLanguageMessage.get(this.key) : this.languageMessage;
    }

    public String asString(IHyriPlayer account) {
        return this.formatter.apply(Bukkit.getPlayer(account.getUniqueId()), this.asLang().getValue(account));
    }

    public String asString(Player player) {
        return this.formatter.apply(player, this.asLang().getValue(player));
    }

    public List<String> asList(IHyriPlayer account) {
        return new ArrayList<>(Arrays.asList(this.asString(account).split("\n")));
    }

    public List<String> asList(Player player) {
        return new ArrayList<>(Arrays.asList(this.asString(player).split("\n")));
    }

}
