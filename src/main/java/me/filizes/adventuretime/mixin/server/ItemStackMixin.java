package me.filizes.adventuretime.mixin.server;

import com.llamalad7.mixinextras.sugar.Local;
import me.filizes.adventuretime.item.weapon.types.sword.FourthDimensionSwordItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Shadow public abstract Item getItem();
    @Shadow public abstract int getMaxDamage();

    @Inject(method = "damage(ILnet/minecraft/entity/LivingEntity;Ljava/util/function/Consumer;)V",
            at = @At(value = "INVOKE", target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V", shift = At.Shift.BEFORE))
    private void onItemBreak(int amount, LivingEntity entity, Consumer<LivingEntity> breakCallback, CallbackInfo ci, @Local(argsOnly = true) int damage) {
        ItemStack stack = (ItemStack) (Object) this;
        if (stack.getItem() instanceof FourthDimensionSwordItem sword && stack.getDamage() + amount >= stack.getMaxDamage()) {
            sword.triggerParadoxicalization(stack, entity.getWorld(), entity instanceof PlayerEntity ? (PlayerEntity) entity : null,
                    entity.getX(), entity.getY(), entity.getZ());
        }
    }
}