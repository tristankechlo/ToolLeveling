package com.tristankechlo.toolleveling.config.util;

import com.tristankechlo.toolleveling.ToolLeveling;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ITagCollection;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

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

    public static <T extends IForgeRegistryEntry<T>> ITag<T> getTagKeyFromTag(String element, final ITagCollection<T> tags) {
        if (element == null || !element.startsWith("#")) {
            return null;
        }
        ResourceLocation loc = ResourceLocation.tryParse(element.substring(1));
        if (loc == null) {
            ToolLeveling.LOGGER.error("Error while parsing tag to ResourceLocation '{}'", element);
            return null;
        }

        ITag<T> tag = tags.getTag(loc);
        if (tag == null) {
            ToolLeveling.LOGGER.error("Error while parsing tag '{}', tag does not exist", element);
            return null;
        }
        return tag;
    }

}
