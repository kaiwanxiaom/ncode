package com.ncode.util;

import com.alibaba.fastjson.JSONObject;
import com.ncode.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.BinaryClient;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Tuple;

import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class JedisAdapter implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(JedisAdapter.class);
    private JedisPool pool;

    @Override
    public void afterPropertiesSet() throws Exception {
        pool = new JedisPool("redis://localhost:6379/10");
    }

    public List<String> brpop(int timeout, String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.brpop(timeout, key);
        } catch (Exception e) {
            logger.error("brpop error" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    public long lpush(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.lpush(key, value);
        } catch (Exception e) {
            logger.error("lpush error" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }

    public boolean sismember(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.sismember(key, value);
        } catch (Exception e) {
            logger.error("sismember error" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return false;
    }

    public long scard(String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.scard(key);
        } catch (Exception e) {
            logger.error("scard error", e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return -1;
    }

    public long sadd(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.sadd(key, value);
        } catch (Exception e) {
            logger.error("Jedis sadd 失败" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }

    public long srem(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.srem(key, value);
        } catch (Exception e) {
            logger.error("Jedis srem 失败" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }



    private static void print(int i, Object obj) {
        System.out.println(String.format("%d: %s", i, obj.toString()));
    }

    public static void main(String[] args) {
        Jedis jedis = new Jedis("redis://localhost:6379/9");
        jedis.flushDB();

        jedis.set("key1", "helloworld");
        print(1, jedis.get("key1"));
        jedis.rename("key1", "key1name");
        print(1, jedis.get("key1name"));
        jedis.setex("key2", 10, "5");

        jedis.set("pv", "100");
        jedis.incr("pv");
        jedis.incrBy("pv", 5);
        print(2, jedis.get("pv"));
        jedis.decrBy("pv", 2);
        print(2, jedis.get("pv"));

        print(2, jedis.keys("*"));


        // list 适用于关注列表、最新列表
        String listName = "list1";

        for (int i = 0; i < 10; i++) {
            jedis.rpush(listName, "a" + String.valueOf(i));
        }
        jedis.rpop(listName);
        print(3, jedis.lrange(listName, 0, 10));
        jedis.linsert(listName, BinaryClient.LIST_POSITION.AFTER, "a2", "b1");
        jedis.lrem(listName, 0, "a2");
        print(3, jedis.lrange(listName, 0, 10));
        print(3, jedis.llen(listName));
        jedis.del(listName);

        // set适用于无顺序的集合，点赞点踩，抽奖，已读，共同好友
        String setName1 = "set1", setName2 = "set2";
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            jedis.sadd(setName1, String.valueOf(random.nextInt(20)));
            jedis.sadd(setName2, String.valueOf(random.nextInt(20)));
        }

        print(4, jedis.smembers(setName1));
        print(4, jedis.smembers(setName2));
        print(4, jedis.sunion(setName1, setName2));
        print(4, jedis.sdiff(setName1, setName2));
        print(4, jedis.sinter(setName1, setName2));

        print(4, jedis.sismember(setName1, "5"));
        print(4, jedis.srandmember(setName1));

        jedis.del(setName2, setName2);

        // hashes
        String hashName1 = "hash1";

        for (int i = 0; i < 5; i++) {
            jedis.hset(hashName1, "field:" + String.valueOf(i), String.valueOf(i));
        }

        print(5, jedis.hkeys(hashName1));
        print(5, jedis.hexists(hashName1, "field:1"));
        print(5, jedis.hget(hashName1, "field:2"));
        jedis.hincrBy(hashName1, "field:2", 5);
        print(5, jedis.hgetAll(hashName1));
        print(5, jedis.hlen(hashName1));
        print(5, jedis.hmget(hashName1, "field:1", "field:2"));


        jedis.del(hashName1);

        // Sorted Sets 排行榜 优先队列
        String zname1 = "zname1";
        jedis.del(zname1);
        for (int i = 0; i < 10; i++) {
            jedis.zadd(zname1, random.nextInt(100), "member:" + String.valueOf(i));
        }

        print(6, jedis.zrevrange(zname1, 0, 5));
        for (Tuple tuple : jedis.zrangeByScoreWithScores(zname1, "60", "100")) {
            print(6, tuple.getElement() + ":" + String.valueOf(tuple.getScore()));
        }
        print(6, jedis.zcard(zname1));
        print(6, jedis.zcount(zname1, 0, 59));

        // object
        User user = new User();
        user.setHeadUrl("1.png");
        user.setSalt("salt");
        user.setName("Jee");
        user.setPassword("123");
        user.setId(1);
        jedis.set("user1", JSONObject.toJSONString(user));

        String v = jedis.get("user1");
        User user2 = JSONObject.parseObject(v, User.class);
        print(7, user2);


    }
}
