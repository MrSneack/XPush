package com.mrsneack.client;

import com.mrsneack.client.modules.XPushScreen;
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
    public static Category xpushCategory = Category.create(Identifier.of("xpush"));
    public static int delay = 1;
    public static int delayTimer = 0;
    public static KeyBinding openGUI;
    public static KeyBinding debugKey;
    public static boolean debug = false;

    @Override
    public void onInitializeClient() {

        toggleKeyBind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.xpush.toggle",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_K,
                xpushCategory
        ));

        openGUI = KeyBindingHelper.registerKeyBinding(new KeyBinding( "key.xpush.openGUI", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, XpushClient.xpushCategory ));
        debugKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.xpush.debug", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_G, XpushClient.xpushCategory));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
           while (openGUI.wasPressed())
           {
               if(client.currentScreen instanceof XPushScreen) {
                   client.setScreen(null);
                   if(debug) {
                       System.out.println("[XPush] Opening GUI");
                   }
               }
               else {
                   client.setScreen(new XPushScreen());
                   if(debug) {
                       System.out.println("[XPush] Closing GUI");
                   }
               }
           };

           while(toggleKeyBind.wasPressed()) {
               toggle = !toggle;
               if(toggle && debug) {
                   System.out.println("[XPush] Enabled");
               }
               if(!toggle && debug) {
                   System.out.println("[XPush] Disabled");
               }
           }



            if(!toggle) {
                return;
            }
            if (client.player == null || client.interactionManager == null) {
                return;
            }
            if(delayTimer > 0){
                delayTimer--;
                return;
            }
            var stack = client.player.getMainHandStack();
            if(stack.isOf(Items.EXPERIENCE_BOTTLE) && client.options.useKey.isPressed()) {
                client.interactionManager.interactItem(
                        client.player,
                        Hand.MAIN_HAND
                );
                delayTimer = delay + 1;
            }

        });

    }
}
