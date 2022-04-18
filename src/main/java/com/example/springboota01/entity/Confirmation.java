package com.example.springboota01.entity;

import com.baomidou.mybatisplus.annotation.*;

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
  @TableName("confirmation")
public class Confirmation implements Serializable {

    private static final long serialVersionUID = 1L;

      /**
     * 主键
     */
      @TableId(value = "confirmation_id", type = IdType.AUTO)
      private Integer confirmationId;

      /**
     * 涉及事件id
     */
      private Integer incidentId;

      /**
     * 确认时间
     */
      @TableField(fill = FieldFill.INSERT_UPDATE)
      private LocalDateTime confirmTime;

      /**
     * 确认信息
     */
      private String context;

      /**
     * 记录时间
     */
      private LocalDateTime createTime;

      /**
       * 处理人名字
       * */
      @TableField(fill = FieldFill.INSERT_UPDATE)
      private String userName;

      private Boolean isConfirm;

      private Boolean isDeal;

  public Confirmation(Integer incidentId) {
    this.incidentId = incidentId;
  }

  public Confirmation() {
  }
}
