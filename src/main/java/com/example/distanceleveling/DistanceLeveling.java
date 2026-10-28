package com.example.distanceleveling;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@Mod(DistanceLeveling.MODID)
public class DistanceLeveling {
    public static final String MODID = "distanceleveling";

    public DistanceLeveling(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        modEventBus.addListener(this::registerPayloads);

        NeoForge.EVENT_BUS.addListener(LevelingEvents::onEntityJoin);
        NeoForge.EVENT_BUS.addListener(LevelingEvents::onStartTracking);
        NeoForge.EVENT_BUS.addListener(LevelingEvents::onExperienceDrop);
    }

    private void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(
                MobLevelPayload.TYPE,
                MobLevelPayload.STREAM_CODEC,
                (payload, context) -> ClientLevels.set(payload.entityId(), payload.level()));
    }
}
