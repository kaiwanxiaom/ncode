package com.ncode.util;

public class JedisUtil {
    private static String SPLIT = ":";
    private static String BLZ_LIKE = "LIKE";
    private static String BLZ_DISLIKE = "DISLIKE";

    public static String getLikeKey(int entityType, int entityId) {
        return BLZ_LIKE + SPLIT + String.valueOf(entityType) + SPLIT + String.valueOf(entityId);
    }

    public static String getDisLikeKey(int entityType, int entityId) {
        return BLZ_DISLIKE + SPLIT + String.valueOf(entityType) + SPLIT + String.valueOf(entityId);
    }
}
