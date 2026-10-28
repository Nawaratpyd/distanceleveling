package com.example.distanceleveling;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/** Server -> client: tells the client which level a tracked mob has. */
public record MobLevelPayload(int entityId, int level) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<MobLevelPayload> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(DistanceLeveling.MODID, "mob_level"));

    public static final StreamCodec<ByteBuf, MobLevelPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, MobLevelPayload::entityId,
            ByteBufCodecs.VAR_INT, MobLevelPayload::level,
            MobLevelPayload::new);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
