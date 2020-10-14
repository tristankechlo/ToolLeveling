package com.tristankechlo.toolleveling.config;

import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.Level;

import com.tristankechlo.toolleveling.ToolLeveling;

import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber
public class ToolLevelingConfig {
	
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final Server SERVER = new Server(BUILDER);
    public static final ForgeConfigSpec spec = BUILDER.build();
    
	public static class Server {

		public final ConfigValue<String> upgradeItem;
		public final ConfigValue<Double> upgradeCostMultiplier;
		public final ConfigValue<List<? extends String>> EnchantmentsBlacklist;
		public final BooleanValue ignoreEnchantmentCaps;
		
		Server(ForgeConfigSpec.Builder builder){
            builder.comment("general configuration settings")
                   .push("Server");
            
            upgradeItem = builder.worldRestart().comment("").define("upgradeItem", Items.NETHERITE_INGOT.getRegistryName().toString(), item -> isValidItem(item));
            upgradeCostMultiplier = builder.worldRestart().comment("").define("upgradeCostMultiplier", 1.0D);
            
            EnchantmentsBlacklist = builder.worldRestart().comment("enchantments in this list can't be leveled in the tool leveling table").defineList("EnchantmentsBlacklist",
    				Arrays.asList(Enchantments.MENDING.getRegistryName().toString(),
    						Enchantments.AQUA_AFFINITY.getRegistryName().toString(),
    						Enchantments.CHANNELING.getRegistryName().toString(),
    						Enchantments.BINDING_CURSE.getRegistryName().toString(),
    						Enchantments.VANISHING_CURSE.getRegistryName().toString(),
    						Enchantments.FLAME.getRegistryName().toString(),
    						Enchantments.INFINITY.getRegistryName().toString(),
    						Enchantments.MULTISHOT.getRegistryName().toString(),
    						Enchantments.SILK_TOUCH.getRegistryName().toString()),
    				enchantment -> isValidEnchantment(enchantment));
            
            ignoreEnchantmentCaps = builder.worldRestart().comment("if set to true, some enchantments will break if leveled to high").define("IgnoreEnchantmentCaps", false);
            
            builder.pop();
		}
	}

    @SubscribeEvent
    public static void onLoad(final ModConfig.Loading configEvent) {
        ToolLeveling.LOGGER.debug("Loaded config file {}", configEvent.getConfig().getFileName());
    }

    @SubscribeEvent
    public static void onFileChange(final ModConfig.Reloading configEvent) {
    	ToolLeveling.LOGGER.debug("Config just got changed on the file system!");
    }

	private static boolean isValidEnchantment(Object enchantment) {
		if (ForgeRegistries.ENCHANTMENTS.containsKey(new ResourceLocation(String.valueOf(enchantment)))) {
			return true;
		}
		ToolLeveling.LOGGER.log(Level.INFO,	"Removing unknown Enchantment[" + String.valueOf(enchantment) + "] from EnchantmentsBlacklist");
		return false;
	}

	private static boolean isValidItem(Object item) {
		if (ForgeRegistries.ITEMS.containsKey(new ResourceLocation(String.valueOf(item)))) {
			return true;
		}
		ToolLeveling.LOGGER.log(Level.INFO,	"Resetting upgrade item, because " + String.valueOf(item) + " is not valid.");
		return false;
	}
	
}
