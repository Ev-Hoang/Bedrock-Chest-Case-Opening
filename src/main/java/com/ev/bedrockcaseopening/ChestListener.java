package com.ev.bedrockcaseopening;

import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;

public class ChestListener {
	
	private static WorldClient lastWorld = null;
	
	public static boolean hasPlayedAnimation = false;
	
	private boolean isCroesus = false;
	private boolean isCatacombsChest = false;
	private int chestID;
	Map<Integer, Boolean> openedChest = new HashMap<>();
	
	public static GuiChest originalGui;
	
    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event) {    	
        if (event.gui instanceof GuiChest) {
            originalGui = (GuiChest) event.gui;

            if (originalGui.inventorySlots instanceof ContainerChest) {    
            	isCatacombsChest = false;
            	
                ContainerChest container = (ContainerChest) originalGui.inventorySlots;
                IInventory lower = container.getLowerChestInventory();

                if (lower.hasCustomName() && lower.getDisplayName().getUnformattedText().contains("Bedrock Chest")) {    
                		
                	if(isCroesus) {
                		if (openedChest.containsKey(chestID)) return;
                		openedChest.put(chestID, true);
                	} else {
                		if (hasPlayedAnimation) return;
                		hasPlayedAnimation = true;
                	}
                	
            		System.out.println("bedrock chest");
                    event.gui = new GuiInterceptChest(container);
                    System.out.println("gui intercept called");
                }
                
                if (lower.hasCustomName() && lower.getDisplayName().getUnformattedText().contains("Obsidian Chest")) {    
            		
                	if(isCroesus) {
                		if (openedChest.containsKey(chestID)) return;
                		openedChest.put(chestID, true);
                	} else {
                		if (hasPlayedAnimation) return;
                		hasPlayedAnimation = true;
                	}
                	
            		System.out.println("bedrock chest");
                    event.gui = new GuiInterceptChest(container);
                    System.out.println("gui intercept called");
                }
                
                if (lower.hasCustomName() && lower.getDisplayName().getUnformattedText().contains("Catacombs")) {               
                	if(!isCroesus) return;
                	if(openedChest.containsKey(chestID)) return;
                	isCatacombsChest = true;                	
                }
                
                if (lower.hasCustomName() && lower.getDisplayName().getUnformattedText().contains("Croesus")) {             
                	isCroesus = true;
                }
            }
        }
    }
    
    @SubscribeEvent
    public void onItemTooltip(ItemTooltipEvent event) {
        if (Minecraft.getMinecraft().currentScreen instanceof GuiChest && isCatacombsChest) {
            GuiChest chest = (GuiChest) Minecraft.getMinecraft().currentScreen;
            Slot hovered = chest.getSlotUnderMouse();
            if (hovered != null && hovered.getHasStack()) {
                if (hovered.slotNumber == 16) {
                    if (event.toolTip.size() > 3) {
                        String first = event.toolTip.get(0);
                        String last1 = event.toolTip.get(event.toolTip.size() - 1);
                        String last2 = event.toolTip.get(event.toolTip.size() - 2);
                        event.toolTip.clear();
                        event.toolTip.add(first);
                        event.toolTip.add("§7Hidden");
                        event.toolTip.add(last2);
                        event.toolTip.add(last1);
                    }
                }
            }
        }
    }
    
    @SubscribeEvent
    public void onMouseClick(GuiScreenEvent.MouseInputEvent.Pre event) {
        if (event.gui instanceof GuiChest) {
            GuiChest chest = (GuiChest) event.gui;
            
            Slot slot = chest.getSlotUnderMouse();
            if (slot != null && org.lwjgl.input.Mouse.getEventButtonState()) {
                
                if(isCroesus) chestID = slot.slotNumber;
            }
        }
    }

    
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        WorldClient currentWorld = Minecraft.getMinecraft().theWorld;

        if (currentWorld != null && currentWorld != lastWorld) {
            lastWorld = currentWorld;
            isCroesus = false;
            hasPlayedAnimation = false;
            chestID = -1;
            openedChest.clear();
        }
    }
}
 