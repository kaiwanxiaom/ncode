package com.ncode.util;

public class JedisUtil {
    private static String SPLIT = ":";
    private static String BLZ_LIKE = "LIKE";
    private static String BLZ_DISLIKE = "DISLIKE";
    private static String EVENTS_QUEUE = "EVENTS_QUEUE";
    private static String BLZ_FOLLOWER = "FOLLOWER";
    private static String BLZ_FOLLOWEE = "FOLLOWEE";
    private static String BLZ_TIMELINE = "TIMELINE";

    public static String getFolloweeKey(int entityType, int entityId) {
        return BLZ_FOLLOWEE + SPLIT + String.valueOf(entityType) + SPLIT + String.valueOf(entityId);
    }

    public static String getFollowerKey(int entityType, int entityId) {
        return BLZ_FOLLOWER + SPLIT + String.valueOf(entityType) + SPLIT + String.valueOf(entityId);
    }

    public static String getLikeKey(int entityType, int entityId) {
        return BLZ_LIKE + SPLIT + String.valueOf(entityType) + SPLIT + String.valueOf(entityId);
    }

    public static String getDisLikeKey(int entityType, int entityId) {
        return BLZ_DISLIKE + SPLIT + String.valueOf(entityType) + SPLIT + String.valueOf(entityId);
    }

    public static String getEventsQueue() {
        return EVENTS_QUEUE;
    }

    public static String getTimeline(int userId) {
        return BLZ_TIMELINE + SPLIT + String.valueOf(userId);
    }
}
