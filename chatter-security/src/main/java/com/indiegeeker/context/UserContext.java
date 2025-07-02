package com.indiegeeker.context;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * 用户上下文信息
 * 在微服务间传递的用户信息载体
 * 
 * Author: wen
 * Date: 2025/6/30
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserContext implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 用户角色列表
     */
    private List<String> roles;

    /**
     * 用户权限列表
     */
    private Set<String> permissions;

    /**
     * 租户ID（多租户场景）
     */
    private String tenantId;

    /**
     * 客户端类型（web、mobile、mini-program等）
     */
    private String clientType;

    /**
     * 请求IP
     */
    private String requestIp;

    /**
     * 请求来源
     */
    private String userAgent;

    /**
     * Token签发时间
     */
    private LocalDateTime issuedAt;

    /**
     * Token过期时间
     */
    private LocalDateTime expiresAt;

    /**
     * 扩展信息
     */
    private Object extra;

    /**
     * 检查是否有指定角色
     */
    public boolean hasRole(String role) {
        return roles != null && roles.contains(role);
    }

    /**
     * 检查是否有任一角色
     */
    public boolean hasAnyRole(String... roles) {
        if (this.roles == null || roles == null) {
            return false;
        }
        for (String role : roles) {
            if (this.roles.contains(role)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查是否有指定权限
     */
    public boolean hasPermission(String permission) {
        return permissions != null && permissions.contains(permission);
    }

    /**
     * 检查是否有任一权限
     */
    public boolean hasAnyPermission(String... permissions) {
        if (this.permissions == null || permissions == null) {
            return false;
        }
        for (String permission : permissions) {
            if (this.permissions.contains(permission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查token是否过期
     */
    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }
} 