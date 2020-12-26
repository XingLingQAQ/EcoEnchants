package com.willfp.ecoenchants.enchantments.meta;

import com.willfp.eco.util.config.Configs;
import com.willfp.eco.util.config.annotations.ConfigUpdater;
import com.willfp.eco.util.interfaces.Updatable;
import com.willfp.eco.util.lambda.ObjectCallable;
import com.willfp.ecoenchants.enchantments.EcoEnchant;
import com.willfp.ecoenchants.enchantments.itemtypes.Artifact;
import com.willfp.ecoenchants.enchantments.itemtypes.Spell;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class EnchantmentType implements Updatable {
    private static final List<EnchantmentType> REGISTERED = new ArrayList<>();

    public static final EnchantmentType NORMAL = new EnchantmentType(
            "normal",
            false,
            () -> Configs.LANG.getString("not-curse-color")
    );
    public static final EnchantmentType CURSE = new EnchantmentType(
            "curse",
            false,
            () -> Configs.LANG.getString("curse-color")
    );
    public static final EnchantmentType SPECIAL = new EnchantmentType(
            "special",
            () -> !Configs.CONFIG.getBool("types.special.allow-multiple"),
            () -> Configs.LANG.getString("special-color")
    );
    public static final EnchantmentType ARTIFACT = new EnchantmentType(
            "artifact",
            () -> !Configs.CONFIG.getBool("types.artifact.allow-multiple"),
            () -> Configs.LANG.getString("artifact-color"),
            Artifact.class
    );
    public static final EnchantmentType SPELL = new EnchantmentType(
            "spell",
            true,
            () -> Configs.LANG.getString("spell-color"),
            Spell.class
    );

    private boolean singular;
    private String color;
    private final String name;
    private final ObjectCallable<String> colorCallable;
    private final ObjectCallable<Boolean> singularCallable;
    private final Class<? extends EcoEnchant> requiredToExtend;

    /**
     * Create simple EnchantmentType
     * <p>
     * Singularity and Color will not be updated using this constructor
     *
     * @param name     The name of the type
     * @param singular Whether an item can have several enchantments of this type
     * @param color    The color for enchantments with this type in lore to have
     */
    public EnchantmentType(@NotNull final String name,
                           final boolean singular,
                           @NotNull final String color) {
        this(name, () -> singular, () -> color);
    }

    /**
     * Create EnchantmentType with updatable color
     * <p>
     * Singularity will not be updated using this constructor
     *
     * @param name          The name of the type
     * @param singular      Whether an item can have several enchantments of this type
     * @param colorCallable Lambda to fetch the color of enchantments with this type to have. Updates on /ecoreload
     */
    public EnchantmentType(@NotNull final String name,
                           final boolean singular,
                           @NotNull final ObjectCallable<String> colorCallable) {
        this(name, () -> singular, colorCallable);
    }

    /**
     * Create EnchantmentType with updatable color that <b>must</b> extend a specified class
     * <p>
     * Singularity will not be updated using this constructor
     *
     * @param name             The name of the type
     * @param singular         Whether an item can have several enchantments of this type
     * @param colorCallable    Lambda to fetch the color of enchantments with this type to have. Updates on /ecoreload
     * @param requiredToExtend Class that all enchantments of this type must extend - or null if not required
     */
    public EnchantmentType(@NotNull final String name,
                           final boolean singular,
                           @NotNull final ObjectCallable<String> colorCallable,
                           @Nullable final Class<? extends EcoEnchant> requiredToExtend) {
        this(name, () -> singular, colorCallable, requiredToExtend);
    }

    /**
     * Create EnchantmentType with updatable color and singularity
     *
     * @param name             The name of the type
     * @param singularCallable Lambda to fetch whether an item can have several enchantments of this type. Updates on /ecoreload
     * @param colorCallable    Lambda to fetch the color of enchantments with this type to have. Updates on /ecoreload
     */
    public EnchantmentType(@NotNull final String name,
                           @NotNull final ObjectCallable<Boolean> singularCallable,
                           @NotNull final ObjectCallable<String> colorCallable) {
        this(name, singularCallable, colorCallable, null);
    }

    /**
     * Create EnchantmentType with updatable color and singularity that <b>must</b> extend a specified class
     *
     * @param name             The name of the type
     * @param singularCallable Lambda to fetch whether an item can have several enchantments of this type. Updates on /ecoreload
     * @param colorCallable    Lambda to fetch the color of enchantments with this type to have. Updates on /ecoreload
     * @param requiredToExtend Class that all enchantments of this type must extend - or null if not required
     */
    public EnchantmentType(@NotNull final String name,
                           @NotNull final ObjectCallable<Boolean> singularCallable,
                           @NotNull final ObjectCallable<String> colorCallable,
                           @Nullable final Class<? extends EcoEnchant> requiredToExtend) {
        this.name = name;
        this.singularCallable = singularCallable;
        this.colorCallable = colorCallable;
        this.requiredToExtend = requiredToExtend;
        color = colorCallable.call();
        singular = singularCallable.call();
        REGISTERED.add(this);
    }

    private void refresh() {
        this.color = colorCallable.call();
        this.singular = singularCallable.call();
    }

    public String getColor() {
        return color;
    }

    public boolean isSingular() {
        return singular;
    }

    public String getName() {
        return name;
    }

    public Class<? extends EcoEnchant> getRequiredToExtend() {
        return requiredToExtend;
    }

    @ConfigUpdater
    public static void update() {
        REGISTERED.forEach(EnchantmentType::refresh);
    }

    public static List<EnchantmentType> values() {
        return new ArrayList<>(REGISTERED);
    }
}
