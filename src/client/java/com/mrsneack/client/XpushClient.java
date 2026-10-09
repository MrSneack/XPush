package com.mrsneack.client;

import com.mrsneack.client.modules.XPushSave;
import com.mrsneack.client.modules.XPushScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import net.minecraft.client.option.KeyBinding.Category;
import net.minecraft.client.option.KeyBinding;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import static net.minecraft.util.Hand.MAIN_HAND;
import static net.minecraft.util.Hand.OFF_HAND;

public class XpushClient implements ClientModInitializer {

    public static KeyBinding toggleKeyBind;
    public static Category xpushCategory = Category.create(Identifier.of("xpush"));
    public static int delayTimer = 0;
    public static KeyBinding openGUI;
    public static Hand hand;
    public static final XPushSave saveManager = new XPushSave();

    // set XPushData to variable for editing
    public static XPushSave.XPushData config = saveManager.load();

    @Override
    public void onInitializeClient() {
        // initialize config in minecraft start
        config = saveManager.load();
        // Create toggle key bind
        toggleKeyBind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.xpush.toggle",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_K,
                xpushCategory
        ));

        // Create OpenGUI KeyBind
        openGUI = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.xpush.openGUI",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_G,
                XpushClient.xpushCategory
        ));

        // Event calling every tick
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            // while if keybind pressed and GUIEnabled has true
            while (openGUI.wasPressed() && config.enabledGUI) {
                // Open GUI if his closed
                if (client.currentScreen instanceof XPushScreen) {
                    client.setScreen(null);
                    System.out.println("[XPush] Opening GUI");
                }
                // Close GUI if his opened
                else {
                    client.setScreen(new XPushScreen());
                    System.out.println("[XPush] Closing GUI");
                }
            }


            // if client equal null and integrationManager equal null else return 0
            if (client.player == null || client.interactionManager == null) {
                return;
            }

            // while if toggle Keybind pressed
            while (toggleKeyBind.wasPressed()) {
                config.enabled = !config.enabled;
                // if XPush enabled with keybind send in chat message about enabling
                if (config.enabled) {
                    client.player.sendMessage(Text.literal("[XPush] ").formatted(Formatting.AQUA).append(Text.literal("Enabled").formatted(Formatting.GREEN)), false);
                }
                // if XPush disabled with keybind send in chat message about disabling
                if (!config.enabled) {
                    client.player.sendMessage(Text.literal("[XPush] ").formatted(Formatting.AQUA).append(Text.literal("Disabled").formatted(Formatting.DARK_RED)), false);
                }
                saveManager.save();
            }

            if (!config.enabled) {
                return;
            }

            // if timer less 0 perform code
            if (delayTimer > 0) {
                delayTimer--;
                return;
            }

            // assign an item in hand to a variable
            var stack = client.player.getMainHandStack();
            var off_stack = client.player.getOffHandStack();

            if (stack.isOf(Items.EXPERIENCE_BOTTLE)){
                hand = MAIN_HAND;
            } else if(off_stack.isOf(Items.EXPERIENCE_BOTTLE)) {
                hand = OFF_HAND;
            }
            else {
                hand = null;
            }

            // if in hand experience bottle and player hold right mouse key using item
            if(hand != null && client.options.useKey.isPressed()) {
                client.interactionManager.interactItem(
                        client.player,
                        hand
                );
                // set timer
                delayTimer = config.delay + 1;
            }

        });

    }
}
