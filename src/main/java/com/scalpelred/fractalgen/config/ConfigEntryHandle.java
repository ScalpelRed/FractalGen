package com.scalpelred.fractalgen.config;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.scalpelred.fractalgen.FractalGen;

public class ConfigEntryHandle<T> {

    private static final Gson GSON = new Gson();

    private final String name;
    private final Class<T> type;

    private final T defaultValue;
    private T value;
    private boolean differentFromFile; // more correct to say, it means that value is different from file's

    public ConfigEntryHandle(String name, Class<T> type, T defaultValue) {
        this.name = name;
        this.type = type;
        this.defaultValue = defaultValue;
        value = defaultValue;
        differentFromFile = true;
    }

    public String getName() {
        return name;
    }

    public void resetValue() {
        value = defaultValue;
        differentFromFile = true;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public void valueFromJsonElement(JsonElement raw){
        try {
            value = GSON.fromJson(raw, type);
            differentFromFile = true;
        }
        catch (JsonSyntaxException e) {
            value = defaultValue;
            differentFromFile = true;
            FractalGen.LOGGER.error("Entry \"{}\": cannot parse value to {}", name, type.getName());
        }
    }

    public void setValue(T value) {
        this.value = value;
        this.differentFromFile = true;
    }

    public T getValue() {
        return value;
    }

    public JsonElement toJsonElement() {
        return GSON.toJsonTree(value, type);
    }

    public void resetDifferentFromFile() {
        differentFromFile = false;
    }

    public boolean isDifferentFromFile(){
        return differentFromFile;
    }
}
