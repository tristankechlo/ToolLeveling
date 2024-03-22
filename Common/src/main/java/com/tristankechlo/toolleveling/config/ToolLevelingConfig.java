package com.tristankechlo.toolleveling.config;

import com.tristankechlo.toolleveling.ToolLeveling;
import com.tristankechlo.toolleveling.config.util.AbstractConfig;
import com.tristankechlo.toolleveling.config.values.AbstractConfigValue;
import com.tristankechlo.toolleveling.config.values.BonusIngredient;
import com.tristankechlo.toolleveling.config.values.BonusIngredientsValue;
import com.tristankechlo.toolleveling.config.values.NumberValue;
import net.minecraft.util.GsonHelper;
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
    private final NumberValue<Integer> baseIterations;
    private final NumberValue<Integer> baseStrength;
    private final BonusIngredientsValue bonusIngredients;
    private final List<AbstractConfigValue<?>> values;
    public static final ToolLevelingConfig INSTANCE = new ToolLevelingConfig();

    private ToolLevelingConfig() {
        super("tool_leveling_table.json", ToolLeveling.CONFIG_INFO_GENERAL);

        minSuccessChance = new NumberValue<>("min_success_chance", 75.0F, 0.0F, 100.0F, GsonHelper::getAsFloat);
        maxSuccessChance = new NumberValue<>("max_success_chance", 100.0F, 0.0F, 100.0F, GsonHelper::getAsFloat);
        requiredBookshelves = new NumberValue<>("required_bookshelves", 20, 0, 32, GsonHelper::getAsInt);
        requiredBooks = new NumberValue<>("required_books", 4, 1, 6, GsonHelper::getAsInt);
        baseIterations = new NumberValue<>("base_num_enchantments", 1, 1, Integer.MAX_VALUE, GsonHelper::getAsInt);
        baseStrength = new NumberValue<>("base_num_levels", 1, 1, Integer.MAX_VALUE, GsonHelper::getAsInt);
        bonusIngredients = new BonusIngredientsValue("bonus_ingredients", new BonusIngredient[]{
            new BonusIngredient(Ingredient.of(Items.NETHER_STAR), 0, 1),
            new BonusIngredient(Ingredient.of(Items.ENCHANTED_GOLDEN_APPLE), 1, 0),
        });

        values = List.of(minSuccessChance, maxSuccessChance, requiredBookshelves, requiredBooks,
            baseIterations, baseStrength, bonusIngredients);
    }

    @Override
    protected List<AbstractConfigValue<?>> getValues() {
        return values;
    }

    @Override
    protected String getComment() {
        return "Checkout '" + ToolLeveling.CONFIG_INFO_GENERAL + "' for more information about this config";
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

    public int getBaseStrength() {
        return baseStrength.get();
    }

    public int getBaseIterations() {
        return baseIterations.get();
    }

    @Deprecated
    public boolean isBonusItemStrength(ItemStack stack) {
        for (BonusIngredient bonus : bonusIngredients.get()) {
            if (bonus.ingredient().test(stack) && bonus.maxLevelBonus() != 0) {
                return true;
            }
        }
        return false;
    }

    public int getBonusItemStrength(ItemStack stack) {
        int total = 0;
        for (BonusIngredient bonus : bonusIngredients.get()) {
            if (bonus.ingredient().test(stack)) {
                total += bonus.maxLevelBonus();
            }
        }
        return total;
    }

    @Deprecated
    public boolean isBonusItemIterations(ItemStack stack) {
        for (BonusIngredient bonus : bonusIngredients.get()) {
            if (bonus.ingredient().test(stack) && bonus.iterationsBonus() != 0) {
                return true;
            }
        }
        return false;
    }

    public int getBonusItemIterations(ItemStack stack) {
        int total = 0;
        for (BonusIngredient bonus : bonusIngredients.get()) {
            if (bonus.ingredient().test(stack)) {
                total += bonus.iterationsBonus();
            }
        }
        return total;
    }

}
