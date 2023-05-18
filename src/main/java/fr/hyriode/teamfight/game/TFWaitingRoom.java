package fr.hyriode.teamfight.game;

import fr.hyriode.api.language.HyriLanguage;
import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.api.leaderboard.HyriLeaderboardScope;
import fr.hyriode.api.leveling.NetworkLeveling;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.hyrame.game.waitingroom.HyriWaitingRoom;
import fr.hyriode.hyrame.utils.DurationFormatter;
import fr.hyriode.hyrame.utils.Symbols;
import fr.hyriode.teamfight.HyriTeamFight;
import fr.hyriode.teamfight.api.TFStatistics;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.function.Function;

/**
 * Created by AstFaster
 * on 16/05/2023 at 18:49
 */
public class TFWaitingRoom extends HyriWaitingRoom {


    private static final Function<String, HyriLanguageMessage> LANG_DATA = name -> HyriLanguageMessage.get("waiting-room.npc.data." + name);

    public TFWaitingRoom(TFGame game) {
        super(game, Material.DIAMOND_SWORD, HyriTeamFight.get().getConfiguration().getWaitingRoom());

        this.addLeaderboard(new Leaderboard(NetworkLeveling.LEADERBOARD_TYPE, HyriTeamFight.ID + "-experience",
                player -> HyriLanguageMessage.get("leaderboard.experience.display").getValue(player))
                .withScopes(HyriLeaderboardScope.DAILY, HyriLeaderboardScope.WEEKLY, HyriLeaderboardScope.MONTHLY));
        this.addLeaderboard(new Leaderboard(HyriTeamFight.ID, "kills", player -> HyriLanguageMessage.get("leaderboard.kills.display").getValue(player)));
        this.addLeaderboard(new Leaderboard(HyriTeamFight.ID, "victories", player -> HyriLanguageMessage.get("leaderboard.victories.display").getValue(player)));
        this.addLeaderboard(new Leaderboard(HyriTeamFight.ID, "rounds-won", player -> HyriLanguageMessage.get("leaderboard.rounds-won.display").getValue(player)));

        this.addStatistics(22, TFGameType.FIVE_FIVE);
    }

    private void addStatistics(int slot, TFGameType gameType) {
        final NPCCategory normal = new NPCCategory(new HyriLanguageMessage("").addValue(HyriLanguage.EN, gameType.getDisplayName()));

        normal.addData(new NPCData(LANG_DATA.apply("kills"), account -> String.valueOf(this.getStatistics(gameType, account).getKills())));
        normal.addData(new NPCData(LANG_DATA.apply("deaths"), account -> String.valueOf(this.getStatistics(gameType, account).getDeaths())));
        normal.addData(NPCData.voidData());
        normal.addData(new NPCData(LANG_DATA.apply("rounds-won"), account -> String.valueOf(this.getStatistics(gameType, account).getRoundsWon())));
        normal.addData(NPCData.voidData());
        normal.addData(new NPCData(LANG_DATA.apply("victories"), account -> String.valueOf(this.getStatistics(gameType, account).getVictories())));
        normal.addData(new NPCData(LANG_DATA.apply("games-played"), account -> String.valueOf(this.getStatistics(gameType, account).getGamesPlayed())));
        normal.addData(new NPCData(LANG_DATA.apply("played-time"), account -> this.formatPlayedTime(account, account.getStatistics().getPlayTime(HyriTeamFight.ID + "#" + gameType.getName()))));

        this.addNPCCategory(slot, normal);
    }

    private String formatPlayedTime(IHyriPlayer account, long playedTime) {
        return playedTime < 1000 ? ChatColor.RED + Symbols.CROSS_STYLIZED_BOLD : new DurationFormatter()
                .withSeconds(false)
                .format(account.getSettings().getLanguage(), playedTime);
    }

    private TFStatistics.Data getStatistics(TFGameType gameType, IHyriPlayer account) {
        return ((TFPlayer) this.game.getPlayer(account.getUniqueId())).getStatistics().getData(gameType);
    }

}
