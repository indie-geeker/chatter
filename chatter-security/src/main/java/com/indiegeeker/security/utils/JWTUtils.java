package com.indiegeeker.security.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.indiegeeker.context.domain.UserContext;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 * 提供JWT token的生成、解析、验证等功能
 * 
 * Author: wen
 * Date: 2025/1/1
 */
@Slf4j
public class JWTUtils {

    // JWT密钥 - 在生产环境中应该从配置文件或环境变量中获取
    private static final String SECRET_KEY = "chatter-secret-key-2025-very-long-secret-for-security";
    
    // 用户信息传递的Header名称
    private static final String USER_INFO_HEADER = "X-User-Info";
    
    // JWT签名密钥
    private static final SecretKey SIGNING_KEY = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    
    // JSON序列化工具
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    
    static {
        OBJECT_MAPPER.registerModule(new JavaTimeModule());
    }

    /**
     * 安全转换为字符串列表
     */
    @SuppressWarnings("unchecked")
    private static java.util.List<String> safeGetStringList(Object obj) {
        if (obj instanceof java.util.List) {
            return (java.util.List<String>) obj;
        }
        return null;
    }

    /**
     * 生成JWT token
     * 
     * @param userContext 用户上下文信息
     * @param expireMinutes 过期时间（分钟）
     * @return JWT token字符串
     */
    public static String generateToken(UserContext userContext, long expireMinutes) {
        if (userContext == null || userContext.getUserId() == null) {
            log.error("用户上下文信息为空，无法生成JWT token");
            return null;
        }

        try {
            // 设置过期时间
            LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(expireMinutes);
            Date expirationDate = Date.from(expirationTime.atZone(ZoneId.systemDefault()).toInstant());
            
            // 更新用户上下文的过期时间
            userContext.setExpiresAt(expirationTime);
            if (userContext.getIssuedAt() == null) {
                userContext.setIssuedAt(LocalDateTime.now());
            }

            // 构建JWT token
            return Jwts.builder()
                    .subject(userContext.getUserId())
                    .issuedAt(new Date())
                    .expiration(expirationDate)
                    .claim("userId", userContext.getUserId())
                    .claim("username", userContext.getUsername())
                    .claim("nickname", userContext.getNickname())
                    .claim("mobile", userContext.getMobile())
                    .claim("roles", userContext.getRoles())
                    .claim("permissions", userContext.getPermissions())
                    .claim("tenantId", userContext.getTenantId())
                    .claim("clientType", userContext.getClientType())
                    .claim("issuedAt", userContext.getIssuedAt().toString())
                    .claim("expiresAt", userContext.getExpiresAt().toString())
                    .signWith(SIGNING_KEY)
                    .compact();
                    
        } catch (Exception e) {
            log.error("生成JWT token失败，用户ID: {}", userContext.getUserId(), e);
            return null;
        }
    }

