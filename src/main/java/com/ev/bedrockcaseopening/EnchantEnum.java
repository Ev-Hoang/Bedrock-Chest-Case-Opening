package com.ev.bedrockcaseopening;

public enum EnchantEnum {
    HELLFIRE_ENCHANT(1),
    CUSTOM_BOOST(2),
    SOUL_DRAIN(3);

    private final int requiredLevel;

    EnchantEnum(int requiredLevel) {
        this.requiredLevel = requiredLevel;
    }

    public int getRequiredLevel() {
        return requiredLevel;
    }

    public static boolean contains(String enchantName, String levelStr) {
        for (EnchantEnum e : values()) {
            if (e.name().equalsIgnoreCase(enchantName)) {
                try {
                    int lvl = Integer.parseInt(levelStr);
                    return lvl == e.getRequiredLevel();
                } catch (NumberFormatException ex) {
                    return false;
                }
            }
        }
        return false;
    }
}
