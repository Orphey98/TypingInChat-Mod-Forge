package me.orphey.typinginchat.networking;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent;


public class TypingPacket {

    private final byte message;

    public TypingPacket(byte message) {
        this.message = message;
    }

    public TypingPacket(FriendlyByteBuf buf) {
        this(buf.readByte());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeByte(this.message);
    }

    public static TypingPacket decode(FriendlyByteBuf buf) {
        return new TypingPacket(buf.readByte());
    }

    public void handle(CustomPayloadEvent.Context context) {
        context.setPacketHandled(true);
    }
}
