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

    private final Map<String, ResourceLocation> citCache = new HashMap<>();

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

            // lấy tất cả file .properties trong cit/
            Set<String> propertyFiles = listFiles(pack, "mcpatcher/cit", ".properties");

            for (String path : propertyFiles) {
                try (InputStream in = pack.getInputStream(new ResourceLocation("minecraft", path))) {
                    Properties props = new Properties();
                    props.load(in);

                    // lấy id trong nbt.ExtraAttributes.id
                    String nbtId = props.getProperty("nbt.ExtraAttributes.id");
                    System.out.println(nbtId);
                    if (nbtId == null) continue;

                    if (!ItemEnum.containsId(nbtId)) continue;
                    
                    // check enchantments
                    boolean enchantMatch = true; // mặc định true
                    for (String key : props.stringPropertyNames()) {
                        if (key.startsWith("nbt.ExtraAttributes.enchantments.")) {
                            String enchantName = key.substring("nbt.ExtraAttributes.enchantments.".length());
                            String expectedLevel = props.getProperty(key);

                            if (!EnchantEnum.contains(enchantName, expectedLevel)) {
                                enchantMatch = false;
                                break;
                            }
                        }
                    }

                    if (!enchantMatch) continue;
                    String texturePath = props.getProperty("texture");
                    if (texturePath == null) {
                        String baseName = path.substring(path.lastIndexOf('/') + 1, path.length() - ".properties".length());
                        texturePath = baseName;
                    }

                    ResourceLocation rl = new ResourceLocation("minecraft", "textures/cit/" + texturePath + ".png");

                    citCache.put(nbtId, rl);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.println("[BCCO] CIT Cache built with " + citCache.size() + " entries.");
    }


    public ResourceLocation getTexture(ItemStack stack) {
        if (stack == null || !stack.hasDisplayName()) {
            return null;
        }
        String normalized = normalize(stack.getDisplayName());
        return citCache.getOrDefault(normalized,
                new ResourceLocation("minecraft", "textures/item/diamond_sword.png"));
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
                    	System.out.println("[BCCO] " + name + " : " + entry);
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
