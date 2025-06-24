package resenkov.work.t1taskmanager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import resenkov.work.t1taskmanager.service.TaskService;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BaseIntegrationTest {

    @Autowired
    protected TaskService taskService;

    @AfterEach
    void tearDown() {
        taskService.cancelAllScheduledTasks();
    }
}