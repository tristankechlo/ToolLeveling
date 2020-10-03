package com.tristankechlo.toolleveling.config;

import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.Level;

import com.tristankechlo.toolleveling.ToolLeveling;

import net.minecraft.enchantment.Enchantments;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
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
		
		public final ConfigValue<List<? extends String>> EnchantmentsBlacklist;
		
		Server(ForgeConfigSpec.Builder builder){
            builder.comment("general configuration settings")
                   .push("Server");
            
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
    				enchantment -> checkEnchantment(enchantment));
            
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


	private static boolean checkEnchantment(Object enchantment) {
		if (ForgeRegistries.ENCHANTMENTS.containsKey(new ResourceLocation(String.valueOf(enchantment)))) {
			return true;
		}
		ToolLeveling.LOGGER.log(Level.INFO,	"Removing unknown Enchantment[" + String.valueOf(enchantment) + "] from EnchantmentsBlacklist");
		return false;
	}
	
}
