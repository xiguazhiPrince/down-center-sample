package com.watermelon.downcenter.config;

import com.watermelon.domain.config.TaskContext;
import com.watermelon.domain.task.DefaultTaskHandlerComponent;
import com.watermelon.domain.task.TaskHandlerComponent;
import com.watermelon.domain.task.repository.TaskRepository;
import com.watermelon.domain.task.service.TaskDomainService;
import com.watermelon.domain.task.service.impl.TaskDomainServiceImpl;
import com.watermelon.domain.watchdog.DefaultWatchDogComponent;
import com.watermelon.domain.watchdog.WatchDogComponent;
import com.watermelon.downcenter.test.handlerunit.TestExcelHandlerUnit;
import com.watermelon.downcenter.test.repository.TestTaskRepository;
import com.watermelon.downcenter.test.service.impl.S3UploadFileServiceImpl;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author water
 * @date 2023/2/24 0:53
 */
@Configuration
public class DownCenterBeanConfig {

    /**
     * 需要自己实现task的Repository接口 {@link com.watermelon.domain.task.repository.TaskRepository}
     * <br>
     * 可以自定义数据库，但是字段需要和 {@link com.watermelon.domain.task.entity.Task} 的一致
     */
    @Bean
    public TaskRepository taskRepository(){
        return new TestTaskRepository();
    }

    /**
     * 自己实现文件上传类 {@link com.watermelon.domain.task.external.UploadFileService}
     */
    @Bean
    public S3UploadFileServiceImpl uploadFileService(){
        return new S3UploadFileServiceImpl();
    }

    /**
     * 注入{@link TaskDomainServiceImpl}
     */
    @Bean
    public TaskDomainService taskDomainService(TaskRepository testTaskRepository){
        return new TaskDomainServiceImpl(testTaskRepository);
    }

    /**
     * 注入组件：任务处理器 {@link TaskHandlerComponent}
     */
    @Bean
    public DefaultTaskHandlerComponent taskHandler(TaskDomainService taskDomainService,
                                                   S3UploadFileServiceImpl uploadFileService){
        return new DefaultTaskHandlerComponent(taskDomainService, uploadFileService);
    }

    /**
     * 注入组件: watchDog {@link WatchDogComponent}
     */
    @Bean
    public DefaultWatchDogComponent watchDog(TaskRepository taskRepository){
        return new DefaultWatchDogComponent(taskRepository);
    }


    /**
     * 注入自己实现生成excel数据的Selector {@link com.watermelon.domain.task.external.DataSelectorUnit}
     */
    @Bean
    public TestExcelHandlerUnit testExcelHandlerUnit(){
        return new TestExcelHandlerUnit();
    }

    /**
     * 可以自定义一些配置
     */
    @Bean
    @ConfigurationProperties(prefix = "down-center", ignoreUnknownFields = true)
    public DownSettingConfig downConfig(){
        return new DownSettingConfig();
    }


    /**
     * 启动两个组件
     */
    @PostConstruct
    public void init(){
        DownSettingConfig downSettingConfig = downConfig();
        TaskContext.setPageSize(downSettingConfig.getPageSize());
        TaskContext.setBindTimeoutMinutes(downSettingConfig.getBindTimeoutMinutes());

        startTask();

        startWatchDog();
    }


    /**
     * 开启异步处理任务
     */
    private void startTask() {
        DefaultTaskHandlerComponent defaultTaskHandler = taskHandler(taskDomainService(taskRepository()), uploadFileService());
        Executors
                .newSingleThreadScheduledExecutor()
                .scheduleWithFixedDelay(
                        defaultTaskHandler::createAndUploadHandler,
                        1, 1, TimeUnit.SECONDS
                );
    }

    /**
     * 启动watchDog
     */
    private void startWatchDog() {
        DefaultWatchDogComponent defaultWatchDog = watchDog(taskRepository());
        Executors
                .newSingleThreadScheduledExecutor()
                .scheduleWithFixedDelay(
                        ()->{
                            defaultWatchDog.delayCurrentInstanceTask();
                            defaultWatchDog.resetTimeoutTask();
                        },
                        1, 1, TimeUnit.SECONDS
                );
    }

}
