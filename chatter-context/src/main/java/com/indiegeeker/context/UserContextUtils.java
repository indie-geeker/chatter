package com.indiegeeker.context;

import com.indiegeeker.context.application.UserContextHolder;
import com.indiegeeker.context.domain.UserContext;

/**
 * 用户上下文工具类
 * 提供便捷的API访问用户上下文相关功能
 * 
 * Author: wen
 * Date: 2025/6/30
 */
public final class UserContextUtils {

    private UserContextUtils() {
        // 工具类，禁止实例化
    }

    /**
     * 获取当前用户上下文
     */
    public static UserContext getCurrentUser() {
        return UserContextHolder.getContext();
    }

    /**
     * 获取当前用户ID
     */
    public static String getCurrentUserId() {
        return UserContextHolder.getCurrentUserId();
    }

    /**
     * 获取当前用户名
     */
    public static String getCurrentUsername() {
        return UserContextHolder.getCurrentUsername();
    }

    /**
     * 获取当前用户昵称
     */
    public static String getCurrentNickname() {
        return UserContextHolder.getCurrentNickname();
    }

    /**
     * 获取当前租户ID
     */
    public static String getCurrentTenantId() {
        return UserContextHolder.getCurrentTenantId();
    }

    /**
     * 检查当前用户是否有指定角色
     */
    public static boolean hasRole(String role) {
        return UserContextHolder.hasRole(role);
    }

    /**
     * 检查当前用户是否有指定权限
     */
    public static boolean hasPermission(String permission) {
        return UserContextHolder.hasPermission(permission);
    }

    /**
     * 检查是否已登录
     */
    public static boolean isAuthenticated() {
        return UserContextHolder.isAuthenticated();
    }

    /**
     * 在指定的用户上下文中执行操作
     */
    public static <T> T executeWithContext(UserContext userContext, java.util.function.Supplier<T> supplier) {
        return UserContextHolder.executeWithContext(userContext, supplier);
    }

    /**
     * 在指定的用户上下文中执行操作（无返回值）
     */
    public static void executeWithContext(UserContext userContext, Runnable runnable) {
        UserContextHolder.executeWithContext(userContext, runnable);
    }

    /**
     * 复制当前用户上下文到新线程
     */
    public static UserContext copyContext() {
        return UserContextHolder.copyContext();
    }
} 