    /**
     * 解析JWT token，返回用户上下文
     * 
     * @param token JWT token字符串
     * @return 用户上下文信息，解析失败返回null
     */
    public static UserContext parseToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            log.warn("JWT token为空");
            return null;
        }

        try {
            Claims claims = Jwts.parser()
                    .verifyWith(SIGNING_KEY)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            java.util.List<String> roles = safeGetStringList(claims.get("roles"));
            java.util.List<String> permissions = safeGetStringList(claims.get("permissions"));
            
            return UserContext.builder()
                    .userId(claims.get("userId", String.class))
                    .username(claims.get("username", String.class))
                    .nickname(claims.get("nickname", String.class))
                    .mobile(claims.get("mobile", String.class))
                    .roles(roles)
                    .permissions(permissions != null ? new java.util.HashSet<>(permissions) : null)
                    .tenantId(claims.get("tenantId", String.class))
                    .clientType(claims.get("clientType", String.class))
                    .issuedAt(LocalDateTime.parse(claims.get("issuedAt", String.class)))
                    .expiresAt(LocalDateTime.parse(claims.get("expiresAt", String.class)))
                    .build();

        } catch (ExpiredJwtException e) {
            log.warn("JWT token已过期: {}", e.getMessage());
            return null;
        } catch (UnsupportedJwtException e) {
            log.warn("不支持的JWT token: {}", e.getMessage());
            return null;
        } catch (MalformedJwtException e) {
            log.warn("JWT token格式错误: {}", e.getMessage());
            return null;
        } catch (SecurityException e) {
            log.warn("JWT token签名验证失败: {}", e.getMessage());
            return null;
        } catch (IllegalArgumentException e) {
            log.warn("JWT token参数非法: {}", e.getMessage());
            return null;
        } catch (Exception e) {
            log.error("解析JWT token时发生未知错误", e);
            return null;
        }
    }

    /**
     * 验证JWT token是否有效
     * 
     * @param token JWT token字符串
     * @return true表示有效，false表示无效
     */
    public static boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(SIGNING_KEY)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            log.debug("JWT token验证失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 生成用于微服务间传递的用户信息Header
     * 
     * @param userContext 用户上下文
     * @return Base64编码的用户信息字符串
     */
    public static String generateUserInfoHeader(UserContext userContext) {
        if (userContext == null) {
            log.warn("用户上下文为空，无法生成用户信息Header");
            return null;
        }

        try {
            // 创建简化的用户信息Map，避免传递敏感信息
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("userId", userContext.getUserId());
            userInfo.put("username", userContext.getUsername());
            userInfo.put("nickname", userContext.getNickname());
            userInfo.put("roles", userContext.getRoles());
            userInfo.put("permissions", userContext.getPermissions());
            userInfo.put("tenantId", userContext.getTenantId());
            userInfo.put("clientType", userContext.getClientType());
            userInfo.put("requestIp", userContext.getRequestIp());

            // 序列化为JSON并Base64编码
            String jsonString = OBJECT_MAPPER.writeValueAsString(userInfo);
            return Base64.getEncoder().encodeToString(jsonString.getBytes(StandardCharsets.UTF_8));

        } catch (Exception e) {
            log.error("生成用户信息Header失败，用户ID: {}", userContext.getUserId(), e);
            return null;
        }
    }

    /**
     * 解析用户信息Header
     * 
     * @param headerValue Base64编码的用户信息字符串
     * @return 用户上下文信息，解析失败返回null
     */
    public static UserContext parseUserInfoHeader(String headerValue) {
        if (headerValue == null || headerValue.trim().isEmpty()) {
            log.debug("用户信息Header为空");
            return null;
        }

        try {
            // Base64解码并反序列化
            String jsonString = new String(Base64.getDecoder().decode(headerValue), StandardCharsets.UTF_8);
            @SuppressWarnings("unchecked")
            Map<String, Object> userInfo = OBJECT_MAPPER.readValue(jsonString, Map.class);

            java.util.List<String> roles = safeGetStringList(userInfo.get("roles"));
            java.util.List<String> permissions = safeGetStringList(userInfo.get("permissions"));
            
            return UserContext.builder()
                    .userId((String) userInfo.get("userId"))
                    .username((String) userInfo.get("username"))
                    .nickname((String) userInfo.get("nickname"))
                    .roles(roles)
                    .permissions(permissions != null ? new java.util.HashSet<>(permissions) : null)
                    .tenantId((String) userInfo.get("tenantId"))
                    .clientType((String) userInfo.get("clientType"))
                    .requestIp((String) userInfo.get("requestIp"))
                    .build();

        } catch (Exception e) {
            log.error("解析用户信息Header失败", e);
            return null;
        }
    }

    /**
     * 获取用户信息Header的名称
     * 
     * @return Header名称
     */
    public static String getUserInfoHeaderName() {
        return USER_INFO_HEADER;
    }

    /**
     * 从JWT token中提取用户ID
     * 
     * @param token JWT token字符串
     * @return 用户ID，提取失败返回null
     */
    public static String extractUserId(String token) {
        UserContext userContext = parseToken(token);
        return userContext != null ? userContext.getUserId() : null;
    }

    /**
     * 检查token是否即将过期（30分钟内）
     * 
     * @param token JWT token字符串
     * @return true表示即将过期
     */
    public static boolean isTokenNearExpiry(String token) {
        UserContext userContext = parseToken(token);
        if (userContext == null || userContext.getExpiresAt() == null) {
            return true;
        }

        LocalDateTime expirationTime = userContext.getExpiresAt();
        LocalDateTime warningTime = LocalDateTime.now().plusMinutes(30);

        return expirationTime.isBefore(warningTime);
    }
} 