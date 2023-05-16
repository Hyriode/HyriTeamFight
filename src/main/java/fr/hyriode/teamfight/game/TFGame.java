package fr.hyriode.teamfight.game;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.api.leaderboard.IHyriLeaderboardProvider;
import fr.hyriode.api.leveling.NetworkLeveling;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.hyggdrasil.api.server.HyggServer;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.HyriGameState;
import fr.hyriode.hyrame.game.HyriGameType;
import fr.hyriode.hyrame.game.protocol.HyriDeathProtocol;
import fr.hyriode.hyrame.game.protocol.HyriLastHitterProtocol;
import fr.hyriode.hyrame.game.team.HyriGameTeam;
import fr.hyriode.hyrame.game.util.HyriGameMessages;
import fr.hyriode.hyrame.game.util.HyriRewardAlgorithm;
import fr.hyriode.hyrame.utils.Pair;
import fr.hyriode.teamfight.HyriTeamFight;
import fr.hyriode.teamfight.api.TFData;
import fr.hyriode.teamfight.api.TFStatistics;
import fr.hyriode.teamfight.config.TFConfig;
import fr.hyriode.teamfight.game.team.ETFTeam;
import fr.hyriode.teamfight.game.team.TFTeam;
import fr.hyriode.teamfight.language.TFMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

/**
 * Created by AstFaster
 * on 16/05/2023 at 18:49
 */
public class TFGame extends HyriGame<TFPlayer> {

    public static final String BLOCKS_METADATA = "TeamFight-Block";

    private TFRound round;

    private TFTeam firstTeam;
    private TFTeam secondTeam;

    private final TFConfig config;

    public TFGame() {
        super(IHyrame.get(),
                HyriTeamFight.get(),
                HyriAPI.get().getConfig().isDevEnvironment() ? HyriAPI.get().getGameManager().createGameInfo(HyriTeamFight.ID, HyriTeamFight.NAME) : HyriAPI.get().getGameManager().getGameInfo(HyriTeamFight.ID),
                TFPlayer.class,
                HyriAPI.get().getConfig().isDevEnvironment() ? TFGameType.FIVE_FIVE : HyriGameType.getFromData(TFGameType.values()));
        this.description = TFMessage.GAME_DESCRIPTION.asLang();
        this.waitingRoom = new TFWaitingRoom(this);
        this.reconnectionTime  = 120;
        this.config = HyriTeamFight.get().getConfiguration();

        this.registerTeams();
    }

    private void registerTeams() {
        final Pair<ETFTeam, ETFTeam> teamsPair = ETFTeam.generatePair();

        this.firstTeam = new TFTeam(teamsPair.getKey(), this.config.getFirstTeam());
        this.secondTeam = new TFTeam(teamsPair.getValue(), this.config.getSecondTeam());

        this.registerTeam(this.firstTeam);
        this.registerTeam(this.secondTeam);
    }

    @Override
    public void handleLogin(Player player) {
        super.handleLogin(player);

        final TFPlayer gamePlayer = this.getPlayer(player);
        final TFStatistics statistics = TFStatistics.get(gamePlayer.getUniqueId());
        final TFData data = TFData.get(gamePlayer.getUniqueId());

        gamePlayer.setStatistics(statistics);
        gamePlayer.setData(data);
    }

    @Override
    public void handleLogout(Player player) {
        final TFPlayer gamePlayer = this.getPlayer(player);

        this.updatePlayer(gamePlayer);

        super.handleLogout(player);

        if (this.getState() == HyriGameState.PLAYING) {
            if (!gamePlayer.getTeam().hasPlayersPlaying()) {
                this.win(gamePlayer.getTeam().getOppositeTeam());
            }
        }
    }

    @Override
    public void start() {
        super.start();

        this.protocolManager.enableProtocol(new HyriLastHitterProtocol(this.hyrame, this.plugin, 5 * 20L));
        this.protocolManager.enableProtocol(new HyriDeathProtocol(this.hyrame, this.plugin, p -> {
            final TFPlayer gamePlayer = p.cast();

            gamePlayer.onDeath();
            return true;
        }, null, null).withOptions(new HyriDeathProtocol.Options()
                .withYOptions(new HyriDeathProtocol.Options.YOptions(this.config.getGameArea().asArea().toCuboid().getLowerY()))
                .withDeathSound(true)));

        for (TFPlayer player : this.players) {
            player.init();
        }

        this.newRound();
    }

