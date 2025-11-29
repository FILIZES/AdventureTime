package me.filizes.adventuretime.client.menu;

import com.mojang.blaze3d.systems.RenderSystem;
import me.filizes.adventuretime.item.weapon.AdventureSword;
import me.filizes.adventuretime.player.PlayerStats;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class CharacterMenuMod {
    public static final KeyBinding OPEN_MENU_KEY = KeyBindingHelper.registerKeyBinding(
            new KeyBinding(
                    "key.adventuretime.open_menu",
                    InputUtil.Type.KEYSYM,
                    GLFW.GLFW_KEY_G,
                    "category.adventuretime"
            )
    );

    private static class CharacterStatsScreen extends Screen {

        private enum Tab {
            CHARACTER, WEAPON, ARTIFACTS
        }

        private Tab currentTab = Tab.CHARACTER;
        private ItemStack selectedWeapon = ItemStack.EMPTY;
        private final Identifier INVENTORY_SLOT_TEXTURE = new Identifier("textures/gui/container/inventory.png");

        protected CharacterStatsScreen() {
            super(Text.literal("Характеристика"));
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, float delta) {
            this.renderBackground(context);
            assert this.client != null;
            PlayerEntity player = this.client.player;
            if (player == null) return;

            renderTabs(context, mouseX, mouseY);

            switch (currentTab) {
                case CHARACTER:
                    renderCharacterTab(context, player);
                    break;
                case WEAPON:
                    renderWeaponTab(context, player, mouseX, mouseY);
                    break;
                case ARTIFACTS:
                    renderArtifactsTab(context);
                    break;
            }

            super.render(context, mouseX, mouseY, delta);
        }

        private void renderTabs(DrawContext context, int mouseX, int mouseY) {
            int tabX = 30;
            int tabY = 40;
            int tabSpacing = 30;

            boolean isCharActive = currentTab == Tab.CHARACTER;
            boolean isWeaponActive = currentTab == Tab.WEAPON;
            boolean isArtifactsActive = currentTab == Tab.ARTIFACTS;

            context.drawText(this.textRenderer, "Характеристика", tabX, tabY, isCharActive ? 0xFFFFFF : 0xA0A0A0, isCharActive);
            context.drawText(this.textRenderer, "Оружие", tabX, tabY + tabSpacing, isWeaponActive ? 0xFFFFFF : 0xA0A0A0, isWeaponActive);
            context.drawText(this.textRenderer, "Артефакты", tabX, tabY + tabSpacing * 2, isArtifactsActive ? 0xFFFFFF : 0xA0A0A0, isArtifactsActive);
        }

        private void renderCharacterTab(DrawContext context, PlayerEntity player) {
            renderPlayerModel(context, player, this.width / 2, this.height);

            int statsX = this.width - 240;
            int statsY = 40;

            PlayerStats stats = new PlayerStats(player);

            ItemStack itemToCalculate = selectedWeapon.isEmpty() ? player.getMainHandStack() : selectedWeapon;

            float finalHealth = stats.getMaxHealth();
            float finalAttack = stats.getAttackStrength(itemToCalculate);
            float finalCritDamage = stats.getCritDamage(itemToCalculate);
            float finalCritChance = stats.getCritChance(itemToCalculate);
            float finalDefense = stats.getDefense(itemToCalculate);

            int lineHeight = 18;
            context.drawTextWithShadow(this.textRenderer, Text.literal(String.format("Макс. здоровье: %.0f", finalHealth)), statsX, statsY, 0xFFFFFF);
            context.drawTextWithShadow(this.textRenderer, Text.literal(String.format("Сила Атаки: %.0f", finalAttack)), statsX, statsY + lineHeight, 0xFFFFFF);
            context.drawTextWithShadow(this.textRenderer, Text.literal(String.format("Крит. урон: %.0f%%", finalCritDamage * 100)), statsX, statsY + lineHeight * 2, 0xFFFFFF);
            context.drawTextWithShadow(this.textRenderer, Text.literal(String.format("Крит. шанс: %.0f%%", finalCritChance * 100)), statsX, statsY + lineHeight * 3, 0xFFFFFF);
            context.drawTextWithShadow(this.textRenderer, Text.literal(String.format("Защита: %.0f", finalDefense)), statsX, statsY + lineHeight * 4, 0xFFFFFF);
        }

        private void renderWeaponTab(DrawContext context, PlayerEntity player, int mouseX, int mouseY) {
            int startX = this.width / 2 - (9 * 18) / 2;
            int startY = 50;
            int slotSize = 18;
            int slotIndex = 0;

            List<ItemStack> swords = getSwordsFromInventory(player);

            for (ItemStack swordStack : swords) {
                int x = startX + (slotIndex % 9) * slotSize;
                int y = startY + (slotIndex / 9) * slotSize;

                context.drawTexture(INVENTORY_SLOT_TEXTURE, x, y, 7, 7, 18, 18);
                context.drawItem(swordStack, x + 1, y + 1);
                context.drawItemInSlot(this.textRenderer, swordStack, x + 1, y + 1);

                if (ItemStack.areEqual(swordStack, selectedWeapon)) {
                    context.fill(x, y, x + slotSize, y + slotSize, 0x80FFFFFF);
                }
                slotIndex++;
            }
        }

        private void renderArtifactsTab(DrawContext context) {
            drawCenteredTextWithShadow(context, Text.literal("Раздел артефактов в разработке"), this.width / 2, this.height / 2, 0xFFFFFF);
        }

        private void renderPlayerModel(DrawContext context, PlayerEntity player, int x, int y) {
            int modelY = y - 60;

            MatrixStack matrices = context.getMatrices();
            matrices.push();
            matrices.translate((float) x, (float) modelY, 50.0f);
            matrices.scale(80.0f, -80.0f, 80.0f);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(205f));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(15f));

            float originalBodyYaw = player.bodyYaw;
            float originalYaw = player.getYaw();
            float originalHeadYaw = player.headYaw;
            float originalPitch = player.getPitch();

            player.bodyYaw = 0f;
            player.setYaw(0f);
            player.headYaw = 0f;
            player.setPitch(0f);

            try {
                DiffuseLighting.enableGuiDepthLighting();
                EntityRenderDispatcher dispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
                dispatcher.setRenderShadows(false);

                VertexConsumerProvider.Immediate vertexConsumers = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
                RenderSystem.runAsFancy(() -> dispatcher.render(player, 0.0, 0.0, 0.0, 0.0f, 1.0f, matrices, vertexConsumers, LightmapTextureManager.MAX_LIGHT_COORDINATE));
                vertexConsumers.draw();

                dispatcher.setRenderShadows(true);
                DiffuseLighting.disableGuiDepthLighting();
            } finally {
                player.bodyYaw = originalBodyYaw;
                player.setYaw(originalYaw);
                player.headYaw = originalHeadYaw;
                player.setPitch(originalPitch);
            }
            matrices.pop();
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (button == 0) {
                int tabX = 30;
                int tabY = 40;
                int tabWidth = 100;
                int tabHeight = 12;
                int tabSpacing = 30;

                if (isMouseOver(mouseX, mouseY, tabX, tabY, tabWidth, tabHeight)) {
                    currentTab = Tab.CHARACTER;
                    return true;
                }
                if (isMouseOver(mouseX, mouseY, tabX, tabY + tabSpacing, tabWidth, tabHeight)) {
                    currentTab = Tab.WEAPON;
                    return true;
                }
                if (isMouseOver(mouseX, mouseY, tabX, tabY + tabSpacing * 2, tabWidth, tabHeight)) {
                    currentTab = Tab.ARTIFACTS;
                    return true;
                }

                if (currentTab == Tab.WEAPON) {
                    assert this.client != null;
                    PlayerEntity player = this.client.player;
                    if (player == null) return false;

                    List<ItemStack> swords = getSwordsFromInventory(player);
                    int startX = this.width / 2 - (9 * 18) / 2;
                    int startY = 50;
                    int slotSize = 18;
                    int slotIndex = 0;

                    for (ItemStack swordStack : swords) {
                        int x = startX + (slotIndex % 9) * slotSize;
                        int y = startY + (slotIndex / 9) * slotSize;

                        if (isMouseOver(mouseX, mouseY, x, y, slotSize, slotSize)) {
                            if (ItemStack.areEqual(swordStack, selectedWeapon)) {
                                selectedWeapon = ItemStack.EMPTY;
                            } else {
                                selectedWeapon = swordStack;
                            }
                            return true;
                        }
                        slotIndex++;
                    }
                }
            }
            return super.mouseClicked(mouseX, mouseY, button);
        }

        private List<ItemStack> getSwordsFromInventory(PlayerEntity player) {
            List<ItemStack> swords = new ArrayList<>();
            for (ItemStack stack : player.getInventory().main) {
                if (stack.getItem() instanceof AdventureSword) {
                    swords.add(stack);
                }
            }
            return swords;
        }

        private boolean isMouseOver(double mouseX, double mouseY, int x, int y, int width, int height) {
            return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
        }

        private void drawCenteredTextWithShadow(DrawContext context, Text text, int centerX, int y, int color) {
            int textWidth = this.textRenderer.getWidth(text);
            context.drawTextWithShadow(this.textRenderer, text, centerX - textWidth / 2, y, color);
        }

        @Override
        public boolean shouldPause() {
            return false;
        }
    }

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (OPEN_MENU_KEY.wasPressed()) {
                if (client.player != null && client.currentScreen == null) {
                    client.setScreen(new CharacterStatsScreen());
                }
            }
        });
    }
}