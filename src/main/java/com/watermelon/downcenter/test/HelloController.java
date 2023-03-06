package com.watermelon.downcenter.test;

import com.watermelon.domain.task.DefaultTaskHandlerComponent;
import com.watermelon.domain.task.entity.Task;
import com.watermelon.domain.watchdog.DefaultWatchDogComponent;
import com.watermelon.downcenter.test.service.TaskApplicationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class HelloController {

	private final TaskApplicationService taskApplicationService;

	private final DefaultTaskHandlerComponent taskHandler;

	private final DefaultWatchDogComponent watchDog;


	public HelloController(TaskApplicationService taskApplicationService, DefaultTaskHandlerComponent taskHandler, DefaultWatchDogComponent watchDog) {
		this.taskApplicationService = taskApplicationService;
		this.taskHandler = taskHandler;
		this.watchDog = watchDog;
	}

	@GetMapping("/")
	public String index() {
		return "Greetings from Spring Boot!";
	}

	@GetMapping("/list")
	public List<Task> list() {
		return taskApplicationService.listByTask(null);
	}

	@GetMapping("/submit")
	public String submit() {
		taskApplicationService.submitTask();

		return "success!";
	}

	@GetMapping("/runTask")
	public String runTask() {
		taskHandler.createAndUploadHandler();
		return "success!";
	}

	@GetMapping("/watchdog/delayCurrentInstanceTask")
	public String delayCurrentInstanceTask() {
		watchDog.delayCurrentInstanceTask();
		return "success!";
	}

	@GetMapping("/watchdog/resetTimeoutTask")
	public String resetTimeoutTask() {
		watchDog.resetTimeoutTask();
		return "success!";
	}







}