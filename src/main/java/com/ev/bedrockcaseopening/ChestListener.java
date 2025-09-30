package com.ev.bedrockcaseopening;

import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;

public class ChestListener {
	
	private static WorldClient lastWorld = null;
	public static boolean hasPlayedAnimation = false;
	
	public static GuiChest originalGui;
	
    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event) {
        if (hasPlayedAnimation) {
            return;
        }
    	
        if (event.gui instanceof GuiChest) {
            originalGui = (GuiChest) event.gui;

            if (originalGui.inventorySlots instanceof ContainerChest) {
                ContainerChest container = (ContainerChest) originalGui.inventorySlots;
                IInventory lower = container.getLowerChestInventory();

                if (lower.hasCustomName() && lower.getDisplayName().getUnformattedText().contains("Bedrock Chest")) { 
                	System.out.println("bedrock chest");
                    event.gui = new GuiInterceptChest(container);
                    System.out.println("gui intercept called");
                }
                
                if (lower.hasCustomName() && lower.getDisplayName().getUnformattedText().contains("Croesus")) { 
                	EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
                	player.addChatMessage(new ChatComponentText("Croesus"));
                	
                    for (int i = 0; i <= 30; i++) {
                        ItemStack stack = lower.getStackInSlot(i);
                        NBTDebugger.printAllTags(stack, player);
                    }
                }
            }
        }
    }
    
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        WorldClient currentWorld = Minecraft.getMinecraft().theWorld;

        if (currentWorld != null && currentWorld != lastWorld) {
            lastWorld = currentWorld;
            hasPlayedAnimation = false;
        }
    }
}
 