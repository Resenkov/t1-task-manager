package resenkov.work.t1taskmanager.service;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import resenkov.work.t1taskmanager.BaseIntegrationTest;
import resenkov.work.t1taskmanager.model.Task;
import resenkov.work.t1taskmanager.repository.TaskRepository;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TaskServiceIntegrationTest extends BaseIntegrationTest{

    private static final Logger log = LoggerFactory.getLogger(TaskServiceIntegrationTest.class);
    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskRepository taskRepository;

    @Test
    void testTaskLifecycle() {
        log.info("Запуск всех тестов!");
        Task task = taskService.createTask("Integration Test Task", 1000);

        List<Task> allTasks = taskService.getAllTasks();
        log.info("Все задачи были найдены: {}", allTasks);
        assertFalse(allTasks.isEmpty(), "Список задач не должен быть пустым");
        assertTrue(allTasks.contains(task), "Созданная задача находится в списке");

        Optional<Task> createdTask = taskService.getTaskById(task.getId());
        assertTrue(createdTask.isPresent(), "Задача успешно создана");
        log.info("Создана задача: {}", createdTask.get());
        assertEquals(Task.Status.IN_PROGRESS, createdTask.get().getStatus(), "Начальный статус должен быть IN_PROGRESS");

        await()
                .atMost(5, TimeUnit.SECONDS)
                .pollInterval(Duration.ofMillis(100))
                .untilAsserted(() -> {
                    Optional<Task> updatedTask = taskService.getTaskById(task.getId());
                    assertTrue(updatedTask.isPresent());
                    assertEquals(
                            Task.Status.DONE,
                            updatedTask.get().getStatus(),
                            "Статус должен автоматически измениться на DONE"
                    );
                    log.info("Статус задачи изменился на: {}", updatedTask.get().getStatus());
                });

        Optional<Task> doneTask = taskService.getTaskById(task.getId());
        log.info("Проверяем обновленный статус задачи: {}", doneTask.get().getStatus());
        assertTrue(doneTask.isPresent());
        assertEquals(Task.Status.DONE, doneTask.get().getStatus(), "Статус должен быть DONE");

        Exception exception = assertThrows(
                IllegalStateException.class,
                () -> taskService.cancelTask(task.getId()),
                "Должна быть ошибка при отмене завершенной задачи"
        );

        log.info("Ошибка при отмены завершенной задачи: {}", exception.getMessage());

        assertEquals("Cannot cancel DONE task", exception.getMessage(), "Сообщение об ошибке должно соответствовать");
    }
}