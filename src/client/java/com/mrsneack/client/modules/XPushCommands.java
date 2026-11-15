package com.mrsneack.client.modules;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mrsneack.client.XpushClient;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class XPushCommands {

    private XPushCommands() {
    }

    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, commandRegistryAccess) -> {
            dispatcher.register(
                    ClientCommandManager.literal("xpush")
                            .executes(context -> {
                                context.getSource().sendFeedback(
                                        Text.translatable("text.xpush.defaultCommand")
                                                .formatted(Formatting.AQUA)
                                                .append(" delay | enabled | enabledJitter | enabledGUI")
                                                .formatted(Formatting.DARK_AQUA)
                                );
                                return 1;
                            })

                            .then(ClientCommandManager.literal("delay")
                                    .then(ClientCommandManager.argument(
                                                    "delay",
                                                    IntegerArgumentType.integer(1, 20)
                                            )
                                            .executes(context -> {
                                                XpushClient.config.delay = IntegerArgumentType.getInteger(context, "delay");
                                                context.getSource().sendFeedback(
                                                        Text.translatable("text.xpush.setDelay")
                                                                .formatted(Formatting.AQUA)
                                                                .append(String.valueOf(XpushClient.config.delay))
                                                                .formatted(Formatting.DARK_AQUA)
                                                );
                                                XpushClient.saveManager.save();
                                                return 1;
                                            })
                                    )
                            )

                            .then(ClientCommandManager.literal("enabled")
                                    .then(ClientCommandManager.argument(
                                                    "enabled",
                                                    BoolArgumentType.bool()
                                            )
                                            .executes(context -> {
                                                XpushClient.config.enabled = BoolArgumentType.getBool(context, "enabled");
                                                Text enabledCommandText = XpushClient.config.enabled
                                                        ? Text.translatable("text.xpush.enabled").formatted(Formatting.GREEN)
                                                        : Text.translatable("text.xpush.disabled").formatted(Formatting.RED);

                                                context.getSource().sendFeedback(
                                                        Text.translatable("text.xpush.setEnabled")
                                                                .formatted(Formatting.AQUA)
                                                                .append(enabledCommandText)
                                                );
                                                XpushClient.saveManager.save();
                                                return 1;
                                            })
                                    )
                            )

                            .then(ClientCommandManager.literal("enabledJitter")
                                    .then(ClientCommandManager.argument(
                                                    "enabledJitter",
                                                    BoolArgumentType.bool()
                                            )
                                            .executes(context -> {
                                                XpushClient.config.enabledJitter = BoolArgumentType.getBool(context, "enabledJitter");
                                                Text enabledJitterCommandText = XpushClient.config.enabledJitter
                                                        ? Text.translatable("text.xpush.enabled").formatted(Formatting.GREEN)
                                                        : Text.translatable("text.xpush.disabled").formatted(Formatting.RED);

                                                context.getSource().sendFeedback(
                                                        Text.translatable("text.xpush.setJitter")
                                                                .formatted(Formatting.AQUA)
                                                                .append(enabledJitterCommandText)
                                                );
                                                XpushClient.saveManager.save();
                                                return 1;
                                            })
                                    )
                            )

                            .then(ClientCommandManager.literal("enabledGUI")
                                    .then(ClientCommandManager.argument(
                                                    "enabledGUI",
                                                    BoolArgumentType.bool()
                                            )
                                            .executes(context -> {
                                                XpushClient.config.enabledGUI = BoolArgumentType.getBool(context, "enabledGUI");
                                                Text enabledGUICommandText = XpushClient.config.enabledGUI
                                                        ? Text.translatable("text.xpush.enabled").formatted(Formatting.GREEN)
                                                        : Text.translatable("text.xpush.disabled").formatted(Formatting.RED);

                                                context.getSource().sendFeedback(
                                                        Text.translatable("text.xpush.setEnabledGUI")
                                                                .formatted(Formatting.AQUA)
                                                                .append(enabledGUICommandText)
                                                );
                                                XpushClient.saveManager.save();
                                                return 1;
                                            })
                                    )
                            )
            );
        });
    }
}
