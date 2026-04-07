package com.coolerpromc.unstriplog.platform.util;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public record FabricClientPayloadContext(ClientPlayNetworking.Context ctx) implements PayloadContext{
    @Override
    public Player player() {
        return ctx.player();
    }

    @Override
    public Level level() {
        return ctx.player().level();
    }

    @Override
    public void execute(Runnable runnable) {
        ctx.client().execute(runnable);
    }

    @Override
    public void disconnect(Component reason) {
        ctx.responseSender().disconnect(reason);
    }
}
