package me.filizes.adventuretime.mixin.server;

import me.filizes.adventuretime.player.PlayerStats;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {
    @Inject(method = "onSpawn", at = @At("TAIL"))
    private void onSpawn(CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        NbtCompound nbt = new NbtCompound();
        player.writeCustomDataToNbt(nbt);
        NbtCompound adventureTimeNbt = nbt.getCompound(PlayerStats.ADVENTURE_TIME_NBT_KEY);
        if (!adventureTimeNbt.getBoolean(PlayerStats.HP_INITIALIZED_KEY)) {
            PlayerStats stats = new PlayerStats(player);
            player.setHealth(stats.getMaxHealth());
            adventureTimeNbt.putBoolean(PlayerStats.HP_INITIALIZED_KEY, true);
            nbt.put(PlayerStats.ADVENTURE_TIME_NBT_KEY, adventureTimeNbt);
            player.readCustomDataFromNbt(nbt);
        }
    }
}