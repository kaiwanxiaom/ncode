package com.ncode.service;

import com.ncode.util.JedisAdapter;
import com.ncode.util.JedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LikeService {
    @Autowired
    JedisAdapter jedisAdapter;

    public boolean isLiked(int userId, int entityType, int entityId) {
        String key = JedisUtil.getLikeKey(entityType, entityId);
        return jedisAdapter.sismember(key, String.valueOf(userId));
    }

    public boolean isDisLiked(int userId, int entityType, int entityId) {
        String key = JedisUtil.getDisLikeKey(entityType, entityId);
        return jedisAdapter.sismember(key, String.valueOf(userId));
    }

    public long like(int userId, int entityType, int entityId) {
        String keyLike = JedisUtil.getLikeKey(entityType, entityId);
        jedisAdapter.sadd(keyLike, String.valueOf(userId));

        String keyDislike = JedisUtil.getDisLikeKey(entityType, entityId);
        jedisAdapter.srem(keyDislike, String.valueOf(userId));

        return jedisAdapter.scard(keyLike);
    }

    public long disLike(int userId, int entityType, int entityId) {
        String keyLike = JedisUtil.getLikeKey(entityType, entityId);
        jedisAdapter.srem(keyLike, String.valueOf(userId));

        String keyDislike = JedisUtil.getDisLikeKey(entityType, entityId);
        jedisAdapter.sadd(keyDislike, String.valueOf(userId));

        return jedisAdapter.scard(keyLike);
    }

    public long getLikeCount(int entityType, int entityId) {
        String key = JedisUtil.getLikeKey(entityType, entityId);
        return jedisAdapter.scard(key);
    }
}
