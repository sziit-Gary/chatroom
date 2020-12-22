package cn.edu.sziit.hw.util;

import redis.clients.jedis.Jedis;

import java.util.Objects;
import java.util.Set;

/**
 * @author zhupengcheng
 * @date 2020/6/19 15:48
 * 功能：完成于redis数据库的交互工作，包括用户名、密码的保存、缓存的增加与删除等
 */
public class RedisUtils {
    /**
     * redis操作实体
     */
    private static Jedis jedis;
    /**
     * 远程服务器ip地址
     */
    private static final String HOST = "101.37.32.165";
    /**
     * 操作正确的返回值
     */
    private static final String OK = "OK";
    /**
     * 存储在线用户的集合
     */
    private static String ALIVEUSERS = "aliveUsers";

    /**
     * 建立连接
     */
    public static Jedis init() {
        jedis = new Jedis(HOST);
        jedis.select(1);
        ToolUtils.getLogger().info(ToolUtils.getRealTime() + "\t" + jedis.ping());
        return jedis;
    }

    /**
     * 验证用户
     * @param username 用户名
     * @return 0-用户不存在，1-用户存在，密码错误，2-用户存在，密码正确
     */
    public static int validUser(String username, String password) {
        if (jedis == null) {
            RedisUtils.init();
        }
        if (! isExists(username)) {
            return 0;
        }
        String recv = jedis.get(username);
        if (! Objects.equals(recv,password)) {
            return 1;
        }
        return 2;
    }

    /**
     * 验证用户名是否存在
     * @param username 用户名
     * @return
     */
    private static boolean isExists(String username) {
        if (jedis == null) {
            RedisUtils.init();
        }
        if (username == null) {
            return false;
        }
        return jedis.exists(username);
    }

    /**
     * 注册用户
     * @param username 用户名
     * @param password 密码
     * @return 0-用户已存在，1-注册失败，2-注册成功
     */
    public static int insertUser(String username, String password) {
        if (jedis == null) {
            RedisUtils.init();
        }
        if (isExists(username)) {
            return 0;
        }
        final String recv = jedis.set(username, password);
        if (OK.equals(recv)) {
            return 2;
        }
        return 1;
    }

    /**
     * 修改密码
     * @param username 用户名
     * @param password  老密码
     * @return
     */
    public static void modifyUser(String username, String password) {
        if (jedis == null) {
            RedisUtils.init();
        }
        jedis.set(username, password);
    }

    /**
     * 关闭连接
     */
    public static void close() {
        if (jedis == null) {
            return;
        }
        if (jedis.isConnected()) {
            jedis.close();
        }
    }


    /**
     * 增添在线用户缓存
     * @param username 用户名
     */
    public static long addAliveUser(String username) {
        if (jedis == null) {
            RedisUtils.init();
        }
        jedis.select(1);
        return jedis.sadd(ALIVEUSERS, username);
    }

    /**
     * 删除在线用户缓存
     * @param username 用户名
     */
    public static long delAliveUser(String username) {
        if (jedis == null) {
            RedisUtils.init();
        }
        jedis.select(1);
        return jedis.srem(ALIVEUSERS, username);
    }

    /**
     * 获得在线用户缓存
     * @return 缓存集合
     */
    public static Set getAliveUsers() {
        if (jedis == null) {
            RedisUtils.init();
        }
        jedis.select(1);
        return jedis.smembers(ALIVEUSERS);
    }

    /**
     * 验证用户是否已登录
     * @param username 用户名
     * @return true-用户已经登录，false-用户未登录
     */
    public static boolean isAlive(String username) {
        if (jedis == null) {
            RedisUtils.init();
        }
        return jedis.sismember(ALIVEUSERS, username);
    }
}
