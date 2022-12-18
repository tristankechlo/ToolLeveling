package com.tristankechlo.toolleveling.config.util;

import com.tristankechlo.toolleveling.ToolLeveling;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.tags.ITag;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class ConfigUtils {

    public static String getModIdFromWildcard(String element) {
        if (element == null || !element.contains("*")) {
            return null;
        }
        String[] splitted = element.split(":");
        return splitted[1].equals("*") && ModList.get().isLoaded(splitted[0]) ? splitted[0] : null;
    }

    public static <T extends IForgeRegistryEntry<T>> List<T> getAllFromWildcard(String modid, final IForgeRegistry<T> registry) {
        List<T> tempValues = new ArrayList<>();
        try {
            registry.getValues().stream().filter((element) -> {
                return Objects.requireNonNull(registry.getKey(element)).getNamespace().equals(modid);
            }).forEach(tempValues::add);
        } catch (NullPointerException e) {
            ToolLeveling.LOGGER.error("Error while parsing entries from modid wildcard '{}'", modid);
        }
        return tempValues;
    }

    public static <T extends IForgeRegistryEntry<T>> TagKey<T> getTagKeyFromTag(String element, final IForgeRegistry<T> registry) {
        if (element == null || !element.startsWith("#")) {
            return null;
        }
        ResourceLocation loc = ResourceLocation.tryParse(element.substring(1));
        if (loc == null) {
            ToolLeveling.LOGGER.error("Error while parsing tag to ResourceLocation '{}'", element);
            return null;
        }
        try {
            return Objects.requireNonNull(registry.tags()).stream().filter((tag) -> {
                return tag.getKey().location().equals(loc);
            }).map(ITag::getKey).findFirst().orElse(null);
        } catch (NullPointerException e) {
            ToolLeveling.LOGGER.error("Error while parsing tag '{}'", element);
            return null;
        }
    }

    public static <T extends IForgeRegistryEntry<T>> List<T> getAllFromTag(TagKey<T> tagKey, final IForgeRegistry<T> registry) {
        List<T> tempValues = new ArrayList<>();
        try {
            tempValues.addAll(Objects.requireNonNull(registry.tags()).getTag(tagKey).stream().toList());
        } catch (NullPointerException e) {
            ToolLeveling.LOGGER.error("Error while parsing entries from tag '{}'", tagKey.location());
        }
        return tempValues;
    }

}
