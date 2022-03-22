package com.example.springboota01.controller.dto.pageDTO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BlackPageDTO {
    @ApiModelProperty(value = "页码（第几页）")
    private Integer pageNum;
    @ApiModelProperty(value = "页面大小")
    private Integer pageSize;
    @ApiModelProperty(value = "车牌号")
    private String number;
    @ApiModelProperty(value = "起始时间-Localdatetime类,yyyy-MM-dd HH:mm:ss类型")
    private LocalDateTime beginTime;
    @ApiModelProperty(value = "终结时间-")
    private LocalDateTime endTime;
}
