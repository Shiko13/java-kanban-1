package tasks;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

public class Task {
    protected String name;
    protected String description;
    protected Integer id;
    protected Status status;
    protected Integer duration;
    protected LocalDateTime startTime;
    protected TypeOfTask typeOfTask;

    public Task() {
    }

    public Task(String name, String description, Integer id, Status status, Integer duration, LocalDateTime startTime) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
        this.typeOfTask = TypeOfTask.TASK;
    }

    public LocalDateTime getEndTime() {
        return Optional.ofNullable(startTime).isPresent() ? startTime.plusMinutes(duration) : null;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Integer getId() {
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

    public Integer getDuration() {
        return duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
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
        return String.format("%d,%s,%s,%s,%s,%d,%s", getId(), getTypeOfTask(), getName(),
                getDescription(), getStatus(), getDuration(), getStartTime());
    }
}
