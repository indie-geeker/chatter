package com.indiegeeker.controller;

import com.indiegeeker.base.BaseJSONResult;
import com.indiegeeker.base.BaseProperties;
import com.indiegeeker.enums.ResponseStatusEnum;
import com.indiegeeker.tasks.SMSTask;
import com.indiegeeker.utils.IPUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * Desc:
 * Author: wen
 * Date: 2025/6/25
 **/
@RestController
@RequestMapping("auth")
@Slf4j
public class AuthController extends BaseProperties {
//    @Resource
//    RedisUtils redis;

    @Resource
    private SMSTask smsTask;


    @GetMapping("getSMSCode")
    public BaseJSONResult<String> getSMSCode(String mobile, HttpServletRequest request) {
        if (mobile == null || mobile.isEmpty()) {
            return BaseJSONResult.error(ResponseStatusEnum.PARAM_MISSING);
        }
        
        // 获得用户的手机号/ip
        String requestIp = IPUtil.getRequestIp(request);
        
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
}
