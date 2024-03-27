package cn.onism.chatglm.utils;

import cn.hutool.json.JSONUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * JWT token工具类
 *
 * @author HeXin
 * @date 2024/03/27
 */
public class JwtTokenUtils {

    private JwtTokenUtils(){

    }

    /**
     * token 过期时间(默认10分钟)
     */
    private static final long TTL = 10 * 60 * 1000L;

    /**
     * 缓存
     */
    public static final Cache<String, String> cache = CacheBuilder
            .newBuilder()
            .expireAfterWrite(TTL - (60 * 1000L), TimeUnit.MICROSECONDS)
            .build();


    /**
     * 根据 ApiKey 获取 token 令牌
     *
     * @param apiKey    API 密钥
     * @param apiSecret API 密钥
     * @return {@link String}
     */
    public static String getToken(String apiKey, String apiSecret) {
        return token(apiKey,apiSecret);
    }

    /**
     * 根据用户id获取令牌
     *
     * @param uid       用户id
     * @param apiSecret API 密钥
     * @return {@link String}
     */
    public static String getToken(Object uid, String apiSecret) {
        // 转换为字符串
        String code;
        // 常见 UID 类型
        if (uid instanceof String || uid instanceof Long || uid instanceof Integer) {
            code = uid.toString();
        } else {
            // 其余类型转换为 Json
            code = JSONUtil.toJsonStr(uid);
        }
        return token(code,apiSecret);
    }

    /**
     * token 令 牌
     *
     * @param check     检查
     * @param apiSecret API 密钥
     * @return {@link String}
     */
    private static String token(String check,String apiSecret) {
        // 从缓存中获取 Token
        String token = cache.getIfPresent(check);
        if (StringUtils.isNotBlank(token)) {
            return token;
        }
        // 创建Token
        Algorithm algorithm = Algorithm.HMAC256(apiSecret.getBytes(StandardCharsets.UTF_8));
        Map<String, Object> payload = new HashMap<>();
        payload.put("api_key", check);
        token = cacheToken(payload, algorithm, check);
        return token;
    }

    /**
     * 缓存 token 令牌
     *
     * @param payload   有效载荷
     * @param algorithm 算法
     * @param key       键
     */
    private static String cacheToken(Map<String, Object> payload, Algorithm algorithm, String key) {
        payload.put("exp", System.currentTimeMillis() + TTL);
        payload.put("timestamp", Calendar.getInstance().getTimeInMillis());
        Map<String, Object> headerClaims = new HashMap<>();
        headerClaims.put("alg", "HS256");
        headerClaims.put("sign_type", "SIGN");
        String token = JWT.create().withPayload(payload).withHeader(headerClaims).sign(algorithm);
        cache.put(key, token);
        return token;
    }

    /**
     * 校验 Token 是否存在
     *
     * @param checkCode 校验码
     * @return boolean
     */
    public static boolean verify(Object checkCode) {
        // 转换为字符串
        String code;
        // 常见 UID 类型
        if (checkCode instanceof String || checkCode instanceof Long || checkCode instanceof Integer) {
            code = checkCode.toString();
        } else {
            // 其余类型转换为 Json
            code = JSONUtil.toJsonStr(checkCode);
        }
        return StringUtils.isNotBlank(cache.getIfPresent(EncryptUtils.encrypt(code)));
    }
}
