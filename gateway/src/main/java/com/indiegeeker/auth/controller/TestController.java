package com.indiegeeker.auth.controller;


import com.indiegeeker.data.utils.RedisUtils;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 网关测试控制器
 * Author: wen
 * Date: 2025/6/22
 **/
@RestController
@RequestMapping("/")
public class TestController {
    @Resource
    RedisUtils redisUtils;

    @GetMapping("hello")
    public Object hello(){
        return "hello gateway";
    }

    @GetMapping("gateway/hello")
    public Object gatewayHello(){
        return "hello gateway from gateway path";
    }

    @GetMapping("setRedis")
    public void setRedis(@RequestParam String key, @RequestParam String value){
        redisUtils.set(key,value);
    }

    @GetMapping("getRedis")
    public String getRedis(@RequestParam String key){
        return redisUtils.get(key);
    }
}
