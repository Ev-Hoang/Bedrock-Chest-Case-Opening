package com.ev.bedrockcaseopening;

import com.ev.bedrockcaseopening.DungeonDropData.CaseMaterial;
import com.ev.bedrockcaseopening.DungeonDropData.Floor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;

public class GuiInterceptChest extends GuiContainer {

	private int delayTicks = 2;
	private DungeonDropData.Rule rewardToOpen = null;
	private boolean doneCollectingReward = false;
	
    private final ContainerChest container;
    private final Floor floor;
    private final CaseMaterial material;
    
    public GuiInterceptChest(ContainerChest container, Floor floor, CaseMaterial material) {
    	super(container);
        this.container = container;
        this.floor = floor;
        this.material = material;
        
        
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
            
            System.out.println("material = " + material);
            System.out.println("floor = " + floor);
            System.out.println("Drops size = " + DungeonDropData.getDrops(material, floor).size());

            for (int i = 10; i <= 16; i++) {
                ItemStack stack = lower.getStackInSlot(i);
                String itemId = NBTUtils.getExtraAttributeId(stack);
                
                System.out.println("[GuiInterceptChest] " + itemId);

                DungeonDropData.Rule foundRule = DungeonDropData.getDrops(material, floor).stream()
                        .filter(r -> r.item.name().equals(itemId))
                        .findFirst()
                        .orElse(null);

                if (foundRule == null) continue;
                
                System.out.println("[GuiInterceptChest] Founded!");

                if (rewardToOpen == null || 
                    foundRule.rarity < rewardToOpen.rarity || 
                    (foundRule.rarity == rewardToOpen.rarity &&
                     foundRule.item.name().compareTo(rewardToOpen.item.name()) < 0)) {
                    rewardToOpen = foundRule;
                }
            }
        }

        if (delayTicks > 0) {
            delayTicks--;
        } else if (rewardToOpen != null) {
        	Minecraft.getMinecraft().displayGuiScreen(new CustomDropAnimationGui(rewardToOpen, floor, material));
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


