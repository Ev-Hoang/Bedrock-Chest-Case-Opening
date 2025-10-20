package com.ev.bedrockcaseopening;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChatComponentText;

public class GuiInterceptChest extends GuiContainer {

	private int delayTicks = 2;
	private BedrockFloorVII rewardToOpen = null;
	private boolean doneCollectingReward = false;
	
    private final ContainerChest container;
    
//    public static String normalizeItemName(String displayName) {
//        String normalized = displayName
//            .replaceAll("(\\§[0-9a-frk-or])+", "")
//            .replaceAll("[^a-zA-Z0-9\\s]", "")
//            .trim()
//            .replaceAll("\\s+", "_")        
//            .toUpperCase();                 
//        
//        return normalized.length() > 0 ? normalized.substring(0) : "";
//    }
    
    public GuiInterceptChest(ContainerChest container) {
    	super(container);
        this.container = container;
        
        if(MyConfig.debugMode) System.out.println("[GuiInterceptChest] Initialized");
    }

    @Override
    public void initGui() {
    	super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    }
    
    @Override
    public void updateScreen() {
        super.updateScreen();

        if (!doneCollectingReward) {
            doneCollectingReward = true;

            IInventory lower = container.getLowerChestInventory();
            BedrockFloorVII reward = null;

            for (int i = 10; i <= 16; i++) {
                ItemStack stack = lower.getStackInSlot(i);
                
                if(MyConfig.debugMode) Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(NBTUtils.getExtraAttributeId(stack)));               
                String itemId = NBTUtils.getExtraAttributeId(stack);                
                try {
                	BedrockFloorVII item = BedrockFloorVII.valueOf(itemId);
                    if (reward == null || item.getIndex() < reward.getIndex()) {
                        reward = item;
                    }
                } catch (IllegalArgumentException ignored) {
                }
            }

            rewardToOpen = reward;
        }

        if (delayTicks > 0) {
            delayTicks--;
        } else if (rewardToOpen != null) {
            Minecraft.getMinecraft().displayGuiScreen(new CustomDropAnimationGui(rewardToOpen));
        } else {
            Minecraft.getMinecraft().displayGuiScreen(ChestListener.originalGui);
            if(MyConfig.debugMode) Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("NO ITEM BLEH BLEH BLUH!"));   
        }
    }
    
    
    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
    }
    
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
    }

    @Override
    public void handleMouseInput() {
    }
}


