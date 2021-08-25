package com.hocztms.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.*;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "任务类")
public class TaskVo {

    @NotNull
    @ApiModelProperty(value = "任务id", required = true,example = "0")
    private Long taskId;

    @Size(max = 10,message = "不能超过10字")
    @NotBlank(message = "taskName不能为空")
    @ApiModelProperty(value = "任务名", required = true)
    private String taskName;

    @Size(max = 200,message = "不能超过200字")
    @NotBlank(message = "taskContent不能为空")
    @ApiModelProperty(value = "任务内容", required = true)
    private String taskContent;

    /*
    说明 移动：134、135、136、137、138、139、147、150、151、152、157、158、159、172、178、182、183、184、187、188、198
      * 联通：130、131、132、145、155、156、166、171、175、176、185、186、166
      * 电信：133、149、153、173、177、180、181、189、199
      总结 13开头 检查【0-9】 14开头检查 5,7,9 15开头检查 【0-3】,【5-9】 16开头检查 6 17开头检查 【1-3】,【5-8】 18检查【0-9】 19【8,9】
     */
    @ApiModelProperty(value = "电话号码",required = false)
    @Pattern(regexp = "^((13[0-9])|(14[579])|(15([0-3]|[5-9]))|(16[6])|(17([1-3]|[5-8]))|(18[0-8])|(19[89]))\\d{8}$"
            , message = "手机号格式不正确")
    private String phone;

    //数字字符串 ：
    @ApiModelProperty(value = "qq群", required = false)
    @Pattern(regexp = "^\\s*|[0-9]*$", message = "编号为数字")
    @Size(max = 20,message = "不超过20为")
    private String group;

    @Future
    @ApiModelProperty(value = "到期时间", required = true)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date deadline; // 截至日期

    @ApiModelProperty(value = "任务类型 0代表审核模式 1代表枪单模式", required = true,example = "0")
    @Range(max = 1,min = 0,message = "type只能为0 或1")
    private Integer type; //0代表审核模式 1代表枪单模式


    @ApiModelProperty(value = "人数限制 -1 为无限制", required = true,example = "0")
    @Range(min = -1,message = "不能小于-1",max = 1000)
    private Integer limit;

    @ApiModelProperty(value = "德育奖励点", required = true,example = "0")
    @Range(min = 0,message = "points要大于等于1")
    private Double points;
}
