package me.filizes.adventuretime.util;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class AbilityDescription {
    private final String nameKey;
    private final String descriptionKey;

    public AbilityDescription(String nameKey, String descriptionKey) {
        this.nameKey = nameKey;
        this.descriptionKey = descriptionKey;
    }

    public String getNameKey() {
        return this.nameKey;
    }

    public MutableText getFormattedName() {
        return Text.translatable(nameKey).formatted(Formatting.GOLD);
    }

    public MutableText getFormattedDescription() {
        return Text.translatable(descriptionKey).formatted(Formatting.BLUE);
    }
}