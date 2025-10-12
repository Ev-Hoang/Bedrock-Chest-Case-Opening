package com.ev.bedrockcaseopening;

public enum ItemEnum {
    NECRON_HANDLE("NECRON_HANDLE"),
    IMPLOSION_SCROLL("IMPLOSION_SCROLL"),
	SHADOW_WARP_SCROLL("SHADOW_WARP_SCROLL"),
	WITHER_SHIELD_SCROLL("WITHER_SHIELD_SCROLL"),
	AUTO_RECOMBOBULATOR("AUTO_RECOMBOBULATOR"),
	WITHER_CHESTPLATE("WITHER_CHESTPLATE"),
	WITHER_LEGGINGS("WITHER_LEGGINGS"),
	WITHER_CLOAK_SWORD("WITHER_CLOAK_SWORD"),
	WITHER_HELMET("WITHER_HELMET"),
	WITHER_BLOOD("WITHER_BLOOD"),
	FUMING_POTATO_BOOK("FUMING_POTATO_BOOK"),
	WITHER_BOOTS("WITHER_BOOTS"),
	WITHER_CATALYST("WITHER_CATALYST"),
	HOT_POTATO_BOOK("HOT_POTATO_BOOK"),
	PRECURSOR_GEAR("PRECURSOR_GEAR"),
	STORM_THE_FISH("STORM_THE_FISH"),
	MAXOR_THE_FISH("MAXOR_THE_FISH"),
	GOLDOR_THE_FISH("GOLDOR_THE_FISH");

    private final String id;

    ItemEnum(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public static boolean containsId(String id) {
        for (ItemEnum item : values()) {
            if (item.id.equalsIgnoreCase(id)) {
                return true;
            }
        }
        return false;
    }

    public static ItemEnum fromId(String id) {
        for (ItemEnum item : values()) {
            if (item.id.equalsIgnoreCase(id)) {
                return item;
            }
        }
        return null;
    }
}