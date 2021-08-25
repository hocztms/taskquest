package com.hocztms.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "微信用户登入类")
public class WxUserVo {
    @ApiModelProperty(value = "code", required = true)
    @NotBlank(message = "code不能为空")
    private String code;

}