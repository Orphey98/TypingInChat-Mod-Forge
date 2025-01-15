package me.orphey.typinginchat.configuration;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;

public class ConfigLoader {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec CLIENT_CONFIG;

    // Configurable options
    public static ForgeConfigSpec.BooleanValue enableMod;
    public static ForgeConfigSpec.BooleanValue ignoreCommands;
    public static ForgeConfigSpec.BooleanValue debug;

    static {
        BUILDER.comment("General settings")
                .push("general");

        enableMod = BUILDER.comment("Enable or disable the mod")
                .define("enableMod", true);

        ignoreCommands = BUILDER.comment("Ignore commands when detecting typing")
                .define("ignoreCommands", false);

        debug = BUILDER.comment("Enable debug mode")
                .define("debug", false);

        BUILDER.pop();

        CLIENT_CONFIG = BUILDER.build();
    }

    public static void register() {
        // Register the client configuration
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CLIENT_CONFIG, "typinginchat-config.toml");

        // Load the configuration file from the config directory
        loadConfig(FMLPaths.CONFIGDIR.get());
    }

    public static void loadConfig(Path configDir) {
        final Path configPath = configDir.resolve("typinginchat-config.toml");
        final CommentedFileConfig configData = CommentedFileConfig.builder(configPath)
                .sync()
                .autosave()
                .writingMode(com.electronwill.nightconfig.core.io.WritingMode.REPLACE)
                .build();
        configData.load();
        CLIENT_CONFIG.setConfig(configData);
    }

    // Accessor methods for configuration options
    public static boolean isEnableMod() {
        return enableMod.get();
    }

    public static boolean isIgnoreCommands() {
        return ignoreCommands.get();
    }

    public static boolean isDebug() {
        return debug.get();
    }
}
