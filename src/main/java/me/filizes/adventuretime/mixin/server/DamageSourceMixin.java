package me.filizes.adventuretime.mixin.server;

import me.filizes.adventuretime.util.CriticalDamageSourceExtensions;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(DamageSource.class)
public abstract class DamageSourceMixin implements CriticalDamageSourceExtensions {
    @Unique
    private boolean isCriticalAttack = false;
    @Unique
    private PlayerEntity critAttacker = null;

    @Override
    public void adventureTimeMod$setCritical(boolean critical) {
        this.isCriticalAttack = critical;
    }

    @Override
    public boolean adventureTimeMod$isCritical() {
        return this.isCriticalAttack;
    }

    @Override
    public void adventureTimeMod$setAttackingPlayerForCritCheck(PlayerEntity player) {
        this.critAttacker = player;
    }

    @Override
    public PlayerEntity adventureTimeMod$getAttackingPlayerForCritCheck() {
        return this.critAttacker;
    }
}