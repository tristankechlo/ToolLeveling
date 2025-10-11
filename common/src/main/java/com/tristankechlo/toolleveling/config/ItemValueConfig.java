package com.tristankechlo.toolleveling.config;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tristankechlo.toolleveling.ToolLeveling;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
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
                    ITEM_TO_LONG.fieldOf("values").forGetter(ItemValueConfig::values)
            ).apply(instance, ItemValueConfig::new)
    );

    public ItemValueConfig(long defaultItemWorth, Map<Either<Item, TagKey<Item>>, Long> values) {
        this(defaultItemWorth, values, resolve(values));
    }

    private static final ItemValueConfig DEFAULT = new ItemValueConfig(10L, getDefaultItemValues());
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
        values.put(Either.left(Items.COAL), 8L);
        values.put(Either.left(Items.COAL_ORE), 30L);
        values.put(Either.left(Items.DEEPSLATE_COAL_ORE), 30L);
        values.put(Either.left(Items.COAL_BLOCK), 73L);

        values.put(Either.left(Items.COPPER_ORE), 10L);
        values.put(Either.left(Items.DEEPSLATE_COPPER_ORE), 10L);
        values.put(Either.left(Items.RAW_COPPER), 11L);
        values.put(Either.left(Items.COPPER_INGOT), 14L);
        values.put(Either.left(Items.COPPER_BLOCK), 126L);
        values.put(Either.left(Items.RAW_COPPER_BLOCK), 99L);

        values.put(Either.left(Items.RAW_IRON), 13L);
        values.put(Either.left(Items.IRON_INGOT), 15L);
        values.put(Either.left(Items.IRON_ORE), 12L);
        values.put(Either.left(Items.DEEPSLATE_IRON_ORE), 12L);
        values.put(Either.left(Items.RAW_IRON_BLOCK), 117L);
        values.put(Either.left(Items.IRON_BLOCK), 135L);

        values.put(Either.left(Items.GOLD_INGOT), 40L);
        values.put(Either.left(Items.RAW_GOLD), 35L);
        values.put(Either.left(Items.GOLD_ORE), 30L);
        values.put(Either.left(Items.DEEPSLATE_GOLD_ORE), 30L);
        values.put(Either.left(Items.RAW_GOLD_BLOCK), 315L);
        values.put(Either.left(Items.GOLD_BLOCK), 360L);

        values.put(Either.left(Items.DIAMOND), 160L);
        values.put(Either.left(Items.DIAMOND_ORE), 160L);
        values.put(Either.left(Items.DEEPSLATE_DIAMOND_ORE), 160L);
        values.put(Either.left(Items.DIAMOND_BLOCK), 1450L);
        values.put(Either.left(Items.NETHERITE_INGOT), 200L);
        values.put(Either.left(Items.NETHERITE_SCRAP), 50L);
        values.put(Either.left(Items.ANCIENT_DEBRIS), 50L);
        values.put(Either.left(Items.NETHERITE_BLOCK), 1800L);
        values.put(Either.left(Items.LAPIS_LAZULI), 8L);
        values.put(Either.left(Items.LAPIS_ORE), 120L);
        values.put(Either.left(Items.DEEPSLATE_LAPIS_ORE), 120L);
        values.put(Either.left(Items.LAPIS_BLOCK), 70L);
        values.put(Either.left(Items.EMERALD), 100L);
        values.put(Either.left(Items.EMERALD_ORE), 800L);
        values.put(Either.left(Items.DEEPSLATE_EMERALD_ORE), 800L);
        values.put(Either.left(Items.EMERALD_BLOCK), 900L);
        values.put(Either.left(Items.QUARTZ), 10L);
        values.put(Either.left(Items.NETHER_QUARTZ_ORE), 40L);
        values.put(Either.left(Items.QUARTZ_BLOCK), 40L);
        values.put(Either.left(Items.REDSTONE), 4L);
        values.put(Either.left(Items.REDSTONE_ORE), 60L);
        values.put(Either.left(Items.DEEPSLATE_REDSTONE_ORE), 60L);
        values.put(Either.left(Items.REDSTONE_BLOCK), 36L);
        values.put(Either.left(Items.GLOWSTONE_DUST), 4L);
        values.put(Either.left(Items.GLOWSTONE), 15L);

        // other
        values.put(Either.left(Items.AMETHYST_BLOCK), 11L);
        values.put(Either.left(Items.AMETHYST_SHARD), 17L);

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
