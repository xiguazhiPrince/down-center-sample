package com.watermelon.downcenter.test.service.impl;

import cn.hutool.core.lang.UUID;
import cn.hutool.json.JSONUtil;
import com.watermelon.domain.task.entity.Task;
import com.watermelon.domain.task.service.TaskDomainService;
import com.watermelon.downcenter.test.handlerunit.TestExcelHandlerUnit;
import com.watermelon.downcenter.test.service.TaskApplicationService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author water
 * @date 2023/2/24 14:30
 */
@Service
public class TaskApplicationServiceImpl implements TaskApplicationService {

    private TaskDomainService taskDomainService;

    private TestExcelHandlerUnit testExcelHandlerUnit;


    public TaskApplicationServiceImpl(TaskDomainService taskDomainService, TestExcelHandlerUnit testExcelHandlerUnit) {
        this.taskDomainService = taskDomainService;
        this.testExcelHandlerUnit = testExcelHandlerUnit;
    }


    @Override
    public boolean submitTask() {
        Task task = new Task();

        task.setTaskId(UUID.fastUUID().toString(true))
                .setInstanceId(null)
                .setExpireTime(null)
                .setTaskStatus(null)
                .setErrorMsgList(new ArrayList<>())
                .setErrorCount(0)
                .setHandlerName(testExcelHandlerUnit.handlerName())
                .setRequestJson(JSONUtil.toJsonStr(new Task()))
                .setOperatorUserId("1111")
                .setFileDownUrl("")
                .setSysCreateTime(LocalDateTime.now())
                .setSysUpdateTime(LocalDateTime.now())
                .setSysCreateUserId("1111")
                .setSysUpdateUserId("1111")
                .setSysIsDelete(false)
                .setSysVersion(0)
        ;
        taskDomainService.submitTask(task);
        return false;
    }

    @Override
    public List<Task> listByTask(Task query) {
        return taskDomainService.listByTask(query);
    }
}
