package com.indiegeeker.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

/**
 * 更新用户信息请求对象
 *
 * @author indiegeeker
 */
@Data
@Schema(description = "更新用户信息请求对象")
public class UpdateUserInfoRequest {

    @Schema(description = "用户昵称", example = "张三", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "昵称不能为空")
    @Size(min = 1, max = 20, message = "昵称长度必须在1-20个字符之间")
    private String nickname;

    @Schema(description = "真实姓名", example = "张三丰")
    @Size(max = 50, message = "真实姓名长度不能超过50个字符")
    private String realName;

    @Schema(description = "邮箱地址", example = "user@example.com")
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过100个字符")
    private String email;

    @Schema(description = "性别", example = "1", allowableValues = {"0", "1", "2"})
    @Min(value = 0, message = "性别值必须为0、1或2")
    @Max(value = 2, message = "性别值必须为0、1或2")
    private Integer sex;

    @Schema(description = "生日", example = "1990-01-01")
    @Past(message = "生日必须是过去的日期")
    private LocalDate birthday;

    @Schema(description = "国家", example = "中国")
    @Size(max = 50, message = "国家名称长度不能超过50个字符")
    private String country;

    @Schema(description = "省份", example = "北京市")
    @Size(max = 50, message = "省份名称长度不能超过50个字符")
    private String province;

    @Schema(description = "城市", example = "北京市")
    @Size(max = 50, message = "城市名称长度不能超过50个字符")
    private String city;

    @Schema(description = "区县", example = "朝阳区")
    @Size(max = 50, message = "区县名称长度不能超过50个字符")
    private String district;

    @Schema(description = "个性签名", example = "今天也要加油哦！")
    @Size(max = 200, message = "个性签名长度不能超过200个字符")
    private String signature;
} 