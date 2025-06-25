package com.indiegeeker;

import com.indiegeeker.utils.SMSUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Desc: 异步发送短信
 * Author: wen
 * Date: 2025/6/25
 **/
@Component
@Slf4j
public class SMSTask {
    @Resource
    SMSUtils smsUtils;

    @Async
    public void sendSMS(String phone, String message) {
        smsUtils.sendSMS(phone, message);
    }
}
