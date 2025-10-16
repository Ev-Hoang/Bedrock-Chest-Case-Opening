package com.ev.bedrockcaseopening;

import net.minecraft.util.ResourceLocation;

public enum BedrockFloorVII {
    NECRON_HANDLE(1),
    IMPLOSION_SCROLL(2),
	SHADOW_WARP_SCROLL(2),
	WITHER_SHIELD_SCROLL(2),
	AUTO_RECOMBOBULATOR(3),
	ultimate_one_for_all_1(3),
	WITHER_CHESTPLATE(3),
	RECOMBOBULATOR_3000(4),
	WITHER_LEGGINGS(4),
	WITHER_CLOAK_SWORD(5),
	WITHER_HELMET(5),
	WITHER_BLOOD(5),
	FUMING_POTATO_BOOK(5),
	WITHER_BOOTS(5),
	WITHER_CATALYST(5),
	HOT_POTATO_BOOK(5),
	PRECURSOR_GEAR55(5),
	STORM_THE_FISH(6),
	MAXOR_THE_FISH(6),
	GOLDOR_THE_FISH(6),
    ultimate_wisdom_2(7),
    ultimate_last_stand_2(7),
    ultiamte_wise_2(7),
    ultimate_combo_2(7),
    ultimate_jerry_3(7),
    ultimate_bank_3(7),
    ultiamte_no_pain_no_gain_2(7),
    infinite_quiver_7(7),
    feather_falling_7(7),
    rejuvenate_3(7);
    
    //
    
