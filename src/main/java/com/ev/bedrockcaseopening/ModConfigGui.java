package com.ev.bedrockcaseopening;

import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;

public class ModConfigGui extends GuiConfig {
    public ModConfigGui(GuiScreen parent) {
        super(parent,
                new ConfigElement(MyConfig.config.getCategory("general")).getChildElements(),
                "bedrockcaseopening", // modid
                false,
                false,
                "Bedrock Chest Opening Config");
    }
}
