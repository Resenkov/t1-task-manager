package resenkov.work.t1taskmanager.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import resenkov.work.t1taskmanager.model.Task;
import resenkov.work.t1taskmanager.repository.TaskRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;

@Slf4j
@Service
public class TaskService {
    private final TaskRepository repository;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);
    private final ConcurrentMap<Long, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

    public TaskService(TaskRepository repository) {
        this.repository = repository;
    }

    public List<Task> getAllTasks() {
        log.debug("Получены все задачи");
        return repository.findAll();
    }

    public Optional<Task> getTaskById(Long id) {
        log.debug("Успешно получена задача, ID: {}",id);
        return repository.findById(id);
    }

    public Task createTask(String description, long duration) {
        Task task = new Task();
        task.setDescription(description);
        task.setDuration(duration);
        task.setStatus(Task.Status.IN_PROGRESS);
        task.setCreatedDate(LocalDateTime.now());
        task.setModifiedDate(LocalDateTime.now());

        Task savedTask = repository.save(task);
        scheduleCompletion(savedTask.getId(), duration);
        log.info("Создана новая задача: {}, лимит: {}мс", description, duration);
        return savedTask;
    }

    private void scheduleCompletion(Long taskId, long duration) {
        ScheduledFuture<?> future = scheduler.schedule(() -> {
            repository.findById(taskId).ifPresent(task -> {
                synchronized (task) {
                    if (task.getStatus() == Task.Status.IN_PROGRESS) {
                        task.setStatus(Task.Status.DONE);
                        task.setModifiedDate(LocalDateTime.now());
                        repository.update(task);
                        scheduledTasks.remove(taskId);
                    }
                }
            });
        }, duration, TimeUnit.MILLISECONDS);

        scheduledTasks.put(taskId, future);
    }

    public void cancelTask(Long taskId) {
        log.debug("Отмена задачи по ID: {}", taskId);
        repository.findById(taskId).ifPresent(task -> {
            synchronized (task) {
                if (task.getStatus() == Task.Status.DONE) {
                    log.warn("Нельзя отменить выполненную задачу!: {}", taskId);
                    throw new IllegalStateException("Cannot cancel DONE task");
                }
                if (task.getStatus() == Task.Status.IN_PROGRESS) {
                    task.setStatus(Task.Status.CANCELED);
                    task.setModifiedDate(LocalDateTime.now());
                    repository.update(task);
                    log.info("Задача успешно отменена, ID: {}", taskId);
                    ScheduledFuture<?> future = scheduledTasks.get(taskId);
                    if (future != null) future.cancel(false);
                    scheduledTasks.remove(taskId);
                }
            }
        });

    }
    public void cancelAllScheduledTasks() {
        for (ScheduledFuture<?> future : scheduledTasks.values()) {
            if (future != null && !future.isDone()) {
                future.cancel(false);
            }
        }
        scheduledTasks.clear();
    }
}