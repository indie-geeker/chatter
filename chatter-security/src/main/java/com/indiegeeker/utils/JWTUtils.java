package com.indiegeeker.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.indiegeeker.context.UserContext;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/**
 * JWT 工具类
 * 用于生成、解析和验证 JWT token
 * 
 * Author: wen
 * Date: 2025/6/30
 */
@Slf4j
public class JWTUtils {

    private static final String SECRET_KEY = "chatter-secret-key-2025"; // 生产环境应从配置文件读取
    private static final String HEADER_USER_INFO = "X-User-Info";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.registerModule(new JavaTimeModule());
    }

    /**
     * 生成 JWT token
     */
    public static String generateToken(UserContext userContext, long expireMinutes) {
        try {
            // 创建header
            Map<String, Object> header = new HashMap<>();
            header.put("typ", "JWT");
            header.put("alg", "HS256");

            // 创建payload
            Map<String, Object> payload = new HashMap<>();
            payload.put("userId", userContext.getUserId());
            payload.put("username", userContext.getUsername());
            payload.put("nickname", userContext.getNickname());
            payload.put("mobile", userContext.getMobile());
            payload.put("roles", userContext.getRoles());
            payload.put("permissions", userContext.getPermissions());
            payload.put("tenantId", userContext.getTenantId());
            payload.put("clientType", userContext.getClientType());
            
            // 设置时间戳
            long issuedAt = System.currentTimeMillis() / 1000;
            long expiresAt = issuedAt + (expireMinutes * 60);
            payload.put("iat", issuedAt);
            payload.put("exp", expiresAt);
            payload.put("nbf", issuedAt); // not before

            // 编码header和payload
            String encodedHeader = base64UrlEncode(OBJECT_MAPPER.writeValueAsString(header));
            String encodedPayload = base64UrlEncode(OBJECT_MAPPER.writeValueAsString(payload));

            // 生成签名
            String signature = generateSignature(encodedHeader + "." + encodedPayload);

            return encodedHeader + "." + encodedPayload + "." + signature;

        } catch (Exception e) {
            log.error("生成JWT token失败", e);
            throw new RuntimeException("生成JWT token失败", e);
        }
    }

    /**
     * 解析 JWT token
     */
    public static UserContext parseToken(String token) {
        try {
            if (token == null || token.trim().isEmpty()) {
                return null;
            }

            // 移除 Bearer 前缀
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                log.warn("JWT token格式错误");
                return null;
            }

            // 验证签名
            String expectedSignature = generateSignature(parts[0] + "." + parts[1]);
            if (!expectedSignature.equals(parts[2])) {
                log.warn("JWT token签名验证失败");
                return null;
            }

            // 解析payload
            String payloadJson = base64UrlDecode(parts[1]);
            Map<String, Object> payload = OBJECT_MAPPER.readValue(payloadJson, Map.class);

            // 检查过期时间
            Long exp = getLongValue(payload, "exp");
            if (exp != null && exp < (System.currentTimeMillis() / 1000)) {
                log.warn("JWT token已过期");
                return null;
            }

            // 检查生效时间
            Long nbf = getLongValue(payload, "nbf");
            if (nbf != null && nbf > (System.currentTimeMillis() / 1000)) {
                log.warn("JWT token尚未生效");
                return null;
            }

            // 构建用户上下文
            return UserContext.builder()
                    .userId(getStringValue(payload, "userId"))
                    .username(getStringValue(payload, "username"))
                    .nickname(getStringValue(payload, "nickname"))
                    .mobile(getStringValue(payload, "mobile"))
                    .roles(getListValue(payload, "roles"))
                    .permissions(getSetValue(payload, "permissions"))
                    .tenantId(getStringValue(payload, "tenantId"))
                    .clientType(getStringValue(payload, "clientType"))
                    .issuedAt(timestampToLocalDateTime(getLongValue(payload, "iat")))
                    .expiresAt(timestampToLocalDateTime(getLongValue(payload, "exp")))
                    .build();

        } catch (Exception e) {
            log.error("解析JWT token失败", e);
            return null;
        }
    }

    /**
     * 验证 token 是否有效
     */
    public static boolean validateToken(String token) {
        UserContext userContext = parseToken(token);
        return userContext != null && !userContext.isExpired();
    }

    /**
     * 从token中提取用户ID
     */
    public static String extractUserId(String token) {
        UserContext userContext = parseToken(token);
        return userContext != null ? userContext.getUserId() : null;
    }

    /**
     * 生成用户信息header值（用于微服务间传递）
     */
    public static String generateUserInfoHeader(UserContext userContext) {
        try {
            String json = OBJECT_MAPPER.writeValueAsString(userContext);
            return Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
        } catch (JsonProcessingException e) {
            log.error("序列化用户信息失败", e);
            return null;
        }
    }

    /**
     * 解析用户信息header（从微服务请求header中获取）
     */
    public static UserContext parseUserInfoHeader(String headerValue) {
        try {
            if (headerValue == null || headerValue.trim().isEmpty()) {
                return null;
            }
            
            String json = new String(Base64.getDecoder().decode(headerValue), StandardCharsets.UTF_8);
            return OBJECT_MAPPER.readValue(json, UserContext.class);
        } catch (Exception e) {
            log.error("解析用户信息header失败", e);
            return null;
        }
    }

    /**
     * 获取用户信息header名称
     */
    public static String getUserInfoHeaderName() {
        return HEADER_USER_INFO;
    }

    // ========== 私有工具方法 ==========

    private static String generateSignature(String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] signatureBytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return base64UrlEncode(signatureBytes);
        } catch (Exception e) {
            throw new RuntimeException("生成签名失败", e);
        }
    }

    private static String base64UrlEncode(String input) {
        return base64UrlEncode(input.getBytes(StandardCharsets.UTF_8));
    }

    private static String base64UrlEncode(byte[] input) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(input);
    }

    private static String base64UrlDecode(String input) {
        byte[] decoded = Base64.getUrlDecoder().decode(input);
        return new String(decoded, StandardCharsets.UTF_8);
    }

    private static String getStringValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value != null ? value.toString() : null;
    }

    private static Long getLongValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static List<String> getListValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value instanceof List) {
            return (List<String>) value;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static Set<String> getSetValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value instanceof Set) {
            return (Set<String>) value;
        } else if (value instanceof List) {
            return new HashSet<>((List<String>) value);
        }
        return null;
    }

    private static LocalDateTime timestampToLocalDateTime(Long timestamp) {
        if (timestamp == null) {
            return null;
        }
        return LocalDateTime.ofInstant(
                java.time.Instant.ofEpochSecond(timestamp),
                ZoneId.systemDefault()
        );
    }
} 