    @Override
    public void win(HyriGameTeam winner) {
        if (winner == null || this.getState() != HyriGameState.PLAYING) {
            return;
        }

        super.win(winner);

        final List<HyriLanguageMessage> positions = Arrays.asList(
                HyriLanguageMessage.get("message.game.end.1"),
                HyriLanguageMessage.get("message.game.end.2"),
                HyriLanguageMessage.get("message.game.end.3")
        );

        final List<TFPlayer> topKillers = new ArrayList<>(this.players);

        topKillers.sort((o1, o2) -> Math.toIntExact(o2.getKills() - o1.getKills()));

        final Function<Player, List<String>> killersLineProvider = player -> {
            final List<String> killersLine = new ArrayList<>();

            for (int i = 0; i <= 2; i++) {
                final String killerLine = HyriLanguageMessage.get("message.game.end.kills").getValue(player).replace("%position%", positions.get(i).getValue(player));

                if (topKillers.size() > i){
                    final TFPlayer topKiller = topKillers.get(i);

                    killersLine.add(killerLine.replace("%player%", topKiller.formatNameWithTeam()).replace("%kills%", String.valueOf(topKiller.getKills())));
                    continue;
                }

                killersLine.add(killerLine.replace("%player%", HyriLanguageMessage.get("message.game.end.nobody").getValue(player)).replace("%kills%", "0"));
            }

            return killersLine;
        };

        // Send message to not-playing players
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (this.getPlayer(player) == null) {
                player.spigot().sendMessage(HyriGameMessages.createWinMessage(this, player, winner, killersLineProvider.apply(player), null));
            }
        }

        for (TFPlayer gamePlayer : this.players) {
            final UUID playerId = gamePlayer.getUniqueId();
            final IHyriPlayer account = gamePlayer.asHyriPlayer();

            // Hyris and XP calculations
            final boolean host = HyriAPI.get().getServer().getAccessibility() == HyggServer.Accessibility.HOST;
            final int kills = gamePlayer.getKills();
            final boolean isWinner = winner.contains(gamePlayer);
            final long hyris = host ? 0 : account.getHyris().add(
                            HyriRewardAlgorithm.getHyris(kills, gamePlayer.getPlayTime(), isWinner)
                                    + gamePlayer.getRoundsWon() * 10L).
                    withMessage(false)
                    .exec();
            final double xp = host ? 0.0D : account.getNetworkLeveling().addExperience(
                    HyriRewardAlgorithm.getXP(kills, gamePlayer.getPlayTime(), isWinner)
                            + gamePlayer.getRoundsWon() * 7.0D);

            if (!host) {
                // Experience leaderboard updates
                final IHyriLeaderboardProvider provider = HyriAPI.get().getLeaderboardProvider();

                provider.getLeaderboard(NetworkLeveling.LEADERBOARD_TYPE, HyriTeamFight.ID + "-experience").incrementScore(playerId, xp);
                provider.getLeaderboard(HyriTeamFight.ID, "kills").incrementScore(playerId, kills);
                provider.getLeaderboard(HyriTeamFight.ID, "rounds-won").incrementScore(playerId, gamePlayer.getRoundsWon());

                if (isWinner) {
                    provider.getLeaderboard(HyriTeamFight.ID, "victories").incrementScore(playerId, 1);

                    gamePlayer.getStatistics().getData((TFGameType) this.getType()).addVictories(1);
                }
            }

            account.update();

            // Send message
            final String rewardsLine = ChatColor.LIGHT_PURPLE + "+" + hyris + " Hyris " + ChatColor.GREEN + "+" + xp + " XP";

            if (gamePlayer.isOnline()) {
                final Player player = gamePlayer.getPlayer();

                player.spigot().sendMessage(HyriGameMessages.createWinMessage(this, gamePlayer.getPlayer(), winner, killersLineProvider.apply(player), rewardsLine));
            } else if (HyriAPI.get().getPlayerManager().isOnline(playerId)) {
                HyriAPI.get().getPlayerManager().sendMessage(playerId, HyriGameMessages.createOfflineWinMessage(this, account, rewardsLine));
            }
        }
    }

    public void newRound() {
        if (this.round != null) {
            this.round.free();
        }

        this.round = new TFRound();
        this.round.start();
    }

    private void updatePlayer(TFPlayer gamePlayer) {
        final IHyriPlayer account = IHyriPlayer.get(gamePlayer.getUniqueId());
        final TFData data = gamePlayer.getData();
        final TFStatistics statistics = gamePlayer.getStatistics();
        final TFStatistics.Data statisticsData = statistics.getData((TFGameType) this.getType());

        if (!this.getState().isAccessible() && !HyriAPI.get().getServer().getAccessibility().equals(HyggServer.Accessibility.HOST)) {
            statisticsData.addGamesPlayed(1);
            statisticsData.addKills(gamePlayer.getKills());
            statisticsData.addDeaths(gamePlayer.getDeaths());
            statisticsData.addRoundsWon(gamePlayer.getRoundsWon());

            statistics.update(account);
        }

        data.update(account);
    }


    public TFRound getRound() {
        return this.round;
    }

    public TFTeam getFirstTeam() {
        return this.firstTeam;
    }

    public TFTeam getSecondTeam() {
        return this.secondTeam;
    }

    public TFConfig getConfig() {
        return this.config;
    }

}

