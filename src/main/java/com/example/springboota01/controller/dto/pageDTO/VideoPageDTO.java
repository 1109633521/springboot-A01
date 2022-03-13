package com.example.springboota01.controller.dto.pageDTO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("接收视频分页查询的类")
public class VideoPageDTO {
    @ApiModelProperty(value = "页码（第几页）")
    private Integer pageNum;
    @ApiModelProperty(value = "页面大小")
    private Integer pageSize;
    @ApiModelProperty(value = "路段名",required = false)
    private String crossName = "";
}
