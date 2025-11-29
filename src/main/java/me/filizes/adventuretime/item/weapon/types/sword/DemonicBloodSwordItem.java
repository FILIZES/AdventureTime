package me.filizes.adventuretime.item.weapon.types.sword;

import me.filizes.adventuretime.item.material.ModToolMaterials;
import me.filizes.adventuretime.item.weapon.AdventureSword;
import me.filizes.adventuretime.util.AbilityDescription;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;


import java.util.List;

public class DemonicBloodSwordItem extends AdventureSword {
    public static final String ID = "demonic_blood_sword";

    public DemonicBloodSwordItem(Settings settings) {
        super(ModToolMaterials.DEMONIC_BLOOD, 14, -2.0f, ID, List.of(
                new AbilityDescription("ability.adventuretime.demonic_aura.name", "ability.adventuretime.demonic_blood_sword.demonic_aura.description"),
                new AbilityDescription("ability.adventuretime.dark_sphere_punishment.name", "ability.adventuretime.demonic_blood_sword.dark_sphere_punishment.description")
        ), 5, settings.fireproof());
        this.attackStrengthBonus = 40.0f;
    }

    @Override
    public Text getName(ItemStack stack) {
        return Text.translatable(this.getTranslationKey()).formatted(Formatting.DARK_RED);
    }

    @Override
    public boolean isFireproof() {
        return true;
    }
}