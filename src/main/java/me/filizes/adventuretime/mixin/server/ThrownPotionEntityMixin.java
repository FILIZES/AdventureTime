package me.filizes.adventuretime.mixin.server;

import me.filizes.adventuretime.util.IEntityDataSaver;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(PotionEntity.class)
public abstract class ThrownPotionEntityMixin {

    @Inject(method = "onCollision", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/thrown/PotionEntity;isLingering()Z"))
    private void onPotionSplash(HitResult hitResult, CallbackInfo ci) {
        PotionEntity self = (PotionEntity)(Object)this;
        Box box = self.getBoundingBox().expand(1.0, 1.0, 1.0);
        List<LivingEntity> list = self.getWorld().getNonSpectatingEntities(LivingEntity.class, box);

        for (LivingEntity entity : list) {
            NbtCompound data = ((IEntityDataSaver) entity).adventureTime$getPersistentData();
            data.putBoolean("IsHydro", true);
        }
    }
}