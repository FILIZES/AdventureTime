package me.filizes.adventuretime;

import me.filizes.adventuretime.item.ModItemGroups;
import me.filizes.adventuretime.item.ModItems;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdventureTime implements ModInitializer {
    public static final String MOD_ID = "adventuretime";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Adventure Time инициализируется!");

        ModItemGroups.registerItemGroups();
        ModItems.registerModItems();

        LOGGER.info("Adventure Time успешно загружен!");
    }
}