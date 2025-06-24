package com.indiegeeker.enums;

import lombok.Getter;

/**
 * Desc: 响应结果枚举
 * Author: wen
 * Date: 2025/6/24
 **/
@Getter
public enum ResponseStatusEnum {

    SUCCESS(200,  "操作成功"),
    FAILED(500, "操作失败"),

    // 510x - 系统错误
    SYSTEM_ERROR(5100, "系统内部错误"),
    SERVICE_UNAVAILABLE(5101, "服务暂不可用"),
    TIMEOUT(5102, "请求超时"),
    BLACK_IP(5103, "请求过于频繁，请稍后重试"),

    // 520x - 权限认证错误
    UNAUTHORIZED(5200, "未授权访问"),
    FORBIDDEN(5201, "无权限访问"),
    TOKEN_EXPIRED(5202, "登录凭证已过期"),
    TOKEN_INVALID(5203, "无效的访问令牌"),

    // 530x - 参数错误
    PARAM_MISSING(5300, "缺少必要参数"),
    PARAM_INVALID(5301, "参数格式错误"),
    PARAM_TYPE_ERROR(5302, "参数类型不匹配"),

    // 540x - 业务逻辑错误
    BUSINESS_RULE_VIOLATION(5400, "业务规则校验失败"),
    DUPLICATE_OPERATION(5401, "重复操作"),


    // 550x - 资源错误
    RESOURCE_NOT_FOUND(5500, "资源不存在"),
    RESOURCE_ALREADY_EXISTS(5501, "资源已存在"),

    ;
    public Integer code;
    public String message;


    private ResponseStatusEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
