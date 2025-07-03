package com.indiegeeker.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户信息数据传输对象
 *
 * @author indiegeeker
 */
@Data
@Accessors(chain = true)
@Schema(description = "用户信息响应对象")
public class UserInfoDTO {

    @Schema(description = "用户ID", example = "1001")
    private String userId;

    @Schema(description = "用户昵称", example = "张三")
    private String nickname;

    @Schema(description = "真实姓名", example = "张三丰")
    private String realName;

    @Schema(description = "手机号", example = "13800138000")
    private String mobile;

    @Schema(description = "邮箱地址", example = "user@example.com")
    private String email;

    @Schema(description = "性别", example = "1", allowableValues = {"0", "1", "2"})
    private Integer sex;

    @Schema(description = "性别描述", example = "男")
    private String sexDesc;

    @Schema(description = "用户头像URL")
    private String face;

    @Schema(description = "生日", example = "1990-01-01")
    private LocalDate birthday;

    @Schema(description = "国家", example = "中国")
    private String country;

    @Schema(description = "省份", example = "北京市")
    private String province;

    @Schema(description = "城市", example = "北京市")
    private String city;

    @Schema(description = "区县", example = "朝阳区")
    private String district;

    @Schema(description = "个性签名", example = "今天也要加油哦！")
    private String signature;

    @Schema(description = "账户创建时间")
    private LocalDateTime createdTime;
} 