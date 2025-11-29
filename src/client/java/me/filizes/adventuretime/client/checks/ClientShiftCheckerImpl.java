package me.filizes.adventuretime.client.checks;

import me.filizes.adventuretime.api.ClientShiftChecker;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;

@Environment(EnvType.CLIENT)
public class ClientShiftCheckerImpl implements ClientShiftChecker {
    @Override
    public boolean isShiftKeyDown() {
        return Screen.hasShiftDown();
    }
}