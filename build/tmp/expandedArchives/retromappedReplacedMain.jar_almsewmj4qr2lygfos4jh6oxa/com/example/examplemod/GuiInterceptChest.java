package com.example.examplemod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class GuiInterceptChest extends GuiContainer {

	private int delayTicks = 2;
	private DungeonDropItem rewardToOpen = null;
	private boolean doneCollectingReward = false;
	
    private final ContainerChest container;
    private boolean replaced = false;
    
    public static String normalizeItemName(String displayName) {
        String normalized = displayName
            .replaceAll("(\\§[0-9a-frk-or])+", "")
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
    public void func_73866_w_() {
    	super.func_73866_w_();
    }

    @Override
    public void func_73863_a(int mouseX, int mouseY, float partialTicks) {
        //super.drawScreen(mouseX, mouseY, partialTicks);
    }
    
    @Override
    public void func_73876_c() {
        super.func_73876_c();

        if (!doneCollectingReward) {
            doneCollectingReward = true;

            IInventory lower = container.func_85151_d();
            DungeonDropItem reward = null;
            String dropitem = null;

            for (int i = 10; i <= 16; i++) {
                ItemStack stack = lower.func_70301_a(i);
                if (stack == null) continue;
                if (stack.func_77973_b() == Items.field_151134_bR && stack.func_77942_o()) {
                    NBTTagCompound tag = stack.func_77978_p();
                    if (tag.func_150297_b("display", 10)) {
                        NBTTagCompound display = tag.func_74775_l("display");
                        if (display.func_150297_b("Lore", 9)) {
                            NBTTagList loreList = display.func_150295_c("Lore", 8);
                            if (loreList.func_74745_c() > 0) {
                                dropitem = loreList.func_150307_f(0);
                            }
                        }
                    }
                } else {
                    dropitem = stack.func_82833_r();
                }

                String normalized = normalizeItemName(dropitem);
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
            Minecraft.func_71410_x().func_147108_a(new CustomDropAnimationGui(rewardToOpen));
        } else {
            ChestListener.hasPlayedAnimation = true;
            Minecraft.func_71410_x().func_147108_a(ChestListener.originalGui);
        }
    }
    
//    ChestListener.skipNext = true;
//	Minecraft.getMinecraft().displayGuiScreen(ChestListener.originalGui);
    
    @Override
    protected void func_146976_a(float partialTicks, int mouseX, int mouseY) {
        // Không vẽ gì hết
    }
    
    @Override
    protected void func_73864_a(int mouseX, int mouseY, int mouseButton) {
        // Không cho click
    }

    @Override
    protected void func_146273_a(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        // Không cho kéo
    }

    @Override
    protected void func_146286_b(int mouseX, int mouseY, int state) {
        // Không cho thả
    }

//    @Override
//    protected void keyTyped(char typedChar, int keyCode) {
//        // Không cho gõ phím
//    }

    @Override
    public void func_146274_d() {
        // Không xử lý gì hết
    }

//    @Override
//    public void handleKeyboardInput() {
//        // Không xử lý gì hết
//    }
}


