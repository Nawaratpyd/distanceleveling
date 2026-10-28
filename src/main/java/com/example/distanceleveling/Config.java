package com.example.distanceleveling;

import java.util.List;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue SHOW_LEVEL_WHEN_LOOKING_AT = BUILDER
            .comment("Show the level above a mob when you aim at it.")
            .define("show_level_when_looking_at", true);

    public static final ModConfigSpec.BooleanValue ALWAYS_SHOW_LEVEL = BUILDER
            .comment("Always show the level above mobs.")
            .define("always_show_level", false);

    public static final ModConfigSpec.IntValue STARTING_LEVEL = BUILDER
            .comment("Level of mobs at spawn. Must be at least 1.")
            .defineInRange("starting_level", 1, 1, 10000);

    public static final ModConfigSpec.IntValue MAX_LEVEL = BUILDER
            .comment("Maximum mob level. 0 = no limit.")
            .defineInRange("max_level", 0, 0, 100000);

    public static final ModConfigSpec.DoubleValue LEVELS_PER_DISTANCE = BUILDER
            .comment("Levels gained per block travelled (horizontally) from world spawn. 0.01 = 1 level per 100 blocks.")
            .defineInRange("levels_per_distance", 0.01, 0.0, 100.0);

    public static final ModConfigSpec.DoubleValue LEVELS_PER_DEEPNESS = BUILDER
            .comment("Levels gained per block below sea level.")
            .defineInRange("levels_per_deepness", 0.0, 0.0, 100.0);

    public static final ModConfigSpec.IntValue RANDOM_LEVEL_BONUS = BUILDER
            .comment("Mobs may get a random extra 0..N levels.")
            .defineInRange("random_level_bonus", 0, 0, 10000);

    public static final ModConfigSpec.DoubleValue EXP_BONUS_PER_LEVEL = BUILDER
            .comment("Extra experience per level. 0.1 = +10% per level.")
            .defineInRange("exp_bonus_per_level", 0.1, 0.0, 100.0);

    public static final ModConfigSpec.ConfigValue<List<? extends String>> ATTRIBUTE_BONUSES = BUILDER
            .comment("Attribute bonuses per level, format \"attribute_id=amount\".",
                    "The amount is a fraction of the base value per level (0.05 = +5% per level).")
            .defineListAllowEmpty("attribute_bonuses",
                    List.of("minecraft:generic.max_health=0.05", "minecraft:generic.attack_damage=0.03"),
                    () -> "minecraft:generic.max_health=0.05",
                    Config::isString);

    public static final ModConfigSpec.ConfigValue<List<? extends String>> BLACKLIST = BUILDER
            .comment("Entities that never get levels. Example: [\"minecraft:zombie\", \"somemod:*\"]")
            .defineListAllowEmpty("blacklist", List.of(), () -> "minecraft:zombie", Config::isString);

    public static final ModConfigSpec.ConfigValue<List<? extends String>> WHITELIST = BUILDER
            .comment("If not empty, ONLY these entities get levels (any mob type). If empty, all hostile mobs get levels.")
            .defineListAllowEmpty("whitelist", List.of(), () -> "minecraft:zombie", Config::isString);

    static final ModConfigSpec SPEC = BUILDER.build();

    private static boolean isString(Object o) {
        return o instanceof String;
    }
}
