package me.filizes.adventuretime.client;

import me.filizes.adventuretime.client.menu.CharacterMenuMod;
import me.filizes.adventuretime.client.networking.packet.StatusSyncDataS2CPacket;
import me.filizes.adventuretime.client.render.ItemRendererService;
import me.filizes.adventuretime.item.ModItems;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class AdventureTimeClient implements ClientModInitializer {
    private static final ItemRendererService RENDERER_SERVICE = new ItemRendererService();

    @Override
    public void onInitializeClient() {
        RENDERER_SERVICE.registerCustomRenderer(ModItems.SCARLET_SWORD);
        RENDERER_SERVICE.registerCustomRenderer(ModItems.FOURTH_DIMENSION_SWORD);
        RENDERER_SERVICE.registerCustomRenderer(ModItems.ROOT_TREE_SWORD);
        RENDERER_SERVICE.registerCustomRenderer(ModItems.DEMONIC_BLOOD_SWORD);
        StatusSyncDataS2CPacket.register();
        CharacterMenuMod.register();
    }
}