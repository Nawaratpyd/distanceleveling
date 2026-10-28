package com.example.distanceleveling.client;

import com.example.distanceleveling.ClientLevels;
import com.example.distanceleveling.Config;
import com.example.distanceleveling.DistanceLeveling;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RenderNameTagEvent;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;

@EventBusSubscriber(modid = DistanceLeveling.MODID, value = Dist.CLIENT)
public final class ClientEvents {
    private ClientEvents() {
    }

    @SubscribeEvent
    public static void onNameTag(RenderNameTagEvent event) {
        Entity entity = event.getEntity();
        int level = ClientLevels.get(entity.getId());
        if (level <= 0) {
            return;
        }

        boolean always = Config.ALWAYS_SHOW_LEVEL.get();
        boolean lookingAt = Config.SHOW_LEVEL_WHEN_LOOKING_AT.get()
                && Minecraft.getInstance().crosshairPickEntity == entity;
        if (!always && !lookingAt) {
            return;
        }

        Component prefix = Component.literal("Lv." + level + " ").withStyle(ChatFormatting.YELLOW);
        event.setContent(Component.empty().append(prefix).append(event.getContent()));
        event.setCanRender(TriState.TRUE);
    }

    @SubscribeEvent
    public static void onEntityLeave(EntityLeaveLevelEvent event) {
        if (event.getLevel().isClientSide()) {
            ClientLevels.remove(event.getEntity().getId());
        }
    }

    @SubscribeEvent
    public static void onLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        ClientLevels.clear();
    }
}
