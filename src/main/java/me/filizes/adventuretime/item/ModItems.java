package me.filizes.adventuretime.item;

import me.filizes.adventuretime.AdventureTime;
import me.filizes.adventuretime.item.weapon.types.sword.DemonicBloodSwordItem;
import me.filizes.adventuretime.item.weapon.types.sword.FourthDimensionSwordItem;
import me.filizes.adventuretime.item.weapon.types.sword.RootTreeSwordItem;
import me.filizes.adventuretime.item.weapon.types.sword.ScarletSwordItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {
    public static final Item SCARLET_SWORD = registerItem(ScarletSwordItem.ID, new ScarletSwordItem(new Item.Settings()));
    public static final Item FOURTH_DIMENSION_SWORD = registerItem(FourthDimensionSwordItem.ID, new FourthDimensionSwordItem(new Item.Settings()));
    public static final Item ROOT_TREE_SWORD = registerItem(RootTreeSwordItem.ID, new RootTreeSwordItem(new Item.Settings()));
    public static final Item DEMONIC_BLOOD_SWORD = registerItem(DemonicBloodSwordItem.ID, new DemonicBloodSwordItem(new Item.Settings()));

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(AdventureTime.MOD_ID, name), item);
    }

    public static void registerModItems() {
    }
}