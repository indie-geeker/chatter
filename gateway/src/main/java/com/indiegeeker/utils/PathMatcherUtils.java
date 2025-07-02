package com.indiegeeker.utils;

import org.springframework.util.AntPathMatcher;

import java.util.List;

/**
 * 路径匹配器工具类
 * 支持 Ant 风格的路径匹配，如：/user/*、/api/**、/file/*.jpg
 * 
 * Author: wen
 * Date: 2025/6/30
 */
public class PathMatcherUtils {

    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    /**
     * 检查路径是否匹配排除列表中的任意一个模式
     * 
     * @param requestPath 请求路径
     * @param excludePatterns 排除路径模式列表
     * @return true 如果匹配任意一个排除模式
     */
    public static boolean isExcluded(String requestPath, List<String> excludePatterns) {
        if (requestPath == null || excludePatterns == null || excludePatterns.isEmpty()) {
            return false;
        }

        // 标准化路径（去除多余的斜杠）
        String normalizedPath = normalizePath(requestPath);

        for (String pattern : excludePatterns) {
            if (PATH_MATCHER.match(pattern, normalizedPath)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查单个路径模式是否匹配
     * 
     * @param pattern 路径模式
     * @param path 实际路径
     * @return true 如果匹配
     */
    public static boolean match(String pattern, String path) {
        return PATH_MATCHER.match(pattern, normalizePath(path));
    }

    /**
     * 路径标准化
     * 1. 去除多余的斜杠
     * 2. 确保以 / 开头
     * 3. 移除末尾的斜杠（除了根路径）
     * 
     * @param path 原始路径
     * @return 标准化后的路径
     */
    private static String normalizePath(String path) {
        if (path == null || path.isEmpty()) {
            return "/";
        }

        // 确保以 / 开头
        if (!path.startsWith("/")) {
            path = "/" + path;
        }

        // 去除多余的斜杠
        path = path.replaceAll("/+", "/");

        // 移除末尾的斜杠（除了根路径）
        if (path.length() > 1 && path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }

        return path;
    }

    /**
     * 提取路径变量
     * 例如：模式 "/user/{id}" 匹配路径 "/user/123"，返回 {id=123}
     * 
     * @param pattern 路径模式
     * @param path 实际路径
     * @return 路径变量 Map
     */
    public static java.util.Map<String, String> extractUriTemplateVariables(String pattern, String path) {
        return PATH_MATCHER.extractUriTemplateVariables(pattern, normalizePath(path));
    }
} 