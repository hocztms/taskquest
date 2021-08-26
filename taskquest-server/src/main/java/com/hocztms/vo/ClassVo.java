package com.hocztms.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "班级类")
public class ClassVo {

    @ApiModelProperty(value = "班级id", required = true,example = "0")
    @NotNull(message = "班级id不能为空")
    private Long classId;


    @ApiModelProperty(value = "学院id", required = true,example = "0")
    @NotNull(message = "学院id不能为空")
    private Long collegeId;

    @ApiModelProperty(value = "班级名称", required = true)
    @Size(max = 10, message = "最长不超过10")
    @NotBlank(message = "班级名不能为空")
    private String className;
}
