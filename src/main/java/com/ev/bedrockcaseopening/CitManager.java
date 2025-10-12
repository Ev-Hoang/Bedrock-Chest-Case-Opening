package com.ev.bedrockcaseopening;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.client.resources.AbstractResourcePack;
import net.minecraft.client.resources.FileResourcePack;
import net.minecraft.client.resources.FolderResourcePack;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.item.ItemStack;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class CitManager {

    private final static Map<String, ResourceLocation> citCache = new HashMap<>();

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
                    checkEnchant(props, path);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.println("[BCCO] CIT Cache built with " + citCache.size() + " entries.");
    }
    
    private void checkID(Properties props, String path) {
    	String nbtId = props.getProperty("nbt.ExtraAttributes.id");
        if (nbtId == null) return;

        if (!ItemEnum.containsId(nbtId)) return;
        
        String textureProp = props.getProperty("texture");
        String texturePath;

        int lastSlash = path.lastIndexOf('/');
        String folder = (lastSlash >= 0) ? path.substring(0, lastSlash) : "";

        if (textureProp != null && !textureProp.isEmpty()) {
            texturePath = textureProp;
            if (texturePath.startsWith("/")) {
                texturePath = texturePath.substring(1); // path tuyệt đối trong assets
            } else {
                // texture tương đối → nối folder
                texturePath = folder + "/" + texturePath;
            }

            if (texturePath.endsWith(".png")) {
                texturePath = texturePath.substring(0, texturePath.length() - 4);
            }

        } else {
            // không có texture prop → dùng basename
            String baseName = path.substring(lastSlash + 1, path.length() - ".properties".length());
            texturePath = folder + "/" + baseName;
        }

        // loại bỏ prefix "assets/minecraft/"
        String prefix = "assets/minecraft/";
        if (texturePath.startsWith(prefix)) {
            texturePath = texturePath.substring(prefix.length());
        }

        ResourceLocation rl = new ResourceLocation("minecraft", texturePath + ".png");

        citCache.put(nbtId, rl);
        System.out.println(nbtId + " : " + texturePath + ".png");
    }
    
    private void checkEnchant(Properties props, String path) {
        for (String key : props.stringPropertyNames()) {
            if (key.startsWith("nbt.ExtraAttributes.enchantments.")) {
                String enchantName = key.substring("nbt.ExtraAttributes.enchantments.".length());
                String expectedLevel = props.getProperty(key);
                
                if (EnchantEnum.contains(enchantName, expectedLevel)) {
                	String texturePath = props.getProperty("texture");
                    if (texturePath == null) {
                        String baseName = path.substring(path.lastIndexOf('/') + 1, path.length() - ".properties".length());
                        texturePath = baseName;
                    }
                    ResourceLocation rl = new ResourceLocation("minecraft", "mcpatcher/cit/" + texturePath + ".png");
                	String nbtId = enchantName + "_" + expectedLevel;
                	
                	citCache.put(nbtId, rl);
                	System.out.println(nbtId + " : " + "mcpatcher/cit/" + texturePath + ".png");
                    break;
                }
            }
        }
    }


    public static ResourceLocation getTexture(String nbtId) {
    	ResourceLocation rl = citCache.getOrDefault(nbtId,
                new ResourceLocation("minecraft", "textures/item/diamond_sword.png"));
    	return rl;
    }

    private String normalize(String s) {
        // ví dụ: "Necron's Handle" -> "necron_s_handle"
        return s.toLowerCase().replace(" ", "_").replace("'", "");
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
