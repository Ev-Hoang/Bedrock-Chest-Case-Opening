package com.ev.bedrockcaseopening;

import net.minecraftforge.common.config.Configuration;
import java.io.File;

public class MyConfig {

    public static Configuration config;

    public static float slowTime;
    public static int slowDistance;

    public static void init(File configFile) {
        config = new Configuration(configFile);
        load();
    }

    public static void load() {
        String category = "general";
        slowTime = config.getFloat("slowTime", category, 7f, 1f, 20f,
                "Thời gian chậm (giây)");
        slowDistance = config.getInt("slowDistance", category, 20, 10, 40,
                "Khoảng cách chậm");

        if (config.hasChanged()) {
            config.save();
        }
    }
}
