package tasks;

import java.util.Objects;

public class Task {
    private final String name;
    private final String description;
    private Integer id;
    private Status status;

    private final TypeOfTask typeOfTask = TypeOfTask.TASK;

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
    public String toString() {
        return String.format("%d,%s,%s,%s,%s", getId(), getTypeOfTask(), getName(), getDescription(), getStatus());
    }
}
