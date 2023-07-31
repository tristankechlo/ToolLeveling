package com.tristankechlo.toolleveling.config;

import com.google.gson.JsonElement;
import com.tristankechlo.toolleveling.ToolLeveling;
import com.tristankechlo.toolleveling.config.util.AbstractConfig;
import com.tristankechlo.toolleveling.config.values.AbstractConfigValue;
import com.tristankechlo.toolleveling.config.values.IngredientValue;
import com.tristankechlo.toolleveling.config.values.NumberValue;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

public final class ToolLevelingConfig extends AbstractConfig {

    // bounds for the success chance of the enchantment process
    private final NumberValue<Float> minSuccessChance;
    private final NumberValue<Float> maxSuccessChance;
    private final NumberValue<Integer> requiredBookshelves;
    private final NumberValue<Integer> requiredBooks;
    private final IngredientValue bonusItemMoreEnchantments;
    private final IngredientValue bonusItemMoreLevels;
    private final List<AbstractConfigValue<?>> values;
    public static final ToolLevelingConfig INSTANCE = new ToolLevelingConfig();

    private ToolLevelingConfig() {
        super("tool_leveling_table.json", ToolLeveling.CONFIG_INFO_GENERAL);
        minSuccessChance = new NumberValue<>("minSuccessChance", 75.0F, 0.0F, 100.0F, JsonElement::getAsFloat, "lower bound for the success chance of the enchantment process");
        maxSuccessChance = new NumberValue<>("maxSuccessChance", 100.0F, 0.0F, 100.0F, JsonElement::getAsFloat, "upper bound for the success chance of the enchantment process");
        requiredBookshelves = new NumberValue<>("requiredBookshelves", 20, 0, 32, JsonElement::getAsInt, "what amount of bookshelves are required to reach the maxSuccessChance");
        requiredBooks = new NumberValue<>("requiredBooks", 4, 1, 6, JsonElement::getAsInt, "how many books are required to start the enchantment process");
        bonusItemMoreEnchantments = new IngredientValue("bonusItemMoreEnchantments", Ingredient.of(Items.NETHER_STAR), "what item is required to increase the amount of enchantments that can be added to the tool");
        bonusItemMoreLevels = new IngredientValue("bonusItemMoreLevels", Ingredient.of(Items.ENCHANTED_GOLDEN_APPLE), "what item is required to increase the amount of levels that can be added to the enchantment");
        values = List.of(minSuccessChance, maxSuccessChance, requiredBookshelves, requiredBooks, bonusItemMoreEnchantments, bonusItemMoreLevels);
    }

    @Override
    protected List<AbstractConfigValue<?>> getValues() {
        return values;
    }

    public float minSuccessChance() {
        return minSuccessChance.get();
    }

    public float maxSuccessChance() {
        return maxSuccessChance.get();
    }

    public int requiredBookshelves() {
        return requiredBookshelves.get();
    }

    public int requiredBooks() {
        return requiredBooks.get();
    }

    public boolean isBonusItemStrength(ItemStack stack) {
        return bonusItemMoreLevels.get().test(stack);
    }

    public boolean isBonusItemIterations(ItemStack stack) {
        return bonusItemMoreEnchantments.get().test(stack);
    }

}
