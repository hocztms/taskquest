package com.hocztms.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

@Data
@NoArgsConstructor
@ApiModel(value = "用户校园认证类")
public class UserAuthVo {

    @ApiModelProperty(value = "学号", required = true, example = "0")
    @NotNull(message = "studentId不能为空")
    private Long studentId;

    @ApiModelProperty(value = "密码", required = true)
    @Size(max = 20, message = "最长不超过20位")
    @NotBlank(message = "password不能为空")
    private String password;
}
