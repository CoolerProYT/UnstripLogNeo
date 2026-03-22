package com.coolerpromc.unstriplog.datagen.model;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

public class BarkItemClientItemExtension implements IClientItemExtensions {
    private final BarkItemRenderer barkItemRenderer = new BarkItemRenderer();

    @Override
    public BlockEntityWithoutLevelRenderer getCustomRenderer() {
        return barkItemRenderer;
    }
}
