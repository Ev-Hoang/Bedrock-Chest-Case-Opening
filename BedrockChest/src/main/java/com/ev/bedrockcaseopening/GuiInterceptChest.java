package com.ev.bedrockcaseopening;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class GuiInterceptChest extends GuiContainer {

	private int delayTicks = 2;
	private DungeonDropItem rewardToOpen = null;
	private boolean doneCollectingReward = false;
	
    private final ContainerChest container;
    
    public static String normalizeItemName(String displayName) {
        String normalized = displayName
            .replaceAll("(\\§[0-9a-frk-or])+", "")
            .replaceAll("[^a-zA-Z0-9\\s]", "")
            .trim()
            .replaceAll("\\s+", "_")        
            .toUpperCase();                 
        
        return normalized.length() > 0 ? normalized.substring(0) : "";
    }
    
    public GuiInterceptChest(ContainerChest container) {
    	super(container);
        this.container = container;
        
        System.out.println("[GuiInterceptChest] Initialized");
    }

    @Override
    public void initGui() {
    	super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        //super.drawScreen(mouseX, mouseY, partialTicks);
    }
    
    @Override
    public void updateScreen() {
        super.updateScreen();

        if (!doneCollectingReward) {
            doneCollectingReward = true;

            IInventory lower = container.getLowerChestInventory();
            DungeonDropItem reward = null;
            String dropitem = null;

            for (int i = 10; i <= 16; i++) {
                ItemStack stack = lower.getStackInSlot(i);
                if (stack == null) continue;
                if (stack.getItem() == Items.enchanted_book && stack.hasTagCompound()) {
                    NBTTagCompound tag = stack.getTagCompound();
                    if (tag.hasKey("display", 10)) {
                        NBTTagCompound display = tag.getCompoundTag("display");
                        if (display.hasKey("Lore", 9)) {
                            NBTTagList loreList = display.getTagList("Lore", 8);
                            if (loreList.tagCount() > 0) {
                                dropitem = loreList.getStringTagAt(0);
                            }
                        }
                    }
                } else {
                    dropitem = stack.getDisplayName();
                }

                String normalized = normalizeItemName(dropitem);
                System.out.print("slot" + i + " " + normalized);
                
                try {
                    DungeonDropItem item = DungeonDropItem.valueOf(normalized);
                    if (reward == null || item.getIndex() < reward.getIndex()) {
                        reward = item;
                    }
                } catch (IllegalArgumentException ignored) {}
            }

            rewardToOpen = reward;
        }

        if (delayTicks > 0) {
            delayTicks--;
        } else if (rewardToOpen != null) {
            ChestListener.hasPlayedAnimation = true;
            Minecraft.getMinecraft().displayGuiScreen(new CustomDropAnimationGui(rewardToOpen));
        } else {
            ChestListener.hasPlayedAnimation = true;
            Minecraft.getMinecraft().displayGuiScreen(ChestListener.originalGui);
        }
    }
    
//    ChestListener.skipNext = true;
//	Minecraft.getMinecraft().displayGuiScreen(ChestListener.originalGui);
    
    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        // Không vẽ gì hết
    }
    
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        // Không cho click
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        // Không cho kéo
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        // Không cho thả
    }

//    @Override
//    protected void keyTyped(char typedChar, int keyCode) {
//        // Không cho gõ phím
//    }

    @Override
    public void handleMouseInput() {
        // Không xử lý gì hết
    }

//    @Override
//    public void handleKeyboardInput() {
//        // Không xử lý gì hết
//    }
}


