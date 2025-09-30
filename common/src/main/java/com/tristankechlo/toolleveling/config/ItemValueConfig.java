package com.tristankechlo.toolleveling.config;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tristankechlo.toolleveling.ToolLeveling;
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
        values.put(Either.left(Items.COAL), 8L);
        values.put(Either.right(ItemTags.BEDS), 12L);
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
