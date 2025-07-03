package com.indiegeeker.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author wen
 * @since 2025-06-30 
 */
@Getter
@Setter
@ToString
@TableName("users")
@Accessors(chain = true)
@Schema(description = "用户信息实体")
public class Users implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "用户ID", example = "1001")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 微信号
     */
    @Schema(description = "微信号", example = "wx_123456")
    @TableField("wechat_num")
    private String wechatNum;

    /**
     * 微信号二维码
     */
    @Schema(description = "微信号二维码URL")
    @TableField("wechat_num_img")
    private String wechatNumImg;

    /**
     * 手机号
     */
    @TableField("mobile")
    @Schema(description = "手机号", example = "13800138000")
    private String mobile;

    /**
     * 昵称
     */
    @Schema(description = "用户昵称", example = "张三")
    @TableField("nickname")
    private String nickname;

    /**
     * 真实姓名
     */
    @TableField("real_name")
    @Schema(description = "真实姓名", example = "张三丰")
    private String realName;

    /**
     * 性别，1:男 0:女 2:保密
     */
    @TableField("sex")
    @Schema(description = "性别", example = "1", allowableValues = {"0", "1", "2"})
    private Integer sex;

    /**
     * 用户头像
     */
    @TableField("face")
    @Schema(description = "用户头像URL")
    private String face;

    /**
     * 邮箱
     */
    @TableField("email")
    @Schema(description = "邮箱地址", example = "user@example.com")
    private String email;

    /**
     * 生日
     */
    @Schema(description = "生日", example = "1990-01-01")
    @TableField("birthday")
    private LocalDate birthday;

    /**
     * 国家
     */
    @TableField("country")
    @Schema(description = "国家", example = "中国")
    private String country;

    /**
     * 省份
     */
    @Schema(description = "省份", example = "北京市")
    @TableField("province")
    private String province;

    /**
     * 城市
     */
    @TableField("city")
    @Schema(description = "城市", example = "北京市")
    private String city;

    /**
     * 区县
     */
    @Schema(description = "区县", example = "朝阳区")
    @TableField("district")
    private String district;

    /**
     * 聊天背景
     */
    @TableField("chat_bg")
    @Schema(description = "聊天背景图片URL")
    private String chatBg;

    /**
     * 朋友圈背景图
     */
    @Schema(description = "朋友圈背景图URL")
    @TableField("friend_circle_bg")
    private String friendCircleBg;

    /**
     * 我的一句话签名
     */
    @TableField("signature")
    @Schema(description = "个性签名", example = "今天也要加油哦！")
    private String signature;

    /**
     * 创建时间
     */
    @Schema(description = "账户创建时间")
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    @Schema(description = "最后更新时间")
    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;
}
