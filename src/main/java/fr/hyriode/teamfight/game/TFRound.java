package fr.hyriode.teamfight.game;

import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.title.Title;
import fr.hyriode.hyrame.utils.BroadcastUtil;
import fr.hyriode.hyrame.utils.block.BlockUtil;
import fr.hyriode.teamfight.HyriTeamFight;
import fr.hyriode.teamfight.game.scoreboard.TFScoreboard;
import fr.hyriode.teamfight.game.team.TFTeam;
import fr.hyriode.teamfight.language.TFMessage;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by AstFaster
 * on 16/05/2023 at 19:10
 */
public class TFRound {

    private Phase phase = Phase.WAITING;
    private final List<Block> placedBlocks = new ArrayList<>();
    private final List<Block> brokenBlocks = new ArrayList<>();

    private TFGame game;

    public void start() {
        this.game = HyriTeamFight.get().getGame();
        this.phase = Phase.WAITING;

        this.game.getTeams().forEach(team -> ((TFTeam) team).spawnPlayers());

        for (TFPlayer player : this.game.getPlayers()) {
            if (!player.isOnline()) {
                continue;
            }

            player.spawn();
        }

        // Start timer
        new BukkitRunnable() {

            private int index = 5;

            @Override
            public void run() {
                if (this.index == 0) {
                    phase = Phase.FIGHTING;

                    this.alert(TFMessage.STARTING_GO::asString, Sound.ENDERDRAGON_GROWL, 1.0f, 0.5f);
                    this.cancel();
                } else if (this.index <= 3){
                    this.alert(target -> ChatColor.AQUA + String.valueOf(this.index), Sound.NOTE_PLING, 0.2f * (5 - this.index), 0.5f);
                } else {
                    this.alert(target -> ChatColor.DARK_AQUA + String.valueOf(this.index), Sound.NOTE_PLING, this.index == 5 ? 0.2f : 0.2f * (5 - this.index), 0.5f);
                }

                this.index--;
            }

            private void alert(Function<Player, String> message, Sound sound, float volume, float pitch) {
                for (TFPlayer gamePlayer : game.getPlayers()) {
                    if (!gamePlayer.isOnline()) {
                        continue;
                    }

                    final Player player = gamePlayer.getPlayer();

                    player.playSound(player.getLocation(), sound, volume, pitch);
                    Title.sendTitle(player, message.apply(player), "", 0, 25, 0);
                }
            }

        }.runTaskTimer(HyriTeamFight.get(), 0L, 20L);
    }

    public void checkWin() {
        // Check if the round is won
        final TFTeam winner = this.getWinner();

        if (winner != null) {
            this.win();
        }
    }

    public void win() {
        final TFTeam winner = this.getWinner();

        this.phase = Phase.ENDED;

        winner.addPoint();

        IHyrame.get().getScoreboardManager().getScoreboards(TFScoreboard.class).forEach(TFScoreboard::update);

        BroadcastUtil.broadcast(player -> TFMessage.ROUND_WIN.asString(player).replace("%team%", winner.getFormattedDisplayName(player)));

        // Check if the team has won enough rounds
        if (winner.hasWon()) {
            this.game.win(winner);
            return;
        }

        new BukkitRunnable() {

            private int i = 0;

            public void run() {
                if (this.i == 3) {
                    this.cancel();
                    return;
                }

                this.spawnFireworks();

                this.i++;
            }

            private void spawnFireworks() {
                final World world = IHyrame.WORLD.get();
                final Location location = game.getConfig().getCenter().asBukkit();
                final Supplier<Double> random = () -> ThreadLocalRandom.current().nextDouble(-4.0, 4.0D);

                for (int i = 0; i < 4; i++) {
                    world.spawnEntity(location.clone().add(random.get(), 0.0D, random.get()), EntityType.FIREWORK);
                }
            }

        }.runTaskTimer(HyriTeamFight.get(), 0L, 20L);

        Bukkit.getScheduler().runTaskLater(HyriTeamFight.get(), this.game::newRound, 4 * 20L);
    }

    public void free() {
        // Reset blocks types
        BlockUtil.setBlocksFaster(this.placedBlocks, 0, 0);
        BlockUtil.setBlocksFaster(this.brokenBlocks, 24, 2);

        // Remove metadata
        for (Block block : this.placedBlocks) {
            block.removeMetadata(TFGame.BLOCKS_METADATA, HyriTeamFight.get());
        }

        // Clear lists
        this.placedBlocks.clear();
        this.brokenBlocks.clear();
    }

    public TFTeam getWinner() {
        final TFTeam firstTeam = this.game.getFirstTeam();
        final TFTeam secondTeam = this.game.getSecondTeam();

        if (firstTeam.getRemainingPlayers().size() > 0 && secondTeam.getRemainingPlayers().size() == 0) {
            return firstTeam;
        } else if (secondTeam.getRemainingPlayers().size() > 0 && firstTeam.getRemainingPlayers().size() == 0) {
            return secondTeam;
        }
        return null;
    }

    public Phase getPhase() {
        return this.phase;
    }

    public boolean isPhase(Phase... phases) {
        for (Phase phase : phases) {
            if (this.phase == phase) {
                return true;
            }
        }
        return false;
    }

    public List<Block> getPlacedBlocks() {
        return this.placedBlocks;
    }

    public void addPlacedBlock(Block block) {
        this.placedBlocks.add(block);
    }

    public List<Block> getBrokenBlocks() {
        return this.brokenBlocks;
    }

    public void addBrokenBlock(Block block) {
        this.brokenBlocks.add(block);
    }

    public enum Phase {

        WAITING,
        FIGHTING,
        ENDED

    }

}
