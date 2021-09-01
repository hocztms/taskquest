package com.hocztms.utils;

import com.hocztms.dto.TaskDto;
import com.hocztms.entity.TaskEntity;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

@Component
public class CommonUtils {

    public static TaskDto TaskEntityParseTaskDto(TaskEntity taskEntity){
        return new TaskDto(taskEntity.getTaskId(),taskEntity.getCollegeId(),taskEntity.getTaskName(),taskEntity.getTaskContent(),taskEntity.getType(),taskEntity.getNumber(),taskEntity.getNumberLimit(),taskEntity.getPoints(),taskEntity.getDeadline());
    }

    public static double getTaskZSetScore(TaskEntity taskEntity){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(taskEntity.getDeadline());

        double days = calendar.get(Calendar.DAY_OF_YEAR);
        return (double) taskEntity.getTaskId()*0.01 + (double) taskEntity.getPoints() * 0.985 +days *0.005 ;
    }

    public static double getTaskZSetScore(TaskDto taskDto){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(taskDto.getDeadline());

        double days = calendar.get(Calendar.DAY_OF_YEAR);
        return (double) taskDto.getTaskId()*0.01 + (double) taskDto.getPoints() * 0.985 +days *0.005 ;
    }
}