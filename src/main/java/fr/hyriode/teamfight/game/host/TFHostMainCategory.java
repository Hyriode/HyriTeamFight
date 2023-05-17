package fr.hyriode.teamfight.game.host;

import fr.hyriode.hyrame.host.HostCategory;
import fr.hyriode.hyrame.host.option.BooleanOption;
import fr.hyriode.hyrame.host.option.IntegerOption;
import fr.hyriode.hyrame.utils.UsefulDisplay;
import fr.hyriode.teamfight.util.TFValues;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Created by AstFaster
 * on 17/05/2023 at 16:20
 */
public class TFHostMainCategory extends HostCategory {

    public TFHostMainCategory() {
        super(UsefulDisplay.categoryDisplay("teamfight-main", new ItemStack(Material.DIAMOND_SWORD)));

        this.addOption(21, new IntegerOption(UsefulDisplay.optionDisplay("golden-apples", Material.GOLDEN_APPLE), TFValues.GOLDEN_APPLES.getDefaultValue(), 0, 64));
        this.addOption(22, new IntegerOption(UsefulDisplay.optionDisplay("win-points", Material.GHAST_TEAR), TFValues.WIN_POINTS.getDefaultValue(), 1, Integer.MAX_VALUE));
        this.addOption(23, new BooleanOption(UsefulDisplay.optionDisplay("spleef", Material.IRON_PICKAXE), TFValues.SPLEEF.getDefaultValue()));
        this.addOption(30, new BooleanOption(UsefulDisplay.optionDisplay("blocks", Material.SANDSTONE), TFValues.BLOCKS.getDefaultValue()));
        this.addOption(31, new BooleanOption(UsefulDisplay.optionDisplay("two-points-difference", Material.LADDER), TFValues.TWO_POINTS_DIFFERENCE.getDefaultValue()));
        this.addOption(32, new BooleanOption(UsefulDisplay.optionDisplay("golden-apple-kill", Material.IRON_SWORD), TFValues.GOLDEN_APPLE_ON_KILL.getDefaultValue()));
    }

}
