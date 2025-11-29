package me.filizes.adventuretime.client.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class InGameHudHealthBarMixin {
    @Unique
    private static final float FADE_DURATION = 2000.0f;
    @Unique
    private float lastHealth = 0.0f;
    @Unique
    private float damageAmount = 0.0f;
    @Unique
    private long damageStartTime = 0L;

    @Inject(method = "renderHealthBar", at = @At("HEAD"), cancellable = true)
    private void renderGreenHealthBar(DrawContext context, PlayerEntity player, int x, int y, int lines, int regeneratingHeartIndex, float maxHealth, int lastHealth, int health, int absorption, boolean blinking, CallbackInfo ci) {
        ci.cancel();
        float currentHealth = player.getHealth();
        maxHealth = Math.max(maxHealth, 20.0f);

        if (currentHealth < this.lastHealth && this.lastHealth > 0) {
            damageAmount = this.lastHealth - currentHealth;
            damageStartTime = System.currentTimeMillis();
        }
        this.lastHealth = currentHealth;

        float fadeProgress = MathHelper.clamp((float)(System.currentTimeMillis() - damageStartTime) / FADE_DURATION, 0.0f, 1.0f);
        float displayDamage = damageAmount * (1.0f - fadeProgress);

        if (fadeProgress >= 1.0f) {
            damageAmount = 0.0f;
        }

        float healthPercentage = MathHelper.clamp(currentHealth / maxHealth, 0.0f, 1.0f);
        float damagePercentage = MathHelper.clamp(displayDamage / maxHealth, 0.0f, 1.0f);
        int barWidth = (int) (healthPercentage * 100);
        int damageWidth = (int) ((healthPercentage + damagePercentage) * 100);
        int barHeight = 12;
        int emptyWidth = 100;

        context.fill(x, y, x + emptyWidth, y + barHeight, 0xFF000000);
        if (damageWidth > barWidth) {
            int alpha = (int) ((1.0f - fadeProgress) * 255);
            context.fill(x + barWidth, y, x + damageWidth, y + barHeight, (alpha << 24) | 0xFFFF00);
        }
        context.fill(x, y, x + barWidth, y + barHeight, 0xFF00FF00);
        context.drawBorder(x, y, emptyWidth, barHeight, 0xFFFFFFFF);

        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        String healthText = String.format("%.0f/%.0f", currentHealth, maxHealth);
        int textWidth = textRenderer.getWidth(healthText);
        context.drawTextWithShadow(textRenderer, healthText, x + (emptyWidth - textWidth) / 2, y + (barHeight - 8) / 2, 0xFFFFFFFF);
    }
}