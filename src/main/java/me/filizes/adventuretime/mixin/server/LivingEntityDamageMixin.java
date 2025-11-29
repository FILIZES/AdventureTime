package me.filizes.adventuretime.mixin.server;

import me.filizes.adventuretime.player.PlayerStats;
import me.filizes.adventuretime.util.IEntityDataSaver;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(LivingEntity.class)
public abstract class LivingEntityDamageMixin extends Entity {

    public LivingEntityDamageMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "getAttributeValue(Lnet/minecraft/entity/attribute/EntityAttribute;)D", at = @At("RETURN"), cancellable = true)
    private void modifyHealthAttribute(EntityAttribute attribute, CallbackInfoReturnable<Double> cir) {
        if (attribute.equals(EntityAttributes.GENERIC_MAX_HEALTH)) {
            LivingEntity entity = (LivingEntity) (Object) this;
            if (entity instanceof PlayerEntity player) {
                cir.setReturnValue((double) new PlayerStats(player).getMaxHealth());
            } else if (entity instanceof HostileEntity) {
                cir.setReturnValue(cir.getReturnValueD() * 500.0);
            } else if (entity instanceof MobEntity) {
                cir.setReturnValue(cir.getReturnValueD() * 50.0);
            }
        }
    }

    @ModifyVariable(method = "damage(Lnet/minecraft/entity/damage/DamageSource;F)Z", at = @At("HEAD"), argsOnly = true)
    private float onDamageAmount(float originalAmount, DamageSource source) {
        LivingEntity self = (LivingEntity) (Object) this;
        NbtCompound data = ((IEntityDataSaver) self).adventureTime$getPersistentData();

        if (source.isOf(DamageTypes.IN_FIRE) || source.isOf(DamageTypes.ON_FIRE) || source.isOf(DamageTypes.LAVA)) {
            data.putInt("PyroLingerTimer", 200);
            return 0.0f;
        }

        if (source.getAttacker() instanceof PlayerEntity player && !self.getWorld().isClient()) {
            int fireAspectLevel = EnchantmentHelper.getFireAspect(player);
            if (fireAspectLevel > 0) {
                data.putInt("PyroLingerTimer", 200);
            }

            PlayerStats stats = new PlayerStats(player);
            boolean isCrit = self.getWorld().getRandom().nextFloat() < stats.getCritChance();
            float modifiedDamage = stats.calculateDamage(originalAmount, isCrit);

            if (self instanceof PlayerEntity targetPlayer) {
                modifiedDamage = new PlayerStats(targetPlayer).applyDefense(modifiedDamage);
            }

            return modifiedDamage;
        }
        return originalAmount;
    }
}