package me.filizes.adventuretime.player;

import me.filizes.adventuretime.item.weapon.AdventureSword;
import me.filizes.adventuretime.item.weapon.types.sword.DemonicBloodSwordItem;
import me.filizes.adventuretime.item.weapon.types.sword.FourthDimensionSwordItem;
import me.filizes.adventuretime.item.weapon.types.sword.RootTreeSwordItem;
import me.filizes.adventuretime.item.weapon.types.sword.ScarletSwordItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class PlayerStats {
    private static final float BASE_HP = 800.0f;
    private static final float BASE_ATTACK_STRENGTH = 50.0f;
    private static final float BASE_CRIT_CHANCE = 0.15f;
    private static final float BASE_CRIT_DAMAGE = 1.5f;
    private static final float BASE_DEFENSE = 40.0f;
    public static final String HP_INITIALIZED_KEY = "AdventureTimeHP";
    public static final String ADVENTURE_TIME_NBT_KEY = "AdventureTimeData";

    private final PlayerEntity player;

    public PlayerStats(PlayerEntity player) {
        this.player = player;
    }

    public float getMaxHealth() {
        return BASE_HP;
    }

    public float getAttackStrength(ItemStack weapon) {
        float bonus = 0.0f;
        if (weapon.getItem() instanceof AdventureSword sword) {
            bonus = sword.getAttackStrengthBonus();
        }
        return BASE_ATTACK_STRENGTH + bonus;
    }

    public float getCritChance(ItemStack weapon) {
        float bonus = 0.0f;
        Item item = weapon.getItem();
        if (item instanceof ScarletSwordItem) bonus = 0.05f;
        if (item instanceof FourthDimensionSwordItem) bonus = 0.10f;
        return BASE_CRIT_CHANCE + bonus;
    }

    public float getCritDamage(ItemStack weapon) {
        float bonus = 0.0f;
        Item item = weapon.getItem();
        if (item instanceof FourthDimensionSwordItem) bonus = 0.1f;
        if (item instanceof DemonicBloodSwordItem) bonus = 0.2f;
        return BASE_CRIT_DAMAGE + bonus;
    }

    public float getDefense(ItemStack weapon) {
        float bonus = 0.0f;
        Item item = weapon.getItem();
        if (item instanceof RootTreeSwordItem) bonus = 20.0f;
        if (item instanceof DemonicBloodSwordItem) bonus = 10.0f;
        return BASE_DEFENSE + bonus;
    }

    public float getAttackStrength() {
        return getAttackStrength(player.getMainHandStack());
    }

    public float getCritChance() {
        return getCritChance(player.getMainHandStack());
    }

    public float getCritDamage() {
        return getCritDamage(player.getMainHandStack());
    }

    public float getDefense() {
        return getDefense(player.getMainHandStack());
    }

    public float calculateDamage(float baseDamage, boolean isCrit) {
        float attackStrength = getAttackStrength();
        return baseDamage + (isCrit ? getCritDamage() * 10.0f * attackStrength : attackStrength);
    }

    public float applyDefense(float incomingDamage) {
        float defense = getDefense();
        return incomingDamage * (1.0f - defense / (defense + incomingDamage));
    }
}