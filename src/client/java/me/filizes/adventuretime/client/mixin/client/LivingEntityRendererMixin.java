package me.filizes.adventuretime.client.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import me.filizes.adventuretime.util.IEntityDataSaver;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Mixin(net.minecraft.client.render.entity.LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity> {

    @Unique
    private static final Map<Integer, Float> adventureTime_lastHealthMap = new ConcurrentHashMap<>();
    @Unique
    private static final Map<Integer, Long> adventureTime_lastDamageTimeMap = new ConcurrentHashMap<>();
    @Unique
    private static final long HEALTH_BAR_VISIBLE_DURATION = 5000L;

    @Unique
    private static final float MAX_RENDER_DISTANCE = 32.0f;
    @Unique
    private static final int BAR_WIDTH = 50;
    @Unique
    private static final int BAR_HEIGHT = 4;

    @Unique
    private static final Identifier PYRO_ICON_TEXTURE = new Identifier("adventuretime", "textures/gui/status/pyro.png");
    @Unique
    private static final Identifier HYDRO_ICON_TEXTURE = new Identifier("adventuretime", "textures/gui/status/hydro.png");

    @Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("TAIL"))
    private void renderCustomUI(T entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;

        if (player == null || entity.isInvisible() || entity == player) return;

        if (!entity.isAlive()) {
            adventureTime_lastHealthMap.remove(entity.getId());
            adventureTime_lastDamageTimeMap.remove(entity.getId());
            return;
        }

        if (entity.squaredDistanceTo(player) > MAX_RENDER_DISTANCE * MAX_RENDER_DISTANCE) return;

        float currentHealth = entity.getHealth();
        float lastHealth = adventureTime_lastHealthMap.getOrDefault(entity.getId(), currentHealth);
        if (currentHealth < lastHealth) {
            adventureTime_lastDamageTimeMap.put(entity.getId(), System.currentTimeMillis());
        }
        adventureTime_lastHealthMap.put(entity.getId(), currentHealth);

        boolean shouldRenderHealthBar = false;
        long lastDamageTime = adventureTime_lastDamageTimeMap.getOrDefault(entity.getId(), 0L);
        if (System.currentTimeMillis() - lastDamageTime < HEALTH_BAR_VISIBLE_DURATION) {
            shouldRenderHealthBar = true;
        }
        if (entity instanceof HostileEntity hostile && hostile.getTarget() == player) {
            shouldRenderHealthBar = true;
        }

        matrices.push();
        matrices.translate(0, entity.getHeight() + 0.5, 0);
        matrices.multiply(client.getEntityRenderDispatcher().getRotation());
        matrices.scale(-0.025f, -0.025f, 0.025f);
        Matrix4f matrix = matrices.peek().getPositionMatrix();

        if (shouldRenderHealthBar) {
            int barColor = (entity instanceof HostileEntity) ? 0xFFFF5555 : 0xFFFFFF55;
            float maxHealth = entity.getMaxHealth();
            float healthPercentage = MathHelper.clamp(currentHealth / maxHealth, 0.0f, 1.0f);
            int currentBarWidth = (int) (healthPercentage * BAR_WIDTH);
            fillRect(matrix, -BAR_WIDTH / 2 - 1, -1, BAR_WIDTH + 2, BAR_HEIGHT + 2, 0, 0, 0, 0.5f);
            float r = (float)(barColor >> 16 & 255) / 255.0F;
            float g = (float)(barColor >> 8 & 255) / 255.0F;
            float b = (float)(barColor & 255) / 255.0F;
            fillRect(matrix, -BAR_WIDTH / 2, 0, currentBarWidth, BAR_HEIGHT, r, g, b, 1.0f);
        }

        int iconXOffset = -BAR_WIDTH / 2;
        int iconY = -12;
        int iconSize = 10;

        NbtCompound data = ((IEntityDataSaver) entity).adventureTime$getPersistentData();

        if (data.getBoolean("IsPyro")) {
            RenderSystem.setShaderTexture(0, PYRO_ICON_TEXTURE);
            drawTexture(matrices, iconXOffset, iconY, iconSize, iconSize);
            iconXOffset += iconSize + 2;
        }
        if (data.getBoolean("IsHydro")) {
            RenderSystem.setShaderTexture(0, HYDRO_ICON_TEXTURE);
            drawTexture(matrices, iconXOffset, iconY, iconSize, iconSize);
        }

        matrices.pop();
    }

    @Unique
    private void fillRect(Matrix4f matrix, int x, int y, int width, int height, float r, float g, float b, float a) {
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();

        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(matrix, x, y, 0.0F).color(r, g, b, a).next();
        bufferBuilder.vertex(matrix, x, y + height, 0.0F).color(r, g, b, a).next();
        bufferBuilder.vertex(matrix, x + width, y + height, 0.0F).color(r, g, b, a).next();
        bufferBuilder.vertex(matrix, x + width, y, 0.0F).color(r, g, b, a).next();
        Tessellator.getInstance().draw();

        RenderSystem.disableBlend();
    }

    @Unique
    private void drawTexture(MatrixStack matrices, int x, int y, int width, int height) {
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();

        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(matrix, x, y, 0).texture(0, 0).next();
        bufferBuilder.vertex(matrix, x, y + height, 0).texture(0, 1).next();
        bufferBuilder.vertex(matrix, x + width, y + height, 0).texture(1, 1).next();
        bufferBuilder.vertex(matrix, x + width, y, 0).texture(1, 0).next();
        Tessellator.getInstance().draw();

        RenderSystem.disableBlend();
    }
}