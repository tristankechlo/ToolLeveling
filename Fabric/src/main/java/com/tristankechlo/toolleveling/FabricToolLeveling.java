package com.tristankechlo.toolleveling;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.world.item.CreativeModeTabs;

public class FabricToolLeveling implements ModInitializer {

    @Override
    public void onInitialize() {
        ToolLeveling.init();
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS).register(this::populateCreativeTab);
    }

    private void populateCreativeTab(FabricItemGroupEntries entries) {
        entries.accept(ToolLeveling.TLT_ITEM.get());
    }

}