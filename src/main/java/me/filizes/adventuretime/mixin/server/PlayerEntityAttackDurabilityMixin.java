package me.filizes.adventuretime.mixin.server;

import me.filizes.adventuretime.item.weapon.types.sword.FourthDimensionSwordItem;
import me.filizes.adventuretime.item.weapon.types.sword.RootTreeSwordItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityAttackDurabilityMixin {
    @Inject(method = "attack(Lnet/minecraft/entity/Entity;)V", at = @At("TAIL"))
    private void applyAttackDurability(Entity target, CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        ItemStack weapon = player.getMainHandStack();
        if (weapon.isEmpty() || player.getWorld().isClient()) return;

        int durabilityCost = weapon.getItem() instanceof FourthDimensionSwordItem ? 2 : 1;
        weapon.damage(durabilityCost, player, p -> p.sendToolBreakStatus(p.getActiveHand()));

        if (weapon.getItem() instanceof RootTreeSwordItem && target instanceof PlayerEntity targetPlayer) {
            targetPlayer.resetLastAttackedTicks();
        }
    }
}