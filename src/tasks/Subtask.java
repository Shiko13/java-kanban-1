package tasks;

import tasks.Task;

public class Subtask extends Task {
    private Integer epicId;
    private Integer id;

    public Subtask(String name, String description, int epicId, int id, Status status) {
        super(name, description, status);
        this.epicId = epicId;
        setId(id);
    }

    public Subtask(String name, String description, int epicId, Status status) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "tasks.Subtask{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", id=" + getId() +
                ", epicId=" + epicId +
                '}';
    }
}
