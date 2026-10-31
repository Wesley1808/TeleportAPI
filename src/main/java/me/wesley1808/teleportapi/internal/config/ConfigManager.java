package me.wesley1808.teleportapi.internal.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import me.wesley1808.teleportapi.internal.TeleportAPI;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.charset.StandardCharsets;

public final class ConfigManager {
    public static final File CONFIG = new File(FabricLoader.getInstance().getConfigDir().toFile(), "teleportapi.json");

    private static final Gson GSON = new GsonBuilder()
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .create();

    public static void load() {
        if (!CONFIG.exists()) {
            ConfigManager.save(CONFIG, Config.instance);
        }

        Config.instance = ConfigManager.load(CONFIG, TypeToken.get(Config.class));
    }

    public static void save() {
        ConfigManager.save(CONFIG, Config.instance);
    }

    private static <T> T load(File file, TypeToken<T> type) {
        try (var reader = getReader(file)) {
            return GSON.fromJson(reader, type);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to load " + file.getName() + "!", ex);
        }
    }

    private static <T> void save(File file, T value) {
        try (var writer = getWriter(file)) {
            writer.write(GSON.toJson(value));
        } catch (Exception ex) {
            TeleportAPI.LOGGER.error("Failed to save {}!", file.getName(), ex);
        }
    }

    private static BufferedWriter getWriter(File file) throws FileNotFoundException {
        return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));
    }

    private static BufferedReader getReader(File file) throws FileNotFoundException {
        return new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
    }
}