package resenkov.work.t1taskmanager.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import resenkov.work.t1taskmanager.dto.TaskDto;
import resenkov.work.t1taskmanager.model.Task;
import resenkov.work.t1taskmanager.service.TaskService;

import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
@RequestMapping("/api/v1/tasks")
public class TaskController {
    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    @GetMapping
    public List<Task> getAllTasks() {
        return service.getAllTasks();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        Optional<Task> task = service.getTaskById(id);
        return task.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public Task createTask(@RequestBody TaskDto dto) {
        return service.createTask(dto.getDescription(), dto.getDuration());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelTask(@PathVariable Long id) {
        try {
            service.cancelTask(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException e) {
            log.error("Нельзя отменить выполненную задачу!");
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}