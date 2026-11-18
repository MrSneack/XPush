package com.mrsneack.client.modules;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.AtomicMoveNotSupportedException;

public class XPushSave {

    public XPushLogger LOGGER = new XPushLogger();

    // Default data
    public static class XPushData {
        public int delay = 1;
        public boolean enabled = false;
        public boolean enabledGUI = true;
        public boolean enabledJitter = false;
    }

    private final Path path = FabricLoader.getInstance()
            .getConfigDir()
            .resolve("xpush.json");

    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private XPushData data = new XPushData();

    // Save data safely
    public void save() {
        Path tempPath = path.resolveSibling(
                path.getFileName().toString() + ".tmp"
        );

        try {
            Path parent = path.getParent();

            if (parent != null) {
                Files.createDirectories(parent);
            }

            // Write config to temporary file
            try (BufferedWriter writer = Files.newBufferedWriter(tempPath)) {
                gson.toJson(data, writer);
            }

            // Replace original config safely
            try {
                Files.move(
                        tempPath,
                        path,
                        StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE
                );
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(
                        tempPath,
                        path,
                        StandardCopyOption.REPLACE_EXISTING
                );
            }

        } catch (IOException e) {
            LOGGER.ERROR("[XPush] Failed to save config: " + e.getMessage());

            try {
                Files.deleteIfExists(tempPath);
            } catch (IOException ignored) {
                // Ignore temporary file cleanup errors
            }
        }
    }

    // Backup corrupted config
    private boolean backupCorruptedConfig() {
        if (Files.notExists(path)) {
            return true;
        }

        Path backupPath = path.resolveSibling(
                "xpush.corrupted-" + System.currentTimeMillis() + ".json"
        );

        try {
            Files.copy(
                    path,
                    backupPath,
                    StandardCopyOption.COPY_ATTRIBUTES
            );

            LOGGER.WARN(
                    "[XPush] Corrupted config backed up: "
                            + backupPath.getFileName()
            );

            return true;

        } catch (IOException e) {
            LOGGER.ERROR(
                    "[XPush] Failed to back up corrupted config: "
                            + e.getMessage()
            );

            return false;
        }
    }

    // Load data
    public XPushData load() {
        try {
            Path parent = path.getParent();

            if (parent != null) {
                Files.createDirectories(parent);
            }

            // Create default config if it does not exist
            if (Files.notExists(path)) {
                data = new XPushData();
                save();
                return data;
            }

            // Read existing config
            try (BufferedReader reader = Files.newBufferedReader(path)) {
                XPushData loaded = gson.fromJson(reader, XPushData.class);

                // Check for empty or invalid config
                if (loaded == null) {
                    throw new IllegalStateException("Config is empty or null.");
                }

                // Validate delay
                int originalDelay = loaded.delay;
                loaded.delay = Math.max(1, Math.min(20, loaded.delay));

                data = loaded;

                // Save corrected values
                if (originalDelay != loaded.delay) {
                    LOGGER.WARN("[XPush] Invalid delay value. Resetting to valid range.");
                    save();
                }

                return data;
            }

        } catch (Exception e) {
            LOGGER.ERROR(
                    "[XPush] Failed to load config: " + e.getMessage()
            );

            // Reset to default settings
            data = new XPushData();

            // Preserve corrupted file before replacing it
            if (backupCorruptedConfig()) {
                save();
                LOGGER.WARN("[XPush] Config restored with default settings.");
            }

            return data;
        }
    }
}
