package com.mrsneack.client.modules;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mrsneack.client.XpushClient;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;
import java.util.stream.IntStream;

public class XPushCommands {

    private XPushCommands() {
    }

    private static Text getStatusText(boolean enabled) {
        return enabled
                ? Text.translatable("text.xpush.enabled").formatted(Formatting.GREEN)
                : Text.translatable("text.xpush.disabled").formatted(Formatting.RED);
    }

    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, commandRegistryAccess) -> {
            dispatcher.register(
                    ClientCommandManager.literal("xpush")
                            .executes(context -> {
                                context.getSource().sendFeedback(
                                        Text.translatable("text.xpush.defaultCommand")
                                                .formatted(Formatting.AQUA)
                                                .append(" Delay | Enable | Disable | TimingJitter | GUI")
                                                .formatted(Formatting.DARK_AQUA)
                                );
                                return 1;
                            })

                            .then(ClientCommandManager.argument("command", StringArgumentType.word())
                                    .suggests((context, builder) ->
                                            CommandSource.suggestMatching(
                                                    List.of("Delay", "Enable", "Disable", "TimingJitter", "GUI"),
                                                    builder
                                            )
                                    )

                                    // Enable / Disable XPush
                                    .executes(context -> {
                                        String command = StringArgumentType.getString(context, "command");

                                        if (command.equalsIgnoreCase("Enable")
                                                || command.equalsIgnoreCase("Disable")) {

                                            boolean enabled = command.equalsIgnoreCase("Enable");
                                            XpushClient.config.enabled = enabled;

                                            context.getSource().sendFeedback(
                                                    Text.translatable("text.xpush.setEnabled")
                                                            .formatted(Formatting.AQUA)
                                                            .append(getStatusText(enabled))
                                            );

                                            XpushClient.saveManager.save();
                                            return 1;
                                        }

                                        if (command.equalsIgnoreCase("Delay")) {
                                            context.getSource().sendFeedback(
                                                    Text.literal("Usage: /xpush Delay <1-20>")
                                                            .formatted(Formatting.YELLOW)
                                            );
                                            return 1;
                                        }

                                        if (command.equalsIgnoreCase("TimingJitter")) {
                                            context.getSource().sendFeedback(
                                                    Text.literal("Usage: /xpush TimingJitter <Enable|Disable>")
                                                            .formatted(Formatting.YELLOW)
                                            );
                                            return 1;
                                        }

                                        if (command.equalsIgnoreCase("GUI")) {
                                            context.getSource().sendFeedback(
                                                    Text.literal("Usage: /xpush GUI <Enable|Disable>")
                                                            .formatted(Formatting.YELLOW)
                                            );
                                            return 1;
                                        }

                                        context.getSource().sendError(
                                                Text.literal("Unknown command.")
                                        );
                                        return 0;
                                    })

                                    // Delay / TimingJitter / GUI arguments
                                    .then(ClientCommandManager.argument("value", StringArgumentType.word())

                                            // Dynamic Tab Completion
                                            .suggests((context, builder) -> {
                                                String command = StringArgumentType.getString(context, "command");

                                                if (command.equalsIgnoreCase("Delay")) {
                                                    return CommandSource.suggestMatching(
                                                            IntStream.rangeClosed(1, 20)
                                                                    .mapToObj(String::valueOf)
                                                                    .toList(),
                                                            builder
                                                    );
                                                }

                                                if (command.equalsIgnoreCase("TimingJitter")
                                                        || command.equalsIgnoreCase("GUI")) {

                                                    return CommandSource.suggestMatching(
                                                            List.of("Enable", "Disable"),
                                                            builder
                                                    );
                                                }

                                                return builder.buildFuture();
                                            })

                                            .executes(context -> {
                                                String command = StringArgumentType.getString(context, "command");
                                                String value = StringArgumentType.getString(context, "value");

                                                // Delay
                                                if (command.equalsIgnoreCase("Delay")) {
                                                    int delay;

                                                    try {
                                                        delay = Integer.parseInt(value);
                                                    } catch (NumberFormatException e) {
                                                        context.getSource().sendError(
                                                                Text.literal("Delay must be a number from 1 to 20.")
                                                        );
                                                        return 0;
                                                    }

                                                    if (delay < 1 || delay > 20) {
                                                        context.getSource().sendError(
                                                                Text.literal("Delay must be between 1 and 20.")
                                                        );
                                                        return 0;
                                                    }

                                                    XpushClient.config.delay = delay;

                                                    context.getSource().sendFeedback(
                                                            Text.translatable("text.xpush.setDelay")
                                                                    .formatted(Formatting.AQUA)
                                                                    .append(String.valueOf(delay))
                                                                    .formatted(Formatting.DARK_AQUA)
                                                    );

                                                    XpushClient.saveManager.save();
                                                    return 1;
                                                }

                                                // Validate Enable / Disable
                                                if (!value.equalsIgnoreCase("Enable")
                                                        && !value.equalsIgnoreCase("Disable")) {

                                                    context.getSource().sendError(
                                                            Text.literal("Use Enable or Disable.")
                                                    );
                                                    return 0;
                                                }

                                                boolean enabled = value.equalsIgnoreCase("Enable");

                                                // TimingJitter
                                                if (command.equalsIgnoreCase("TimingJitter")) {
                                                    XpushClient.config.enabledJitter = enabled;

                                                    context.getSource().sendFeedback(
                                                            Text.translatable("text.xpush.setJitter")
                                                                    .formatted(Formatting.AQUA)
                                                                    .append(getStatusText(enabled))
                                                    );

                                                    XpushClient.saveManager.save();
                                                    return 1;
                                                }

                                                // GUI
                                                if (command.equalsIgnoreCase("GUI")) {
                                                    XpushClient.config.enabledGUI = enabled;

                                                    context.getSource().sendFeedback(
                                                            Text.translatable("text.xpush.setEnabledGUI")
                                                                    .formatted(Formatting.AQUA)
                                                                    .append(getStatusText(enabled))
                                                    );

                                                    XpushClient.saveManager.save();
                                                    return 1;
                                                }

                                                context.getSource().sendError(
                                                        Text.literal("This command does not accept an argument.")
                                                );
                                                return 0;
                                            })
                                    )
                            )
            );
        });
    }
}
