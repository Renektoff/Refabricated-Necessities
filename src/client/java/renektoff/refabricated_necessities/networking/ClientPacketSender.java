package renektoff.refabricated_necessities.networking;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

public class ClientPacketSender {
    public static void sendToolMorphPacket(BlockPos lookBlockTarget, Hand hand) {
        var buf = PacketByteBufs.create();

        buf.writeBlockPos(lookBlockTarget);
        buf.writeBoolean(hand == Hand.OFF_HAND);

        ClientPlayNetworking.send(ServerPacketReceiver.MORPHING_TOOL_MORPH_PACKET_ID, buf);
    }

    public static void sendToolRemoveItemPacket(boolean spawnEntity) {
        var buf = PacketByteBufs.create();

        buf.writeBoolean(spawnEntity);

        ClientPlayNetworking.send(ServerPacketReceiver.MORPHING_TOOL_CLIENT_REMOVE_ITEM_PACKET_ID, buf);
    }

    public static void sendToolScrollPacket(int direction) {
        var buf = PacketByteBufs.create();

        buf.writeInt(direction);

        ClientPlayNetworking.send(ServerPacketReceiver.MORPHING_TOOL_SCROLL_PACKET_ID, buf);
    }
}
