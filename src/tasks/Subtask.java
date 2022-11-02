package tasks;

import java.time.LocalDateTime;
import java.util.Objects;

public class Subtask extends Task {
    private Integer epicId;

    public Subtask() {
    }

    public Subtask(String name, String description, Integer epicId, Integer id, Status status, Integer duration, LocalDateTime startTime) {
        super(name, description, id, status, duration, startTime);
        this.typeOfTask = TypeOfTask.SUBTASK;
        this.epicId = epicId;
        setId(id);
    }

    public int getEpicId() {
        return epicId;
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
        return String.format("%d,%s,%s,%s,%s,%s,%d,%s", getId(), getTypeOfTask(), getName(),
                getDescription(), getStatus(), getEpicId(), getDuration(), getStartTime());
    }
}
