package com.ev.bedrockcaseopening;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.client.resources.AbstractResourcePack;
import net.minecraft.client.resources.FileResourcePack;
import net.minecraft.client.resources.FolderResourcePack;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.item.ItemStack;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.imageio.ImageIO;

import java.io.InputStreamReader;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class CitManager {

    private final static Map<String, TextureData> citCache = new HashMap<>();
    private IResourceManager rm = Minecraft.getMinecraft().getResourceManager();

    public CitManager() {
        ((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager())
            .registerReloadListener(manager -> {
                try {
                	System.out.println("[BCCO] CIT Cache building...");
                    rebuildCache();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        );
    }

    private void rebuildCache() throws IOException {
        citCache.clear();

        for (ResourcePackRepository.Entry entry :
                Minecraft.getMinecraft().getResourcePackRepository().getRepositoryEntries()) {

            IResourcePack pack = entry.getResourcePack();
            Set<String> propertyFiles = listFiles(pack, "mcpatcher/cit", ".properties");

            for (String path : propertyFiles) {
                try (InputStream in = pack.getInputStream(new ResourceLocation("minecraft", path))) {
                    Properties props = new Properties();
                    props.load(in);

                    checkID(props,path);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.println("[BCCO] CIT Cache built with " + citCache.size() + " entries.");
    }
    
    private void checkID(Properties props, String path) {
    	String nbtId = props.getProperty("nbt.ExtraAttributes.id");
    	String itemType = props.getProperty("items", "");

        if ("minecraft:enchanted_book".equals(itemType)) {
        	System.out.println("BOOK");
            for (String key : props.stringPropertyNames()) {
                if (key.startsWith("nbt.ExtraAttributes.enchantments.")) {
                    String enchantName = key.substring("nbt.ExtraAttributes.enchantments.".length());
                    String level = props.getProperty(key);
                    nbtId = enchantName + "_" + level;
                    System.out.println(nbtId);
                    break;
                }
            }
        }
        
        if (nbtId == null) return;

        try {
            ItemEnum item = ItemEnum.valueOf(nbtId);
        } catch (IllegalArgumentException ignored) {
            return;
        }
        
        String textureProp = props.getProperty("texture");
        String texturePath;

        int lastSlash = path.lastIndexOf('/');
        String folder = (lastSlash >= 0) ? path.substring(0, lastSlash) : "";

        if (textureProp != null && !textureProp.isEmpty()) {
            texturePath = textureProp;
            if (texturePath.startsWith("/")) {
                texturePath = texturePath.substring(1);
            } else {
                texturePath = folder + "/" + texturePath;
            }

            if (texturePath.endsWith(".png")) {
                texturePath = texturePath.substring(0, texturePath.length() - 4);
            }

        } else {
            String baseName = path.substring(lastSlash + 1, path.length() - ".properties".length());
            texturePath = folder + "/" + baseName;
        }

        String prefix = "assets/minecraft/";
        if (texturePath.startsWith(prefix)) {
            texturePath = texturePath.substring(prefix.length());
        }

        ResourceLocation rl = new ResourceLocation("minecraft", texturePath + ".png");
        int frameTime = getFrameTime(rm, rl);
        int frames = getFrames(rm, rl);


        citCache.put(nbtId, new TextureData(rl, frameTime , frames));
        
        //System.out.println(nbtId + " : " + texturePath + ".png");
    }
    
    private int getFrameTime(IResourceManager rm, ResourceLocation rl) {
        try {
            ResourceLocation metaLoc = new ResourceLocation(
                rl.getResourceDomain(),
                rl.getResourcePath() + ".mcmeta"
            );

            IResource res = rm.getResource(metaLoc);
            if (res == null) return 1;

            JsonParser parser = new JsonParser();
            JsonObject root = parser.parse(new InputStreamReader(res.getInputStream())).getAsJsonObject();

            if (root.has("animation")) {
                JsonObject anim = root.getAsJsonObject("animation");
                if (anim.has("frametime")) {
                	int ft = anim.get("frametime").getAsInt();
                    return ft;
                }
            }

        } catch (Exception e) {
        }
        return 1;
    }
    
    public static int getFrames(IResourceManager rm, ResourceLocation rl) {
        InputStream stream = null;
        try {
            IResource res = rm.getResource(rl);
            stream = res.getInputStream();
            BufferedImage img = ImageIO.read(stream);
            if (img == null) return 1;

            int width = img.getWidth();
            int height = img.getHeight();

            return Math.max(height / width, 1);
        } catch (Exception e) {
            return 1;
        } finally {
            if (stream != null) {
                try { stream.close(); } catch (Exception ignored) {}
            }
        }
    }

    public static TextureData getTextureData(String nbtId) {
    	TextureData data = citCache.getOrDefault(nbtId,
    			new TextureData(new ResourceLocation("minecraft", "textures/item/diamond_sword.png"),1,1));  	
    	return data;
    }

    private Set<String> listFiles(IResourcePack pack, String folder, String ext) throws IOException {
        Set<String> result = new HashSet<>();
        File base = getPackFile(pack);
        if (base == null) return result;
        if (base.isDirectory()) {
            File target = new File(base, "assets/minecraft/" + folder);
            if (target.exists() && target.isDirectory()) {
                Files.walk(target.toPath())
                     .filter(p -> p.toString().endsWith(ext))
                     .forEach(p -> {
                         String relative = base.toPath().relativize(p).toString().replace("\\", "/");
                         result.add(relative.substring("assets/minecraft/".length()));
                     });
            }
        } else {
            try (ZipFile zip = new ZipFile(base)) {
                Enumeration<? extends ZipEntry> entries = zip.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();
                    String name = entry.getName();
                    if (name.startsWith("assets/minecraft/" + folder) && name.endsWith(ext)) {
                        result.add(name.substring("assets/minecraft/".length()));
                    }
                }
            }
        }

        return result;
    }
    
    private File getPackFile(IResourcePack pack) {
        try {
            Field f = AbstractResourcePack.class.getDeclaredField("resourcePackFile");
            f.setAccessible(true);
            return (File) f.get(pack);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }



}
