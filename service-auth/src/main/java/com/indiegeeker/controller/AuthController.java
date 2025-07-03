package com.indiegeeker.controller;

import com.indiegeeker.base.BaseJSONResult;
import com.indiegeeker.BaseProperties;
import com.indiegeeker.enums.ResponseStatusEnum;
import com.indiegeeker.doc.utils.ApiResponseWrapper;
import com.indiegeeker.pojo.dto.UpdateUserInfoRequest;
import com.indiegeeker.pojo.dto.UserInfoDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户认证控制器
 * 演示 chatter-doc 模块的使用
 * Author: wen
 * Date: 2025/6/25
 **/
@Tag(name = "用户认证", description = "用户注册、登录、验证码等认证相关接口")
@RestController
@RequestMapping("auth")
@Slf4j
public class AuthController extends BaseProperties {

    @Operation(
            summary = "获取短信验证码",
            description = "根据手机号发送6位数字验证码，60秒内只能发送一次，验证码5分钟内有效"
    )
    @ApiResponseWrapper.Success("验证码发送成功")
    @GetMapping("getSMSCode")
    public BaseJSONResult<String> getSMSCode(
            @Parameter(description = "手机号码", required = true, example = "13888888888")
            @RequestParam String mobile,
            HttpServletRequest request) {

        if (mobile == null || mobile.isEmpty()) {
            return BaseJSONResult.error(ResponseStatusEnum.PARAM_MISSING);
        }

        log.info("为手机号 {} 发送验证码", mobile);
        
        // todo 简化实现：直接返回成功（实际应用中需要调用短信服务）
        return BaseJSONResult.ok("验证码已发送");
    }

    @Operation(
            summary = "用户登录",
            description = "通过手机号和验证码进行用户登录，返回JWT token"
    )
    @ApiResponseWrapper.Create("登录成功")
    @PostMapping("login")
    public BaseJSONResult<Map<String, Object>> login(
            @Parameter(description = "手机号", required = true, example = "13888888888")
            @RequestParam String mobile,
            @Parameter(description = "验证码", required = true, example = "123456")
            @RequestParam String smsCode) {

        // 简化验证逻辑
        if (!"123456".equals(smsCode)) {
            return BaseJSONResult.error("验证码错误");
        }

        // 模拟生成 token 和用户信息
        Map<String, Object> result = new HashMap<>();
        result.put("token", "mock_jwt_token_" + System.currentTimeMillis());
        result.put("userId", "1001");
        result.put("mobile", mobile);
        result.put("expireTime", LocalDateTime.now().plusDays(7));

        log.info("用户 {} 登录成功", mobile);
        return BaseJSONResult.ok("登录成功", result);
    }

    @Operation(
            summary = "获取当前用户信息",
            description = "获取当前登录用户的详细信息（需要认证）"
    )
    @ApiResponseWrapper.Success("获取成功")
    @GetMapping("getCurrentUser")
    public BaseJSONResult<Map<String, Object>> getCurrentUser() {
        // 模拟用户信息
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("userId", "1001");
        userInfo.put("username", "testuser");
        userInfo.put("nickname", "测试用户");
        userInfo.put("mobile", "13888888888");
        userInfo.put("createTime", LocalDateTime.now().minusDays(30));

        log.info("获取用户信息成功");
        return BaseJSONResult.ok(userInfo);
    }

    @Operation(
            summary = "更新用户昵称",
            description = "更新当前用户的昵称"
    )
    @ApiResponseWrapper.Update("更新成功")
    @PostMapping("updateNickname")
    public BaseJSONResult<String> updateNickname(
            @Parameter(description = "新昵称", required = true, example = "新昵称")
            @RequestParam String nickname) {

        log.info("更新用户昵称为: {}", nickname);
        return BaseJSONResult.ok("昵称更新成功");
    }

    @Operation(
            summary = "用户登出",
            description = "用户退出登录，清除token"
    )
    @ApiResponseWrapper.Success("登出成功")
    @PostMapping("logout")
    public BaseJSONResult<String> logout() {
        log.info("用户登出");
        return BaseJSONResult.ok("登出成功");
    }

    @Operation(
            summary = "管理员专用接口",
            description = "只有管理员角色才能访问的接口（演示角色权限检查）"
    )
    @ApiResponseWrapper.Common
    @GetMapping("adminOnly")
    public BaseJSONResult<String> adminOnly() {
        // 简化权限检查
        log.info("管理员接口被访问");
        return BaseJSONResult.ok("管理员接口访问成功");
    }

    @Operation(
            summary = "获取用户详细信息",
            description = "获取用户详细信息，演示使用 UserInfoDTO"
    )
    @ApiResponseWrapper.Success("获取成功")
    @GetMapping("getUserInfo")
    public BaseJSONResult<UserInfoDTO> getUserInfo() {
        // 创建用户信息 DTO 示例
        UserInfoDTO userInfo = new UserInfoDTO()
                .setUserId("1001")
                .setNickname("测试用户")
                .setRealName("张三丰")
                .setMobile("13888888888")
                .setEmail("user@example.com")
                .setSex(1)
                .setSexDesc("男")
                .setBirthday(LocalDate.of(1990, 1, 1))
                .setCountry("中国")
                .setProvince("北京市")
                .setCity("北京市")
                .setDistrict("朝阳区")
                .setSignature("今天也要加油哦！")
                .setCreatedTime(LocalDateTime.now().minusDays(30));

        log.info("获取用户详细信息成功");
        return BaseJSONResult.ok(userInfo);
    }

    @Operation(
            summary = "更新用户信息",
            description = "更新用户信息，演示使用 UpdateUserInfoRequest 和验证注解"
    )
    @ApiResponseWrapper.Update("更新成功")
    @PostMapping("updateUserInfo")
    public BaseJSONResult<String> updateUserInfo(
            @Valid @RequestBody UpdateUserInfoRequest request) {

        log.info("更新用户信息: {}", request);
        
        // 这里可以添加业务逻辑，比如更新数据库等
        // userService.updateUserInfo(request);
        
        return BaseJSONResult.ok("用户信息更新成功");
    }
}
