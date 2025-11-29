package me.filizes.adventuretime.mixin.server;

import me.filizes.adventuretime.util.CriticalDamageSourceExtensions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityAttackServerMixin {
    @Shadow public abstract float getAttackCooldownProgress(float baseTime);

    @Unique
    private boolean canPerformCriticalHit(PlayerEntity player) {
        return !player.isTouchingWater() && !player.isClimbing() && !player.hasVehicle() && !player.hasStatusEffect(StatusEffects.BLINDNESS);
    }

    @ModifyArgs(method = "attack(Lnet/minecraft/entity/Entity;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"))
    private void setCriticalFlag(Args args, Entity target) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (player.getWorld().isClient) return;

        DamageSource source = args.get(0);
        boolean isCritical = false;
        float cooldown = getAttackCooldownProgress(0.5f);

        if (target instanceof LivingEntity && cooldown > 0.9f && canPerformCriticalHit(player)) {
            isCritical = player.fallDistance > 0.0f && !player.isOnGround() ||
                    (player.isOnGround() && (player.isSprinting() || player.hasStatusEffect(StatusEffects.SPEED) || player.hasStatusEffect(StatusEffects.STRENGTH)));
        }

        if (source instanceof CriticalDamageSourceExtensions extendedSource) {
            extendedSource.adventureTimeMod$setCritical(isCritical);
            extendedSource.adventureTimeMod$setAttackingPlayerForCritCheck(player);
        }
    }
}