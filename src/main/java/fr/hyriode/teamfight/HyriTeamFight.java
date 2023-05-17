package fr.hyriode.teamfight;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.hyggdrasil.api.server.HyggServer;
import fr.hyriode.hyrame.HyrameLoader;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.plugin.IPluginProvider;
import fr.hyriode.teamfight.config.TFConfig;
import fr.hyriode.teamfight.game.TFGame;
import fr.hyriode.teamfight.game.host.TFHostMainCategory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

/**
 * Created by AstFaster
 * on 16/05/2023 at 18:42
 */
public class HyriTeamFight extends JavaPlugin implements IPluginProvider {

    public static final String NAME = "TeamFight";
    public static final String ID = "teamfight";
    private static final String PACKAGE = "fr.hyriode.teamfight";

    private static HyriTeamFight instance;

    private IHyrame hyrame;
    private TFConfig config;
    private TFGame game;

    @Override
    public void onEnable() {
        instance = this;

        log("Starting " + NAME + "...");

        this.hyrame = HyrameLoader.load(this);
        this.config = HyriAPI.get().getServer().getConfig(TFConfig.class);
        this.hyrame.getGameManager().registerGame(() -> this.game = new TFGame());

        if (HyriAPI.get().getServer().getAccessibility().equals(HyggServer.Accessibility.HOST)) {
            this.hyrame.getHostController().addCategory(25, new TFHostMainCategory());
        }

        HyriAPI.get().getServer().setState(HyggServer.State.READY);
    }

    @Override
    public void onDisable() {
        log("Stopping " + NAME + "...");

        this.hyrame.getGameManager().unregisterGame(this.game);
    }

    public static void log(Level level, String message) {
        String prefix = ChatColor.BLUE + "[" + NAME + "] ";

        if (level == Level.SEVERE) {
            prefix += ChatColor.RED;
        } else if (level == Level.WARNING) {
            prefix += ChatColor.YELLOW;
        } else {
            prefix += ChatColor.RESET;
        }

        Bukkit.getConsoleSender().sendMessage(prefix + message);
    }

    public static void log(String msg) {
        log(Level.INFO, msg);
    }

    public static HyriTeamFight get() {
        return instance;
    }

    public TFConfig getConfiguration() {
        return this.config;
    }

    public TFGame getGame() {
        return this.game;
    }

    @Override
    public JavaPlugin getPlugin() {
        return this;
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String[] getCommandsPackages() {
        return new String[] {PACKAGE};
    }

    @Override
    public String[] getListenersPackages() {
        return new String[] {PACKAGE};
    }

    @Override
    public String[] getItemsPackages() {
        return new String[] {PACKAGE};
    }

    @Override
    public String getLanguagesPath() {
        return "/lang/";
    }

}
