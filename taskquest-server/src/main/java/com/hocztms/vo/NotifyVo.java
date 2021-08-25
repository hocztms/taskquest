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
@ApiModel(value = "任务通知类")
public class NotifyVo {

    @ApiModelProperty(value = "任务id", required = true,example = "0")
    @NotNull(message = "taskId不能为空")
    private Long taskId;


    @ApiModelProperty(value = "通知内容", required = true)
    @Size(max = 200,message = "最长不能超过200字")
    @NotBlank(message = "content不能为空")
    private String content;
}
