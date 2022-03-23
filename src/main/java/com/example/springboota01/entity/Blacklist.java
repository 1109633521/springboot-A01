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
 * @since 2022-03-13
 */
@Getter
@Setter
@TableName("blacklist")
public class Blacklist implements Serializable {

    private static final long serialVersionUID = 1L;

      /**
     * 主键
     */
      @TableId(value = "id", type = IdType.AUTO)
      private Integer id;

      /**
     * 黑名单车辆编号
     */
      private Integer carId;

      /**
     * 拉黑时间
     */
      private LocalDateTime time;


}
