package com.tristankechlo.toolleveling.config.util;

import com.tristankechlo.toolleveling.ToolLeveling;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.registry.Registry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class ConfigUtils {

    public static String getModIdFromWildcard(String element) {
        if (element == null || !element.contains("*")) {
            return null;
        }
        String[] splitted = element.split(":");
        return splitted[1].equals("*") && FabricLoader.getInstance().isModLoaded(splitted[0]) ? splitted[0] : null;
    }

    public static <T> List<T> getAllFromWildcard(String modid, final Registry<T> registry) {
        List<T> tempValues = new ArrayList<>();
        try {
            registry.stream().filter((element) -> {
                return Objects.requireNonNull(registry.getId(element)).getNamespace().equals(modid);
            }).forEach(tempValues::add);
        } catch (NullPointerException e) {
            ToolLeveling.LOGGER.error("Error while parsing entries from modid wildcard '{}'", modid);
        }
        return tempValues;
    }

    public static <T> TagKey<T> getTagKeyFromTag(String element, final Registry<T> registry) {
        if (element == null || !element.startsWith("#")) {
            return null;
        }
        Identifier loc = Identifier.tryParse(element.substring(1));
        if (loc == null) {
            ToolLeveling.LOGGER.error("Error while parsing tag to ResourceLocation '{}'", element);
            return null;
        }
        try {
            return registry.streamTags().filter((tag) -> {
                return tag.id().equals(loc);
            }).findFirst().orElse(null);
        } catch (NullPointerException e) {
            ToolLeveling.LOGGER.error("Error while parsing tag '{}'", element);
            return null;
        }
    }

    public static <T> List<T> getAllFromTag(TagKey<T> tagKey, final Registry<T> registry) {
        List<T> tempValues = new ArrayList<>();
        try {
            registry.getOrCreateEntryList(tagKey).forEach((entry) -> tempValues.add(entry.value()));
        } catch (NullPointerException e) {
            ToolLeveling.LOGGER.error("Error while parsing entries from tag '{}'", tagKey.id());
        }
        return tempValues;
    }

}