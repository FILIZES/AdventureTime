package me.filizes.adventuretime.item;

import me.filizes.adventuretime.AdventureTime;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {
    public static final ItemGroup ADVENTURE_TIME_SWORDS_GROUP = Registry.register(
            Registries.ITEM_GROUP,
            new Identifier(AdventureTime.MOD_ID, "adventuretime_swords"),
            FabricItemGroup.builder()
                    .displayName(Text.translatable("itemgroup.adventuretime_swords"))
                    .icon(() -> new ItemStack(ModItems.SCARLET_SWORD))
                    .entries((displayContext, entries) -> {
                        entries.add(ModItems.SCARLET_SWORD);
                        entries.add(ModItems.FOURTH_DIMENSION_SWORD);
                        entries.add(ModItems.ROOT_TREE_SWORD);
                        entries.add(ModItems.DEMONIC_BLOOD_SWORD);
                    }).build());

    public static void registerItemGroups() {
    }
}