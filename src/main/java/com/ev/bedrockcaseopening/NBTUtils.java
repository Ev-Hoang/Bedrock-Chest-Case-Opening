package com.ev.bedrockcaseopening;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;

import java.util.Set;

public class NBTUtils {

    public static void printAllNBT(ItemStack stack) {
        if (stack == null) {
        	Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("STACK NULL"));
            return;
        }

        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) {
        	Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("NO NBT"));
            return;
        }

        printCompound(tag, 0);
    }

    private static void printCompound(NBTTagCompound compound, int indent) {
        Set<String> keys = compound.getKeySet();
        String pad = new String(new char[indent]).replace("\0", "  "); 

        for (String key : keys) {
            NBTBase base = compound.getTag(key);
            if (key == "textures") break;
            byte type = base.getId();

            // Nếu là Compound → đệ quy
            if (type == 10) { // 10 = NBTTagCompound
            	Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(pad + key + ": {"));
                printCompound((NBTTagCompound) base, indent + 1);
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(pad + "}"));
            } else {
            	Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(pad + key + " = " + base.toString()));
            }
        }
    }
    
    public static String getExtraAttributeId(ItemStack stack) {
        if (stack == null) return "STACKNULL";

        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) return "TAGNULL";

        if (!tag.hasKey("ExtraAttributes")) return "KEYNULL";

        NBTTagCompound extra = tag.getCompoundTag("ExtraAttributes");
        if (!extra.hasKey("id")) return "IDNULL";

        String id = extra.getString("id");

        if ("ENCHANTED_BOOK".equalsIgnoreCase(id) && extra.hasKey("enchantments")) {
            NBTTagCompound enchants = extra.getCompoundTag("enchantments");

            for (String key : enchants.getKeySet()) {
                int level = enchants.getInteger(key);
                return key + "_" + level;
            }

            return "ENCHNULL";
        }

        return id;
    }
}
