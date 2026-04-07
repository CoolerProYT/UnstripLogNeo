package com.coolerpromc.unstriplog.platform.util;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record NeoForgePayloadContext(IPayloadContext ctx) implements PayloadContext{
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
        ctx.enqueueWork(runnable);
    }

    @Override
    public void disconnect(Component reason) {
        ctx.disconnect(reason);
    }
}
