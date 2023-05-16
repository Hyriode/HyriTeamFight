package fr.hyriode.teamfight.game;

import fr.hyriode.hyrame.game.waitingroom.HyriWaitingRoom;
import fr.hyriode.teamfight.HyriTeamFight;
import org.bukkit.Material;

/**
 * Created by AstFaster
 * on 16/05/2023 at 18:49
 */
public class TFWaitingRoom extends HyriWaitingRoom {

    public TFWaitingRoom(TFGame game) {
        super(game, Material.DIAMOND_SWORD, HyriTeamFight.get().getConfiguration().getWaitingRoom());
    }

}
