package me.filizes.adventuretime.item.weapon.types.sword;

import me.filizes.adventuretime.item.material.ModToolMaterials;
import me.filizes.adventuretime.item.weapon.AdventureSword;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class ScarletSwordItem extends AdventureSword {
    public static final String ID = "scarlet_sword";

    public ScarletSwordItem(Settings settings) {
        super(ModToolMaterials.SCARLET, 8, -2.2f, ID, List.of(), 3, settings);
        this.attackStrengthBonus = 20.0f;
    }

    @Override
    public Text getName(ItemStack stack) {
        return Text.translatable(this.getTranslationKey()).formatted(Formatting.YELLOW);
    }
}