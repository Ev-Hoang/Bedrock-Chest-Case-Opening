package com.ev.bedrockcaseopening;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DungeonDropData {

    public enum CaseMaterial {
        WOOD,
        GOLD,
        DIAMOND,
        EMERALD,
        OBSIDIAN,
        BEDROCK
    }

    public enum Floor {
        I(1),
        II(2),
        III(3),
        IV(4),
        V(5),
        VI(6),
        VII(7),
        MI(8),
        MII(9),
        MIII(10),
        MIV(11),
        MV(12),
        MVI(13),
        MVII(14);

        public final int number;

        Floor(int num) {
            this.number = num;
        }
    }

    public static class Rule {
        public final ItemEnum item;
        public final CaseMaterial material;
        public final Floor floor;
        public final int rarity;

        public Rule(ItemEnum item, CaseMaterial material, Floor floor, int rarity) {
            this.item = item;
            this.material = material;
            this.floor = floor;
            this.rarity = rarity;
        }
    }

    private static final List<Rule> RULES = new ArrayList<>();

    static {
        //BEDROCK FLOOR VII
    	add(ItemEnum.NECRON_HANDLE, CaseMaterial.BEDROCK, Floor.VII, 1);
    	add(ItemEnum.IMPLOSION_SCROLL, CaseMaterial.BEDROCK, Floor.VII, 2);
    	add(ItemEnum.SHADOW_WARP_SCROLL, CaseMaterial.BEDROCK, Floor.VII, 2);
    	add(ItemEnum.WITHER_SHIELD_SCROLL, CaseMaterial.BEDROCK, Floor.VII, 2);
    	add(ItemEnum.AUTO_RECOMBOBULATOR, CaseMaterial.BEDROCK, Floor.VII, 3);
    	add(ItemEnum.ultimate_one_for_all_1, CaseMaterial.BEDROCK, Floor.VII, 3);
    	add(ItemEnum.WITHER_CHESTPLATE, CaseMaterial.BEDROCK, Floor.VII, 3);
    	add(ItemEnum.RECOMBOBULATOR_3000, CaseMaterial.BEDROCK, Floor.VII, 4);
    	add(ItemEnum.WITHER_LEGGINGS, CaseMaterial.BEDROCK, Floor.VII, 4);
    	add(ItemEnum.WITHER_CLOAK, CaseMaterial.BEDROCK, Floor.VII, 5);
    	add(ItemEnum.WITHER_HELMET, CaseMaterial.BEDROCK, Floor.VII, 5);
    	add(ItemEnum.WITHER_BLOOD, CaseMaterial.BEDROCK, Floor.VII, 5);
    	add(ItemEnum.FUMING_POTATO_BOOK, CaseMaterial.BEDROCK, Floor.VII, 5);
    	add(ItemEnum.WITHER_BOOTS, CaseMaterial.BEDROCK, Floor.VII, 5);
    	add(ItemEnum.WITHER_CATALYST, CaseMaterial.BEDROCK, Floor.VII, 5);
    	add(ItemEnum.HOT_POTATO_BOOK, CaseMaterial.BEDROCK, Floor.VII, 5);
    	add(ItemEnum.PRECURSOR_GEAR, CaseMaterial.BEDROCK, Floor.VII, 5);
    	add(ItemEnum.STORM_THE_FISH, CaseMaterial.BEDROCK, Floor.VII, 6);
    	add(ItemEnum.MAXOR_THE_FISH, CaseMaterial.BEDROCK, Floor.VII, 6);
    	add(ItemEnum.GOLDOR_THE_FISH, CaseMaterial.BEDROCK, Floor.VII, 6);
    	add(ItemEnum.ultimate_wisdom_2, CaseMaterial.BEDROCK, Floor.VII, 7);
    	add(ItemEnum.ultimate_last_stand_2, CaseMaterial.BEDROCK, Floor.VII, 7);
    	add(ItemEnum.ultimate_wise_2, CaseMaterial.BEDROCK, Floor.VII, 7);
    	add(ItemEnum.ultimate_combo_2, CaseMaterial.BEDROCK, Floor.VII, 7);
    	add(ItemEnum.ultimate_jerry_3, CaseMaterial.BEDROCK, Floor.VII, 7);
    	add(ItemEnum.ultimate_bank_3, CaseMaterial.BEDROCK, Floor.VII, 7);
    	add(ItemEnum.ultimate_no_pain_no_gain_2, CaseMaterial.BEDROCK, Floor.VII, 7);
    	add(ItemEnum.infinite_quiver_7, CaseMaterial.BEDROCK, Floor.VII, 7);
    	add(ItemEnum.feather_falling_7, CaseMaterial.BEDROCK, Floor.VII, 7);
    	add(ItemEnum.rejuvenate_3, CaseMaterial.BEDROCK, Floor.VII, 7);

    }

    private static void add(ItemEnum item, CaseMaterial m, Floor floor, int rarity) {
        RULES.add(new Rule(item, m, floor, rarity));
    }

    public static List<Rule> getDrops(CaseMaterial material, Floor floor) {
        return RULES.stream()
                    .filter(r -> r.material == material)
                    .filter(r -> r.floor == floor)
                    .collect(Collectors.toList());
    }
}
