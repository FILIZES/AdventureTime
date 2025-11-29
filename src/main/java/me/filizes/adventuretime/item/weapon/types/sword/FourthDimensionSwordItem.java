package me.filizes.adventuretime.item.weapon.types.sword;

import me.filizes.adventuretime.AdventureTime;
import me.filizes.adventuretime.item.material.ModToolMaterials;
import me.filizes.adventuretime.item.weapon.AdventureSword;
import me.filizes.adventuretime.util.AbilityDescription;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import net.minecraft.util.math.Box;
import org.jetbrains.annotations.Nullable;


import java.util.List;

public class FourthDimensionSwordItem extends AdventureSword {
    public static final String ID = "fourth_dimension_sword";
    private static final float EXPLOSION_RADIUS = 8.0f;
    private static final double EFFECT_RADIUS = 40.0;

    public FourthDimensionSwordItem(Settings settings) {
        super(ModToolMaterials.FOURTH_DIMENSION, 8, -3.0f, ID, List.of(
                new AbilityDescription("ability.adventuretime.reality_shift.name", "ability.adventuretime.fourth_dimension_sword.reality_shift.description"),
                new AbilityDescription("ability.adventuretime.paradoxicalization.name", "ability.adventuretime.fourth_dimension_sword.paradoxicalization.description")
        ), 4, settings);
        this.attackStrengthBonus = 25.0f;
    }

    @Override
    public Text getName(ItemStack stack) {
        return Text.translatable(this.getTranslationKey()).formatted(Formatting.DARK_GRAY);
    }

    public void triggerParadoxicalization(ItemStack stack, World world, @Nullable PlayerEntity player, double x, double y, double z) {
        if (world.isClient) {
            AdventureTime.LOGGER.debug("Paradoxicalization skipped on client side");
            return;
        }

        world.createExplosion(null, x, y, z, EXPLOSION_RADIUS, World.ExplosionSourceType.MOB);
        Box effectRadius = new Box(x - EFFECT_RADIUS, y - EFFECT_RADIUS, z - EFFECT_RADIUS, x + EFFECT_RADIUS, y + EFFECT_RADIUS, z + EFFECT_RADIUS);
        List<LivingEntity> entities = world.getEntitiesByClass(LivingEntity.class, effectRadius, entity -> true);

        for (LivingEntity entity : entities) {
            if (player != null && entity == player) continue;
            applyStatusEffects(entity);
            if (entity instanceof PlayerEntity targetPlayer) {
                damagePlayerInventory(targetPlayer);
            }
        }

        AdventureTime.LOGGER.info("Paradoxicalization at ({}, {}, {}) affected {} entities", x, y, z, entities.size());
    }

    private void applyStatusEffects(LivingEntity entity) {
        entity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 20 * 30, 1));
        entity.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 20 * 30, 1));
        entity.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, 20 * 30, 1));
    }

    private void damagePlayerInventory(PlayerEntity player) {
        for (int i = 0; i < player.getInventory().size(); i++) {
            damageItem(player.getInventory().getStack(i));
        }
        for (ItemStack armor : player.getArmorItems()) {
            damageItem(armor);
        }
        damageItem(player.getOffHandStack());
    }

    private void damageItem(ItemStack stack) {
        if (stack.isEmpty() || !stack.isDamageable() || stack.getDamage() >= stack.getMaxDamage()) return;
        int damageToApply = Math.max(1, (stack.getMaxDamage() - stack.getDamage()) / 2);
        stack.setDamage(Math.min(stack.getMaxDamage(), stack.getDamage() + damageToApply));
    }
}