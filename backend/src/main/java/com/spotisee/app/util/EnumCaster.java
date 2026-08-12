package com.spotisee.app.util;

import com.spotisee.app.models.enums.ItemType;

public class EnumCaster {
    public static ItemType castToItemType(String itemType) {
        ItemType item;
        try {
            item = ItemType.valueOf(itemType);
        } catch (IllegalArgumentException e) {
            item = switch (itemType) {
                case "songs" -> ItemType.SONG;
                case "albums" -> ItemType.ALBUM;
                case "artists" -> ItemType.ARTIST;
                case "all" -> ItemType.COMBINED;
                default -> throw e;
            };
        }
        return item;
    }
}
