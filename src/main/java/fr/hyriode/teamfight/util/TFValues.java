package fr.hyriode.teamfight.util;

import fr.hyriode.hyrame.game.util.value.HostValueModifier;
import fr.hyriode.hyrame.game.util.value.ValueProvider;

/**
 * Created by AstFaster
 * on 16/05/2023 at 20:44
 */
public class TFValues {

    public static final ValueProvider<Integer> GOLDEN_APPLES = new ValueProvider<>(8).addModifiers(new HostValueModifier<>(1, Integer.class, "golden-apples"));
    public static final ValueProvider<Integer> WIN_POINTS = new ValueProvider<>(3).addModifiers(new HostValueModifier<>(1, Integer.class, "win-points"));
    public static final ValueProvider<Integer> MAX_POINTS = new ValueProvider<>(5).addModifiers(new HostValueModifier<>(1, Integer.class, "max-points"));
    public static final ValueProvider<Boolean> SPLEEF = new ValueProvider<>(true).addModifiers(new HostValueModifier<>(1, Boolean.class, "spleef"));
    public static final ValueProvider<Boolean> BLOCKS = new ValueProvider<>(true).addModifiers(new HostValueModifier<>(1, Boolean.class, "blocks"));
    public static final ValueProvider<Boolean> TWO_POINTS_DIFFERENCE = new ValueProvider<>(true).addModifiers(new HostValueModifier<>(1, Boolean.class, "two-points-difference"));
    public static final ValueProvider<Boolean> GOLDEN_APPLE_ON_KILL = new ValueProvider<>(false).addModifiers(new HostValueModifier<>(1, Boolean.class, "golden-apple-kill"));

}
