package managers;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    void updateTask(Task task);

    void updateSubtask(Subtask subtask);

    void updateEpic(Epic epic);

    int addTask(Task task);

    Subtask addSubtask(Subtask subtask);

    Epic addEpic(Epic epic);

    int getCounterId();

    ArrayList<Task> getTasks();

    ArrayList<Subtask> getSubtasks();

    ArrayList<Epic> getEpics();

    void removeAllTasks();

    void removeAllSubtasks();

    void removeAllEpics();

    Task getTaskById(Integer id);

    Subtask getSubtaskById(Integer id);

    Epic getEpicById(Integer id);

    void removeTaskById(Integer id);

    void removeSubtaskById(Integer id);

    void removeEpicById(Integer id);

    ArrayList<Integer> getIdSubtasksOfEpic(Integer epicId);

    void checkStatusOfEpic(Subtask subtask);

    HistoryManager getHistoryManager();

    void checkStatusEpicAfterChangeOrDeleteSubtask(int epicId);

    void calculateTimeOfEpic(Subtask subtask);

    boolean checkForIntersections(Task task);

    List<Task> getPrioritizedTasks();

    Boolean[] createTableWithIntervals();

    int[] calculateBounds(Task task);

    Boolean[] getTable();

}
