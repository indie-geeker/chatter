package com.indiegeeker.controller;

import com.indiegeeker.tasks.SMSTask;
import com.indiegeeker.utils.SMSUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证服务测试控制器
 * Author: wen
 * Date: 2025/6/22
 **/
@Tag(name = "认证测试", description = "认证服务的测试和调试接口")
@RestController
@RequestMapping("authTest")
public class TestController {

    @Resource
    SMSUtils smsUtils;

    @Resource
    SMSTask smsTask;

    @Operation(summary = "服务健康检查", description = "检查认证服务是否正常运行")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "服务正常")
    })
    @GetMapping("hello")
    public Object hello(){
        return "hello auth";
    }

    @Operation(summary = "直接发送短信测试", description = "直接调用短信工具类发送测试短信")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "调用成功")
    })
    @GetMapping("senSms")
    public Object senSms(){
        smsUtils.sendSMS("phoneNumber","1234");
        return "send sms";
    }

    @Operation(summary = "异步任务发送短信测试", description = "通过异步任务发送测试短信")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "任务提交成功")
    })
    @GetMapping("senSmsInTask")
    public Object senSmsInTask(){
        smsTask.sendSMS("phoneNumber","4321");
        return "send sms in task";
    }
}
