package resenkov.work.t1taskmanager.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    private Long id;
    private String description;
    private long duration;
    private Status status;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public enum Status { IN_PROGRESS, DONE, CANCELED }
}