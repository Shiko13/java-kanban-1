package managers;

import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {
    private int counterId = 0;
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();

    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    private HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        checkStatusEpicAfterChangeOrDeleteSubtask(subtask.getEpicId());
    }

    @Override
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    @Override
    public int addTask(Task task) {
        task.setId(++counterId);
        tasks.put(task.getId(), task);
        return task.getId();
    }

    @Override
    public Subtask addSubtask(Subtask subtask) {
        subtask.setId(++counterId);
        subtasks.put(subtask.getId(), subtask);
        getEpicById(subtask.getEpicId()).getSubtasksOfEpic().add(counterId);
        checkStatusOfEpic(subtask);
        return subtask;
    }

    private void checkStatusOfEpic(Subtask subtask) {
        if ((subtask.getStatus() == Status.NEW && getEpicById(subtask.getEpicId()).getStatus() == Status.DONE) ||
                (subtask.getStatus() == Status.IN_PROGRESS && getEpicById(subtask.getEpicId()).getStatus() == Status.NEW) ||
                (subtask.getStatus() == Status.IN_PROGRESS && getEpicById(subtask.getEpicId()).getStatus() == Status.DONE) ||
                (subtask.getStatus() == Status.DONE && getEpicById(subtask.getEpicId()).getStatus() == Status.NEW)) {
            getEpicById(subtask.getEpicId()).setStatus(Status.IN_PROGRESS);
        }
    }

    @Override
    public Epic addEpic(Epic epic) {
        epic.setId(++counterId);
        epics.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public ArrayList<Task> getTasks() {
        ArrayList<Task> taskList = new ArrayList<>();
        for (Task task : tasks.values()) {
            taskList.add(task);
        }
        return taskList;
    }

    @Override
    public ArrayList<Subtask> getSubtasks() {
        ArrayList<Subtask> subtaskList = new ArrayList<>();
        for (Subtask subtask : subtasks.values()) {
            subtaskList.add(subtask);
        }
        return subtaskList;
    }

    @Override
    public ArrayList<Epic> getEpics() {
        ArrayList<Epic> epicList = new ArrayList<>();
        for (Epic epic : epics.values()) {
            epicList.add(epic);
        }
        return epicList;
    }

    @Override
    public void removeAllTasks() {
        tasks.clear();
    }

    @Override
    public void removeAllSubtasks() {
        subtasks.clear();
    }

    @Override
    public void removeAllEpics() {
        epics.clear();
        removeAllSubtasks();
    }

    @Override
    public Task getTaskById(Integer id) {
        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public Subtask getSubtaskById(Integer id) {
        historyManager.add(subtasks.get(id));
        return subtasks.get(id);
    }

    @Override
    public Epic getEpicById(Integer id) {
        historyManager.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public void removeTaskById(Integer id) {
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void removeSubtaskById(Integer id) {
        int epicId = subtasks.get(id).getEpicId();
        subtasks.remove(id);
        getEpicById(epicId).getSubtasksOfEpic().remove(id);
        if (!getEpicById(epicId).getSubtasksOfEpic().isEmpty()) {
            checkStatusEpicAfterChangeOrDeleteSubtask(epicId);
        }
        historyManager.remove(id);
    }

    @Override
    public void removeEpicById(Integer id) {
        ArrayList<Integer> idSubtaskOfEpic = getIdSubtasksOfEpic(id);
        for (int i = idSubtaskOfEpic.size() - 1; i > -1; i--) {
            removeSubtaskById(idSubtaskOfEpic.get(i));
        }
        epics.remove(id);
        historyManager.remove(id);
    }

    @Override
    public ArrayList<Integer> getIdSubtasksOfEpic(Integer epicId) {
        ArrayList<Integer> idSubtaskOfEpic = getEpicById(epicId).getSubtasksOfEpic();
        return idSubtaskOfEpic;
    }

    @Override
    public void checkStatusEpicAfterChangeOrDeleteSubtask(int epicId) {
        int newCount = 0;
        int newDone = 0;
        for (int i = 0; i < getEpicById(epicId).getSubtasksOfEpic().size(); i++) {
            if (getSubtaskById(getEpicById(epicId).getSubtasksOfEpic().get(i)).getStatus() == Status.NEW) {
                newCount++;
            } else if (getSubtaskById(getEpicById(epicId).getSubtasksOfEpic().get(i)).getStatus() == Status.DONE) {
                newDone++;
            }
        }
        if (newCount == getEpicById(epicId).getSubtasksOfEpic().size()) {
            getEpicById(epicId).setStatus(Status.NEW);
        } else if (newDone == getEpicById(epicId).getSubtasksOfEpic().size()) {
            getEpicById(epicId).setStatus(Status.DONE);
        } else {
            getEpicById(epicId).setStatus(Status.IN_PROGRESS);
        }
    }
}
