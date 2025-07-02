package com.indiegeeker.controller;

import com.indiegeeker.base.BaseJSONResult;
import com.indiegeeker.BaseProperties;
import com.indiegeeker.context.UserContext;
import com.indiegeeker.context.UserContextHolder;
import com.indiegeeker.enums.ResponseStatusEnum;
import com.indiegeeker.tasks.SMSTask;
import com.indiegeeker.utils.IPUtils;
import com.indiegeeker.utils.JWTUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 用户认证控制器
 * Author: wen
 * Date: 2025/6/25
 **/
@Tag(name = "用户认证", description = "用户注册、登录、验证码等认证相关接口")
@RestController
@RequestMapping("auth")
@Slf4j
public class AuthController extends BaseProperties {
//    @Resource
//    RedisUtils redis;

    @Resource
    private SMSTask smsTask;

    @Operation(
            summary = "获取短信验证码",
            description = "根据手机号发送6位数字验证码，60秒内只能发送一次，验证码5分钟内有效"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "操作成功"),
            @ApiResponse(responseCode = "500", description = "操作失败"),

    })
    @GetMapping("getSMSCode")
    public BaseJSONResult<String> getSMSCode(
            @Parameter(description = "手机号码", required = true, example = "13800138000")
            @RequestParam String mobile, 
            HttpServletRequest request) {
        
        if (mobile == null || mobile.isEmpty()) {
            return BaseJSONResult.error(ResponseStatusEnum.PARAM_MISSING);
        }
        
        // 获得用户的手机号/ip
        String requestIp = IPUtils.getRequestIp(request);
        
        // 限制该用户的手机号/ip在60秒内只能获得一次验证码
        String limitKey = MOBILE_SMSCODE + ":" + requestIp;
        Boolean setResult = redis.setIfAbsent(limitKey, mobile, 60, TimeUnit.SECONDS);
        
        // 如果setResult为false，说明60秒内已经请求过验证码
        if (setResult == null || !setResult) {
            log.warn("IP: {} 在60秒内重复请求短信验证码，手机号: {}", requestIp, mobile);
            return BaseJSONResult.error(ResponseStatusEnum.SMS_NEED_WAIT_ERROR);
        }
        
        log.info("IP: {} 首次请求短信验证码，手机号: {}", requestIp, mobile);

        // 生成6位验证码
        String smsCode = String.valueOf((int) (Math.random() * 900000) + 100000);
//        smsTask.sendSMS(mobile, smsCode);

        // 把验证码存入到redis中，用于后续的注册/登录的校验
        redis.set(MOBILE_SMSCODE + ":" + mobile, smsCode, 5, TimeUnit.MINUTES);
        return BaseJSONResult.ok();
    }

    @Operation(
            summary = "模拟登录接口",
            description = "模拟用户登录，生成JWT token（仅用于演示上下文传递）"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "登录成功"),
            @ApiResponse(responseCode = "500", description = "登录失败")
    })
    @PostMapping("login")
    public BaseJSONResult<Map<String, Object>> login(
            @Parameter(description = "手机号", required = true, example = "13800138000")
            @RequestParam String mobile,
            @Parameter(description = "验证码", required = true, example = "123456")
            @RequestParam String smsCode) {
        
        // 验证验证码（简化实现）
        String storedCode = redis.get(MOBILE_SMSCODE + ":" + mobile);
        if (!smsCode.equals(storedCode)) {
            return BaseJSONResult.error("验证码错误或已过期");
        }
        
        // 模拟用户信息（实际应从数据库查询）
        UserContext userContext = UserContext.builder()
                .userId("1001")
                .username("user_" + mobile)
                .nickname("张三")
                .mobile(mobile)
                .roles(Arrays.asList("USER", "VIP"))
                .permissions(new HashSet<>(Arrays.asList("READ", "WRITE")))
                .tenantId("tenant_001")
                .clientType("web")
                .issuedAt(LocalDateTime.now())
                .build();
        
        // 生成JWT token (7天过期)
        String token = JWTUtils.generateToken(userContext, 7 * 24 * 60);
        
        // 删除验证码
        redis.delKey(MOBILE_SMSCODE + ":" + mobile);
        
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userInfo", userContext);
        result.put("expireTime", LocalDateTime.now().plusDays(7));
        
        return BaseJSONResult.ok("登录成功", result);
    }

    @Operation(
            summary = "获取当前用户信息",
            description = "获取当前登录用户的详细信息（需要认证）"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "401", description = "未登录")
    })
    @GetMapping("getCurrentUser")
    public BaseJSONResult<UserContext> getCurrentUser() {
        // 从ThreadLocal中获取当前用户信息
        UserContext currentUser = UserContextHolder.getContext();
        
        if (currentUser == null) {
            return BaseJSONResult.error("用户未登录");
        }
        
        log.info("用户 {} 获取个人信息", currentUser.getUserId());
        return BaseJSONResult.ok(currentUser);
    }

    @Operation(
            summary = "更新用户昵称",
            description = "更新当前用户的昵称（需要认证，演示业务中使用用户上下文）"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "更新成功"),
            @ApiResponse(responseCode = "401", description = "未登录")
    })
    @PostMapping("updateNickname")
    public BaseJSONResult<String> updateNickname(
            @Parameter(description = "新昵称", required = true, example = "新昵称")
            @RequestParam String nickname) {
        
        // 获取当前用户ID
        String currentUserId = UserContextHolder.getCurrentUserId();
        String currentUsername = UserContextHolder.getCurrentUsername();
        
        if (currentUserId == null) {
            return BaseJSONResult.error("用户未登录");
        }
        
        // TODO: 实际业务中这里应该更新数据库
        log.info("用户 {} (ID: {}) 更新昵称为: {}", currentUsername, currentUserId, nickname);
        
        return BaseJSONResult.ok("昵称更新成功");
    }

    @Operation(
            summary = "管理员专用接口",
            description = "只有管理员角色才能访问的接口（演示角色权限检查）"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "访问成功"),
            @ApiResponse(responseCode = "403", description = "权限不足")
    })
    @GetMapping("adminOnly")
    public BaseJSONResult<String> adminOnly() {
        // 检查当前用户是否有管理员角色
        if (!UserContextHolder.hasRole("ADMIN")) {
            return BaseJSONResult.error("权限不足，需要管理员角色");
        }
        
        String currentUserId = UserContextHolder.getCurrentUserId();
        log.info("管理员用户 {} 访问管理员专用接口", currentUserId);
        
        return BaseJSONResult.ok("管理员接口访问成功");
    }

    @Operation(
            summary = "获取用户上下文调试信息",
            description = "获取当前请求的用户上下文详细信息（用于调试）"
    )
    @GetMapping("debugUserContext")
    public BaseJSONResult<Map<String, Object>> debugUserContext() {
        UserContext userContext = UserContextHolder.getContext();
        
        Map<String, Object> debugInfo = new HashMap<>();
        debugInfo.put("hasUserContext", userContext != null);
        debugInfo.put("isAuthenticated", UserContextHolder.isAuthenticated());
        
        if (userContext != null) {
            debugInfo.put("userId", userContext.getUserId());
            debugInfo.put("username", userContext.getUsername());
            debugInfo.put("nickname", userContext.getNickname());
            debugInfo.put("roles", userContext.getRoles());
            debugInfo.put("permissions", userContext.getPermissions());
            debugInfo.put("requestIp", userContext.getRequestIp());
            debugInfo.put("userAgent", userContext.getUserAgent());
            debugInfo.put("isExpired", userContext.isExpired());
        }
        
        return BaseJSONResult.ok(debugInfo);
    }
}
