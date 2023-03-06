package com.watermelon.downcenter.test.repository;


import cn.hutool.core.util.NumberUtil;
import com.watermelon.domain.task.entity.Task;
import com.watermelon.domain.task.enums.TaskStatusEnum;
import com.watermelon.domain.task.repository.TaskRepository;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author water
 * @date 2023/2/24 1:04
 */
@Slf4j
public class TestTaskRepository implements TaskRepository {

    private final static List<Task> db = Collections.synchronizedList(new ArrayList<>()) ;

    final transient ReentrantLock lock = new ReentrantLock();

    @Override
    public boolean submitTask(Task task) {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            log.debug("submitTask；{}", task);
            db.add(task);
            return true;
        } finally {
            lock.unlock();
        }


    }

    @Override
    public List<Task> listByTask(Task query) {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return db;
        } finally {
            lock.unlock();
        }

    }

    @Override
    public boolean bindTask(Task taskToSet, Task taskToSelect) {

        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return db.stream()
                    .filter(task ->
                            taskToSelect.getInstanceId().equals(task.getInstanceId())
                                    && taskToSelect.getTaskStatus().equals(task.getTaskStatus())
                                    && NumberUtil.compare(taskToSelect.getErrorCount(), task.getErrorCount()) > 0
                                    && false == task.getSysIsDelete()
                    )
                    .findFirst()
                    .map(task -> {
                        task.setInstanceId(taskToSet.getInstanceId()).setTaskStatus(taskToSet.getTaskStatus());
                        return task;
                    }).isPresent();

        } finally {
            lock.unlock();
        }
    }


    /**
     *  select *
     *     from task_info
     *     where instance_Id == instanceId and sys_is_delete = false limit 1;
     */
    @Override
    public Task pickBindTask(String instanceId) {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return db.stream()
                    .filter(task -> task.getInstanceId().equals(instanceId)
                            && task.getSysIsDelete().equals(false)
                    )
                    .findFirst()
                    .get();
        } finally {
            lock.unlock();
        }

    }


    /**
     *  update task_info
     *     set instance_Id = "", task_status='done', file_down_url='xxx'
     *     where task_Id == taskIdToQuery and sys_is_delete = false and task_status='pending';
     */
    @Override
    public boolean releaseTaskAndDone(Task taskToSet, Task taskToSelect) {

        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            db.stream()
                    .filter(task ->
                            task.getTaskId().equals(taskToSelect.getTaskId())
                                    && task.getSysIsDelete().equals(false)
                                    && task.getTaskStatus().equals(taskToSelect.getTaskStatus())
                    )
                    .forEach(task ->
                            task.setInstanceId(taskToSet.getInstanceId())
                                    .setTaskStatus(taskToSet.getTaskStatus())
                                    .setFileDownUrl(taskToSet.getFileDownUrl())
                    );
            return true;
        } finally {
            lock.unlock();
        }

    }


    /**
     *   <pre>
     *     update task_info
     *     set instance_Id = "", task_status='ready', error_count='xxx', error_msg_list='xxx'
     *     where task_Id == taskId and sys_is_delete = false and task_status='pending';
     *   </pre>
     */
    @Override
    public boolean releaseTaskAndFail(Task taskToSet, Task taskToSelect) {

        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            db.stream()
                    .filter(task ->
                            task.getTaskId().equals(taskToSelect.getTaskId())
                                    && task.getSysIsDelete().equals(false)
                                    && task.getTaskStatus().equals(taskToSelect.getTaskStatus())
                    )
                    .forEach(task ->
                            task.setInstanceId(taskToSet.getInstanceId())
                                    .setTaskStatus(taskToSet.getTaskStatus())
                                    .setErrorCount(taskToSet.getErrorCount())
                                    .setErrorMsgList(taskToSet.getErrorMsgList())
                    );
            return true;
        } finally {
            lock.unlock();
        }

    }

    /**
     * update task_info
     * set expire_time = new_expire_time
     * where task_status='pending' and instance_id = current_instance_id and sys_is_delete = false;
     */
    @Override
    public void resetExpireTime(String instanceId, TaskStatusEnum taskStatusEnum, LocalDateTime newExpireTime) {

        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            db.stream()
                    .filter(task ->
                            task.getInstanceId().equals(instanceId)
                            && task.getSysIsDelete().equals(false)
                            && task.getTaskStatus().equals(taskStatusEnum)
                    )
                    .forEach(task -> task.setExpireTime(newExpireTime));
        } finally {
            lock.unlock();
        }
    }


    /**
     * update task_info
     * set instance_Id="" and task_status = "ready"
     * where instance_Id != "" and expire_time < now and sys_is_delete = false;
     */
    @Override
    public boolean resetTimeoutTask(TaskStatusEnum taskStatusEnum) {

        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            db.stream()
                    .filter(task ->
                            !"".equals(task.getInstanceId())
                            && task.getSysIsDelete().equals(false)
                            && task.getExpireTime().compareTo(LocalDateTime.now()) <= 0
                    )
                    .forEach(task -> task.setInstanceId("").setTaskStatus(taskStatusEnum));
        } finally {
            lock.unlock();
        }
        return true;
    }
}
