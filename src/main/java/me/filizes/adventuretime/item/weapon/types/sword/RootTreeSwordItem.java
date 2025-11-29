package me.filizes.adventuretime.item.weapon.types.sword;

import me.filizes.adventuretime.item.material.ModToolMaterials;
import me.filizes.adventuretime.item.weapon.AdventureSword;
import me.filizes.adventuretime.util.AbilityDescription;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class RootTreeSwordItem extends AdventureSword {
    public static final String ID = "root_tree_sword";

    public RootTreeSwordItem(Settings settings) {
        super(ModToolMaterials.ROOT_TREE, 11, -2.2f, ID, List.of(), 3, settings);
        this.attackStrengthBonus = 30.0f;
    }

    @Override
    public Text getName(ItemStack stack) {
        return Text.translatable(this.getTranslationKey()).formatted(Formatting.BLUE);
    }
}