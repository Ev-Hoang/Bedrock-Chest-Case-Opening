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
	WITHER_CLOAK(5),
	WITHER_HELMET(5),
	WITHER_BLOOD(5),
	FUMING_POTATO_BOOK(5),
	WITHER_BOOTS(5),
	WITHER_CATALYST(5),
	HOT_POTATO_BOOK(5),
	PRECURSOR_GEAR(5),
	STORM_THE_FISH(6),
	MAXOR_THE_FISH(6),
	GOLDOR_THE_FISH(6),
    ultimate_wisdom_2(7),
    ultimate_last_stand_2(7),
    ultimate_wise_2(7),
    ultimate_combo_2(7),
    ultimate_jerry_3(7),
    ultimate_bank_3(7),
    ultimate_no_pain_no_gain_2(7),
    infinite_quiver_7(7),
    feather_falling_7(7),
    rejuvenate_3(7);

    private final int rarity;
    
    BedrockFloorVII(int rarity) {
        this.rarity = rarity;
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

