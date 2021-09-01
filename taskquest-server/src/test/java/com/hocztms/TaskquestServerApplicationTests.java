package com.hocztms;

import com.alibaba.fastjson.JSONObject;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.hocztms.commons.Email;
import com.hocztms.dto.TaskDto;
import com.hocztms.dto.UserDto;
import com.hocztms.entity.TaskEntity;
import com.hocztms.entity.TaskRecords;
import com.hocztms.mapper.TaskMapper;
import com.hocztms.mapper.user.UserMapper;
import com.hocztms.mqvo.OperaLogs;
import com.hocztms.service.TaskRecordsService;
import com.hocztms.service.TaskService;
import com.hocztms.service.UserService;
import com.hocztms.service.impl.RabbitmqService;
import com.hocztms.utils.CommonUtils;
import com.hocztms.utils.RedisPageUtils;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TaskquestServerApplicationTests {

    @Autowired
    private UserService  userService;
    @Autowired
    private UserMapper userMapper;
    @Resource
    private RedisTemplate<Object,Object> redisTemplate;
    @Autowired
    private TaskService taskService;
    @Autowired
    private RedisPageUtils redisPageUtils;
    @Autowired
    private TaskRecordsService recordsService;
    @Autowired
    private RabbitmqService rabbitmqService;
    @Autowired
    private TaskMapper taskMapper;
    @Test
    void contextLoads() {
//        taskService.insertTask(new TaskEntity(0,"123",1,"123","123","123",new Date(),0,0,0,0,0));
        TaskEntity taskByTaskId = taskService.findTaskByTaskId(Long.parseLong("2"));
        System.out.println(taskByTaskId);

    }

    @Test
    void test(){
        UserDto userDtoByOpenId = userService.findUserDtoByOpenId("123456");
        System.out.println(userDtoByOpenId);
//        redisTemplate.opsForValue().set("userDto::123456",userDtoByOpenId);
        Object userDto =  redisTemplate.opsForValue().get("userDto::123456");
        System.out.println(userDto);
//
//        String userDto =  redisTemplate.opsForValue().get("1234567");
//        System.out.println(userDto.toString());
    }

    @Test
    void testCache(){
//        List<TaskDto> collegeTaskByCollegeIdPage = redisPageUtils.getCollegeTaskByCollegeIdPage(1, new Long(1));
//        System.out.println(collegeTaskByCollegeIdPage.toString());
//        List<TaskDto> tasksByCollegeId = taskService.findTasksByCollegeId(new Long(1), 0,2, 5);
//
//        System.out.println(tasksByCollegeId.toString());

//        List<TaskRecords> taskRecordsPageByTaskId = recordsService.findTaskRecordsPageByTaskId(new Long(5), new Long(1), 1);
//        System.out.println(taskRecordsPageByTaskId);

//        List<Email> emails = new ArrayList<>();
//        emails.add(new Email("123","123","123",new Date(),"123"));
//        emails.add(new Email("1234","123","123",new Date(),"123"));
//        rabbitmqService.sendEmailList(emails);

//        List<TaskDto> taskDtos = taskMapper.selectTaskDtoByKeyWord("test");
//        System.out.println(taskDtos.toString());
//        List<Map<String, Object>> taskRecordsMap = recordsService.findTaskRecordsMap(new Long(5));
//        System.out.println(taskRecordsMap.toString());
    }

    @Test
    public void test2(){
//        recordsService.findTaskApplyByTaskId(new Long(5), 1L);
//        List<TaskRecords> taskRecordsTest = recordsService.findTaskRecordsTest(5L);
//        List<TaskRecords> taskRecords = new ArrayList<>();
//        redisPageUtils.addTaskRecords(taskRecords,6L);
//        List<TaskRecords> taskRecordsById = redisPageUtils.findTaskRecordsById(5L, 1L, 0);
//
//        if (taskRecordsById.isEmpty()){
//            System.out.println("null");
//        }
//        else {
//            System.out.println(taskRecordsById.toString());
//        }

//        if (redisTemplate.opsForZSet().zCard("taskRecords-100") ==null){
//            System.out.println("ok");
//        }
//        Long a = new Long(200);
//        Long b = new Long(200);
//        Thread threadA = new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//                recordsService.findTaskRecordsList(a);
//            }
//        });
//        Thread threadB = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                recordsService.findTaskRecordsList(b);
//            }
//        });
//        threadA.start();
//        threadB.start();
//
//        TaskRecords taskRecordsById = recordsService.findTaskRecordsById(5L);
//        System.out.println(taskRecordsById);
//
//
//        try {
//            Thread.sleep(50000);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
        redisTemplate.opsForZSet().add("123","123",0);
        redisTemplate.opsForZSet().add("123","123",0);

    }

    @Test
    public void test3(){

        rabbitmqService.insertOperaLog(new OperaLogs("123","123","123","123","123","212","123","123",new Date()));
//        \private String operaModule;
//        private String operaName;
//        private String account;
//        private String authorities;
//        private String uri;
//        private String ip;
//        private String reqParam;
//        private String resParam;
//        private Date operaDate;
    }

}
