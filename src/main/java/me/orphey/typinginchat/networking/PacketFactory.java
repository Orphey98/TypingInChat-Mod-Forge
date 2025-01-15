package me.orphey.typinginchat.networking;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.Supplier;

public class PacketFactory {

    private PacketFactory() {}

    public static final String PROTOCOL_VERSION = "1";
    private static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation("typinginchatmod", "typing_status"))
            .networkProtocolVersion(() -> PROTOCOL_VERSION)
            .clientAcceptedVersions(PROTOCOL_VERSION::equals)
            .serverAcceptedVersions(PROTOCOL_VERSION::equals)
            .simpleChannel();

    public static void register() {
        int id = 0;
        CHANNEL.messageBuilder(TypingPacket.class, id++)
                .encoder(TypingPacket::encode)
                .decoder(TypingPacket::decode)
                .consumerMainThread(PacketFactory::handle)
                .add();
    }

    public static void sendPacket(byte message) {
        TypingPacket data = new TypingPacket(message);
        CHANNEL.send(PacketDistributor.SERVER.noArg(), data);
    }

    private static void handle(TypingPacket data, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            // Server-side handling of the packet
            // Example: Logging or triggering server-side events based on typing state
        });
        context.setPacketHandled(true);
    }
}
