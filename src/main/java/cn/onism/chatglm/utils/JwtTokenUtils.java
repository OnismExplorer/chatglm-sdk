package cn.onism.chatglm.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

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
        // 缓存Token
        String token = cache.getIfPresent(apiKey);
        if (null != token) return token;
        // 创建Token
        Algorithm algorithm = Algorithm.HMAC256(apiSecret.getBytes(StandardCharsets.UTF_8));
        Map<String, Object> payload = new HashMap<>();
        payload.put("api_key", apiKey);
        payload.put("exp", System.currentTimeMillis() + TTL);
        payload.put("timestamp", Calendar.getInstance().getTimeInMillis());
        Map<String, Object> headerClaims = new HashMap<>();
        headerClaims.put("alg", "HS256");
        headerClaims.put("sign_type", "SIGN");
        token = JWT.create().withPayload(payload).withHeader(headerClaims).sign(algorithm);
        cache.put(apiKey, token);
        return token;
    }
}
