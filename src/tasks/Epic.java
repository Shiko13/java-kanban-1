package tasks;

import java.time.LocalDateTime;
import java.util.*;

public class Epic extends Task {
    protected ArrayList<Integer> subtasksOfEpic;
    protected transient TreeSet<Subtask> sortedByStartTimeSubtasks;
    protected transient TreeSet<Subtask> sortedByEndTimeSubtasks;

    public Epic () {
    }

    public Epic(String name, String description, Integer id, Status status, Integer duration, LocalDateTime startTime) {
        super(name, description, id, status, duration, startTime);
        this.typeOfTask = TypeOfTask.EPIC;
        this.subtasksOfEpic = new ArrayList<>();
        this.sortedByStartTimeSubtasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
        this.sortedByEndTimeSubtasks = new TreeSet<>(Comparator.comparing(Task::getEndTime).reversed());
    }

    public ArrayList<Integer> getSubtasksOfEpic() {
        return subtasksOfEpic;
    }

    public TreeSet<Subtask> getSortedByStartTimeSubtasks() {
        return sortedByStartTimeSubtasks;
    }

    public TreeSet<Subtask> getSortedByEndTimeSubtasks() {
        return sortedByEndTimeSubtasks;
    }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s,%d,%s", getId(), getTypeOfTask(), getName(),
                getDescription(), getStatus(), getDuration(), getStartTime());
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
