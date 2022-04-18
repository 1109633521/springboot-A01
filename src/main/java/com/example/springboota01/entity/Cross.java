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
@TableName("cross")
public class Cross implements Serializable {

    private static final long serialVersionUID = 1L;

      /**
     * 路段编号
     */
        @TableId(value = "cross_id", type = IdType.AUTO)
      private Integer crossId;

      /**
     * 名称
     */
      private String name;
      private Double centerLng;
      private Double centerLat;
}
