package com.example.distanceleveling;

import java.util.List;
import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.monster.Enemy;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingExperienceDropEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;

/** Server-side logic: assigns levels to mobs, applies bonuses, syncs levels to clients. */
public final class LevelingEvents {
    /** NBT key (in the entity's persistent data) holding the mob's level. */
    public static final String LEVEL_KEY = DistanceLeveling.MODID + "_level";

    private static final ResourceLocation MODIFIER_ID =
            ResourceLocation.fromNamespaceAndPath(DistanceLeveling.MODID, "level_bonus");

    private LevelingEvents() {
    }

    public static int getLevel(Entity entity) {
        CompoundTag data = entity.getPersistentData();
        return data.contains(LEVEL_KEY) ? data.getInt(LEVEL_KEY) : 0;
    }

    public static void onEntityJoin(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) {
            return;
        }
        if (!(event.getEntity() instanceof Mob mob) || !(event.getLevel() instanceof ServerLevel level)) {
            return;
        }
        CompoundTag data = mob.getPersistentData();
        if (data.contains(LEVEL_KEY)) {
            return; // already levelled (e.g. reloaded from disk)
        }
        if (!isLevelable(mob)) {
            return;
        }

        int mobLevel = computeLevel(level, mob);
        data.putInt(LEVEL_KEY, mobLevel);
        applyBonuses(mob, mobLevel);
    }

    public static void onStartTracking(PlayerEvent.StartTracking event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        Entity target = event.getTarget();
        int mobLevel = getLevel(target);
        if (mobLevel > 0) {
            PacketDistributor.sendToPlayer(player, new MobLevelPayload(target.getId(), mobLevel));
        }
    }

    public static void onExperienceDrop(LivingExperienceDropEvent event) {
        LivingEntity entity = event.getEntity();
        int mobLevel = getLevel(entity);
        if (mobLevel <= 1) {
            return;
        }
        double multiplier = 1.0 + Config.EXP_BONUS_PER_LEVEL.get() * (mobLevel - 1);
        event.setDroppedExperience((int) Math.round(event.getDroppedExperience() * multiplier));
    }

    private static int computeLevel(ServerLevel level, Mob mob) {
        BlockPos spawn = level.getSharedSpawnPos();
        double dx = mob.getX() - spawn.getX();
        double dz = mob.getZ() - spawn.getZ();
        double distance = Math.sqrt(dx * dx + dz * dz);
        double depth = Math.max(0.0, level.getSeaLevel() - mob.getY());

        double gained = distance * Config.LEVELS_PER_DISTANCE.get() + depth * Config.LEVELS_PER_DEEPNESS.get();
        int result = Config.STARTING_LEVEL.get() + (int) gained;

        int randomBonus = Config.RANDOM_LEVEL_BONUS.get();
        if (randomBonus > 0) {
            result += mob.getRandom().nextInt(randomBonus + 1);
        }

        int max = Config.MAX_LEVEL.get();
        if (max > 0) {
            result = Math.min(result, max);
        }
        return Math.max(1, result);
    }

    private static void applyBonuses(Mob mob, int mobLevel) {
        int extraLevels = mobLevel - 1;
        if (extraLevels <= 0) {
            return;
        }
        for (String entry : Config.ATTRIBUTE_BONUSES.get()) {
            int eq = entry.indexOf('=');
            if (eq < 0) {
                continue;
            }
            ResourceLocation attributeId = ResourceLocation.tryParse(entry.substring(0, eq).trim());
            if (attributeId == null) {
                continue;
            }
            double perLevel;
            try {
                perLevel = Double.parseDouble(entry.substring(eq + 1).trim());
            } catch (NumberFormatException e) {
                continue;
            }
            Optional<Holder.Reference<Attribute>> attribute = BuiltInRegistries.ATTRIBUTE.getHolder(attributeId);
            if (attribute.isEmpty()) {
                continue;
            }
            AttributeInstance instance = mob.getAttribute(attribute.get());
            if (instance == null) {
                continue; // this mob doesn't have that attribute
            }
            instance.removeModifier(MODIFIER_ID);
            instance.addPermanentModifier(new AttributeModifier(
                    MODIFIER_ID, perLevel * extraLevels, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        }
        mob.setHealth(mob.getMaxHealth());
    }

    private static boolean isLevelable(Mob mob) {
        String id = BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType()).toString();
        if (matches(Config.BLACKLIST.get(), id)) {
            return false;
        }
        List<? extends String> whitelist = Config.WHITELIST.get();
        if (!whitelist.isEmpty()) {
            return matches(whitelist, id);
        }
        return mob instanceof Enemy;
    }

    /** Matches exact ids ("minecraft:zombie") and namespace wildcards ("minecraft:*"). */
    private static boolean matches(List<? extends String> patterns, String id) {
        for (String pattern : patterns) {
            if (pattern.endsWith(":*")) {
                if (id.startsWith(pattern.substring(0, pattern.length() - 1))) {
                    return true;
                }
            } else if (pattern.equals(id)) {
                return true;
            }
        }
        return false;
    }
}
