package me.filizes.adventuretime.client.networking.packet;

import me.filizes.adventuretime.networking.ModMessages;
import me.filizes.adventuretime.util.IEntityDataSaver;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.nbt.NbtCompound;

@Environment(EnvType.CLIENT)
public class StatusSyncDataS2CPacket {
    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(ModMessages.STATUS_SYNC_ID, (client, handler, buf, responseSender) -> {
            int entityId = buf.readInt();
            NbtCompound nbt = buf.readNbt();

            client.execute(() -> {
                if (client.world != null && client.world.getEntityById(entityId) instanceof IEntityDataSaver entity) {
                    entity.adventureTime$getPersistentData().copyFrom(nbt);
                }
            });
        });
    }
}