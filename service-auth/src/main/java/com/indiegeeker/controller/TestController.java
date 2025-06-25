package com.indiegeeker.controller;

import com.indiegeeker.tasks.SMSTask;
import com.indiegeeker.utils.SMSUtils;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Desc:
 * Author: wen
 * Date: 2025/6/22
 **/
@RestController
@RequestMapping("auth")
public class TestController {

    @Resource
    SMSUtils smsUtils;

    @Resource
    SMSTask smsTask;

    @GetMapping("hello")
    public Object hello(){
        return "hello auth";
    }

    @GetMapping("senSms")
    public Object senSms(){
        smsUtils.sendSMS("phoneNumber","1234");
        return "send sms";
    }

    @GetMapping("senSmsInTask")
    public Object senSmsInTask(){
        smsTask.sendSMS("phoneNumber","4321");
        return "send sms in task";
    }
}
