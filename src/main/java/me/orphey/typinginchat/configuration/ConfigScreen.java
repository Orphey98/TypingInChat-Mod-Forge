package me.orphey.typinginchat.configuration;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class ConfigScreen extends Screen {

    private final Screen parent;

    public ConfigScreen(Screen parent) {
        super(Component.literal("TypingInChat Configuration"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int buttonHeight = 20;
        int verticalSpacing = 24;
        int startY = this.height / 2 - 24; // Initial Y position for the first button

        // Example: Add a button to toggle a config option
        this.addRenderableWidget(Button.builder(
                Component.literal("Enable Mod: " + ConfigLoader.isEnableMod()),
                button -> {
                    boolean newValue = !ConfigLoader.isEnableMod();
                    ConfigLoader.enableMod.set(newValue);
                    button.setMessage(Component.literal("Enable Mod: " + newValue));
                }
        ).bounds(this.width / 2 - 100, startY, 200, buttonHeight).build());

        startY += verticalSpacing;

        this.addRenderableWidget(Button.builder(
                Component.literal("Ignore Commands: " + ConfigLoader.isIgnoreCommands()),
                button -> {
                    boolean newValue = !ConfigLoader.isIgnoreCommands();
                    ConfigLoader.ignoreCommands.set(newValue);
                    button.setMessage(Component.literal("Ignore Commands: " + newValue));
                }
        ).bounds(this.width / 2 - 100, startY, 200, buttonHeight).build());

        startY += verticalSpacing;

        this.addRenderableWidget(Button.builder(
                Component.literal("Debug: " + ConfigLoader.isDebug()),
                button -> {
                    boolean newValue = !ConfigLoader.isDebug();
                    ConfigLoader.debug.set(newValue);
                    button.setMessage(Component.literal("Debug: " + newValue));
                }
        ).bounds(this.width / 2 - 100, startY, 200, buttonHeight).build());

        startY += verticalSpacing * 2;

        // Add a "Done" button to go back to the previous screen
        this.addRenderableWidget(Button.builder(
                Component.literal("Done"),
                button -> this.minecraft.setScreen(parent)
        ).bounds(this.width / 2 - 100, startY, 200, buttonHeight).build());
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        // Render the dirt background
        this.renderDirtBackground(poseStack);
        // Render the screen elements
        super.render(poseStack, mouseX, mouseY, partialTicks);
        // Optionally render a title
        drawCenteredString(poseStack, this.font, this.title.getString(), this.width / 2, 20, 0xFFFFFF);
        // Check if mouse is hovering over a button and display a tooltip
        this.renderButtonTooltips(poseStack, mouseX, mouseY);
    }

    private void renderButtonTooltips(PoseStack poseStack, int mouseX, int mouseY) {
        for (Button button : this.renderables.stream().filter(Button.class::isInstance).map(Button.class::cast).toList()) {
            if (button.isHoveredOrFocused()) {
                // Tooltip text for each button
                Component tooltip = switch (button.getMessage().getString()) {
                    case "Enable Mod: true", "Enable Mod: false" -> Component.literal("Toggle the mod's functionality.");
                    case "Ignore Commands: true", "Ignore Commands: false" -> Component.literal("Is command input should be ignored by mod.");
                    case "Debug: true", "Debug: false" -> Component.literal("Debug mod (for developers).");
                    default -> null;
                };

                if (tooltip != null) {
                    this.renderTooltip(poseStack, tooltip, mouseX, mouseY);
                }
            }
        }
    }

    @Override
    public void onClose() {
        // Save config changes when the screen is closed
        ConfigLoader.CLIENT_CONFIG.save();
        super.onClose();
    }
}
