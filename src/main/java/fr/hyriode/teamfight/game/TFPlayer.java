package fr.hyriode.teamfight.game;

import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.game.protocol.HyriLastHitterProtocol;
import fr.hyriode.hyrame.game.team.HyriGameTeam;
import fr.hyriode.hyrame.item.ItemBuilder;
import fr.hyriode.hyrame.utils.PlayerUtil;
import fr.hyriode.teamfight.HyriTeamFight;
import fr.hyriode.teamfight.api.TFData;
import fr.hyriode.teamfight.api.TFHotBar;
import fr.hyriode.teamfight.api.TFStatistics;
import fr.hyriode.teamfight.game.scoreboard.TFScoreboard;
import fr.hyriode.teamfight.game.team.TFTeam;
import fr.hyriode.teamfight.util.TFValues;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.List;

/**
 * Created by AstFaster
 * on 16/05/2023 at 18:52
 */
public class TFPlayer extends HyriGamePlayer {

    private int kills = 0;
    private int deaths = 0;
    private int roundsWon = 0;

    private TFStatistics statistics;
    private TFData data;

    private TFScoreboard scoreboard;

    private final TFGame game;

    public TFPlayer(Player player) {
        super(player);
        this.game = HyriTeamFight.get().getGame();
    }

    public void init() {
        this.scoreboard = new TFScoreboard(HyriTeamFight.get(), this.player);
        this.scoreboard.show();
    }

    public void spawn() {
        PlayerUtil.resetPlayer(this.player, true);

        this.player.setGameMode(GameMode.SURVIVAL);
        this.player.setMaxHealth(28.0D);
        this.player.setHealth(28.0D);

        this.setNotDead();

        this.player.setAllowFlight(false);
        this.player.setFlying(false);
        this.player.spigot().setCollidesWithEntities(true);

        this.show();

        this.giveItems();
    }

    private void giveItems() {
        final PlayerInventory inventory = this.player.getInventory();
        final TFHotBar hotBar = this.data.getHotBar();

        inventory.addItem(new ItemBuilder(Material.SANDSTONE, 64 * 9, 2).build());
        inventory.setItem(hotBar.getSlot(TFHotBar.Item.SWORD), new ItemBuilder(Material.IRON_SWORD).withEnchant(Enchantment.DAMAGE_ALL, 1).unbreakable().build());
        inventory.setItem(hotBar.getSlot(TFHotBar.Item.PICKAXE), new ItemBuilder(Material.IRON_PICKAXE).withEnchant(Enchantment.DIG_SPEED, 2).unbreakable().build());
        inventory.setItem(hotBar.getSlot(TFHotBar.Item.GOLDEN_APPLE), new ItemBuilder(Material.GOLDEN_APPLE, TFValues.GOLDEN_APPLES.get()).build());

        final Color color = this.getTeam().getColor().getDyeColor().getColor();

        inventory.setHelmet(new ItemBuilder(Material.LEATHER_HELMET)
                .withLeatherArmorColor(color)
                .withEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1)
                .unbreakable()
                .build());

        inventory.setChestplate(new ItemBuilder(Material.LEATHER_CHESTPLATE)
                .withEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                .unbreakable()
                .build());

        inventory.setLeggings(new ItemBuilder(Material.LEATHER_LEGGINGS)
                .withLeatherArmorColor(color)
                .withEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                .unbreakable()
                .build());

        inventory.setBoots(new ItemBuilder(Material.LEATHER_BOOTS)
                .withLeatherArmorColor(color)
                .withEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1)
                .withEnchant(Enchantment.PROTECTION_FALL, 1)
                .unbreakable()
                .build());
    }

    public void onDeath() {
        // Save hot bar
        final PlayerInventory inventory = this.player.getInventory();

        for (TFHotBar.Item item : this.data.getHotBar().getItems().keySet()) {
            for (int i = 0; i <= 9; i++) {
                final ItemStack itemStack = inventory.getItem(i);

                if (itemStack != null && itemStack.getType() == Material.getMaterial(item.getName())) {
                    this.data.getHotBar().setItem(item, i);
                }
            }
        }

        // Killer handling
        final TFPlayer killer = this.getLastHitter();

        if (killer != null) {
            killer.addKill();
            killer.getScoreboard().update();
        }

        this.deaths++;

        this.scoreboard.update();
        this.game.getRound().checkWin();
    }

    private TFPlayer getLastHitter() {
        final List<HyriLastHitterProtocol.LastHitter> lastHitters = HyriTeamFight.get().getGame().getProtocolManager().getProtocol(HyriLastHitterProtocol.class).getLastHitters(this.player);

        if (lastHitters != null) {
            return lastHitters.get(0).asGamePlayer().cast();
        }
        return null;
    }

    public TFScoreboard getScoreboard() {
        return this.scoreboard;
    }

    public TFStatistics getStatistics() {
        return this.statistics;
    }

    public void setStatistics(TFStatistics statistics) {
        this.statistics = statistics;
    }

    public TFData getData() {
        return this.data;
    }

    public void setData(TFData data) {
        this.data = data;
    }

    public void addKill() {
        this.kills++;
    }

    public int getKills() {
        return this.kills;
    }

    public int getDeaths() {
        return this.deaths;
    }

    public void addRoundWon() {
        this.roundsWon++;
    }

    public int getRoundsWon() {
        return this.roundsWon;
    }

    @Override
    public TFTeam getTeam() {
        return (TFTeam) super.getTeam();
    }

}
