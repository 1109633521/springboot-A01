package com.example.springboota01.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
  @TableName("video")
public class Video implements Serializable {

    private static final long serialVersionUID = 1L;

      /**
     * 主键
     */
      @TableId(value = "video_id", type = IdType.AUTO)
      private Integer videoId;

      /**
     * 外键
     */
      private Integer cameraId;

      /**
     * 视频时长
     */
      private LocalTime length;

      /**
     * 记录时间
     */
      private LocalDateTime time;

      /**
     * 视频地址
     */
      private String url;
      public Video(Integer cameraId, LocalTime length, String url) {
        this.cameraId = cameraId;
        this.length = length;
      }
      public Video(Integer cameraId, LocalTime length) {
        this.cameraId = cameraId;
        this.length = length;
        this.url = url;
      }
      public Video(){};
}
