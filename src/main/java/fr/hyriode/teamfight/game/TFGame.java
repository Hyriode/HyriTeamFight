package fr.hyriode.teamfight.game;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.HyriGameType;
import fr.hyriode.hyrame.game.team.HyriGameTeam;
import fr.hyriode.teamfight.HyriTeamFight;
import fr.hyriode.teamfight.language.TFMessage;
import org.bukkit.entity.Player;

/**
 * Created by AstFaster
 * on 16/05/2023 at 18:49
 */
public class TFGame extends HyriGame<TFPlayer> {

    public TFGame() {
        super(IHyrame.get(),
                HyriTeamFight.get(),
                HyriAPI.get().getConfig().isDevEnvironment() ? HyriAPI.get().getGameManager().createGameInfo(HyriTeamFight.ID, HyriTeamFight.NAME) : HyriAPI.get().getGameManager().getGameInfo(HyriTeamFight.ID),
                TFPlayer.class,
                HyriAPI.get().getConfig().isDevEnvironment() ? TFGameType.FIVE_FIVE : HyriGameType.getFromData(TFGameType.values()));
        this.description = TFMessage.GAME_DESCRIPTION.asLang();
        this.waitingRoom = new TFWaitingRoom(this);
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
    }

    @Override
    public void win(HyriGameTeam winner) {
        super.win(winner);
    }

}

