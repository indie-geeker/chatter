package com.indiegeeker.pojo.enums;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 性别枚举
 *
 * @author indiegeeker
 */
@Schema(description = "用户性别枚举")
public enum GenderEnum {

    @Schema(description = "女性")
    FEMALE(0, "女"),

    @Schema(description = "男性") 
    MALE(1, "男"),

    @Schema(description = "保密")
    SECRET(2, "保密");

    @Schema(description = "性别代码")
    private final Integer code;

    @Schema(description = "性别描述")
    private final String description;

    GenderEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 根据代码获取枚举
     *
     * @param code 性别代码
     * @return 性别枚举
     */
    public static GenderEnum fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (GenderEnum gender : values()) {
            if (gender.getCode().equals(code)) {
                return gender;
            }
        }
        return null;
    }

    /**
     * 检查代码是否有效
     *
     * @param code 性别代码
     * @return 是否有效
     */
    public static boolean isValid(Integer code) {
        return fromCode(code) != null;
    }
} 