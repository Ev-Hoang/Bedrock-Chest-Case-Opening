package com.ev.bedrockcaseopening;

import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ev.bedrockcaseopening.DungeonDropData.CaseMaterial;
import com.ev.bedrockcaseopening.DungeonDropData.Floor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.ChatComponentText;

public class ChestListener {
	
	private static WorldClient lastWorld = null;
	
	public static boolean hasPlayedAnimation = false;
	
	private boolean isCroesus = false;
	private boolean isCatacombsChestList = false;
	private int chestID;
	
	Map<Integer, Boolean> crOpenedChestOb = new HashMap<>();
	Map<Integer, Boolean> crOpenedChestBr = new HashMap<>();
	private boolean openedChestOb = false;
	private boolean openedChestBr = false;
	
	
	private Floor curFloor;
	private CaseMaterial curMaterial;
	
	public static GuiChest originalGui;
	
    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event) {    	
        if (event.gui instanceof GuiChest) {
            originalGui = (GuiChest) event.gui;

            if (originalGui.inventorySlots instanceof ContainerChest) {    
            	
                ContainerChest container = (ContainerChest) originalGui.inventorySlots;
                IInventory lower = container.getLowerChestInventory();
                
                if (lower.hasCustomName()) {
                    String name = lower.getDisplayName().getUnformattedText();
                    
                    if (name.endsWith(" Chest")) {
                        String materialName = name.substring(0, name.length() - " Chest".length()).trim();
                        CaseMaterial parsedMaterial = null;
                        try {
                            parsedMaterial = CaseMaterial.valueOf(materialName.toUpperCase().replace(" ", "_"));
                        } catch (IllegalArgumentException ignored) {}

                        if (parsedMaterial != null) {
                            curMaterial = parsedMaterial;
                        }

                        if (isCroesus) {
                        	if (curMaterial == CaseMaterial.BEDROCK) {
                                if (crOpenedChestBr.containsKey(chestID)) return;
                                crOpenedChestBr.put(chestID, true);
                        	}
                        	if (curMaterial == CaseMaterial.OBSIDIAN) {
                                if (crOpenedChestOb.containsKey(chestID)) return;
                                crOpenedChestOb.put(chestID, true);
                        	}
                        } else {
                        	if (MyConfig.debugMode)
                        	    Minecraft.getMinecraft().thePlayer.addChatMessage(
                        	            new ChatComponentText("Not Croesus, Searching scoreboard"));

                        	Scoreboard sb = Minecraft.getMinecraft().theWorld.getScoreboard();
                        	ScoreObjective sidebar = sb.getObjectiveInDisplaySlot(1);

                        	if (sidebar != null) {
                        	    Collection<Score> scores = sb.getSortedScores(sidebar);

                        	    for (Score sc : scores) {
                        	        ScorePlayerTeam team = sb.getPlayersTeam(sc.getPlayerName());
                        	        String line = ScorePlayerTeam.formatPlayerName(team, sc.getPlayerName());

                        	        line = clean(line);
                        	        
                        	        if (MyConfig.debugMode) System.out.println(line);

                        	        if (MyConfig.debugMode)
                        	            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(line));

                        	        if (line.contains("The Catacombs (")) {
                        	            Matcher m = Pattern.compile("\\((.*?)\\)").matcher(line);

                        	            if (m.find()) {
                        	                String floorId = m.group(1).trim();
                        	                boolean isMaster = floorId.startsWith("M");
                        	                int num = Integer.parseInt(floorId.substring(1));

                        	                String[] roman = {"I","II","III","IV","V","VI","VII"};
                        	                if (num >= 1 && num <= 7)
                        	                    floorId = (isMaster ? "M" : "") + roman[num - 1];

                        	                try {
                        	                    curFloor = DungeonDropData.Floor.valueOf(floorId);
                        	                } catch (Exception ignored) {}
                        	            }
                        	            break;
                        	        }
                        	    }
                        	    
                        	    if (curMaterial == CaseMaterial.BEDROCK)
                        	    	if (!openedChestBr) openedChestBr = true; else return;
                        	    if (curMaterial == CaseMaterial.OBSIDIAN)
                        	    	if (!openedChestOb) openedChestOb = true; else return;
                        	}
                        }
                        
                        if (MyConfig.debugMode)
                            Minecraft.getMinecraft().thePlayer.addChatMessage(
                                    new ChatComponentText("Material = " + curMaterial + " , Floor = " + curFloor));
                        event.gui = new GuiInterceptChest(container, curFloor, curMaterial);
                        if (MyConfig.debugMode) System.out.println("gui intercept called");
                    }

                    
                    if (name.contains("Catacombs")) {
                        boolean isMasterMode = name.contains("Master Mode");
                        Floor curFloor = null;
                        String prefix = isMasterMode ? "M" : "";

                        String[] roman = {"I","II","III","IV","V","VI","VII"};
                        for (String p : name.split(" ")) {
                            for (int i = 0; i < roman.length; i++) {
                                if (p.equalsIgnoreCase(roman[i])) {
                                    Floor base = Floor.values()[i]; 
                                    if (isMasterMode) {
                                        curFloor = Floor.valueOf(prefix + base.name());
                                    } else {
                                        curFloor = base;
                                    }
                                    break;
                                }
                            }
                            if (curFloor != null) break;
                        }

                        if (MyConfig.debugMode) {
                            Minecraft.getMinecraft().thePlayer.addChatMessage(
                                new ChatComponentText("Detected floor: " + curFloor));
                        }
                        
                        if (!isCroesus) return;

                        if (MyConfig.debugMode)
                            Minecraft.getMinecraft().thePlayer.addChatMessage(
                                new ChatComponentText(chestID + " Haven't opened yet"));

                        isCatacombsChestList = true;     
                        this.curFloor = curFloor;
                    }
                }

                
                if (lower.hasCustomName() && lower.getDisplayName().getUnformattedText().contains("Croesus")) {
                	if(MyConfig.debugMode) Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Croesus"));   
                	isCroesus = true;
                	isCatacombsChestList = false;
                }
            }
        }
    }
    
    @SubscribeEvent
    public void onItemTooltip(ItemTooltipEvent event) {
        if (Minecraft.getMinecraft().currentScreen instanceof GuiChest && isCatacombsChestList) {
            GuiChest chest = (GuiChest) Minecraft.getMinecraft().currentScreen;
            Slot hovered = chest.getSlotUnderMouse();
            if (hovered != null && hovered.getHasStack()) {
                if ( (hovered.slotNumber == 16 && !crOpenedChestBr.containsKey(chestID)) || (hovered.slotNumber == 15 && !crOpenedChestOb.containsKey(chestID)) ) {
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
                
                if(isCroesus && !isCatacombsChestList) chestID = slot.slotNumber;
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
            
            openedChestOb = false;
            openedChestBr = false;
            crOpenedChestOb.clear();
            crOpenedChestBr.clear();
        }
    }
    
    private static String clean(String s) {
        StringBuilder sb = new StringBuilder();

        int i = 0;
        while (i < s.length()) {
            int cp = s.codePointAt(i);
            if (cp == 0x00A7) {
                i += 2;
                continue;
            }
            if ((cp >= 32 && cp <= 126)) {
                sb.appendCodePoint(cp);
            }
            i += Character.charCount(cp);
        }

        return sb.toString();
    }
}
 