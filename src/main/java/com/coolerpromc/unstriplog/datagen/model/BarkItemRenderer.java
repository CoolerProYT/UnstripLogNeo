package com.coolerpromc.unstriplog.datagen.model;

import com.coolerpromc.unstriplog.UnstripLog;
import com.coolerpromc.unstriplog.component.ModDataComponents;
import com.coolerpromc.unstriplog.config.BarkTypeConfig;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class BarkItemRenderer extends BlockEntityWithoutLevelRenderer {
    private static final float DEPTH = 1.0f / 16.0f;
    private static final float HALF_DEPTH = DEPTH / 2.0f;
    private static final float FRONT_Z = 8.5f / 16.0f;
    private static final float BACK_Z = 7.5f / 16.0f;
    private static final float UV_INSET = 0.1f;
    private static final ConcurrentMap<ResourceLocation, TextureShape> SHAPE_CACHE = new ConcurrentHashMap<>();

    public BarkItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource buffer, int lightCoords, int overlayCoords) {
        BarkTypeConfig.BarkTypeEntry barkTypeEntry = stack.get(ModDataComponents.BARK_TYPE);
        ResourceLocation texture = UnstripLog.id("textures/item/bark.png");

        if (barkTypeEntry != null) {
            texture = barkTypeEntry.texture();
        }

        TextureShape shape = SHAPE_CACHE.computeIfAbsent(texture, BarkItemRenderer::loadTextureShape);

        VertexConsumer vertexConsumer = displayContext == ItemDisplayContext.GUI ? buffer.getBuffer(RenderType.text(texture)) : buffer.getBuffer(RenderType.entityCutout(texture));
        PoseStack.Pose pose = poseStack.last();

        // Front face
        vertex(vertexConsumer, pose, 0, 1, FRONT_Z, 0, 0, overlayCoords, lightCoords, 0, 0, 1);
        vertex(vertexConsumer, pose, 0, 0, FRONT_Z, 0, 1, overlayCoords, lightCoords, 0, 0, 1);
        vertex(vertexConsumer, pose, 1, 0, FRONT_Z, 1, 1, overlayCoords, lightCoords, 0, 0, 1);
        vertex(vertexConsumer, pose, 1, 1, FRONT_Z, 1, 0, overlayCoords, lightCoords, 0, 0, 1);

        // Back face
        vertex(vertexConsumer, pose, 1, 1, BACK_Z, 1, 0, overlayCoords, lightCoords, 0, 0, -1);
        vertex(vertexConsumer, pose, 1, 0, BACK_Z, 1, 1, overlayCoords, lightCoords, 0, 0, -1);
        vertex(vertexConsumer, pose, 0, 0, BACK_Z, 0, 1, overlayCoords, lightCoords, 0, 0, -1);
        vertex(vertexConsumer, pose, 0, 1, BACK_Z, 0, 0, overlayCoords, lightCoords, 0, 0, -1);

        emitOutlineGeometry(vertexConsumer, pose, shape, overlayCoords, lightCoords);
    }

    private static void emitOutlineGeometry(VertexConsumer consumer, PoseStack.Pose pose, TextureShape shape, int overlay, int light) {
        float width = shape.width();
        float height = shape.height();

        for (int y = 0; y < shape.height(); y++) {
            float yTop = 1.0f - (float) y / height;
            float yBottom = 1.0f - (float) (y + 1) / height;

            for (int x = 0; x < shape.width(); x++) {
                if (!shape.opaque(x, y)) {
                    continue;
                }

                float xLeft = (float) x / width;
                float xRight = (float) (x + 1) / width;

                float u0 = (x + UV_INSET) / width;
                float u1 = (x + 1.0f - UV_INSET) / width;
                float v0 = (y + UV_INSET) / height;
                float v1 = (y + 1.0f - UV_INSET) / height;

                if (shape.transparent(x, y - 1)) {
                    // Top edge
                    vertex(consumer, pose, xLeft, yTop, FRONT_Z, u0, v0, overlay, light, 0, 1, 0);
                    vertex(consumer, pose, xRight, yTop, FRONT_Z, u1, v0, overlay, light, 0, 1, 0);
                    vertex(consumer, pose, xRight, yTop, BACK_Z, u1, v1, overlay, light, 0, 1, 0);
                    vertex(consumer, pose, xLeft, yTop, BACK_Z, u0, v1, overlay, light, 0, 1, 0);
                }

                if (shape.transparent(x, y + 1)) {
                    // Bottom edge
                    vertex(consumer, pose, xLeft, yBottom, BACK_Z, u0, v0, overlay, light, 0, -1, 0);
                    vertex(consumer, pose, xRight, yBottom, BACK_Z, u1, v0, overlay, light, 0, -1, 0);
                    vertex(consumer, pose, xRight, yBottom, FRONT_Z, u1, v1, overlay, light, 0, -1, 0);
                    vertex(consumer, pose, xLeft, yBottom, FRONT_Z, u0, v1, overlay, light, 0, -1, 0);
                }

                if (shape.transparent(x - 1, y)) {
                    // Left edge
                    vertex(consumer, pose, xLeft, yTop, BACK_Z, u0, v0, overlay, light, -1, 0, 0);
                    vertex(consumer, pose, xLeft, yBottom, BACK_Z, u0, v1, overlay, light, -1, 0, 0);
                    vertex(consumer, pose, xLeft, yBottom, FRONT_Z, u1, v1, overlay, light, -1, 0, 0);
                    vertex(consumer, pose, xLeft, yTop, FRONT_Z, u1, v0, overlay, light, -1, 0, 0);
                }

                if (shape.transparent(x + 1, y)) {
                    // Right edge
                    vertex(consumer, pose, xRight, yTop, FRONT_Z, u0, v0, overlay, light, 1, 0, 0);
                    vertex(consumer, pose, xRight, yBottom, FRONT_Z, u0, v1, overlay, light, 1, 0, 0);
                    vertex(consumer, pose, xRight, yBottom, BACK_Z, u1, v1, overlay, light, 1, 0, 0);
                    vertex(consumer, pose, xRight, yTop, BACK_Z, u1, v0, overlay, light, 1, 0, 0);
                }
            }
        }
    }

    private static TextureShape loadTextureShape(ResourceLocation texture) {
        try (NativeImage image = NativeImage.read(Minecraft.getInstance().getResourceManager().open(texture))) {
            int width = image.getWidth();
            int height = image.getHeight();
            boolean[] opaque = new boolean[width * height];

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    opaque[y * width + x] = ((image.getPixelRGBA(x, y) >>> 24) & 0xFF) > 0;
                }
            }

            return new TextureShape(width, height, opaque);
        } catch (IOException e) {
            UnstripLog.LOGGER.warn("Failed to read bark texture {} for generated outline rendering, using a solid fallback", texture, e);
            return TextureShape.solidFallback();
        }
    }

    private static void vertex(VertexConsumer consumer, PoseStack.Pose pose, float x, float y, float z, float u, float v, int overlay, int light, float normalX, float normalY, float normalZ) {
        consumer.addVertex(pose, x, y, z)
                .setColor(255, 255, 255, 255)
                .setUv(u, v)
                .setOverlay(overlay)
                .setLight(light)
                .setNormal(pose, normalX, normalY, normalZ);
    }

    private record TextureShape(int width, int height, boolean[] opaquePixels) {
        private static TextureShape solidFallback() {
            return new TextureShape(16, 16, createSolidPixels());
        }

        private static boolean[] createSolidPixels() {
            boolean[] pixels = new boolean[16 * 16];
            java.util.Arrays.fill(pixels, true);
            return pixels;
        }

        private boolean opaque(int x, int y) {
            return x >= 0 && y >= 0 && x < width && y < height && opaquePixels[y * width + x];
        }

        private boolean transparent(int x, int y) {
            return !opaque(x, y);
        }
    }
}
