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
 * @since 2022-03-19
 */
@Getter
@Setter
  @TableName("catch")
public class Catch implements Serializable {

    private static final long serialVersionUID = 1L;

      /**
     * 主键
     */
        @TableId(value = "catch_id", type = IdType.AUTO)
      private Integer catchId;

      /**
     * 捕获摄像机
     */
      private Integer cameraId;

      /**
     * 捕获车辆
     */
      private Integer carId;

      /**
     * 捕获时间
     */
      private LocalDateTime catchTime;


}