    //Must get frames, frametime, and texture yourself
    
//    SHADOW_WARP(2, 2, new ResourceLocation("bedrockcaseopening", "textures/dungeonDropItem/shadow_warp.png"), 1, 1),
//    WITHER_SHIELD(3, 2, new ResourceLocation("bedrockcaseopening", "textures/dungeonDropItem/wither_shield.png"), 1, 1),
//    IMPLOSION(4, 2, new ResourceLocation("bedrockcaseopening", "textures/dungeonDropItem/implosion.png"), 1, 1),
//    AUTO_RECOMBOBULATOR(5, 3, new ResourceLocation("bedrockcaseopening", "textures/dungeonDropItem/auto_recombobulator.png"), 16, 1),
//    WITHER_CHESTPLATE(6, 3, new ResourceLocation("bedrockcaseopening", "textures/dungeonDropItem/wither_chestplate.png"), 1, 1),
//    ONE_FOR_ALL_I(7, 3, new ResourceLocation("bedrockcaseopening", "textures/dungeonDropItem/ultimate_one_for_all1.png"), 1, 1),
//    RECOMBOBULATOR_3000(8, 4, new ResourceLocation("bedrockcaseopening", "textures/dungeonDropItem/recombobulator_3000.png"), 8, 2),
//    WITHER_LEGGINGS(9, 4, new ResourceLocation("bedrockcaseopening", "textures/dungeonDropItem/wither_leggings.png"), 1, 1),
//    WITHER_CLOAK_SWORD(10, 5, new ResourceLocation("bedrockcaseopening", "textures/dungeonDropItem/wither_cloak_sword.png"), 8, 2),
//    WITHER_HELMET(11, 5, new ResourceLocation("bedrockcaseopening", "textures/dungeonDropItem/wither_helmet.png"), 1, 1),
//    SOUL_EATER_I(12, 5, new ResourceLocation("bedrockcaseopening", "textures/dungeonDropItem/ultimate_soul_eater1.png"), 1, 1),
//    WITHER_BLOOD(13, 5, new ResourceLocation("bedrockcaseopening", "textures/dungeonDropItem/wither_blood.png"), 1, 1),
//    FUMING_POTATO_BOOK(14, 5, new ResourceLocation("bedrockcaseopening", "textures/dungeonDropItem/fuming_potato_book.png"), 5, 1),
//    WITHER_BOOTS(15, 5, new ResourceLocation("bedrockcaseopening", "textures/dungeonDropItem/wither_boots.png"), 1, 1),
//    WITHER_CATALYST(16, 5, new ResourceLocation("bedrockcaseopening", "textures/dungeonDropItem/wither_catalyst.png"), 1, 1),
//    PRECURSOR_GEAR(17, 5, new ResourceLocation("bedrockcaseopening", "textures/dungeonDropItem/precursor_gear.png"), 1, 1),
//    HOT_POTATO_BOOK(18, 5, new ResourceLocation("bedrockcaseopening", "textures/dungeonDropItem/hot_potato_book.png"), 6, 2),
//    STORM_THE_FISH(19, 6, new ResourceLocation("bedrockcaseopening", "textures/dungeonDropItem/storm_the_fish.png"), 1, 1),
//    MAXOR_THE_FISH(20, 6, new ResourceLocation("bedrockcaseopening", "textures/dungeonDropItem/maxor_the_fish.png"), 1, 1),
//    GOLDOR_THE_FISH(21, 6, new ResourceLocation("bedrockcaseopening", "textures/dungeonDropItem/goldor_the_fish.png"), 1, 1),
//    WISDOM_II(22, 7, new ResourceLocation("bedrockcaseopening", "textures/dungeonDropItem/ultimate_wisdom2.png"), 1, 1),
//    LAST_STAND_II(23, 7, new ResourceLocation("bedrockcaseopening", "textures/dungeonDropItem/ultimate_last_stand2.png"), 1, 1),
//    ULTIMATE_WISE_II(24, 7, new ResourceLocation("bedrockcaseopening", "textures/dungeonDropItem/ultimate_wise2.png"), 1, 1),
//    COMBO_II(25, 7, new ResourceLocation("bedrockcaseopening", "textures/dungeonDropItem/ultimate_combo2.png"), 1, 1),
//    ULTIMATE_JERRY_III(26, 7, new ResourceLocation("bedrockcaseopening", "textures/dungeonDropItem/ultimate_jerry3.png"), 1, 1),
//    BANK_III(27, 7, new ResourceLocation("bedrockcaseopening", "textures/dungeonDropItem/ultimate_bank3.png"), 1, 1),
//    NO_PAIN_NO_GAIN_II(28, 7, new ResourceLocation("bedrockcaseopening", "textures/dungeonDropItem/ultimate_no_pain_no_gain2.png"), 1, 1),
//    INFINITE_QUIVER_VII(29, 7, new ResourceLocation("bedrockcaseopening", "textures/dungeonDropItem/infinite_quiver7.png"), 1, 1),
//    FEATHER_FALLING_VII(30, 7, new ResourceLocation("bedrockcaseopening", "textures/dungeonDropItem/feather_falling7.png"), 1, 1),
//    REJUVENATE_III(31, 7, new ResourceLocation("bedrockcaseopening", "textures/dungeonDropItem/rejuvenate3.png"), 1, 1);


//	private final ResourceLocation image;
//    private final int index;
//    private final int frameCount;
//    private final int frameTick;

    private final int rarity;
    
//    BedrockFloorVII(int index, int rarity, ResourceLocation image, int frameCount, int frameTick) {
    BedrockFloorVII(int rarity) {
//        this.index = index;
        this.rarity = rarity;
//        this.image = image;
//        this.frameCount = frameCount;
//        this.frameTick = frameTick;
    }
    
    public int getIndex() {
        return this.ordinal();
    }

    public DropRarity getRarity() {
        return DropRarity.fromIndex(rarity);
    }
    
    public int getRarityIndex() {
    	return rarity;
    }
    
    public ResourceLocation getImage() {
    	return CitManager.getTextureData(this.name()).getRl();
    }
    
    public int getFrameCount() {
    	return CitManager.getTextureData(this.name()).getFrames();
    }
    
    public int getFrameTick() {
    	return CitManager.getTextureData(this.name()).getFrametime();
    }
}

