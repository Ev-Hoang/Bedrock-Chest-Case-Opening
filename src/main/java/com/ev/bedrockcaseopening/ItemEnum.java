package com.ev.bedrockcaseopening;

import net.minecraft.util.ResourceLocation;

public enum ItemEnum {
    NECRON_HANDLE, //
    IMPLOSION_SCROLL, //
	SHADOW_WARP_SCROLL, //
	WITHER_SHIELD_SCROLL, //
	AUTO_RECOMBOBULATOR, //
	RECOMBOBULATOR_3000,
	WITHER_CHESTPLATE, // 
	WITHER_LEGGINGS, //
	WITHER_CLOAK, //
	WITHER_HELMET,
	WITHER_BLOOD,
	FUMING_POTATO_BOOK, //
	WITHER_BOOTS, //
	WITHER_CATALYST, //
	HOT_POTATO_BOOK, //
	PRECURSOR_GEAR,
	STORM_THE_FISH, //
	MAXOR_THE_FISH, //
	GOLDOR_THE_FISH, //
	ultimate_one_for_all_1, //
    ultimate_wisdom_2, //
    ultimate_last_stand_2, //
    ultimate_wise_2, //
    ultimate_combo_2, //
    ultimate_jerry_3, //
    ultimate_bank_3, //
    ultimate_no_pain_no_gain_2, //
    infinite_quiver_7, //
    feather_falling_7, //
    rejuvenate_3; //
	
	ResourceLocation getDefaultRl() {
		return new ResourceLocation("bedrockcaseopening", "textures/dungeonDropItem/" + this.name().toLowerCase() + ".png");
	}
}
