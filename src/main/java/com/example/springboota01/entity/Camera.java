package com.example.springboota01.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author luoxu
 * @since 2022-03-11
 */
@Getter
@Setter
  @TableName("camera")
public class Camera implements Serializable {

    private static final long serialVersionUID = 1L;

      /**
     * 主键
     */
        @TableId(value = "camera_id", type = IdType.AUTO)
      private Integer cameraId;

      /**
     * 编号
     */
      private Integer cameraNumber;

      /**
     * 外键
     */
      private Integer crossId;


  public Camera(Integer cameraNumber, Integer crossId) {
    this.cameraNumber = cameraNumber;
    this.crossId = crossId;
  }
}
