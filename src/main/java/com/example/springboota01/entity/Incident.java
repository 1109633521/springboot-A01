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
@TableName("incident")
public class Incident implements Serializable {

    private static final long serialVersionUID = 1L;

      /**
     * 主键
     */
      @TableId(value = "incident_id", type = IdType.AUTO)
      private Integer incidentId;

      /**
     * 事件类型
     */
      private String type;

      /**
     * 捕获摄像头编号
     */
      private Integer cameraId;

      /**
     * 涉事车辆编号
     */
      private Integer carId;

      /**
     * 事件发生时间
     */
      private LocalDateTime time;

    public Incident(String type, Integer cameraId, Integer carId, LocalDateTime time) {
        this.type = type;
        this.cameraId = cameraId;
        this.carId = carId;
        this.time = time;
    }

    public Incident() {
    }
}
