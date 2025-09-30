package com.ev.bedrockcaseopening;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.entity.player.EntityPlayer;

import java.util.Set;

public class NBTDebugger {

    public static void printAllTags(ItemStack stack, EntityPlayer player) {
        if (stack != null && stack.hasTagCompound()) {
            NBTTagCompound tag = stack.getTagCompound();
            Set<String> keys = tag.getKeySet();

            player.addChatMessage(new ChatComponentText(
                "==== NBT Data for " + stack.getDisplayName() + " ===="));

            for (String key : keys) {
                NBTBase value = tag.getTag(key);
                player.addChatMessage(new ChatComponentText(key + " = " + value.toString()));
            }
        } else {
            player.addChatMessage(new ChatComponentText("No NBT."));
        }
    }
}
