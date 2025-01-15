package me.orphey.typinginchat;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;


// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Forge's config APIs
@Mod.EventBusSubscriber(modid = TypingInChat.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ConfigLoader
{
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.BooleanValue MOD_ENABLE = BUILDER
            .comment("Option to disable mod functions if you have problems on some servers.")
            .define("enableMod", true);

    private static final ForgeConfigSpec.BooleanValue IGNORE_COMMANDS = BUILDER
            .comment("Ignore all input that is a console command.")
            .define("ignoreCommands", false);

    public static final ForgeConfigSpec.BooleanValue DEBUG = BUILDER
            .comment("Show debug messages.")
            .define("debug", false);

    static final ForgeConfigSpec SPEC = BUILDER.build();

    private static boolean enableMod;
    private static boolean ignoreCommands;
    private static boolean showDebug;
    private static final ConfigLoader instance = new ConfigLoader();

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        enableMod = MOD_ENABLE.get();
        ignoreCommands = IGNORE_COMMANDS.get();
        showDebug = DEBUG.get();
    }

    public static void register() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, SPEC, "typinginchat-config.toml");
    }

    public boolean isEnableMod() {
        return ConfigLoader.enableMod;
    }
    public boolean isIgnoreCommands() {
        return ConfigLoader.ignoreCommands;
    }
    public boolean isDebug() {
        return ConfigLoader.showDebug;
    }

    public static ConfigLoader getInstance() {
        return instance;
    }
}
