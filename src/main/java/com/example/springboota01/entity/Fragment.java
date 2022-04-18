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
 * @since 2022-03-26
 */
@Getter
@Setter
  @TableName("fragment")
public class Fragment implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "fragment_id", type = IdType.AUTO)
      private Integer fragmentId;

    private Integer confirmationId;

      /**
     * 路径
     */
      private String url;

    private LocalDateTime createTime;


    public Fragment(Integer confirmationId, String url) {
        this.confirmationId = confirmationId;
        this.url = url;
    }
}
