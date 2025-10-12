package com.ev.bedrockcaseopening;

public enum ItemEnum {
    NECRON_HANDLE("NECRON_HANDLE"),
    IMPLOSION_SCROLL("IMPLOSION_SCROLL");

    private final String id;

    ItemEnum(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    /**
     * Kiểm tra xem có item với id này trong enum không
     */
    public static boolean containsId(String id) {
        for (ItemEnum item : values()) {
            if (item.id.equalsIgnoreCase(id)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Lấy ItemEnum từ id (nếu không có thì trả null)
     */
    public static ItemEnum fromId(String id) {
        for (ItemEnum item : values()) {
            if (item.id.equalsIgnoreCase(id)) {
                return item;
            }
        }
        return null;
    }
}