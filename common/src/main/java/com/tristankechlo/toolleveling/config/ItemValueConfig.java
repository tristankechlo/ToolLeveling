package com.tristankechlo.toolleveling.config;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
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

    private static final Codec<Item> CORRECT_ITEM = Registry.ITEM.byNameCodec().flatXmap(ItemValueConfig::validateItem, DataResult::success);
    private static final Codec<Map<Either<Item, TagKey<Item>>, Long>> ITEM_TO_LONG = Codec.unboundedMap(
            Codec.either(CORRECT_ITEM, TagKey.hashedCodec(Registry.ITEM_REGISTRY)),
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
        for (Map.Entry<Either<Item, TagKey<Item>>, Long> entry : values.entrySet()) {
            if (entry.getKey().left().isPresent()) {
                itemValues.put(entry.getKey().left().get(), entry.getValue());
            } else if (entry.getKey().right().isPresent()) {
                long value = entry.getValue();
                for (Item item : getAllFromTag(entry.getKey().right().get())) {
                    itemValues.put(item, value);
                }
            }
        }
        return itemValues;
    }

    private static List<Item> getAllFromTag(TagKey<Item> tagKey) {
        List<Item> tempValues = new ArrayList<>();
        Registry.ITEM.getTagOrEmpty(tagKey).forEach((holder) -> tempValues.add(holder.value()));
        return tempValues;
    }

}
