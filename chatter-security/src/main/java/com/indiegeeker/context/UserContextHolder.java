package com.indiegeeker.context;

import lombok.extern.slf4j.Slf4j;

/**
 * 用户上下文持有者
 * 使用 ThreadLocal 在当前线程中保存和获取用户上下文信息
 * 
 * Author: wen
 * Date: 2025/6/30
 */
@Slf4j
public class UserContextHolder {

    private static final ThreadLocal<UserContext> CONTEXT_HOLDER = new ThreadLocal<>();

    /**
     * 设置用户上下文
     */
    public static void setContext(UserContext userContext) {
        if (userContext != null) {
            log.debug("设置用户上下文: userId={}, username={}", 
                     userContext.getUserId(), userContext.getUsername());
        }
        CONTEXT_HOLDER.set(userContext);
    }

    /**
     * 获取用户上下文
     */
    public static UserContext getContext() {
        return CONTEXT_HOLDER.get();
    }

    /**
     * 获取当前用户ID
     */
    public static String getCurrentUserId() {
        UserContext context = getContext();
        return context != null ? context.getUserId() : null;
    }

    /**
     * 获取当前用户名
     */
    public static String getCurrentUsername() {
        UserContext context = getContext();
        return context != null ? context.getUsername() : null;
    }

    /**
     * 获取当前用户昵称
     */
    public static String getCurrentNickname() {
        UserContext context = getContext();
        return context != null ? context.getNickname() : null;
    }

    /**
     * 获取当前租户ID
     */
    public static String getCurrentTenantId() {
        UserContext context = getContext();
        return context != null ? context.getTenantId() : null;
    }

    /**
     * 检查当前用户是否有指定角色
     */
    public static boolean hasRole(String role) {
        UserContext context = getContext();
        return context != null && context.hasRole(role);
    }

    /**
     * 检查当前用户是否有指定权限
     */
    public static boolean hasPermission(String permission) {
        UserContext context = getContext();
        return context != null && context.hasPermission(permission);
    }

    /**
     * 检查是否已登录（有用户上下文）
     */
    public static boolean isAuthenticated() {
        UserContext context = getContext();
        return context != null && context.getUserId() != null;
    }

    /**
     * 清除用户上下文
     * 重要：请求处理完成后必须调用此方法，防止内存泄漏
     */
    public static void clear() {
        UserContext context = CONTEXT_HOLDER.get();
        if (context != null) {
            log.debug("清除用户上下文: userId={}, username={}", 
                     context.getUserId(), context.getUsername());
        }
        CONTEXT_HOLDER.remove();
    }

    /**
     * 复制用户上下文到新线程
     * 在异步处理时使用
     */
    public static UserContext copyContext() {
        UserContext context = getContext();
        if (context == null) {
            return null;
        }
        
        // 创建副本，避免线程间数据污染
        return UserContext.builder()
                .userId(context.getUserId())
                .username(context.getUsername())
                .nickname(context.getNickname())
                .mobile(context.getMobile())
                .roles(context.getRoles())
                .permissions(context.getPermissions())
                .tenantId(context.getTenantId())
                .clientType(context.getClientType())
                .requestIp(context.getRequestIp())
                .userAgent(context.getUserAgent())
                .issuedAt(context.getIssuedAt())
                .expiresAt(context.getExpiresAt())
                .extra(context.getExtra())
                .build();
    }

    /**
     * 在指定的用户上下文中执行操作
     * 自动管理上下文的设置和清理
     */
    public static <T> T executeWithContext(UserContext userContext, java.util.function.Supplier<T> supplier) {
        UserContext previousContext = getContext();
        try {
            setContext(userContext);
            return supplier.get();
        } finally {
            setContext(previousContext);
        }
    }

    /**
     * 在指定的用户上下文中执行操作（无返回值）
     */
    public static void executeWithContext(UserContext userContext, Runnable runnable) {
        executeWithContext(userContext, () -> {
            runnable.run();
            return null;
        });
    }
} 