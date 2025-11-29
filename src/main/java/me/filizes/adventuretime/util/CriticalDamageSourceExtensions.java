package me.filizes.adventuretime.util;

import net.minecraft.entity.player.PlayerEntity;

public interface CriticalDamageSourceExtensions {
    void adventureTimeMod$setCritical(boolean critical);
    boolean adventureTimeMod$isCritical();
    void adventureTimeMod$setAttackingPlayerForCritCheck(PlayerEntity player);
    PlayerEntity adventureTimeMod$getAttackingPlayerForCritCheck();
}