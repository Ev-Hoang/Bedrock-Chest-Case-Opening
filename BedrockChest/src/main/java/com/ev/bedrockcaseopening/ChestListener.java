package com.ev.bedrockcaseopening;

import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;

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

                if (lower.hasCustomName() && lower.getDisplayName().getUnformattedText().contains("Bedrock Chest")) { // bạn có thể kiểm tra tên rương tại đây
                    // Thay GUI gốc bằng GUI chặn
                	System.out.println("bedrock chest");
                    event.gui = new GuiInterceptChest(container);
                    System.out.println("gui intercept called");
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
            hasPlayedAnimation = false; // Reset mỗi lần chuyển world
            System.out.println("World changed → reset animation");
        }
    }
}
 