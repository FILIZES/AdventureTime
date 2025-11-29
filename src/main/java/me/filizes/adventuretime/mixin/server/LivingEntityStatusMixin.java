package me.filizes.adventuretime.mixin.server;

import me.filizes.adventuretime.mixin.accessor.MobEntityAccessor;
import me.filizes.adventuretime.networking.ModMessages;
import me.filizes.adventuretime.util.IEntityDataSaver;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityStatusMixin extends Entity implements IEntityDataSaver {

    @Shadow public abstract boolean isDead();
    @Shadow public abstract EntityGroup getGroup();

    @Unique
    private NbtCompound persistentData;

    public LivingEntityStatusMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Override
    public NbtCompound adventureTime$getPersistentData() {
        if (this.persistentData == null) {
            this.persistentData = new NbtCompound();
        }
        return this.persistentData;
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("HEAD"))
    protected void injectWriteMethod(NbtCompound nbt, CallbackInfo ci) {
        if (persistentData != null) {
            nbt.put("adventuretime.data", persistentData);
        }
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("HEAD"))
    protected void injectReadMethod(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.contains("adventuretime.data", NbtCompound.COMPOUND_TYPE)) {
            persistentData = nbt.getCompound("adventuretime.data");
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void tickCustomStatuses(CallbackInfo ci) {
        if (this.getWorld().isClient || this.isDead()) return;

        LivingEntity self = (LivingEntity) (Object) this;
        NbtCompound data = ((IEntityDataSaver) self).adventureTime$getPersistentData();

        boolean oldIsPyro = data.getBoolean("IsPyro");
        boolean oldIsHydro = data.getBoolean("IsHydro");

        boolean isPyroSourceActive = self.isOnFire() || self.isInLava() || (this.getGroup() == EntityGroup.UNDEAD && self.getWorld().isDay() && !this.getWorld().isRaining() && self instanceof MobEntity mob && ((MobEntityAccessor) mob).invokeIsAffectedByDaylight());

        int pyroTimer = data.getInt("PyroLingerTimer");
        if (isPyroSourceActive) {
            data.putInt("PyroLingerTimer", 200);
        } else if (pyroTimer > 0) {
            data.putInt("PyroLingerTimer", pyroTimer - 1);
        }

        boolean newIsPyro = data.getInt("PyroLingerTimer") > 0;
        data.putBoolean("IsPyro", newIsPyro);

        if (newIsPyro && this.age % 20 == 0) {
            self.damage(self.getDamageSources().magic(), 3.0f);
        }

        boolean isHydroSourceActive = self.isWet() || self.isSubmergedIn(FluidTags.WATER) || (this.getWorld().isRaining() && this.getWorld().isSkyVisible(this.getBlockPos()));
        data.putBoolean("IsHydro", isHydroSourceActive);
        boolean newIsHydro = data.getBoolean("IsHydro");

        if (oldIsPyro != newIsPyro || oldIsHydro != newIsHydro) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeInt(self.getId());
            NbtCompound nbtToSend = new NbtCompound();
            nbtToSend.putBoolean("IsPyro", newIsPyro);
            nbtToSend.putBoolean("IsHydro", newIsHydro);
            buf.writeNbt(nbtToSend);

            for (PlayerEntity player : this.getWorld().getPlayers()) {
                if (player instanceof ServerPlayerEntity serverPlayer) {
                    if (serverPlayer.getPos().isInRange(self.getPos(), 128)) {
                        ServerPlayNetworking.send(serverPlayer, ModMessages.STATUS_SYNC_ID, buf);
                    }
                }
            }
        }
    }
}