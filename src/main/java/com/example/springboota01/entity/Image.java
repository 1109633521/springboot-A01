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
 * @since 2022-03-25
 */
@Getter
@Setter
  @TableName("image")
public class Image implements Serializable {

    private static final long serialVersionUID = 1L;

      /**
     * 主键
     */
        @TableId(value = "image_id", type = IdType.AUTO)
      private Integer imageId;

      /**
     * 图片地址解析
     */
      private String url;

      /**
     * 事件索引
     */
      private Integer confirmationId;

      /**
     * 创建时间
     */
      private LocalDateTime createTime;


    public Image(Integer confirmationId, String url) {
        this.confirmationId = confirmationId;
        this.url = url;
    }
}
