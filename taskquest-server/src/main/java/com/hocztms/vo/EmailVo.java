package com.hocztms.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "邮箱类")
public class EmailVo {

    @ApiModelProperty(value = "邮箱地址", required = true)
    @Email(message = "邮箱格式不正确")
    private String email;
}
