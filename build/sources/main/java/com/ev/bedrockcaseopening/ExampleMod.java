package com.ev.bedrockcaseopening;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = "bedrockcaseopening", name = "Bedrock Chest Opening", version = "0.1")
public class ExampleMod {
    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
    	MinecraftForge.EVENT_BUS.register(new ChestListener());
    }
}
