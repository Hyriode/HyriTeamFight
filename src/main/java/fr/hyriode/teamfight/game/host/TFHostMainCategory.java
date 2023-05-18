package fr.hyriode.teamfight.game.host;

import fr.hyriode.hyrame.host.HostCategory;
import fr.hyriode.hyrame.host.option.BooleanOption;
import fr.hyriode.hyrame.host.option.IntegerOption;
import fr.hyriode.hyrame.item.ItemBuilder;
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
        super(UsefulDisplay.categoryDisplay("teamfight-main", new ItemBuilder(Material.DIAMOND_SWORD).withAllItemFlags().build()));

        this.addOption(20, new IntegerOption(UsefulDisplay.optionDisplay("win-points", Material.GHAST_TEAR), TFValues.WIN_POINTS.getDefaultValue(), 1, Integer.MAX_VALUE));
        this.addOption(21, new IntegerOption(UsefulDisplay.optionDisplay("max-points", Material.REDSTONE), TFValues.MAX_POINTS.getDefaultValue(), 1, Integer.MAX_VALUE));
        this.addOption(29, new BooleanOption(UsefulDisplay.optionDisplay("two-points-difference", Material.LADDER), TFValues.TWO_POINTS_DIFFERENCE.getDefaultValue()));
        this.addOption(23, new IntegerOption(UsefulDisplay.optionDisplay("golden-apples", Material.GOLDEN_APPLE), TFValues.GOLDEN_APPLES.getDefaultValue(), 0, 64));
        this.addOption(24, new BooleanOption(UsefulDisplay.optionDisplay("golden-apple-kill", Material.IRON_SWORD), TFValues.GOLDEN_APPLE_ON_KILL.getDefaultValue()));
        this.addOption(32, new BooleanOption(UsefulDisplay.optionDisplay("spleef", Material.IRON_PICKAXE), TFValues.SPLEEF.getDefaultValue()));
        this.addOption(33, new BooleanOption(UsefulDisplay.optionDisplay("blocks", Material.SANDSTONE), TFValues.BLOCKS.getDefaultValue()));
    }

}
