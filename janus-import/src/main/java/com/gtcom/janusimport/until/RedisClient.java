package com.gtcom.janusimport.until;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.ResourceBundle;
import java.util.Set;

/**
 * @ClassName: RedisClient
 * @Description:
 * @Auther: GH
 * @Date: 2018/12/13 17:21
 */
public class RedisClient {

    public static JedisPool jedisPool; // 池化管理jedis链接池






    static {
        // 读取相关的配置
        ResourceBundle resourceBundle = ResourceBundle.getBundle("redis");
        int maxActive = Integer.parseInt(resourceBundle
                .getString("redis.pool.maxActive"));
        int maxIdle = Integer.parseInt(resourceBundle
                .getString("redis.pool.maxIdle"));
        int maxWait = Integer.parseInt(resourceBundle
                .getString("redis.pool.maxWait"));

        String ip = resourceBundle.getString("redis.ip");

        int port = Integer.parseInt(resourceBundle.getString("redis.port"));

        JedisPoolConfig config = new JedisPoolConfig();


        // 设置最大连接数
        config.setMaxTotal(maxActive);
        // 设置最大空闲数
        config.setMaxIdle(maxIdle);
        // 设置超时时间
        config.setMaxWaitMillis(maxWait);
        // 初始化连接池
        jedisPool = new JedisPool(config, ip, port);
    }



    /**
     * 获得key的剩余过期时间，key不存在返回－2，未设过期时间返回－1，否则返回过期时间（单位秒）
     *
     */
    public static Long getTTL(String key) throws Exception {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.ttl(key);
        } catch (Exception e) {
            e.printStackTrace();
            return -2l;
        } finally {
            if (jedis != null)
                jedis.close();
        }
    }

    /**
     * 获得key的剩余过期时间，key不存在返回－2，未设过期时间返回－1，否则返回过期时间（单位秒）
     *
     */
    public static String get(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.get(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (jedis != null)
                jedis.close();
        }
    }

    /**
     * 向缓存中设置字符串内容，并且指定过期时间
     *
     */
    public static boolean setKeyWithTime(String key, String value, int seconds) throws Exception {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.set(key, value);
            jedis.expire(key, seconds);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (jedis != null)
                jedis.close();
        }
    }

    /**
     * 向缓存中设置字符串内容
     *
     */
    public static boolean set(String key, String value){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            Long flag =jedisPool.getResource().persist(key);
            System.out.println("redis ==" +flag);
            jedis.set(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (jedis != null)
                jedis.close();
        }
    }

    /**
     * 向有序集合中设置key-value，并且更新分值，以及有效时间
     *
     */
    public static boolean addToSortedSet(String key, double score, String member) throws Exception {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.zincrby(key, score, member);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (jedis != null)
                jedis.close();
        }
    }

    /**
     * 将key数值增1
     *
     */
    public static boolean incrKey(String key) throws Exception {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.incr(key);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (jedis != null)
                jedis.close();
        }
    }

    /**
     * 将key数值增1
     *
     */
    public static long incr(String key) throws Exception {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.incr(key);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        } finally {
            if (jedis != null)
                jedis.close();
        }
    }

    /**
     * 设置超时时间
     *
     */
    public static long expire(String key, int seconds) throws Exception {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.expire(key, seconds);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        } finally {
            if (jedis != null)
                jedis.close();
        }
    }

    /**
     * 将value加入一个有序集合keyName,并且有分数。
     *
     */
    public static boolean incrIntoSorted(String keyName, int score, String value) throws Exception {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.zadd(keyName, score, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (jedis != null)
                jedis.close();
        }
    }

    /**
     * 从一个有序集合中获得排名为start到end的value
     *
     */
    public static Set<String> getFromSorted(String keyName, long start, long end) throws Exception {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.zrevrange(keyName, start, end);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (jedis != null)
                jedis.close();
        }
    }

    /**
     * 删除有序集合中的所有数据
     *
     */
    public static boolean delSorted(String keyName) throws Exception {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            Double min = 0.0;
            Double max = 8000.0;
            Set<String> v = jedis.zrange(keyName, 0, 0);
            for(String v1 : v)
                min = jedis.zscore(keyName, v1);
            Set<String> V = jedis.zrevrange(keyName, 0, 0);
            for(String v1 : V)
                max = jedis.zscore(keyName, v1);
            jedis.zremrangeByScore(keyName, min, max);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (jedis != null)
                jedis.close();
        }
    }

    /**
     * 删除缓存中的内容，根据key
     *
     * @param key
     * @return
     */
    public static boolean del(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.del(key);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (jedis != null)
                jedis.close();
        }
    }

    /**
     * 根据pattern获得keys
     *
     * @param
     * @return
     */
    public static Set<String> getKeys(String pattern) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.keys(pattern);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (jedis != null)
                jedis.close();
        }
    }

   /* public static void main(String[] args) {

        try {
            boolean paramValue = RedisClient.setKeyWithTime("10000", "", 11);
          *//*  System.out.println(RedisClient.get("1"));
            long s = 2000;
            long s2 = 10000;

            System.out.println("百分比"+  (float)s/(float) s2);*//*

          long ss =RedisClient.incr("10000");
           // System.out.println( ss);
            System.out.println(RedisClient.get("10000"));
        }catch (Exception E){
            E.printStackTrace();
        }
    }*/
}
