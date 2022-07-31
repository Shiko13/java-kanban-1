package tasks;

import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<Integer> subtasksOfEpic;

    public Epic(String name, String description, Status status) {
        super(name, description, status);
        subtasksOfEpic = new ArrayList<>();
    }

    public ArrayList<Integer> getSubtasksOfEpic() {
        return subtasksOfEpic;
    }

    @Override
    public String toString() {
        return "tasks.Epic{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", id=" + getId() +
                '}';
    }
}
