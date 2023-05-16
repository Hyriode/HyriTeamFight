package fr.hyriode.teamfight.listener;

import fr.hyriode.hyrame.game.HyriGameState;
import fr.hyriode.hyrame.listener.HyriListener;
import fr.hyriode.teamfight.HyriTeamFight;
import fr.hyriode.teamfight.game.TFGame;
import fr.hyriode.teamfight.game.TFPlayer;
import fr.hyriode.teamfight.game.TFRound;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.metadata.FixedMetadataValue;

import static fr.hyriode.teamfight.game.TFRound.*;

/**
 * Created by AstFaster
 * on 16/05/2023 at 18:57
 */
public class PlayerListener extends HyriListener<HyriTeamFight> {

    public PlayerListener(HyriTeamFight plugin) {
        super(plugin);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        final TFGame game = this.plugin.getGame();
        final Player player = event.getPlayer();
        final TFPlayer gamePlayer = game.getPlayer(player);

        if (gamePlayer == null || game.getState() != HyriGameState.PLAYING) {
            return;
        }

        final TFRound round = game.getRound();

        if (round.isPhase(Phase.WAITING, Phase.ENDED)) {
            event.setCancelled(true);
        } else {
            final Block block = event.getBlockPlaced();

            if (this.plugin.getConfiguration().getGameArea().asArea().isInArea(event.getBlockPlaced().getLocation())) {
                if (block.getType() == Material.SANDSTONE) {
                    block.setMetadata(TFGame.BLOCKS_METADATA, new FixedMetadataValue(this.plugin, System.currentTimeMillis()));
                    player.getItemInHand().setAmount(64);

                    round.addPlacedBlock(block);
                }
            } else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        final Block block = event.getBlock();

        if (block.getType() != Material.SANDSTONE) {
            event.setCancelled(true);
        } else if (!block.hasMetadata(TFGame.BLOCKS_METADATA)) {
            this.plugin.getGame().getRound().addBrokenBlock(block);
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        final TFGame game = this.plugin.getGame();
        final TFPlayer gamePlayer = game.getPlayer(event.getPlayer());

        if (gamePlayer == null) {
            return;
        }

        if (game.getRound().isPhase(Phase.WAITING, Phase.ENDED)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getSlotType() == InventoryType.SlotType.ARMOR) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }

}