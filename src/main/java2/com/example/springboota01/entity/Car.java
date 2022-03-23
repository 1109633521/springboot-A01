package com.example.springboota01.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author luoxu
 * @since 2022-03-18
 */
@Getter
@Setter
  @TableName("car")
public class Car implements Serializable {

    private static final long serialVersionUID = 1L;

      /**
     * 主键
     */
        @TableId(value = "car_id", type = IdType.AUTO)
      private Integer carId;

      /**
     * 车牌号
     */
      private String number;

      /**
     * 车型
     */
      private String type;

      /**
     * 违章次数
     */
      private Integer illegalNum;

      /**
     * 是否进入黑名单
     */
      private Boolean blacklist;

      /**
     * 颜色
     */
      private String color;

      /**
     * 违章详情链接
     */
      private String illegalUrl;

      /**
     * 驾驶情况链接
     */
      private String conditionUrl;


}
