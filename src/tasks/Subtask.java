package tasks;

import java.time.LocalDateTime;
import java.util.Objects;

public class Subtask extends Task {
    private final Integer epicId;
    private Integer id;
    private final TypeOfTask typeOfTask = TypeOfTask.SUBTASK;

    public Subtask(String name, String description, int epicId, Status status) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public Subtask(String name, String description, int epicId, int id, Status status) {
        super(name, description, status);
        this.epicId = epicId;
        setId(id);
    }

    public Subtask(String name, String description, int epicId, Status status, int duration, LocalDateTime startTime) {
        super(name, description, status, duration, startTime);
        this.epicId = epicId;
    }

    public Subtask(String name, String description, int epicId, int id, Status status, int duration, LocalDateTime startTime) {
        super(name, description, status, duration, startTime);
        this.epicId = epicId;
        setId(id);
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public TypeOfTask getTypeOfTask() {
        return typeOfTask;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return Objects.equals(epicId, subtask.epicId) && Objects.equals(id, subtask.id) && typeOfTask == subtask.typeOfTask;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId, id, typeOfTask);
    }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s,%s", getId(), getTypeOfTask(), getName(),
                getDescription(), getStatus(), getEpicId());
    }
}
