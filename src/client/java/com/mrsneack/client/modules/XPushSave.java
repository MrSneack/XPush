package com.mrsneack.client.modules;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class XPushSave {

    // Default data
    public static class XPushData {
        public int delay = 1;
        public boolean enabled = false;
        public boolean enabledGUI = true;
    }

    private final Path path = FabricLoader.getInstance()
            .getConfigDir()
            .resolve("xpush.json");

    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private XPushData data = new XPushData();

    // Save data
    public void save() {
        try {
            Path parent = path.getParent();

            if (parent != null) {
                Files.createDirectories(parent);
            }

            try (BufferedWriter writer = Files.newBufferedWriter(path)) {
                gson.toJson(data, writer);
            }

        } catch (IOException e) {
            System.err.println("[XPush] Failed to save config!");
            e.printStackTrace();
        }
    }

    // Load data
    public XPushData load() {
        try {
            Path parent = path.getParent();

            if (parent != null) {
                Files.createDirectories(parent);
            }

            // Create a default config if it does not exist
            if (Files.notExists(path)) {
                data = new XPushData();
                save();
                return data;
            }

            // Read existing config
            try (BufferedReader reader = Files.newBufferedReader(path)) {
                XPushData loaded = gson.fromJson(reader, XPushData.class);

                data = loaded != null ? loaded : new XPushData();

                // Validate delay
                data.delay = Math.max(1, data.delay);

                return data;
            }

        } catch (Exception e) {
            System.err.println("[XPush] Failed to load config!");
            e.printStackTrace();

            data = new XPushData();
            return data;
        }
    }

    public XPushData getData() {
        return data;
    }
}