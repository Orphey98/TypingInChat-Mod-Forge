package me.orphey.typinginchat.networking;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.SimpleChannel;

public class PacketFactory {

    private PacketFactory() {}

    private static final SimpleChannel INSTANCE = ChannelBuilder.named(
            new ResourceLocation("typinginchatmod", "typing_status"))
            .serverAcceptedVersions((status, version) -> true)
            .clientAcceptedVersions((status, version) -> true)
            .networkProtocolVersion(1)
            .simpleChannel();

    public static void register() {
        INSTANCE.messageBuilder(TypingPacket.class, NetworkDirection.PLAY_TO_SERVER)
                .encoder(TypingPacket::encode)
                .decoder(TypingPacket::decode)
                .consumerMainThread(TypingPacket::handle)
                .add();

    }

    public static void sendToServer(TypingPacket packet) {
        INSTANCE.send(packet, PacketDistributor.SERVER.noArg());
    }
}
