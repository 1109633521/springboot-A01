package com.example.springboota01.controller.dto.pageDTO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class CarPageDTO {
    @ApiModelProperty(value = "页码（第几页）")
    private Integer pageNum;
    @ApiModelProperty(value = "页面大小")
    private Integer pageSize;
    @ApiModelProperty(value = "车牌号")
    private String number;
}
