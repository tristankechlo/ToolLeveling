package com.tristankechlo.toolleveling.config.values;

import com.google.gson.reflect.TypeToken;
import com.tristankechlo.toolleveling.ToolLeveling;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public final class ItemValues extends RegistryMapConfig<Item, Long> {

    private static final Type TYPE = new TypeToken<Map<String, Long>>() {}.getType();

    public ItemValues(String identifier) {
        super(identifier, ForgeRegistries.ITEMS, ItemTags.getAllTags(), getDefaultItemValues());
    }

    @Override
    protected boolean isKeyValid(Item item, ResourceLocation identifier) {
        if (item == null || item == Items.AIR) {
            ToolLeveling.LOGGER.warn("Item {} not found in registry", identifier);
            return false;
        }
        ItemStack stack = new ItemStack(item);
        if (item.isDamageable(stack)) {
            ToolLeveling.LOGGER.warn("Item {} is damageable, it not a valid item to use in the toolleveling table", identifier);
            return false;
        }
        if (item.isEnchantable(stack)) {
            ToolLeveling.LOGGER.warn("Item {} is enchantable, it not a valid item to use in the toolleveling table", identifier);
            return false;
        }
        return true;
    }

    @Override
    protected boolean isValueValid(Long value) {
        return value >= 0L;
    }

    @Override
    protected Type getType() {
        return TYPE;
    }

    public static Map<Item, Long> getDefaultItemValues() {
        Map<Item, Long> values = new HashMap<>();

        // Ores
        values.put(Items.COAL, 8L);
        values.put(Items.COAL_ORE, 30L);
        values.put(Items.COAL_BLOCK, 73L);

        values.put(Items.IRON_INGOT, 15L);
        values.put(Items.IRON_ORE, 12L);
        values.put(Items.IRON_BLOCK, 135L);

        values.put(Items.GOLD_INGOT, 40L);
        values.put(Items.GOLD_ORE, 30L);
        values.put(Items.GOLD_BLOCK, 360L);

        values.put(Items.DIAMOND, 160L);
        values.put(Items.DIAMOND_ORE, 160L);
        values.put(Items.DIAMOND_BLOCK, 1450L);
        values.put(Items.NETHERITE_INGOT, 200L);
        values.put(Items.NETHERITE_SCRAP, 50L);
        values.put(Items.ANCIENT_DEBRIS, 50L);
        values.put(Items.NETHERITE_BLOCK, 1800L);
        values.put(Items.LAPIS_LAZULI, 8L);
        values.put(Items.LAPIS_ORE, 120L);
        values.put(Items.LAPIS_BLOCK, 70L);
        values.put(Items.EMERALD, 100L);
        values.put(Items.EMERALD_ORE, 800L);
        values.put(Items.EMERALD_BLOCK, 900L);
        values.put(Items.QUARTZ, 10L);
        values.put(Items.NETHER_QUARTZ_ORE, 40L);
        values.put(Items.QUARTZ_BLOCK, 40L);
        values.put(Items.REDSTONE, 4L);
        values.put(Items.REDSTONE_ORE, 60L);
        values.put(Items.REDSTONE_BLOCK, 36L);
        values.put(Items.GLOWSTONE_DUST, 4L);
        values.put(Items.GLOWSTONE, 15L);

        // Food
        values.put(Items.GOLDEN_APPLE, 400L);
        values.put(Items.GOLDEN_CARROT, 100L);
        values.put(Items.GLISTERING_MELON_SLICE, 100L);
        values.put(Items.ENCHANTED_GOLDEN_APPLE, 2500L);

        // Drops
        values.put(Items.SLIME_BALL, 25L);
        values.put(Items.SLIME_BLOCK, 225L);
        values.put(Items.ENDER_PEARL, 20L);
        values.put(Items.BLAZE_ROD, 30L);
        values.put(Items.ENDER_EYE, 50L);
        values.put(Items.BLAZE_POWDER, 15L);
        values.put(Items.MAGMA_CREAM, 50L);
        values.put(Items.GHAST_TEAR, 200L);
        values.put(Items.NETHER_STAR, 2500L);
        values.put(Items.SHULKER_SHELL, 200L);
        values.put(Items.END_CRYSTAL, 300L);
        values.put(Items.EXPERIENCE_BOTTLE, 100L);
        values.put(Items.DRAGON_EGG, 2000L);
        values.put(Items.DRAGON_HEAD, 2000L);

        // Decorative
        values.put(Items.ENDER_CHEST, 140L);
        values.put(Items.BEACON, 2500L);
        return values;
    }

}

