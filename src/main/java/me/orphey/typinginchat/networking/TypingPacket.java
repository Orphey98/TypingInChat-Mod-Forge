package me.orphey.typinginchat.networking;

import net.minecraft.network.FriendlyByteBuf;


public class TypingPacket {

    private byte message;

    public TypingPacket(byte message) {
        this.message = message;
    }

    public static void encode(TypingPacket data, FriendlyByteBuf buf) {
        buf.writeByte(data.message);
    }

    public static TypingPacket decode(FriendlyByteBuf buf) {
        return new TypingPacket(buf.readByte());
    }

    public byte getMessage() {
        return message;
    }
}
