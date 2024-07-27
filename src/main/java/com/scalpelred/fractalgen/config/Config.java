package com.scalpelred.fractalgen.config;

import com.google.gson.*;
import com.scalpelred.fractalgen.FractalGen;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public abstract class Config {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final File configFile;
    private JsonObject jsonRoot;
    private final HashMap<String, JsonElement> jsonEntries = new HashMap<>();
    private final HashMap<String, ConfigEntryHandle<?>> entryHandles = new HashMap<>();

    public Config(String modId) {
        configFile = Paths.get(System.getProperty("user.dir"), "config", modId + ".json").toFile();
    }

    protected void registerEntryHandle(ConfigEntryHandle<?> entry) {
        entryHandles.put(entry.getName(), entry);
    }

    public void load() {
        if (!configFile.exists()) {
            FractalGen.LOGGER.error("Can't load config: file is missing.");
            return;
        }

        jsonEntries.clear();
        String content;
        try {
            content = new String(Files.readAllBytes(configFile.toPath()));
        }
        catch (IOException e) {
            FractalGen.LOGGER.error("Can't load config: {}", e.getMessage());
            return;
        }
        try {
            jsonRoot = JsonParser.parseString(content).getAsJsonObject();
        }
        catch (IllegalStateException e) {
            FractalGen.LOGGER.error("Error loading config: {}", e.getMessage());
            jsonRoot = null;
            return;
        }

        for (Map.Entry<String, JsonElement> pair : jsonRoot.entrySet()) {
            jsonEntries.put(pair.getKey(), pair.getValue());
        }

        for (Map.Entry<String, ConfigEntryHandle<?>> pair : entryHandles.entrySet()) {
            JsonElement entry = jsonEntries.get(pair.getKey());
            ConfigEntryHandle<?> handle = pair.getValue();
            if (entry == null) handle.resetValue();
            else {
                handle.valueFromJsonElement(entry);
                handle.resetDifferentFromFile();
                /*System.out.println(pair.getKey());
                System.out.println(handle.getValue());*/
            }
        }
    }

    public void save() {
        if (jsonRoot == null) jsonRoot = new JsonObject();

        for (Map.Entry<String, ConfigEntryHandle<?>> pair : entryHandles.entrySet()) {
            ConfigEntryHandle<?> handle = pair.getValue();
            if (!handle.isDifferentFromFile()) continue;
            String name = pair.getKey();
            if (jsonEntries.get(name) != null) jsonRoot.remove(name);
            JsonElement json = handle.toJsonElement();
            jsonRoot.add(name, json);
            jsonEntries.put(name, json);
            handle.resetDifferentFromFile();
        }

        if (!configFile.exists()) {
            try {
                Files.createDirectories(configFile.toPath().getParent());
                Files.createFile(configFile.toPath());
            }
            catch (IOException e) {
                FractalGen.LOGGER.error("Can't create config file: {}", e.getMessage());
                return;
            }
        }

        try (FileWriter fileWriter = new FileWriter(configFile)) {
            GSON.toJson(jsonRoot, fileWriter);
        }
        catch (IOException e) {
            FractalGen.LOGGER.error("Can't save config: {}", e.getMessage());
        }
    }

    public boolean isLoaded() {
        return jsonRoot == null;
    }
}
