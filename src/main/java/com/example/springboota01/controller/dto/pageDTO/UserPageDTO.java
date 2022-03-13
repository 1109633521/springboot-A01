package com.example.springboota01.controller.dto.pageDTO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("接收用户分页查询的类")
public class UserPageDTO {
    @ApiModelProperty(value = "页码（第几页）")
    private Integer pageNum;
    @ApiModelProperty(value = "页面大小")
    private Integer pageSize;
    @ApiModelProperty(value = "用户名",required = false)
    private String nickname = "";
}
