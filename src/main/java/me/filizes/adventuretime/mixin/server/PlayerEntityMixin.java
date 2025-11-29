package me.filizes.adventuretime.mixin.server;

import me.filizes.adventuretime.player.PlayerStats;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {
    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.contains(PlayerStats.ADVENTURE_TIME_NBT_KEY, 10)) {
            nbt.put(PlayerStats.ADVENTURE_TIME_NBT_KEY, nbt.getCompound(PlayerStats.ADVENTURE_TIME_NBT_KEY));
        }
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        if (!nbt.contains(PlayerStats.ADVENTURE_TIME_NBT_KEY, 10)) {
            nbt.put(PlayerStats.ADVENTURE_TIME_NBT_KEY, new NbtCompound());
        }
    }
}