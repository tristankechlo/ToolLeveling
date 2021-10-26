package com.tristankechlo.toolleveling.client;

import com.tristankechlo.toolleveling.utils.Names;
import com.tristankechlo.toolleveling.utils.Utils;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.Item;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod.EventBusSubscriber(modid = Names.MOD_ID, value = Dist.CLIENT, bus = Bus.FORGE)
public final class TooltipEvents {

	@SubscribeEvent
	public static void onTooltip(final ItemTooltipEvent event) {
		if (event.getFlags().isAdvanced() && Screen.hasShiftDown()) {
			final Item item = event.getItemStack().getItem();
			final long worth = Utils.getItemWorth(item);
			event.getToolTip().add(new StringTextComponent(""));
			event.getToolTip().add(new TranslationTextComponent("tooltip.toolleveling.itemworth", worth)
					.withStyle(TextFormatting.AQUA));
		}
	}

}
