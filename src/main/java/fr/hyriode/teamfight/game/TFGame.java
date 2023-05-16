package fr.hyriode.teamfight.game;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.HyriGameType;
import fr.hyriode.hyrame.game.protocol.HyriDeathProtocol;
import fr.hyriode.hyrame.game.protocol.HyriLastHitterProtocol;
import fr.hyriode.hyrame.game.team.HyriGameTeam;
import fr.hyriode.hyrame.utils.Pair;
import fr.hyriode.teamfight.HyriTeamFight;
import fr.hyriode.teamfight.config.TFConfig;
import fr.hyriode.teamfight.game.team.ETFTeam;
import fr.hyriode.teamfight.game.team.TFTeam;
import fr.hyriode.teamfight.language.TFMessage;
import org.bukkit.entity.Player;

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
    }

    @Override
    public void handleLogout(Player player) {
        super.handleLogout(player);
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

    public void newRound() {
        if (this.round != null) {
            this.round.free();
        }

        this.round = new TFRound();
        this.round.start();
    }

    @Override
    public void win(HyriGameTeam winner) {
        super.win(winner);
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

