package com.tristankechlo.toolleveling.config.values;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tristankechlo.toolleveling.ToolLeveling;
import com.tristankechlo.toolleveling.config.util.AbstractConfigValue;
import com.tristankechlo.toolleveling.config.util.ConfigUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraftforge.registries.IForgeRegistry;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class RegistryMapConfig<X, Y> extends AbstractConfigValue<ImmutableMap<X, Y>> {

    private ImmutableMap<X, Y> values;
    private final ImmutableMap<X, Y> defaultValues;
    private Map<String, Y> rawValues = new HashMap<>();
    private final IForgeRegistry<X> registry;
    private static final Gson GSON = new Gson();

    public RegistryMapConfig(String identifier, IForgeRegistry<X> registry, Map<X, Y> defaultValues) {
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
        JsonElement jsonElement = GSON.toJsonTree(rawValues, this.getType());
        jsonObject.add(getIdentifier(), jsonElement);
    }

    @Override
    public void deserialize(JsonObject jsonObject) {
        JsonElement jsonElement = jsonObject.get(getIdentifier());
        if (jsonElement == null) {
            this.setToDefault();
            ToolLeveling.LOGGER.error("Error while loading the config value '{}', using default values instead", getIdentifier());
            return;
        }
        rawValues = GSON.fromJson(jsonElement, this.getType());
        if (rawValues == null) {
            this.setToDefault();
            ToolLeveling.LOGGER.error("Error while parsing the config value '{}', using default values instead", getIdentifier());
            return;
        }

        Iterator<Map.Entry<String, Y>> iterator = rawValues.entrySet().iterator();
        Map<X, Y> parsedValues = new HashMap<>();
        while (iterator.hasNext()) {
            Map.Entry<String, Y> entry = iterator.next();

            //check if value is valid
            Y value = entry.getValue();
            if (!isValueValid(value)) {
                ToolLeveling.LOGGER.warn("Removing '{}' because the value '{}' is not valid", entry.getKey(), entry.getValue());
                iterator.remove();
                continue;
            }

            //if key is wildcard, add all entries of the registry
            if (entry.getKey().contains("*")) {
                String modid = ConfigUtils.getModIdFromWildcard(entry.getKey());
                if (modid != null) {
                    List<X> entries = ConfigUtils.getAllFromWildcard(modid, registry);
                    addAllEntries(parsedValues, entries, value);
                    ToolLeveling.LOGGER.info("Found wildcard with modid: '{}' in '{}'", modid, getIdentifier());
                    continue;
                }
                ToolLeveling.LOGGER.warn("Found wildcard with invalid modid '{}' in '{}'", entry.getKey(), getIdentifier());
            }

            //if key is tag, add all entries of the tag
            if (entry.getKey().startsWith("#")) {
                TagKey<X> tagKey = ConfigUtils.getTagKeyFromTag(entry.getKey(), registry);
                if (tagKey != null) {
                    List<X> entries = ConfigUtils.getAllFromTag(tagKey, registry);
                    addAllEntries(parsedValues, entries, value);
                    ToolLeveling.LOGGER.info("Found tag '{}' in '{}'", tagKey.location(), getIdentifier());
                    continue;
                }
                ToolLeveling.LOGGER.warn("Found tag with invalid name '{}' in '{}'", entry.getKey(), getIdentifier());
            }

            //if key is single valid entry, add it
            ResourceLocation loc = ResourceLocation.tryParse(entry.getKey());
            if (loc != null && registry.containsKey(loc)) {
                X singleEntry = registry.getValue(loc);
                if (singleEntry != null && isKeyValid(singleEntry, loc)) {
                    parsedValues.put(singleEntry, value);
                    continue;
                }
                ToolLeveling.LOGGER.warn("Ignoring value '{}' from '{}' because it is not allowed", loc, getIdentifier());
            }

            //else key is invalid, remove it
            ToolLeveling.LOGGER.warn("Found invalid entry '{}' in '{}'", entry.getKey(), getIdentifier());
            iterator.remove();
        }
        this.values = ImmutableMap.copyOf(parsedValues);
    }

    public void addAllEntries(Map<X, Y> parsedValues, List<X> entries, Y value) {
        entries.forEach((key) -> {
            if (isKeyValid(key, registry.getKey(key))) {
                parsedValues.put(key, value);
            } else {
                ToolLeveling.LOGGER.warn("Ignoring value '{}' from '{}' because it is not allowed", key, registry.getKey(key).toString());
            }
        });
    }

    protected abstract Type getType();

    protected abstract boolean isValueValid(Y value);

    protected abstract boolean isKeyValid(X key, ResourceLocation identifier);

}
