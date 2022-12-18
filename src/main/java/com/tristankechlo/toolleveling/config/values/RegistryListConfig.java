package com.tristankechlo.toolleveling.config.values;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.tristankechlo.toolleveling.ToolLeveling;
import com.tristankechlo.toolleveling.config.util.AbstractConfigValue;
import com.tristankechlo.toolleveling.config.util.ConfigUtils;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ITagCollection;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RegistryListConfig<T extends IForgeRegistryEntry<T>> extends AbstractConfigValue<ImmutableList<T>> {

    private final ImmutableList<T> defaultValues;
    private ImmutableList<T> values;
    private List<String> rawValues = new ArrayList<>();
    private final Type type = new TypeToken<List<String>>() {}.getType();
    private final Gson GSON = new Gson();
    private final IForgeRegistry<T> registry;
    private final ITagCollection<T> tagCollection;

    public RegistryListConfig(String identifier, IForgeRegistry<T> registry, ITagCollection<T> tags, List<T> defaultValues) {
        super(identifier);
        if (registry == null) {
            throw new NullPointerException("registry of the config value can't be null");
        }
        if (defaultValues == null) {
            throw new NullPointerException("defaultValues of the config value can't be null");
        }
        this.registry = registry;
        this.defaultValues = ImmutableList.copyOf(defaultValues);

        for (T arg : defaultValues) {
            rawValues.add(registry.getKey(arg).toString());
        }
        values = ImmutableList.copyOf(defaultValues);
        this.tagCollection = tags;
    }

    @Override
    public void setToDefault() {
        values = ImmutableList.copyOf(defaultValues);
        this.rawValues.clear();
        this.defaultValues.forEach(x -> this.rawValues.add(this.registry.getKey(x).toString()));
    }

    @Override
    public ImmutableList<T> getValue() {
        return values;
    }

    @Override
    public void serialize(JsonObject jsonObject) {
        JsonElement jsonElement = GSON.toJsonTree(rawValues, type);
        jsonObject.add(getIdentifier(), jsonElement);
    }

    @Override
    public void deserialize(JsonObject jsonObject) {
        try {
            JsonElement jsonElement = jsonObject.get(getIdentifier());
            if (jsonElement == null) {
                this.setToDefault();
                ToolLeveling.LOGGER.warn("Error while loading the config value " + getIdentifier() + ", using default values instead");
                return;
            }
            rawValues = GSON.fromJson(jsonElement, type);
            if (rawValues == null) {
                this.setToDefault();
                ToolLeveling.LOGGER.warn("Error while loading the config value " + getIdentifier() + ", using default values instead");
                return;
            }

            Iterator<String> iterator = rawValues.iterator();
            List<T> parsedValues = new ArrayList<>();
            while (iterator.hasNext()) {
                String nextValue = iterator.next();

                //if nextValue is wildcard, add all entries of the registry
                if (nextValue.contains("*")) {
                    String modId = ConfigUtils.getModIdFromWildcard(nextValue);
                    if (modId != null) {
                        ToolLeveling.LOGGER.info("Found wildcard with modid: '{}' in '{}'", modId, getIdentifier());
                        parsedValues.addAll(ConfigUtils.getAllFromWildcard(modId, registry));
                        continue;
                    }
                    ToolLeveling.LOGGER.warn("Found wildcard with invalid modid '{}' in '{}'", nextValue, getIdentifier());
                }

                //if nextValue is tag, add all entries of the tag
                if (nextValue.startsWith("#")) {
                    ITag<T> tag = ConfigUtils.getTagKeyFromTag(nextValue, tagCollection);
                    if (tag != null) {
                        ToolLeveling.LOGGER.info("Found tag '{}' in '{}'", tag, getIdentifier());
                        parsedValues.addAll(tag.getValues());
                        continue;
                    }
                    ToolLeveling.LOGGER.warn("Found tag with invalid name '{}' in '{}'", nextValue, getIdentifier());
                }

                //if nextValue is single valid entry, add it
                ResourceLocation loc = ResourceLocation.tryParse(nextValue);
                if (loc != null && registry.containsKey(loc)) {
                    T entry = registry.getValue(loc);
                    if (entry != null) {
                        parsedValues.add(entry);
                        continue;
                    }
                }

                //else nextValue is invalid, remove it
                ToolLeveling.LOGGER.warn("Found invalid entry '{}' in '{}'", nextValue, getIdentifier());
                iterator.remove();
            }

            values = ImmutableList.copyOf(parsedValues);
        } catch (Exception e) {
            values = ImmutableList.copyOf(defaultValues);
            ToolLeveling.LOGGER.error("Error while loading the config value " + getIdentifier() + ", using default values instead");
        }
    }

}
