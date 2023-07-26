package fr.hyriode.teamfight.listener;

import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.game.HyriGameState;
import fr.hyriode.hyrame.game.event.player.HyriGameDeathEvent;
import fr.hyriode.hyrame.listener.HyriListener;
import fr.hyriode.hyrame.utils.LocationUtil;
import fr.hyriode.teamfight.HyriTeamFight;
import fr.hyriode.teamfight.game.TFGame;
import fr.hyriode.teamfight.game.TFPlayer;
import fr.hyriode.teamfight.game.TFRound;
import fr.hyriode.teamfight.util.TFValues;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Arrays;
import java.util.List;

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
        final TFGame game = this.plugin.getGame();
        final TFPlayer gamePlayer = game.getPlayer(event.getPlayer().getUniqueId());

        if (gamePlayer == null) {
            return;
        }

        if (block.getType() != Material.SANDSTONE) {
            event.setCancelled(true);
            return;
        } else if (!block.hasMetadata(TFGame.BLOCKS_METADATA) && TFValues.BLOCKS.get()) {
            this.plugin.getGame().getRound().addBrokenBlock(block);
        }

        if (!TFValues.SPLEEF.get()){
            final Location location = block.getLocation();
            final List<Location> locations = Arrays.asList(
                    location,
                    location.clone().add(1, 0, 0),
                    location.clone().add(0, 0, 1),
                    location.clone().add(-1, 0, 0),
                    location.clone().add(0, 0, -1)
            );

            boolean cancel = false;
            for (HyriGamePlayer target : game.getPlayers()) {
                if (!target.isOnline() || target.isDead() || target.isSpectator()) {
                    continue;
                }

                if (!target.equals(gamePlayer)) {
                    for (Location loc : locations) {
                        if (LocationUtil.roundLocation(target.getPlayer().getLocation().subtract(0, 1, 0), 0).equals(loc)) {
                            cancel = true;
                        }
                    }
                }
            }

            if (cancel) {
                event.setCancelled(true);
            }
        } else if (!TFValues.BLOCKS.get()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getSlotType() == InventoryType.SlotType.ARMOR) {
            event.setCancelled(true);
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        final TFGame game = this.plugin.getGame();
        final TFPlayer gamePlayer = game.getPlayer(player);

        if (gamePlayer == null) {
            return;
        }
        
        if (game.getState() != HyriGameState.PLAYING) {
            return;
        }

        if (game.getRound().isPhase(Phase.WAITING)) {
            if (!player.isOnGround()) {
                event.setTo(event.getFrom());
            }
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onItemSpawn(ItemSpawnEvent event) {
        event.setCancelled(true);
    }

}