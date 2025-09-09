package com.ev.bedrockcaseopening;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = "bedrockcaseopening",
	name = "Bedrock Chest Opening",
	version = "0.2",
	guiFactory = "com.ev.bedrockcaseopening.ModGuiFactory"
	)
public class ExampleMod {
	
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        MyConfig.init(event.getSuggestedConfigurationFile());
    }
	
    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
    	MinecraftForge.EVENT_BUS.register(new ChestListener());
    	MinecraftForge.EVENT_BUS.register(new ConfigEventHandler());
    }
}
