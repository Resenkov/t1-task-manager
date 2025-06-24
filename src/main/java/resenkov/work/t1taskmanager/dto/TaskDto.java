package resenkov.work.t1taskmanager.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskDto {
    private String description;
    private long duration;
}