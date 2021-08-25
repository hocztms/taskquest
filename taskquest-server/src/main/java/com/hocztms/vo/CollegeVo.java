package com.hocztms.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "学院类")
public class CollegeVo {

    @ApiModelProperty(value = "学院Id",required = true,example = "0")
    @NotNull(message = "collegeID 不能为空 默认为0")
    private Long collegeId;

    @ApiModelProperty(value = "学院名称", required = true)
    @Size(max = 10, message = "最长不超过10位")
    @NotBlank(message = "collegeName不能为空")
    private String collegeName;
}
