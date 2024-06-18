package renektoff.refabricated_necessities.networking;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class ServerPacketSender {
    public static final Identifier FERAL_FLARE_LANTERN_BROKEN_PACKET_ID = new Identifier("feral-flare-lantern-broken");
    public static final Identifier MORPHING_TOOL_SERVER_ITEM_REMOVED_PACKET_ID = new Identifier("morphing-tool-server-item-removed");

    public static void sendBrokenLanternPacket(ServerPlayerEntity serverPlayer) {
        ServerPlayNetworking.send(serverPlayer, FERAL_FLARE_LANTERN_BROKEN_PACKET_ID, PacketByteBufs.empty());
    }

    public static void sendMorphToolItemRemovedPacket(ServerPlayerEntity serverPlayer) {
        ServerPlayNetworking.send(serverPlayer, MORPHING_TOOL_SERVER_ITEM_REMOVED_PACKET_ID, PacketByteBufs.empty());
    }
}
