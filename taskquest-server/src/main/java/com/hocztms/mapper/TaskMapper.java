package com.hocztms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hocztms.dto.TaskDto;
import com.hocztms.entity.TaskEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

@Mapper
public interface TaskMapper extends BaseMapper<TaskEntity> {


    @Select("select * from tb_task where task_id = #{taskId}")
    TaskDto selectTaskDtoByTaskId(Long taskId);

    @Select("select * from tb_task where college_id = #{collegeId} and status = #{status} order by task_id desc")
    List<TaskDto> selectTaskDtoListByCollegeId(@Param("collegeId") Long collegeId,@Param("status") Integer status,IPage page);

    @Select("select * from tb_task where college_id = #{collegeId} and deadline > #{deadline} and status = 0")
    List<TaskDto> selectTaskHotPointList(@Param("collegeId") Long collegeId, @Param("deadline")Date date, IPage page);

    //匹配度搜索
    @Select("SELECT * FROM tb_task WHERE status = 0 AND college_id = #{collegeId} AND MATCH (task_name) AGAINST ( #{keyword} IN NATURAL LANGUAGE MODE)")
    List<TaskDto> selectTaskDtoByKeyWord(@Param("keyword") String keyWord,@Param("collegeId") Long collegeId);
}
