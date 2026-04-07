package com.coolerpromc.unstriplog.platform.services;

import net.minecraft.server.MinecraftServer;

import java.nio.file.Path;

public interface IPlatformHelper {
    String getPlatformName();
    boolean isModLoaded(String modId);
    boolean isDevelopmentEnvironment();
    Path getConfigDir();
    boolean isClient();
    MinecraftServer getServer();

    default String getEnvironmentName() {
        return isDevelopmentEnvironment() ? "development" : "production";
    }
}