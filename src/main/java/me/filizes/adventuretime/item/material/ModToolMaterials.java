package me.filizes.adventuretime.item.material;

import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;

import java.util.function.Supplier;

public enum ModToolMaterials implements ToolMaterial {
    // Название(Прочность, СкоростьДобычи, БонусУронаМатериала, УровеньДобычи, Зачаровываемость, ИнгредиентДляПочинки)
    SCARLET(4000, 0.0f, 0.0f, 0, 15, () -> Ingredient.EMPTY),
    FOURTH_DIMENSION(2000, 0.0f, 0.0f, 0, 18, () -> Ingredient.EMPTY),
    ROOT_TREE(6000, 0.0f, 0.0f, 0, 12, () -> Ingredient.EMPTY),
    DEMONIC_BLOOD(6000, 0.0f, 0.0f, 0, 20, () -> Ingredient.EMPTY);

    private final int durability;
    private final float miningSpeedMultiplier;
    private final float attackDamage;
    private final int miningLevel;
    private final int enchantability;
    private final Supplier<Ingredient> repairIngredient;

    ModToolMaterials(int durability, float miningSpeedMultiplier, float attackDamage, int miningLevel, int enchantability, Supplier<Ingredient> repairIngredient) {
        this.durability = durability;
        this.miningSpeedMultiplier = miningSpeedMultiplier;
        this.attackDamage = attackDamage;
        this.miningLevel = miningLevel;
        this.enchantability = enchantability;
        this.repairIngredient = repairIngredient;
    }

    @Override
    public int getDurability() {
        return this.durability;
    }

    @Override
    public float getMiningSpeedMultiplier() {
        return this.miningSpeedMultiplier;
    }

    @Override
    public float getAttackDamage() {
        return this.attackDamage;
    }

    @Override
    public int getMiningLevel() {
        return this.miningLevel;
    }

    @Override
    public int getEnchantability() {
        return this.enchantability;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return this.repairIngredient.get();
    }
}