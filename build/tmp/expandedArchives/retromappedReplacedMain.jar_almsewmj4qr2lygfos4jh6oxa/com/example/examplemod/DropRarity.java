package com.example.examplemod;

public enum DropRarity {
    COMMON(7),
    FISH(6),
    EPIC(5),
    LEGENDARY(4),
    MYTHIC(3),
    DIVINE(2),
    PRAYTORNG(1);
	
	private final int index;
	
	DropRarity(int index) {
        this.index = index;
    }
	
	public int getIndex() {
        return index;
    }
	
	public static DropRarity fromIndex(int index) {
	    for (DropRarity item : values()) {
	        if (item.getIndex() == index) {
	            return item;
	        }
	    }
	    return null; 
	}
}