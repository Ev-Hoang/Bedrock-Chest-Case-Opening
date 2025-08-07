package com.example.examplemod;

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

            if (originalGui.field_147002_h instanceof ContainerChest) {
                ContainerChest container = (ContainerChest) originalGui.field_147002_h;
                IInventory lower = container.func_85151_d();

                if (lower.func_145818_k_() && lower.func_145748_c_().func_150260_c().contains("Bedrock Chest")) { // bạn có thể kiểm tra tên rương tại đây
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

        WorldClient currentWorld = Minecraft.func_71410_x().field_71441_e;

        if (currentWorld != null && currentWorld != lastWorld) {
            lastWorld = currentWorld;
            hasPlayedAnimation = false; // Reset mỗi lần chuyển world
            System.out.println("World changed → reset animation");
        }
    }
}
 