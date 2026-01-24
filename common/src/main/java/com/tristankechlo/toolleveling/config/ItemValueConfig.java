package com.tristankechlo.toolleveling.config;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tristankechlo.toolleveling.ToolLeveling;
import com.tristankechlo.toolleveling.config.util.CodecHelper;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record ItemValueConfig(
        long defaultItemWorth,
        Map<Either<Item, TagKey<Item>>, Long> values, // for internal use only
        Map<Item, Long> itemValues // all item tags resolved to items
) {

    private static final Codec<Item> CORRECT_ITEM = Registry.ITEM.byNameCodec();
    private static final Codec<Map<Either<Item, TagKey<Item>>, Long>> ITEM_TO_LONG = Codec.unboundedMap(
            Codec.either(CORRECT_ITEM, TagKey.hashedCodec(Registry.ITEM_REGISTRY)).flatXmap(ItemValueConfig::validate, DataResult::success),
            CodecHelper.NON_NEGATIVE_LONG
    );
    public static final Codec<ItemValueConfig> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    CodecHelper.NON_NEGATIVE_LONG.fieldOf("default_item_worth").forGetter(ItemValueConfig::defaultItemWorth),
                    ITEM_TO_LONG.fieldOf("item_values").forGetter(ItemValueConfig::values)
            ).apply(instance, ItemValueConfig::new)
    );

    public ItemValueConfig(long defaultItemWorth, Map<Either<Item, TagKey<Item>>, Long> values) {
        this(defaultItemWorth, values, resolve(values));
    }

    private static final ItemValueConfig DEFAULT = new ItemValueConfig(5L, getDefaultItemValues());
    private static ItemValueConfig INSTANCE = DEFAULT;

    public static ItemValueConfig get() {
        return INSTANCE;
    }

    public static void set(ItemValueConfig config) {
        INSTANCE = config;
    }

    public static void setToDefault() {
        INSTANCE = ItemValueConfig.DEFAULT;
    }

    private static DataResult<Either<Item, TagKey<Item>>> validate(Either<Item, TagKey<Item>> item) {
        if (item.left().isPresent()) {
            return validateItem(item.left().get()).map(Either::left);
        }
        return DataResult.success(item);
    }

    private static DataResult<Item> validateItem(Item item) {
        String id = item == null ? "unknown" : Registry.ITEM.getKey(item).toString();
        if (item == null || item == Items.AIR) {
            return DataResult.error("Item [" + id + "] was not found in registry");
        }
        ItemStack stack = new ItemStack(item);
        if (stack.isDamageableItem()) {
            return DataResult.error("Item [" + id + "] is damageable, it is not a valid item to use in the toolleveling table");
        }
        if (item.isEnchantable(stack)) {
            return DataResult.error("Item [" + id + "] is enchantable, it is not a valid item to use in the toolleveling table");
        }
        return DataResult.success(item);
    }

    public static Map<Either<Item, TagKey<Item>>, Long> getDefaultItemValues() {
        Map<Either<Item, TagKey<Item>>, Long> values = new HashMap<>();

        // Ores
        values.put(Either.right(ItemTags.COALS), 8L);
        values.put(Either.right(ItemTags.COAL_ORES), 30L);
        values.put(Either.left(Items.COAL_BLOCK), 9 * 8L);

        values.put(Either.left(Items.RAW_COPPER), 11L);
        values.put(Either.left(Items.RAW_COPPER_BLOCK), 9 * 11L);
        values.put(Either.right(ItemTags.COPPER_ORES), 10L);
        values.put(Either.left(Items.COPPER_INGOT), 14L);
        values.put(Either.left(Items.COPPER_BLOCK), 9 * 14L);

        values.put(Either.left(Items.RAW_IRON), 13L);
        values.put(Either.left(Items.RAW_IRON_BLOCK), 9 * 13L);
        values.put(Either.right(ItemTags.IRON_ORES), 12L);
        values.put(Either.left(Items.IRON_INGOT), 15L);
        values.put(Either.left(Items.IRON_BLOCK), 9 * 15L);

        values.put(Either.left(Items.RAW_GOLD), 35L);
        values.put(Either.left(Items.RAW_GOLD_BLOCK), 9 * 35L);
        values.put(Either.right(ItemTags.GOLD_ORES), 30L);
        values.put(Either.left(Items.GOLD_INGOT), 40L);
        values.put(Either.left(Items.GOLD_BLOCK), 9 * 40L);

        values.put(Either.left(Items.DIAMOND), 160L);
        values.put(Either.right(ItemTags.DIAMOND_ORES), 160L);
        values.put(Either.left(Items.DIAMOND_BLOCK), 9 * 160L);

        values.put(Either.left(Items.NETHERITE_SCRAP), 50L);
        values.put(Either.left(Items.ANCIENT_DEBRIS), 50L);
        values.put(Either.left(Items.NETHERITE_INGOT), 200L);
        values.put(Either.left(Items.NETHERITE_BLOCK), 9 * 200L);

        values.put(Either.left(Items.LAPIS_LAZULI), 8L);
        values.put(Either.right(ItemTags.LAPIS_ORES), 120L);
        values.put(Either.left(Items.LAPIS_BLOCK), 9 * 8L);

        values.put(Either.left(Items.EMERALD), 100L);
        values.put(Either.right(ItemTags.EMERALD_ORES), 800L);
        values.put(Either.left(Items.EMERALD_BLOCK), 9 * 100L);

        values.put(Either.left(Items.QUARTZ), 10L);
        values.put(Either.left(Items.NETHER_QUARTZ_ORE), 40L);
        values.put(Either.left(Items.QUARTZ_BLOCK), 4 * 10L);

        values.put(Either.left(Items.REDSTONE), 6L);
        values.put(Either.right(ItemTags.REDSTONE_ORES), 60L);
        values.put(Either.left(Items.REDSTONE_BLOCK), 9 * 6L);

        values.put(Either.left(Items.GLOWSTONE_DUST), 6L);
        values.put(Either.left(Items.GLOWSTONE), 4 * 6L);

        // other
        values.put(Either.left(Items.AMETHYST_BLOCK), 11L);
        values.put(Either.left(Items.AMETHYST_SHARD), 17L);
        values.put(Either.left(Items.PRISMARINE_SHARD), 15L);
        values.put(Either.left(Items.PRISMARINE_CRYSTALS), 15L);
        values.put(Either.left(Items.NAUTILUS_SHELL), 30L);
        values.put(Either.left(Items.HEART_OF_THE_SEA), 1000L);
        values.put(Either.left(Items.SEA_LANTERN), 140L);
        values.put(Either.left(Items.SPONGE), 150L);
        values.put(Either.left(Items.WET_SPONGE), 140L);

        // Food
        values.put(Either.left(Items.GOLDEN_APPLE), 400L);
        values.put(Either.left(Items.GOLDEN_CARROT), 100L);
        values.put(Either.left(Items.GLISTERING_MELON_SLICE), 100L);
        values.put(Either.left(Items.ENCHANTED_GOLDEN_APPLE), 2500L);

        // Drops
        values.put(Either.left(Items.SLIME_BALL), 25L);
        values.put(Either.left(Items.SLIME_BLOCK), 225L);
        values.put(Either.left(Items.ENDER_PEARL), 20L);
        values.put(Either.left(Items.BLAZE_ROD), 30L);
        values.put(Either.left(Items.ENDER_EYE), 50L);
        values.put(Either.left(Items.BLAZE_POWDER), 15L);
        values.put(Either.left(Items.MAGMA_CREAM), 50L);
        values.put(Either.left(Items.GHAST_TEAR), 200L);
        values.put(Either.left(Items.NETHER_STAR), 2500L);
        values.put(Either.left(Items.SHULKER_SHELL), 200L);
        values.put(Either.left(Items.END_CRYSTAL), 300L);
        values.put(Either.left(Items.EXPERIENCE_BOTTLE), 100L);
        values.put(Either.left(Items.DRAGON_EGG), 2000L);
        values.put(Either.left(Items.DRAGON_HEAD), 2000L);

        // Decorative
        values.put(Either.left(Items.ENDER_CHEST), 140L);
        values.put(Either.left(Items.BEACON), 2500L);
        return values;
    }

    private static Map<Item, Long> resolve(Map<Either<Item, TagKey<Item>>, Long> values) {
        Map<Item, Long> itemValues = new HashMap<>();
        // resolve all item tags first
        values.entrySet().stream().filter(entry -> entry.getKey().right().isPresent()) // find all tag entries
                .map(entry -> Map.entry(entry.getKey().right().get(), entry.getValue())) // Map<TagKey<Item>, Long>
                .forEach(entry -> {
                    ToolLeveling.LOGGER.info("Resolving items for tag: {}", entry.getKey().location());
                    for (Item item : getAllFromTag(entry.getKey())) {
                        if (itemValues.containsKey(item)) {
                            ResourceLocation loc = Registry.ITEM.getKey(item);
                            ToolLeveling.LOGGER.warn("Duplicate item value entry for Item[{}] in ItemTag[{}], overriding the value!", loc, entry.getKey().location());
                        }
                        itemValues.put(item, entry.getValue());
                    }
                });
        // specifically mentioned items override the ones from item tags
        values.entrySet().stream().filter(entry -> entry.getKey().left().isPresent()) // find all item entries
                .map(entry -> Map.entry(entry.getKey().left().get(), entry.getValue())) // Map<Item, Long>
                .forEach(entry -> {
                    if (itemValues.containsKey(entry.getKey())) {
                        ResourceLocation loc = Registry.ITEM.getKey(entry.getKey());
                        ToolLeveling.LOGGER.warn("Duplicate item value entry for Item[{}], overriding the value!", loc);
                    }
                    itemValues.put(entry.getKey(), entry.getValue());
                });
        return itemValues;
    }

    public static List<Item> getAllFromTag(TagKey<Item> tagKey) {
        List<Item> tempValues = new ArrayList<>();
        Registry.ITEM.getTagOrEmpty(tagKey).forEach((holder) -> tempValues.add(holder.value()));
        return tempValues;
    }

}
