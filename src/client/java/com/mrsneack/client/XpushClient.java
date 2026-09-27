package com.mrsneack.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import net.minecraft.client.option.KeyBinding.Category;
import net.minecraft.client.option.KeyBinding;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;


public class XpushClient implements ClientModInitializer {

    public static KeyBinding toggleKeyBind;
    public static boolean toggle;
    public static Category category = Category.create(Identifier.of("xpush"));
    public int delay = 0;
    @Override
    public void onInitializeClient() {
        toggleKeyBind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.xpush.toggle",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_K,
                category
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if(delay >= 0){
                delay--;
                return;
            }

           while(toggleKeyBind.wasPressed()) {
               toggle = !toggle;
               if(toggle) {
                   System.out.println("[XPush] Enabled");
               }
               if(!toggle) {
                   System.out.println("[XPush] Disabled");
               }
           }

            if(!toggle) {
                return;
            }
            if (client.player == null || client.interactionManager == null) {
                return;
            }
            var stack = client.player.getMainHandStack();
            if(stack.isOf(Items.EXPERIENCE_BOTTLE) && client.options.useKey.isPressed()) {
                client.interactionManager.interactItem(
                        client.player,
                        Hand.MAIN_HAND
                );
                delay = 5;
            }

        });

    }
}
