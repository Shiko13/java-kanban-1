package tasks;

import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {

    private final ArrayList<Integer> subtasksOfEpic;
    private final TypeOfTask typeOfTask = TypeOfTask.EPIC;

    public Epic(String name, String description, Status status) {
        super(name, description, status);
        subtasksOfEpic = new ArrayList<>();
    }

    public Epic(String name, String description, Integer id, Status status) {
        super(name, description, id, status);
        subtasksOfEpic = new ArrayList<>();
    }

    public ArrayList<Integer> getSubtasksOfEpic() {
        return subtasksOfEpic;
    }

    @Override
    public TypeOfTask getTypeOfTask() {
        return typeOfTask;
    }
    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s", getId(), typeOfTask, getName(), getDescription(), getStatus());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtasksOfEpic, epic.subtasksOfEpic) && typeOfTask == epic.typeOfTask;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtasksOfEpic, typeOfTask);
    }
}
