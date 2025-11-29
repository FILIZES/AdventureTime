package me.filizes.adventuretime.item.weapon;

import me.filizes.adventuretime.AdventureTime;
import me.filizes.adventuretime.api.ClientShiftChecker;
import me.filizes.adventuretime.util.AbilityDescription;
import me.filizes.adventuretime.util.TextUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;
import java.util.ServiceLoader;

public abstract class AdventureSword extends SwordItem {
    protected final String swordNameId;
    protected final String detailedDescriptionKey;
    protected final List<AbilityDescription> abilities;
    protected final Identifier textureIdentifier;
    protected float attackStrengthBonus;
    protected final int prestige;

    @Environment(EnvType.CLIENT)
    private static ClientShiftChecker clientShiftChecker;

    public AdventureSword(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, String swordNameId, List<AbilityDescription> abilities, int prestige, Settings settings) {
        super(toolMaterial, attackDamage, attackSpeed, settings);
        this.swordNameId = swordNameId;
        this.detailedDescriptionKey = "tooltip." + AdventureTime.MOD_ID + "." + swordNameId + ".details";
        this.abilities = abilities;
        this.textureIdentifier = new Identifier(AdventureTime.MOD_ID, "item/" + swordNameId);
        this.prestige = prestige;
    }

    public float getAttackStrengthBonus() {
        return attackStrengthBonus;
    }

    @Override
    public boolean isDamageable() {
        return false;
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(getPrestigeText());

        if (getClientShiftChecker().isShiftKeyDown()) {
            appendDetailedTooltip(tooltip);
        } else {
            tooltip.add(Text.translatable("tooltip.adventuretime.shift_details").formatted(Formatting.YELLOW));
        }
    }

    @Unique
    private Text getPrestigeText() {
        MutableText stars = Text.empty();
        for (int i = 0; i < 5; i++) {
            if (i < this.prestige) {
                stars.append(Text.literal("★").formatted(Formatting.GOLD));
            } else {
                stars.append(Text.literal("☆").formatted(Formatting.GRAY));
            }
        }
        return stars;
    }

    @Environment(EnvType.CLIENT)
    protected void appendDetailedTooltip(List<Text> tooltip) {
        if (!abilities.isEmpty()) {
            tooltip.add(Text.translatable("tooltip.adventuretime.abilities").formatted(Formatting.GOLD)
                    .append(Text.literal(":").formatted(Formatting.GOLD)));
            for (AbilityDescription ability : abilities) {
                if (ability.getNameKey().contains("parry")) continue;
                tooltip.add(Text.literal(" • ").formatted(Formatting.BLUE)
                        .append(ability.getFormattedName()));
                TextUtils.wrapText(ability.getFormattedDescription(), 50).forEach(line ->
                        tooltip.add(Text.literal("   ").append(line)));
            }
            tooltip.add(Text.empty());
        }
        tooltip.addAll(TextUtils.wrapText(Text.translatable(detailedDescriptionKey).formatted(Formatting.GRAY), 50));
    }

    @Environment(EnvType.CLIENT)
    private static ClientShiftChecker getClientShiftChecker() {
        if (clientShiftChecker == null) {
            clientShiftChecker = ServiceLoader.load(ClientShiftChecker.class).findFirst().orElse(() -> false);
        }
        return clientShiftChecker;
    }
}