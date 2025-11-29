package me.filizes.adventuretime.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.item.ItemModels;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.item.Item;

@Environment(EnvType.CLIENT)
public class ItemRendererService {
    public void registerCustomRenderer(Item item) {
        BuiltinItemRendererRegistry.INSTANCE.register(item, (stack, mode, matrices, vertexConsumers, light, overlay) -> {
            ItemModels models = MinecraftClient.getInstance().getItemRenderer().getModels();
            BakedModel model = models.getModel(stack);
            MinecraftClient.getInstance().getItemRenderer().renderItem(stack, mode, false, matrices, vertexConsumers, light, overlay, model);
        });
    }
}