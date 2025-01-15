package me.orphey.typinginchat;

import com.mojang.logging.LogUtils;
import me.orphey.typinginchat.configuration.ConfigLoader;
import me.orphey.typinginchat.configuration.ConfigScreen;
import me.orphey.typinginchat.mixin.ChatAccessor;
import me.orphey.typinginchat.networking.PacketFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.slf4j.Logger;

import java.util.List;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(TypingInChat.MOD_ID)
public class TypingInChat
{
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "typinginchat";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    private boolean chatOpen = false;
    private boolean chatTyping = false;
    private int stoppedTypingCounter;
    private static final int STOPPED_TYPING_DELAY = 80;
    private String chatTextBuf = "";


    public TypingInChat()
    {
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        ConfigLoader.loadConfig(FMLPaths.CONFIGDIR.get());
        PacketFactory.register();
        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () ->
                new ConfigScreenHandler.ConfigScreenFactory((mc, screen) -> {
                    return new ConfigScreen(screen);
                })
        );
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (!ConfigLoader.isEnableMod() || Minecraft.getInstance().player == null) {
            return;
        }
        Screen currentScreen = Minecraft.getInstance().screen;
        if (currentScreen instanceof ChatScreen) {
            handleChatScreen();
        } else {
            handleScreenClose();
        }
    }

    private void handleChatScreen() {
        if (!chatOpen) {
            onChatOpen();
        }
        boolean currentlyTyping = isTyping((ChatScreen) Minecraft.getInstance().screen);
        if (currentlyTyping && playersNearby(Minecraft.getInstance())) {
            onTyping();
        }
        if (!currentlyTyping) {
            stoppedTypingCounter++;
        } else {
            stoppedTypingCounter = 0;
        }
        if (stoppedTypingCounter >= STOPPED_TYPING_DELAY) {
            handleStoppedTyping();
        }
    }

    private void onChatOpen() {
        chatOpen = true;
        if (ConfigLoader.isDebug()) {
            Minecraft.getInstance().player.sendSystemMessage(Component.literal("Chat GUI opened!"));
        }
    }

    private void onTyping() {
        if (!chatTyping) {
            chatTyping = true;
            PacketFactory.sendPacket((byte) 1);
            if (ConfigLoader.isDebug()) {
                Minecraft.getInstance().player.sendSystemMessage(Component.literal("Player is Typing"));
            }
        }
    }

    private void handleStoppedTyping() {
        if (chatTyping) {
            chatTyping = false;
            stoppedTypingCounter = 0;
            PacketFactory.sendPacket((byte) 0);
            if (ConfigLoader.isDebug()) {
                Minecraft.getInstance().player.sendSystemMessage(Component.literal("Player stopped typing"));
            }
        }
    }

    private void handleScreenClose() {
        if (chatOpen) {
            chatOpen = false;
            chatTyping = false;
            chatTextBuf = "";
            stoppedTypingCounter = 0;
            PacketFactory.sendPacket((byte) 0);
            if (ConfigLoader.isDebug()) {
                Minecraft.getInstance().player.sendSystemMessage(Component.literal("Chat GUI closed!"));
            }
        }
    }

    private boolean isTyping(ChatScreen chatScreen) {
        ChatAccessor chatScreenAccessor = (ChatAccessor) chatScreen;
        String chatText = chatScreenAccessor.getChatField().getValue();
        if (ConfigLoader.isIgnoreCommands() && isCommand(chatText)) {
            return false;
        }
        if (chatText != null && !chatText.equals(chatTextBuf)) {
            chatTextBuf = chatText;
            return true;
        } else {
            return false;
        }
    }

    private boolean playersNearby(Minecraft client) {
        List<Player> nearbyPlayers = client.player.level().getEntitiesOfClass(
                Player.class, client.player.getBoundingBox().inflate(25), p -> p != client.player
        );
        return !nearbyPlayers.isEmpty();
    }

    private boolean isCommand(String chatText) {
        String trimmedInput = chatText == null ? "" : chatText.trim();
        return !trimmedInput.isEmpty() && trimmedInput.charAt(0) == '/';
    }
}
