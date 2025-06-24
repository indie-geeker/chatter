package com.indiegeeker.controller;

import com.indiegeeker.base.BaseProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Desc:
 * Author: wen
 * Date: 2025/6/22
 **/
@RestController
@RequestMapping("gateway")
public class TestController extends BaseProperties {

    @GetMapping("hello")
    public Object hello(){
        return "hello gateway";
    }

    @GetMapping("setRedis")
    public void setRedis(String key,String value){
        redis.set(key,value);
    }

    @GetMapping("getRedis")
    public String getRedis(String key){
        return redis.get(key);
    }
}
