package com.xiaolanshu.fitnessGuidance.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import java.util.Date;
import java.util.Map;

public class Jwtutil {
    private static final String KEY = resolveKey();

    // 接收业务数据，生成token并返回
    public static String genToken(Map<String, Object> claims) {
        return JWT.create()
                .withClaim( "claims", claims)
                .withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 12))
                .sign(Algorithm.HMAC256(KEY));
    }

    // 接收token，验证token，并返回业务数据
    public static Map<String, Object> parseToken(String token) {
        return JWT.require(Algorithm.HMAC256(KEY))
                .build()
                .verify(normalizeToken(token))
                .getClaim("claims")
                .asMap();
    }

    private static String normalizeToken(String token) {
        if (token == null) {
            return "";
        }
        String trimmed = token.trim();
        if (trimmed.regionMatches(true, 0, "Bearer ", 0, 7)) {
            return trimmed.substring(7).trim();
        }
        return trimmed;
    }

    private static String resolveKey() {
        String property = System.getProperty("xiaolanshu.jwt.secret");
        if (property != null && !property.isBlank()) {
            return property;
        }
        String env = System.getenv("XIAOLANSHU_JWT_SECRET");
        if (env != null && !env.isBlank()) {
            return env;
        }
        return "local-dev-only-change-this-secret";
    }
}
