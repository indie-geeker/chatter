package com.indiegeeker.sms;

import com.indiegeeker.base.BaseJSONResult;
import com.indiegeeker.utils.RedisUtils;
import com.indiegeeker.utils.SMSUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

/**
 * 短信验证码服务
 * Author: wen
 * Date: 2025/1/14
 */
@Service
@Slf4j
public class SMSCodeService {

} 