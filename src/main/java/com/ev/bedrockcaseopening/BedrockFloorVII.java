package com.ev.bedrockcaseopening;

import net.minecraft.util.ResourceLocation;

public enum BedrockFloorVII {
	//Temporaly, must change!!
    NECRONS_HANDLE(1, 1, CitManager.getTextureData("NECRON_HANDLE").getRl(), CitManager.getTextureData("NECRON_HANDLE").getFrames(), CitManager.getTextureData("NECRON_HANDLE").getFrametime()),
    //
    
    
    SHADOW_WARP(2, 2, new ResourceLocation("bedrockcaseopening", "textures/dungeonDropItem/shadow_warp.png"), 1, 1),
    WITHER_SHIELD(3, 2, new ResourceLocation("bedrockcaseopening", "textures/dungeonDropItem/wither_shield.png"), 1, 1),
    IMPLOSION(4, 2, new ResourceLocation("bedrockcaseopening", "textures/dungeonDropItem/implosion.png"), 1, 1),
    AUTO_RECOMBOBULATOR(5, 3, new ResourceLocation("bedrockcaseopening", "textures/dungeonDropItem/auto_recombobulator.png"), 16, 1),
    WITHER_CHESTPLATE(6, 3, new ResourceLocation("bedrockcaseopening", "textures/dungeonDropItem/wither_chestplate.png"), 1, 1),
    ONE_FOR_ALL_I(7, 3, new ResourceLocation("bedrockcaseopening", "textures/dungeonDropItem/ultimate_one_for_all1.png"), 1, 1),
    RECOMBOBULATOR_3000(8, 4, new ResourceLocation("bedrockcaseopening", "textures/dungeonDropItem/recombobulator_3000.png"), 8, 2),
    WITHER_LEGGINGS(9, 4, new ResourceLocation("bedrockcaseopening", "textures/dungeonDropItem/wither_leggings.png"), 1, 1),
    WITHER_CLOAK_SWORD(10, 5, new ResourceLocation("bedrockcaseopening", "textures/dungeonDropItem/wither_cloak_sword.png"), 8, 2),
    WITHER_HELMET(11, 5, new ResourceLocation("bedrockcaseopening", "textures/dungeonDropItem/wither_helmet.png"), 1, 1),
    SOUL_EATER_I(12, 5, new ResourceLocation("bedrockcaseopening", "textures/dungeonDropItem/ultimate_soul_eater1.png"), 1, 1),
    WITHER_BLOOD(13, 5, new ResourceLocation("bedrockcaseopening", "textures/dungeonDropItem/wither_blood.png"), 1, 1),
    FUMING_POTATO_BOOK(14, 5, new ResourceLocation("bedrockcaseopening", "textures/dungeonDropItem/fuming_potato_book.png"), 5, 1),
    WITHER_BOOTS(15, 5, new ResourceLocation("bedrockcaseopening", "textures/dungeonDropItem/wither_boots.png"), 1, 1),
    WITHER_CATALYST(16, 5, new ResourceLocation("bedrockcaseopening", "textures/dungeonDropItem/wither_catalyst.png"), 1, 1),
    PRECURSOR_GEAR(17, 5, new ResourceLocation("bedrockcaseopening", "textures/dungeonDropItem/precursor_gear.png"), 1, 1),
    HOT_POTATO_BOOK(18, 5, new ResourceLocation("bedrockcaseopening", "textures/dungeonDropItem/hot_potato_book.png"), 6, 2),
    STORM_THE_FISH(19, 6, new ResourceLocation("bedrockcaseopening", "textures/dungeonDropItem/storm_the_fish.png"), 1, 1),
    MAXOR_THE_FISH(20, 6, new ResourceLocation("bedrockcaseopening", "textures/dungeonDropItem/maxor_the_fish.png"), 1, 1),
    GOLDOR_THE_FISH(21, 6, new ResourceLocation("bedrockcaseopening", "textures/dungeonDropItem/goldor_the_fish.png"), 1, 1),
    WISDOM_II(22, 7, new ResourceLocation("bedrockcaseopening", "textures/dungeonDropItem/ultimate_wisdom2.png"), 1, 1),
    LAST_STAND_II(23, 7, new ResourceLocation("bedrockcaseopening", "textures/dungeonDropItem/ultimate_last_stand2.png"), 1, 1),
    ULTIMATE_WISE_II(24, 7, new ResourceLocation("bedrockcaseopening", "textures/dungeonDropItem/ultimate_wise2.png"), 1, 1),
    COMBO_II(25, 7, new ResourceLocation("bedrockcaseopening", "textures/dungeonDropItem/ultimate_combo2.png"), 1, 1),
    ULTIMATE_JERRY_III(26, 7, new ResourceLocation("bedrockcaseopening", "textures/dungeonDropItem/ultimate_jerry3.png"), 1, 1),
    BANK_III(27, 7, new ResourceLocation("bedrockcaseopening", "textures/dungeonDropItem/ultimate_bank3.png"), 1, 1),
    NO_PAIN_NO_GAIN_II(28, 7, new ResourceLocation("bedrockcaseopening", "textures/dungeonDropItem/ultimate_no_pain_no_gain2.png"), 1, 1),
    INFINITE_QUIVER_VII(29, 7, new ResourceLocation("bedrockcaseopening", "textures/dungeonDropItem/infinite_quiver7.png"), 1, 1),
    FEATHER_FALLING_VII(30, 7, new ResourceLocation("bedrockcaseopening", "textures/dungeonDropItem/feather_falling7.png"), 1, 1),
    REJUVENATE_III(31, 7, new ResourceLocation("bedrockcaseopening", "textures/dungeonDropItem/rejuvenate3.png"), 1, 1);


	private final ResourceLocation image;
    private final int index;
    private final int rarity;
    private final int frameCount;
    private final int frameTick;
    
    BedrockFloorVII(int index, int rarity, ResourceLocation image, int frameCount, int frameTick) {
        this.index = index;
        this.rarity = rarity;
        this.image = image;
        this.frameCount = frameCount;
        this.frameTick = frameTick;
    }
    
    BedrockFloorVII(int index, int rarity) {
        this(index, rarity, null, 1, 1);
    }

    public int getIndex() {
        return index;
    }

    public DropRarity getRarity() {
        return DropRarity.fromIndex(rarity);
    }
    
    public int getRarityIndex() {
    	return rarity;
    }
    
    public ResourceLocation getImage() {
        return image;
    }
    
    public int getFrameCount() {
    	return frameCount;
    }
    
    public int getFrameTick() {
    	return frameTick;
    }
}
