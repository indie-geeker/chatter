package com.indiegeeker.auth.controller;

import com.indiegeeker.config.SecurityExcludeConfig;
import com.indiegeeker.web.utils.PathMatcherUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 安全配置测试控制器
 * 用于测试和验证排除路径功能
 * 
 * Author: wen
 * Date: 2025/6/30
 */
@RestController
@RequestMapping("/security-test")
public class SecurityTestController {

    private final SecurityExcludeConfig securityExcludeConfig;

    public SecurityTestController(SecurityExcludeConfig securityExcludeConfig) {
        this.securityExcludeConfig = securityExcludeConfig;
    }

    /**
     * 获取所有排除路径
     */
    @GetMapping("/exclude-paths")
    public Map<String, Object> getExcludePaths() {
        Map<String, Object> result = new HashMap<>();
        result.put("allExcludePaths", securityExcludeConfig.getAllExcludePaths());
        result.put("configPaths", securityExcludeConfig.getPaths());
        result.put("totalCount", securityExcludeConfig.getAllExcludePaths().size());
        return result;
    }

    /**
     * 测试路径是否匹配排除列表
     */
    @GetMapping("/check-path")
    public Map<String, Object> checkPath(@RequestParam String path) {
        List<String> excludePaths = securityExcludeConfig.getAllExcludePaths();
        boolean isExcluded = PathMatcherUtils.isExcluded(path, excludePaths);
        
        Map<String, Object> result = new HashMap<>();
        result.put("path", path);
        result.put("isExcluded", isExcluded);
        result.put("normalizedPath", path.replaceAll("/+", "/"));
        
        // 找出匹配的模式
        String matchedPattern = null;
        for (String pattern : excludePaths) {
            if (PathMatcherUtils.match(pattern, path)) {
                matchedPattern = pattern;
                break;
            }
        }
        result.put("matchedPattern", matchedPattern);
        
        return result;
    }

    /**
     * 批量测试路径
     */
    @GetMapping("/batch-check")
    public Map<String, Boolean> batchCheckPaths() {
        String[] testPaths = {
                "/auth/login",
                "/auth/getSMSCode", 
                "/user/profile",
                "/doc.html",
                "/swagger-ui.html",
                "/test/hello",
                "/authTest/hello",
                "/public/info",
                "/dev/test",
                "/api/secret"
        };
        
        Map<String, Boolean> results = new HashMap<>();
        List<String> excludePaths = securityExcludeConfig.getAllExcludePaths();
        
        for (String path : testPaths) {
            results.put(path, PathMatcherUtils.isExcluded(path, excludePaths));
        }
        
        return results;
    }
} 