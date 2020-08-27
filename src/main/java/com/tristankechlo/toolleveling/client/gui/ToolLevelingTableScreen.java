package com.tristankechlo.toolleveling.client.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.tristankechlo.toolleveling.ToolLeveling;
import com.tristankechlo.toolleveling.container.ToolLevelingTableContainer;
import com.tristankechlo.toolleveling.network.ToolLevelingPacketHandler;
import com.tristankechlo.toolleveling.network.packets.SyncToolLevelingEnchantment;
import com.tristankechlo.toolleveling.tileentity.ToolLevelingTableTileEntity;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
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
        this.ySize = 214;
        //offset for inv title
        this.playerInventoryTitleX += 2;
        this.playerInventoryTitleY += 47;
    }
    
    @Override
    protected void init() {
    	super.init();
    	this.EnchantmentButton0 = new Button(this.guiLeft + 47, this.guiTop + 21, 120, 20, new StringTextComponent(""), (button) -> { handleButtonClick(0); });
    	this.EnchantmentButton1 = new Button(this.guiLeft + 47, this.guiTop + 45, 120, 20, new StringTextComponent(""), (button) -> { handleButtonClick(1); });
    	this.EnchantmentButton2 = new Button(this.guiLeft + 47, this.guiTop + 69, 120, 20, new StringTextComponent(""), (button) -> { handleButtonClick(2); });
    	this.EnchantmentButton3 = new Button(this.guiLeft + 47, this.guiTop + 93, 120, 20, new StringTextComponent(""), (button) -> { handleButtonClick(3); });
    	this.pageBackward = new Button(this.guiLeft + 47, this.guiTop + 93, 20, 20, new StringTextComponent("<"), (button) -> { pageBackward(); });
    	this.pageForward = new Button(this.guiLeft + 147, this.guiTop + 93, 20, 20, new StringTextComponent(">"), (button) -> { pageForward(); });

    	//hide all buttons before rendering
		this.EnchantmentButton0.visible = false;
		this.EnchantmentButton1.visible = false;
		this.EnchantmentButton2.visible = false;
		this.EnchantmentButton3.visible = false;
		this.pageForward.visible = false;
		this.pageBackward.visible = false;
		
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
    	if(this.ticksSinceUpdate > 3) {
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
    
    private void updateEnchantmentList() {
    	ItemStack stack = this.entity.getStackInSlot(0);
    	if(!stack.getItem().equals(Items.AIR)) {
    		//reset previous data
    		this.buttonData.clear();
    		
			Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
			for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
				//only list enchantments where a higher level is not useless
				if(entry.getKey().getMaxLevel() > 1) {
					//although the level is defined as an integer, the actual maximum is a short
					//a higher enchantment level than a short will result in a negative level
					if(entry.getValue() < Short.MAX_VALUE) {
						this.buttonData.add(new ButtonData(entry.getKey().getRegistryName(), entry.getKey().getName(), entry.getValue()));
					}
				}
			}
    	} else {
    		this.buttonData.clear();
    	}
    }
    
    private void updateButtonData(){
    	//get the number of required pages
		this.maxPages = (byte)Math.ceil(this.buttonData.size() / 3.0);
		
		//check if 1st button is needed on current page
		if(this.buttonData.size() >= 1 + ((this.currentPage - 1) * 3)) {
    		this.EnchantmentButton0.visible = true;
    		this.EnchantmentButton0.setMessage(getButtonText(this.buttonData.get(0 + ((this.currentPage - 1) * 3)).NAME, this.buttonData.get(0 + ((this.currentPage - 1) * 3)).LEVEL));
		} else {
    		this.EnchantmentButton0.visible = false;
		}

		//check if 2nd button is needed on current page
		if(this.buttonData.size() >= 2 + ((this.currentPage - 1) * 3)) {
    		this.EnchantmentButton1.visible = true;
    		this.EnchantmentButton1.setMessage(getButtonText(this.buttonData.get(1 + ((this.currentPage - 1) * 3)).NAME, this.buttonData.get(1 + ((this.currentPage - 1) * 3)).LEVEL));
		} else {
    		this.EnchantmentButton1.visible = false;			
		}

		//check if 3rd button is needed on current page
		if(this.buttonData.size() >= 3 + ((this.currentPage - 1) * 3)) {
    		this.EnchantmentButton2.visible = true;
    		this.EnchantmentButton2.setMessage(getButtonText(this.buttonData.get(2 + ((this.currentPage - 1) * 3)).NAME, this.buttonData.get(2 + ((this.currentPage - 1) * 3)).LEVEL));
		} else {
    		this.EnchantmentButton2.visible = false;
		}
		
		//either show 4th button or page selection
    	if(this.buttonData.size() == 4) {
    		this.EnchantmentButton3.visible = true;
    		this.EnchantmentButton3.setMessage(getButtonText(this.buttonData.get(3).NAME, this.buttonData.get(3).LEVEL));
    		this.pageForward.visible = false;
    		this.pageBackward.visible = false;
    	} else if(this.buttonData.size() > 4) {
    		this.EnchantmentButton3.visible = false;
    		this.pageForward.visible = true;
    		this.pageBackward.visible = true;
    	}
    }
    
    private void pageForward() {
    	if(this.currentPage == this.maxPages) {
    		this.currentPage = 1;
    	} else {
    		this.currentPage++;
    	}
    	updateButtonData();
    }
    
    private void pageBackward() {
    	if(this.currentPage == 1) {
    		this.currentPage = this.maxPages;
    	} else {
    		this.currentPage--;
    	}
    	updateButtonData();
    }
    
    private void handleButtonClick(int id) {

    	//next level must be below 32767
    	int nextLevel = this.buttonData.get(id + ((this.currentPage - 1) * 3)).LEVEL + 1;
    	if(nextLevel >= 1 && nextLevel <= Short.MAX_VALUE) {

        	//send new data to server
    		ToolLevelingPacketHandler.INSTANCE.sendToServer(
    				new SyncToolLevelingEnchantment(this.pos,
    						this.buttonData.get(id + ((this.currentPage - 1) * 3)).ENCHANTMENT,
    						this.buttonData.get(id + ((this.currentPage - 1) * 3)).LEVEL + 1
    				)
    		);
    	}
    }
    
    private void hideAllButtons() {
		for (Widget button : this.buttons) {
			button.visible = false;
		}
    }
    
    private ITextComponent getButtonText(String tranlation, int currentlevel) {
    	return new StringTextComponent(new TranslationTextComponent(tranlation).getString() + " " + (currentlevel + 1));
    }
    
    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.func_230459_a_(matrixStack, mouseX, mouseY);
        if(this.buttonData.size() > 4) {
        	//draw string for page overview
        	ITextComponent text = new TranslationTextComponent("container.toolleveling.tool_leveling_table.page", this.currentPage, this.maxPages);
            IReorderingProcessor ireorderingprocessor = text.func_241878_f();
            float left = this.guiLeft + 107;
            float top = this.guiTop + 99;
            this.font.drawString(matrixStack, text.getString(), (float)(left - this.font.func_243245_a(ireorderingprocessor) / 2), top, 0);
        }
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
	
	//helper class
	private class ButtonData {
		
		public ResourceLocation ENCHANTMENT;
		public String NAME;
		public int LEVEL;
		
		public ButtonData (ResourceLocation enchantment, String name, int level) {
			this.ENCHANTMENT = enchantment;
			this.LEVEL = level;
			this.NAME = name;
		}
	}

}
