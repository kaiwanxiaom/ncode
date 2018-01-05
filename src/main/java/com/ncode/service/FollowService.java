package com.ncode.service;

import com.ncode.model.EntityType;
import com.ncode.util.JedisAdapter;
import com.ncode.util.JedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class FollowService {
    @Autowired
    JedisAdapter jedisAdapter;

    public Set<String> commonFollowUsers(int userId1, int userId2) {
        Set<String> set1 = getFollowees(EntityType.ENTITY_USER, userId1, 100);
        Set<String> set2 = getFollowees(EntityType.ENTITY_USER, userId2, 100);
        Set<String> interSet = new HashSet<>();
        interSet.addAll(set1);
        interSet.retainAll(set2);

        return interSet;
    }

    public boolean followEntity(int entityType, int entityId, int userId) {
        String keyFollower = JedisUtil.getFollowerKey(entityType, entityId);
        String keyFollowee = JedisUtil.getFolloweeKey(entityType, userId);

        Jedis jedis = jedisAdapter.getJedis();
        Transaction tx = jedisAdapter.multi(jedis);
        tx.zadd(keyFollower, new Date().getTime(), String.valueOf(userId));
        tx.zadd(keyFollowee, new Date().getTime(), String.valueOf(entityId));
        List<Object> ls = jedisAdapter.excu(tx, jedis);
        return ls.size() == 2 && (long) ls.get(0) > 0 && (long) ls.get(1) > 0;
    }

    public boolean unFollowEntity(int entityType, int entityId, int userId) {
        String keyFollower = JedisUtil.getFollowerKey(entityType, entityId);
        String keyFollowee = JedisUtil.getFolloweeKey(entityType, userId);

        Jedis jedis = jedisAdapter.getJedis();
        Transaction tx = jedisAdapter.multi(jedis);
        tx.zrem(keyFollower, String.valueOf(userId));
        tx.zrem(keyFollowee, String.valueOf(entityId));
        List<Object> ls = jedisAdapter.excu(tx, jedis);
        return ls.size() == 2 && (long) ls.get(0) > 0 && (long) ls.get(1) > 0;
    }

    public Set<String> getFollowers(int entityType, int entityId, long count) {
        String key = JedisUtil.getFollowerKey(entityType, entityId);
        return jedisAdapter.zrevrange(key, 0, count);
    }

    public Set<String> getFollowees(int entityType, int entityId, long count) {
        String key = JedisUtil.getFolloweeKey(entityType, entityId);
        return jedisAdapter.zrevrange(key, 0, count);
    }

    public long getFollowerCount(int entityType, int entityId) {
        String key = JedisUtil.getFollowerKey(entityType, entityId);
        return jedisAdapter.zcard(key);
    }

    public long getFolloweeCount(int entityType, int entityId) {
        String key = JedisUtil.getFolloweeKey(entityType, entityId);
        return jedisAdapter.zcard(key);
    }

    public boolean isFollowed(int entityType, int entityId, int userId) {
        String key = JedisUtil.getFollowerKey(entityType, entityId);
        return jedisAdapter.zscore(key, String.valueOf(userId)) != null;
    }
}
