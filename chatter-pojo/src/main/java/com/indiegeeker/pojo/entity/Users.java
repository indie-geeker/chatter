package com.indiegeeker.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Users对象",description = "用户表")
public class Users implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 微信号
     */
    @Parameter(description = "微信号")
    @TableField("wechat_num")
    private String wechatNum;

    /**
     * 微信号二维码
     */
    @Parameter(description = "微信号二维码")
    @TableField("wechat_num_img")
    private String wechatNumImg;

    /**
     * 手机号
     */
    @TableField("mobile")
    @Parameter(description = "手机号")
    private String mobile;

    /**
     * 昵称
     */
    @Parameter(description = "昵称")
    @TableField("nickname")
    private String nickname;

    /**
     * 真实姓名
     */
    @TableField("real_name")
    @Parameter(description = "真实姓名")
    private String realName;

    /**
     * 性别，1:男 0:女 2:保密
     */
    @TableField("sex")
    @Parameter(description = "性别，1:男 0:女 2:保密")
    private Integer sex;

    /**
     * 用户头像
     */
    @TableField("face")
    @Parameter(description = "用户头像")
    private String face;

    /**
     * 邮箱
     */
    @TableField("email")
    @Parameter(description = "邮箱")
    private String email;

    /**
     * 生日
     */
    @Parameter(description = "生日")
    @TableField("birthday")
    private LocalDate birthday;

    /**
     * 国家
     */
    @TableField("country")
    @Parameter(description = "国家")
    private String country;

    /**
     * 省份
     */
    @Parameter(description = "省份")
    @TableField("province")
    private String province;

    /**
     * 城市
     */
    @TableField("city")
    @Parameter(description = "城市")
    private String city;

    /**
     * 区县
     */
    @Parameter(description = "区县")
    @TableField("district")
    private String district;

    /**
     * 聊天背景
     */
    @TableField("chat_bg")
    @Parameter(description = "聊天背景")
    private String chatBg;

    /**
     * 朋友圈背景图
     */
    @Parameter(description = "朋友圈背景图")
    @TableField("friend_circle_bg")
    private String friendCircleBg;

    /**
     * 我的一句话签名
     */
    @TableField("signature")
    @Parameter(description = "我的一句话签名")
    private String signature;

    /**
     * 创建时间
     */
    @Parameter(description = "创建时间")
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    @Parameter(description = "更新时间")
    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;
}
