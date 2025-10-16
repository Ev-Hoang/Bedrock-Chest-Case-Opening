package com.ev.bedrockcaseopening;

public enum EnchantEnum {
	ultimate_one_for_all(1),
    ultimate_wisdom(2),
    ultimate_last_stand(2),
    ultiamte_wise(2),
    ultimate_combo(2),
    ultimate_jerry(3),
    ultimate_bank(3),
    ultiamte_no_pain_no_gain(2),
    infinite_quiver(7),
    feather_falling(7),
    rejuvenate(3);

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
