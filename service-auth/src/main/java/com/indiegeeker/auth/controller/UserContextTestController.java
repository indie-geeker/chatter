package com.indiegeeker.auth.controller;

import com.indiegeeker.context.application.UserContextHolder;
import com.indiegeeker.context.domain.UserContext;
import com.indiegeeker.core.base.BaseJSONResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户上下文测试控制器
 * 用于验证用户上下文功能是否正常工作
 * 
 * Author: wen
 * Date: 2025/6/30
 */
@Slf4j
@RestController
@RequestMapping("/test/user-context")
public class UserContextTestController {


    /**
     * 测试完整的用户上下文流程
     * 1. 设置上下文
     * 2. 立即获取验证
     * 3. 返回用于后续请求的header
     */
    @PostMapping("/test-full-context-flow")
    public BaseJSONResult<Map<String, Object>> testFullContextFlow(@RequestBody UserContext userContext) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 1. 设置上下文
            UserContextHolder.setContext(userContext);
            
            // 2. 立即验证
            UserContext currentContext = UserContextHolder.getContext();
            result.put("contextSet", currentContext != null);
            result.put("currentUserId", currentContext != null ? currentContext.getUserId() : null);
            
            // 3. 生成header
            String base64Header = generateBase64Header(userContext);
            result.put("headerName", "X-User-Info");
            result.put("headerValue", base64Header);
            
            log.info("完整上下文流程测试: userId={}, contextSet={}", 
                    userContext.getUserId(), currentContext != null);
            
            return BaseJSONResult.ok(result);
        } catch (Exception e) {
            log.error("完整上下文流程测试失败", e);
            return BaseJSONResult.error("测试失败: " + e.getMessage());
        }
    }

    /**
     * 测试清除用户上下文
     */
    @PostMapping("/clear-context")
    public BaseJSONResult<String> clearUserContext() {
        UserContextHolder.clear();
        log.info("清除用户上下文");
        return BaseJSONResult.ok("用户上下文清除成功");
    }

    /**
     * 测试异步上下文传递
     */
    @GetMapping("/async-test")
    public BaseJSONResult<Map<String, Object>> testAsyncContext() {
        UserContext originalContext = UserContextHolder.getContext();
        
        // 模拟异步处理
        new Thread(() -> {
            try {
                // 复制上下文到新线程
                UserContext copiedContext = UserContextHolder.copyContext();
                UserContextHolder.setContext(copiedContext);
                
                log.info("异步线程中的用户上下文: userId={}", 
                        UserContextHolder.getCurrentUserId());
                
                // 模拟处理完成后清理
                UserContextHolder.clear();
            } catch (Exception e) {
                log.error("异步处理异常", e);
            }
        }).start();
        
        Map<String, Object> result = new HashMap<>();
        result.put("originalUserId", originalContext != null ? originalContext.getUserId() : null);
        result.put("message", "异步测试已启动，请查看日志");
        
        return BaseJSONResult.ok(result);
    }

    /**
     * 生成测试用的用户上下文数据
     */
    @GetMapping("/generate-test-data")
    public BaseJSONResult<Map<String, Object>> generateTestData() {
        UserContext testContext = UserContext.builder()
                .userId("test-user-001")
                .username("testuser")
                .nickname("测试用户")
                .mobile("13800138000")
                .roles(List.of("USER", "ADMIN"))
                .permissions(java.util.Set.of("read", "write", "delete"))
                .tenantId("tenant-001")
                .clientType("web")
                .requestIp("127.0.0.1")
                .userAgent("Mozilla/5.0 (Test Browser)")
                .issuedAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusHours(2))
                .build();
        
        Map<String, Object> result = new HashMap<>();
        result.put("testContext", testContext);
        result.put("base64Header", generateBase64Header(testContext));
        
        log.info("生成测试数据: {}", result);
        return BaseJSONResult.ok(result);
    }

    /**
     * 生成Base64编码的header值
     */
    private String generateBase64Header(UserContext userContext) {
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
            String json = mapper.writeValueAsString(userContext);
            return java.util.Base64.getEncoder().encodeToString(json.getBytes());
        } catch (Exception e) {
            log.error("生成Base64 header失败", e);
            return null;
        }
    }
} 