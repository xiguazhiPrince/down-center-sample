package com.watermelon.downcenter.test.service;

import com.watermelon.domain.task.entity.Task;

import java.util.List;

/**
 * @author water
 * @date 2023/2/24 14:30
 */
public interface TaskApplicationService {

    boolean submitTask();
    List<Task> listByTask(Task query);

}
