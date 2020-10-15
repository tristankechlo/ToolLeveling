package com.tristankechlo.toolleveling.client.screen;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.tristankechlo.toolleveling.ToolLeveling;
import com.tristankechlo.toolleveling.config.ToolLevelingConfig;
import com.tristankechlo.toolleveling.container.ToolLevelingTableContainer;
import com.tristankechlo.toolleveling.network.PacketHandler;
import com.tristankechlo.toolleveling.network.packets.SyncToolLevelingEnchantment;
import com.tristankechlo.toolleveling.tileentity.ToolLevelingTableTileEntity;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ToolLevelingTableScreen extends ContainerScreen<ToolLevelingTableContainer> {

    private ResourceLocation GUI_TEXTURE = new ResourceLocation(ToolLeveling.MOD_ID, "textures/gui/tool_leveling_table.png");
    private ToolLevelingTableTileEntity entity;
    private BlockPos pos;
    private byte ticksSinceUpdate = 0;
    private byte currentPage = 1;
    private byte maxPages = 1;
    private Button EnchantmentButton0;
    private Button EnchantmentButton1;
    private Button EnchantmentButton2;
    private Button EnchantmentButton3;
    private Button pageForward;
    private Button pageBackward;
    private List<ButtonData> buttonData = new ArrayList<>();

    public ToolLevelingTableScreen(ToolLevelingTableContainer container, PlayerInventory inv, ITextComponent name) {
        super(container, inv, name);
        this.pos = container.getEntityPos();
        this.entity = container.getEntity();
        //texture size
        this.xSize = 180;
        this.ySize = 216;
        //offset for inv title
        this.playerInventoryTitleX += 2;
        this.playerInventoryTitleY += 49;
    }
    
    @Override
    protected void init() {
    	super.init();
    	//create basic buttons
    	this.EnchantmentButton0 = new Button(this.guiLeft + 37, this.guiTop + 21, 130, 20, new StringTextComponent(""), (button) -> { handleButtonClick(0); });
    	this.EnchantmentButton1 = new Button(this.guiLeft + 37, this.guiTop + 45, 130, 20, new StringTextComponent(""), (button) -> { handleButtonClick(1); });
    	this.EnchantmentButton2 = new Button(this.guiLeft + 37, this.guiTop + 69, 130, 20, new StringTextComponent(""), (button) -> { handleButtonClick(2); });
    	this.EnchantmentButton3 = new Button(this.guiLeft + 37, this.guiTop + 93, 130, 20, new StringTextComponent(""), (button) -> { handleButtonClick(3); });
    	this.pageBackward = new Button(this.guiLeft + 37, this.guiTop + 93, 20, 20, new StringTextComponent("<"), (button) -> { pageBackward(); });
    	this.pageForward = new Button(this.guiLeft + 147, this.guiTop + 93, 20, 20, new StringTextComponent(">"), (button) -> { pageForward(); });

    	//hide all buttons before rendering
		this.EnchantmentButton0.visible = false;
		this.EnchantmentButton1.visible = false;
		this.EnchantmentButton2.visible = false;
		this.EnchantmentButton3.visible = false;
		this.pageForward.visible = false;
		this.pageBackward.visible = false;
		
		//add Buttons to screen
    	this.addButton(this.EnchantmentButton0);
    	this.addButton(this.EnchantmentButton1);
    	this.addButton(this.EnchantmentButton2);
    	this.addButton(this.EnchantmentButton3);
    	this.addButton(this.pageForward);
    	this.addButton(this.pageBackward);
    }    
    
    @Override
    public void tick() {
    	super.tick();
    	this.ticksSinceUpdate++;
    	if(this.ticksSinceUpdate % 2 == 0) {
    		this.ticksSinceUpdate = 0;
    		updateEnchantmentList();
    	}
    	if(this.buttonData.isEmpty()) {
    		this.currentPage = 1;
    		hideAllButtons();
    	} else {
    		updateButtonData();
    	}
    }
    
    /**
     * get information about the current item in the table
     */
    private void updateEnchantmentList() {
    	ItemStack stack = this.entity.getStackToEnchant();
    	if(!stack.getItem().equals(Items.AIR)) {
    		//reset previous data
    		this.buttonData.clear();
    		
			Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
			for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
				
				List<? extends String> EnchantmentsBlacklist = ToolLevelingConfig.SERVER.EnchantmentsBlacklist.get();
				
				//only list enchantments that are not on the blacklist
				if(!EnchantmentsBlacklist.contains(entry.getKey().getRegistryName().toString())) {
					
					//although the level is defined as an integer, the actual maximum is a short
					//a higher enchantment level than a short will result in a negative level
					if(entry.getValue() < Short.MAX_VALUE) {
						
						//check if the enchantment can still be leveled
						if(canStillBeLeveled(entry.getKey(), entry.getValue()) || ToolLevelingConfig.SERVER.ignoreEnchantmentCaps.get()) {
							this.buttonData.add(new ButtonData(entry.getKey().getRegistryName(), entry.getKey().getName(), entry.getValue()));
						}
					}
				}
			}
    	} else {
    		this.buttonData.clear();
    	}
    }
    
    /**
     * change the data stored in the buttons
     */
    private void updateButtonData(){
    	//get the number of required pages
		this.maxPages = (byte)Math.ceil(this.buttonData.size() / 3.0);
		
		//check if 1st button is needed on current page
		if(this.buttonData.size() >= 1 + ((this.currentPage - 1) * 3)) {
    		this.EnchantmentButton0.visible = true;
    		this.EnchantmentButton0.setMessage(getButtonText(this.getButtonByIndex(0).NAME, this.getButtonByIndex(0).NEXT_LEVEL));
    		boolean active = (this.getButtonByIndex(0).UPGRADE_COST <= this.entity.getPaymentAmount());
    		this.EnchantmentButton0.active = active;
		} else {
    		this.EnchantmentButton0.visible = false;
		}

		//check if 2nd button is needed on current page
		if(this.buttonData.size() >= 2 + ((this.currentPage - 1) * 3)) {
    		this.EnchantmentButton1.visible = true;
    		this.EnchantmentButton1.setMessage(getButtonText(this.getButtonByIndex(1).NAME, this.getButtonByIndex(1).NEXT_LEVEL));
    		boolean active = (this.getButtonByIndex(1).UPGRADE_COST <= this.entity.getPaymentAmount());
    		this.EnchantmentButton1.active = active;
		} else {
    		this.EnchantmentButton1.visible = false;			
		}

		//check if 3rd button is needed on current page
		if(this.buttonData.size() >= 3 + ((this.currentPage - 1) * 3)) {
    		this.EnchantmentButton2.visible = true;
    		this.EnchantmentButton2.setMessage(getButtonText(this.getButtonByIndex(2).NAME, this.getButtonByIndex(2).NEXT_LEVEL));
    		boolean active = (this.getButtonByIndex(2).UPGRADE_COST <= this.entity.getPaymentAmount());
    		this.EnchantmentButton2.active = active;
		} else {
    		this.EnchantmentButton2.visible = false;
		}
		
		//either show 4th button or page selection
    	if(this.buttonData.size() == 4) {
    		this.EnchantmentButton3.visible = true;
    		this.EnchantmentButton3.setMessage(getButtonText(this.buttonData.get(3).NAME, this.buttonData.get(3).NEXT_LEVEL));
    		boolean active = (this.getButtonByIndex(3).UPGRADE_COST <= this.entity.getPaymentAmount());
    		this.EnchantmentButton3.active = active;
    		this.pageForward.visible = false;
    		this.pageBackward.visible = false;
    	} else if(this.buttonData.size() > 4) {
    		this.EnchantmentButton3.visible = false;
    		this.pageForward.visible = true;
    		this.pageBackward.visible = true;
    	}
    	
    }
    
    /**
     * handle button click for page forward
     */
    private void pageForward() {
    	if(this.currentPage == this.maxPages) {
    		this.currentPage = 1;
    	} else {
    		this.currentPage++;
    	}
    	updateButtonData();
    }

    /**
     * handle button click for page backwards
     */
    private void pageBackward() {
    	if(this.currentPage == 1) {
    		this.currentPage = this.maxPages;
    	} else {
    		this.currentPage--;
    	}
    	updateButtonData();
    }
    
    /**
     * handle button click for enchantment buttons
     * @param id
     */
    private void handleButtonClick(int id) {

    	//next level must be below 32767
    	int nextLevel = this.getButtonByIndex(id).NEXT_LEVEL;
    	if(nextLevel >= 1 && nextLevel <= Short.MAX_VALUE) {

        	//send new data to server
    		PacketHandler.INSTANCE.sendToServer(
    				new SyncToolLevelingEnchantment(this.pos,
    						this.getButtonByIndex(id).ENCHANTMENT,
    						this.getButtonByIndex(id).NEXT_LEVEL
    				)
    		);
    	}
    }
    
    /**
     * hides all buttons when called
     */
    private void hideAllButtons() {
		for (Widget button : this.buttons) {
			button.visible = false;
		}
    }

    /**
     * get the n-th button of the current page, does not check if there is actually a button
     * @param id
     * @return
     */
    private ButtonData getButtonByIndex(int id) {
    	return this.buttonData.get(id + ((this.currentPage - 1) * 3));
    }
    
    /**
     * returns true if the enchantment should still be leveled, because some enchantments will break when leveled to high
     * @param enchantment
     * @param level
     * @return
     */
    private boolean canStillBeLeveled(Enchantment enchantment, int level) {
    	if(enchantment.equals(Enchantments.LUCK_OF_THE_SEA)) {
    		return level < 84;
    	}
    	if(enchantment.equals(Enchantments.THORNS)) {
    		return level < 7;
    	}
    	if(enchantment.equals(Enchantments.QUICK_CHARGE)) {
    		return level < 5;
    	}
    	if(enchantment.equals(Enchantments.LURE)) {
    		return level < 5;
    	}
    	return true;
    }
    
    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderHoveredTooltip(matrixStack, mouseX, mouseY);
        
        this.renderPersonalTooltips(matrixStack, mouseX, mouseY);
        
        if(this.buttonData.size() > 4) {
        	//draw string for page overview
        	ITextComponent text = new TranslationTextComponent("container.toolleveling.tool_leveling_table.page", this.currentPage, this.maxPages);
            IReorderingProcessor ireorderingprocessor = text.func_241878_f();
            float left = this.guiLeft + 102;
            float top = this.guiTop + 99;
            this.font.drawString(matrixStack, text.getString(), (float)(left - this.font.func_243245_a(ireorderingprocessor) / 2), top, 0);	
        }

    	//draw string ?
    	ITextComponent text = new StringTextComponent("?").mergeStyle(TextFormatting.BOLD);
        float left = this.guiLeft + 168;
        float top = this.guiTop + 6;
        this.font.func_243248_b(matrixStack, text, left, top, 0);
    }

	@SuppressWarnings("deprecation")
	@Override
	protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {
	      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
	      this.minecraft.getTextureManager().bindTexture(GUI_TEXTURE);
	      int i = (this.width - this.xSize) / 2;
	      int j = (this.height - this.ySize) / 2;
	      blit(matrixStack, i, j, 0, 0, this.xSize, this.ySize, this.xSize, this.ySize);
	}
	
	/**
	 * @param matrixStack
	 * @param mouseX
	 * @param mouseY
	 */
	private void renderPersonalTooltips(MatrixStack matrixStack, int mouseX, int mouseY) {

		//tooltip payment item
		if (this.isPointInRegion(166, 4, 10, 10, (double) mouseX, (double) mouseY)) {
			List<ITextComponent> tooltip = Lists.newArrayList();
			tooltip.add((new TranslationTextComponent("container.toolleveling.tool_leveling_table.payment_item")).mergeStyle(TextFormatting.AQUA));
			tooltip.add((new TranslationTextComponent(ToolLevelingTableContainer.PAYMENT_ITEM.getTranslationKey())).mergeStyle(TextFormatting.DARK_GRAY));
			this.func_243308_b(matrixStack, tooltip, mouseX, mouseY);
		}
		
		//tooltip button1
		if ((this.buttonData.size() >= 1 + ((this.currentPage - 1) * 3)) && this.isPointInRegion(37, 21, 130, 20, (double) mouseX, (double) mouseY)) {
			List<ITextComponent> tooltip = Lists.newArrayList();
			ButtonData buttondata = this.getButtonByIndex(0);
			tooltip.add(this.getTooltipText(buttondata, 0).mergeStyle(TextFormatting.AQUA));
			tooltip.add(this.getTooltipText(buttondata, 1).mergeStyle(TextFormatting.DARK_GRAY));
			tooltip.add(this.getTooltipText(buttondata, 2).mergeStyle(TextFormatting.DARK_GRAY));
			tooltip.add(this.getTooltipText(buttondata, 3).mergeStyle(TextFormatting.DARK_GRAY));
			this.func_243308_b(matrixStack, tooltip, mouseX, mouseY);
		}
		//tooltip button2
		if ((this.buttonData.size() >= 2 + ((this.currentPage - 1) * 3)) && this.isPointInRegion(37, 45, 130, 20, (double) mouseX, (double) mouseY)) {
			List<ITextComponent> tooltip = Lists.newArrayList();
			ButtonData buttondata = this.getButtonByIndex(1);
			tooltip.add(this.getTooltipText(buttondata, 0).mergeStyle(TextFormatting.AQUA));
			tooltip.add(this.getTooltipText(buttondata, 1).mergeStyle(TextFormatting.DARK_GRAY));
			tooltip.add(this.getTooltipText(buttondata, 2).mergeStyle(TextFormatting.DARK_GRAY));
			tooltip.add(this.getTooltipText(buttondata, 3).mergeStyle(TextFormatting.DARK_GRAY));
			this.func_243308_b(matrixStack, tooltip, mouseX, mouseY);
		}
		//tooltip button3
		if ((this.buttonData.size() >= 3 + ((this.currentPage - 1) * 3)) && this.isPointInRegion(37, 69, 130, 20, (double) mouseX, (double) mouseY)) {
			List<ITextComponent> tooltip = Lists.newArrayList();
			ButtonData buttondata = this.getButtonByIndex(2);
			tooltip.add(this.getTooltipText(buttondata, 0).mergeStyle(TextFormatting.AQUA));
			tooltip.add(this.getTooltipText(buttondata, 1).mergeStyle(TextFormatting.DARK_GRAY));
			tooltip.add(this.getTooltipText(buttondata, 2).mergeStyle(TextFormatting.DARK_GRAY));
			tooltip.add(this.getTooltipText(buttondata, 3).mergeStyle(TextFormatting.DARK_GRAY));
			this.func_243308_b(matrixStack, tooltip, mouseX, mouseY);
		}
		
		if(this.buttonData.size() > 4) {

    		//tooltip previous page button
    		if (this.isPointInRegion(37, 93, 20, 20, (double) mouseX, (double) mouseY)) {
    			ITextComponent tooltip = (new TranslationTextComponent("spectatorMenu.previous_page")).mergeStyle(TextFormatting.AQUA);
    			this.renderTooltip(matrixStack, tooltip, mouseX, mouseY);
    		}

    		//tooltip next page button
    		if (this.isPointInRegion(147, 93, 20, 20, (double) mouseX, (double) mouseY)) {
    			ITextComponent tooltip = (new TranslationTextComponent("spectatorMenu.next_page")).mergeStyle(TextFormatting.AQUA);
    			this.renderTooltip(matrixStack, tooltip, mouseX, mouseY);
    		}
    		
		} else if(this.buttonData.size() == 4 && this.isPointInRegion(37, 93, 130, 20, (double) mouseX, (double) mouseY)) {
			
        	//render tooltip button4
			List<ITextComponent> tooltip = Lists.newArrayList();
			ButtonData buttondata = this.getButtonByIndex(3);
			tooltip.add(this.getTooltipText(buttondata, 0).mergeStyle(TextFormatting.AQUA));
			tooltip.add(this.getTooltipText(buttondata, 1).mergeStyle(TextFormatting.DARK_GRAY));
			tooltip.add(this.getTooltipText(buttondata, 2).mergeStyle(TextFormatting.DARK_GRAY));
			tooltip.add(this.getTooltipText(buttondata, 3).mergeStyle(TextFormatting.DARK_GRAY));
			this.func_243308_b(matrixStack, tooltip, mouseX, mouseY);
		}
	}
    
    private ITextComponent getButtonText(String translation, int nextlevel) {
    	return new StringTextComponent(new TranslationTextComponent(translation).getString() + " " + nextlevel);
    }
    
	private TranslationTextComponent getTooltipText(ButtonData buttondata, int line) {
    	switch (line) {
		case 1:
			return new TranslationTextComponent("container.toolleveling.tool_leveling_table.current_level", (buttondata.NEXT_LEVEL-1));
		case 2:
			return new TranslationTextComponent("container.toolleveling.tool_leveling_table.next_level", buttondata.NEXT_LEVEL);
		case 3:
			return new TranslationTextComponent("container.toolleveling.tool_leveling_table.cost", buttondata.UPGRADE_COST);
		case 0:
		default:
			return new TranslationTextComponent(buttondata.NAME);
		}
    }
	
	//helper class
	private class ButtonData {
		
		public ResourceLocation ENCHANTMENT;
		public String NAME;
		public int NEXT_LEVEL;
		public int UPGRADE_COST;
		
		public ButtonData (ResourceLocation enchantment, String name, int level) {
			this.ENCHANTMENT = enchantment;
			this.NEXT_LEVEL = level + 1;
			this.NAME = name;
			double modifier = Math.max(0.0D, ToolLevelingConfig.SERVER.upgradeCostMultiplier.get());
			int minCost = Math.max(0, ToolLevelingConfig.SERVER.minUpgradeCost.get());
			this.UPGRADE_COST = (int) Math.max(minCost, ((4.5D * level) - 12) * modifier);
		}
	}

}
