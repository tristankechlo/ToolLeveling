package com.tristankechlo.toolleveling.jei;

import com.tristankechlo.toolleveling.ToolLeveling;
import com.tristankechlo.toolleveling.client.ToolLevelingTableScreen;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

@JeiPlugin
public class JustEnoughItemsPlugin implements IModPlugin {

    private static final ResourceLocation UID = new ResourceLocation(ToolLeveling.MOD_ID, "jei_plugin");

    @Override
    public ResourceLocation getPluginUid() {
        return UID;
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        // adding areas on the screen that JEI should avoid drawing over
        registration.addGuiContainerHandler(ToolLevelingTableScreen.class, new ToolLevelingTableGuiHandler());
    }

    public static class ToolLevelingTableGuiHandler implements IGuiContainerHandler<ToolLevelingTableScreen> {

        @Override
        public List<Rect2i> getGuiExtraAreas(ToolLevelingTableScreen screen) {
            return screen.getExtraAreas();
        }

    }

}
