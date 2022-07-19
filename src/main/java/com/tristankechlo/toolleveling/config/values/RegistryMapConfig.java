package com.tristankechlo.toolleveling.config.values;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tristankechlo.toolleveling.ToolLeveling;
import com.tristankechlo.toolleveling.config.util.AbstractConfigValue;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class RegistryMapConfig<X, Y> extends AbstractConfigValue<ImmutableMap<X, Y>> {

    private ImmutableMap<X, Y> values;
    private final ImmutableMap<X, Y> defaultValues;
    private final Registry<X> registry;
    private static final Gson GSON = new Gson();

    public RegistryMapConfig(String identifier, Registry<X> registry, Map<X, Y> defaultValues) {
        super(identifier);
        this.registry = registry;
        this.defaultValues = ImmutableMap.copyOf(defaultValues);
    }

    @Override
    public void setToDefault() {
        this.values = ImmutableMap.copyOf(this.defaultValues);
    }

    @Override
    public ImmutableMap<X, Y> getValue() {
        return this.values;
    }

    @Override
    public void serialize(JsonObject jsonObject) {
        Map<String, Y> tempValues = new HashMap<>();
        for (Map.Entry<X, Y> entry : getValue().entrySet()) {
            X key = entry.getKey();
            String keyString = Objects.requireNonNull(registry.getId(key)).toString();
            Y value = entry.getValue();
            tempValues.put(keyString, value);
        }
        JsonElement jsonElement = GSON.toJsonTree(tempValues, this.getType());
        jsonObject.add(getIdentifier(), jsonElement);
    }

    @Override
    public void deserialize(JsonObject jsonObject) {
        JsonElement jsonElement = jsonObject.get(getIdentifier());
        if (jsonElement == null) {
            this.setToDefault();
            ToolLeveling.LOGGER.warn("Error while loading the config value {}, using default values instead", getIdentifier());
            return;
        }
        Map<String, Y> rawValues = GSON.fromJson(jsonElement, this.getType());
        if (rawValues == null) {
            this.setToDefault();
            ToolLeveling.LOGGER.warn("Error while parsing the config value {}, using default values instead", getIdentifier());
            return;
        }
        Map<X, Y> tempValues = new HashMap<>();
        for (Map.Entry<String, Y> entry : rawValues.entrySet()) {
            //try parsing the key
            Identifier identifier = Identifier.tryParse(entry.getKey());
            if (identifier == null) {
                ToolLeveling.LOGGER.warn("Ignoring unknown element {} from {}", entry.getKey(), getIdentifier());
                continue;
            }
            //check if value is in range
            Y value = entry.getValue();
            if (!isValueValid(value)) {
                ToolLeveling.LOGGER.warn("Ignoring value {} from {} because it is out of range", value.toString(), identifier);
                continue;
            }
            //check if key is in the registry
            if (registry.containsId(identifier)) {
                X key = registry.get(identifier);
                if (!isKeyValid(key, identifier)) {
                    ToolLeveling.LOGGER.warn("Ignoring value {} from {} because it is not allowed", identifier, getIdentifier());
                    continue;
                }
                tempValues.put(key, value);
            } else {
                ToolLeveling.LOGGER.warn("Ignoring unknown element {} from {}", entry.getKey(), getIdentifier());
            }
        }
        this.values = ImmutableMap.copyOf(tempValues);
    }

    protected abstract Type getType();

    protected abstract boolean isValueValid(Y value);

    protected abstract boolean isKeyValid(X key, Identifier identifier);

}
