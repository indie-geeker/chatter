package com.indiegeeker.auth.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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


    @Operation(summary = "服务健康检查", description = "检查认证服务是否正常运行")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "服务正常")
    })
    @GetMapping("hello")
    public Object hello(){
        return "hello auth";
    }

}
