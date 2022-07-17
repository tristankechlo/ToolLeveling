package com.tristankechlo.toolleveling.config.values;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.tristankechlo.toolleveling.ToolLeveling;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.IForgeRegistry;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ForgeRegistryConfig<T> extends AbstractConfigValue<ImmutableList<T>> {

    private final ImmutableList<T> defaultValues;
    private ImmutableList<T> values;
    private List<String> rawValues = new ArrayList<>();
    private final Type type = new TypeToken<List<String>>() {}.getType();
    private final Gson GSON = new Gson();
    private final IForgeRegistry<T> registry;

    public ForgeRegistryConfig(String identifier, IForgeRegistry<T> registry, List<T> defaultValues) {
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
    }

    @Override
    public void setToDefault() {
        values = ImmutableList.copyOf(defaultValues);
    }

    @Override
    public ImmutableList<T> getValue() {
        return values;
    }

    @Override
    public void serialize(JsonObject jsonObject) {
        List<String> tempValues = getValue().stream().map((element) -> {
            return registry.getKey(element).toString();
        }).collect(Collectors.toList());
        JsonElement jsonElement = GSON.toJsonTree(tempValues, type);
        jsonObject.add(getIdentifier(), jsonElement);
    }

    @Override
    public void deserialize(JsonObject jsonObject) {
        try {
            JsonElement jsonElement = jsonObject.get(getIdentifier());
            if (jsonElement == null) {
                this.setToDefault();
                ToolLeveling.LOGGER.warn("Error while loading the config value " + getIdentifier() + ", using defaultvalue instead");
                return;
            }
            rawValues = GSON.fromJson(jsonElement, type);
            if (rawValues == null) {
                this.setToDefault();
                ToolLeveling.LOGGER.warn("Error while loading the config value " + getIdentifier() + ", using defaultvalue instead");
                return;
            }
            List<T> tempValues = new ArrayList<>();
            List<String> modids = new ArrayList<>();
            for (String element : rawValues) {
                ResourceLocation loc = ResourceLocation.tryParse(element);
                if (loc != null && registry.containsKey(loc)) {
                    tempValues.add(registry.getValue(loc));
                } else {
                    String modid = getModidFromWildcard(element);
                    if (modid != null) {
                        ToolLeveling.LOGGER.info("Found wildcard for mod: '" + modid + "' in '" + getIdentifier() + "'");
                        modids.add(modid);
                    }
                }
            }
            addAllWildcards(tempValues, modids, registry);
            values = ImmutableList.copyOf(tempValues);
        } catch (Exception e) {
            values = ImmutableList.copyOf(defaultValues);
            ToolLeveling.LOGGER.warn("Error while loading the config value " + getIdentifier() + ", using defaultvalue instead");
        }
    }

    private static <T> void addAllWildcards(List<T> tempValues, List<String> modids, final IForgeRegistry<T> registry) {
        if (modids.isEmpty()) {
            return;
        }
        registry.getValues().stream().filter((element) -> {
            return modids.contains(registry.getKey(element).getNamespace());
        }).forEach((element) -> tempValues.add(element));
    }

    private static String getModidFromWildcard(String element) {
        if (element.contains(":")) {
            String[] splitted = element.split(":");
            if (splitted[1].equals("*") && ModList.get().isLoaded(splitted[0])) {
                return splitted[0];
            }
        }
        return null;
    }

}
