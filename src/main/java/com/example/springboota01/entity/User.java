package com.example.springboota01.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author luoxu
 * @since 2022-03-13
 */
@Data
@TableName("sys_user")
@ApiModel
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

      /**
     * id
     */
      //@JsonIgnore
      @TableId(value = "id", type = IdType.AUTO)
      @ApiModelProperty(value = "id",hidden = true)
      private Integer id;

      /**
     * 用户名
     */
      @ApiModelProperty("用户名")
      private String username;

      /**
     * 密码
     */
      @ApiModelProperty("密码")
      private String password;

      /**
     * 昵称
     */
      @ApiModelProperty("用户昵称")
      private String nickname;

      /**
     * 用户身份
     */
      @ApiModelProperty("用户类型")
      private String type;

      /**
     * 电话
     */
      @ApiModelProperty(value = "电话", required=false)
      private String phone;

      /**
     * 性别
     */
      @ApiModelProperty(value = "性别", required = false)
      private String sex;

      /**
     * 邮箱
     */
      @ApiModelProperty(value = "邮箱", required = false)
      private String email;

      /**
     * 地址
     */
      @ApiModelProperty(value = "地址",required = false)
      private String address;

      /**
     * 创建时间
     */
      @ApiModelProperty(value = "创建时间",hidden = true)
      private LocalDateTime createTime;

      /**
     * 修改时间
     */
      @ApiModelProperty(value = "操作时间",required = false)
      private LocalDateTime changeTime;

      /**
     * 用户头像
     */
      @JsonIgnore
      @ApiModelProperty(value = "用户头像",hidden = true)
      private String image;
}
