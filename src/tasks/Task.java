package tasks;

import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    private final String name;
    private final String description;
    private Integer id;
    private Status status;
    private int duration;
    private LocalDateTime startTime;
    private LocalDateTime getEndTime;

    private final TypeOfTask typeOfTask = TypeOfTask.TASK;


    public Task(String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public Task(String name, String description, Integer id, Status status) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
    }

    public Task(String name, String description, Status status, int duration, LocalDateTime startTime) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
        this.getEndTime = startTime.plusMinutes(duration);
    }

    public Task(String name, String description, Integer id, Status status, int duration, LocalDateTime startTime) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
        this.getEndTime = startTime.plusMinutes(duration);
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public TypeOfTask getTypeOfTask() {
        return typeOfTask;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(name, task.name) && Objects.equals(description, task.description) && Objects.equals(id, task.id) && status == task.status && typeOfTask == task.typeOfTask;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, id, status, typeOfTask);
    }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s", getId(), getTypeOfTask(), getName(), getDescription(), getStatus());
    }
}
