package fr.hyriode.teamfight.listener;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.event.HyriEventHandler;
import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.game.HyriGameSpectator;
import fr.hyriode.hyrame.game.event.player.HyriGameDeathEvent;
import fr.hyriode.hyrame.game.event.player.HyriGameReconnectedEvent;
import fr.hyriode.hyrame.game.event.player.HyriGameSpectatorEvent;
import fr.hyriode.hyrame.game.protocol.HyriDeathProtocol;
import fr.hyriode.hyrame.listener.HyriListener;
import fr.hyriode.teamfight.HyriTeamFight;
import fr.hyriode.teamfight.game.TFGame;
import fr.hyriode.teamfight.game.TFPlayer;
import org.bukkit.entity.Player;

/**
 * Created by AstFaster
 * on 16/05/2023 at 18:57
 */
public class GameListener extends HyriListener<HyriTeamFight> {

    public GameListener(HyriTeamFight plugin) {
        super(plugin);

        HyriAPI.get().getEventBus().register(this);
    }

    @HyriEventHandler
    public void onReconnected(HyriGameReconnectedEvent event) {
        final TFPlayer player = (TFPlayer) event.getGamePlayer();
        final TFGame game = this.plugin.getGame();

        game.getProtocolManager().getProtocol(HyriDeathProtocol.class).runDeath(HyriGameDeathEvent.Reason.VOID, player.getPlayer());
    }

    @HyriEventHandler
    public void onSpectator(HyriGameSpectatorEvent event) {
        final TFGame game = (TFGame) event.getGame();
        final HyriGameSpectator spectator = event.getSpectator();
        final Player player = spectator.getPlayer();

        if (!(spectator instanceof HyriGamePlayer)) { // Player is an outside spectator
            player.teleport(game.getWaitingRoom().getConfig().getSpawn().asBukkit());
        }
    }

}
