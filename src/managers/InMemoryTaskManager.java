package managers;

import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.time.*;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected int counterId = 0;
    protected static final LocalDateTime START_POINT = LocalDateTime.now();
    protected static final long START_POINT_LONG = START_POINT.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    protected static final long INTERVAL = 15L * 60 * 1000;
    protected Boolean[] table = createTableWithIntervals();
    protected HashMap<Integer, Task> tasks = new HashMap<>();
    protected HashMap<Integer, Subtask> subtasks = new HashMap<>();
    protected HashMap<Integer, Epic> epics = new HashMap<>();
    protected HistoryManager historyManager = Managers.getDefaultHistory();
    protected Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime,
            Comparator.nullsLast(Comparator.naturalOrder())).thenComparing(Task::getId));


    @Override
    public void updateTask(Task task) {
        if (task.getStartTime() != null && !checkForIntersections(task)) {
            System.out.println("Задача пересекается по времени с уже существующей, " +
                    "попробуйте изменить временные рамки");
            System.exit(0);
        }

        changeTableAfterAddTask(task);

        Task oldTask = tasks.get(task.getId());
        prioritizedTasks.remove(oldTask);
        prioritizedTasks.add(task);

        tasks.put(task.getId(), task);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtask.getStartTime() != null && !checkForIntersections(subtask)) {
            System.out.println("Задача пересекается по времени с уже существующей, " +
                    "попробуйте изменить временные рамки");
            System.exit(0);
        }
        changeTableAfterAddTask(subtask);

        Subtask oldSubtask = subtasks.get(subtask.getId());
        prioritizedTasks.remove(oldSubtask);
        prioritizedTasks.add(subtask);

        subtasks.put(subtask.getId(), subtask);
        checkStatusEpicAfterChangeOrDeleteSubtask(subtask.getEpicId());
    }

    @Override
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    @Override
    public int addTask(Task task) {
        if (task.getStartTime() != null && !checkForIntersections(task)) {
            System.out.println("Задача пересекается по времени с уже существующей, " +
                    "попробуйте изменить временные рамки");
            System.exit(0);
        }
        changeTableAfterAddTask(task);

        task.setId(++counterId);

        tasks.put(task.getId(), task);
        prioritizedTasks.add(task);

        return task.getId();
    }

    @Override
    public Epic addEpic(Epic epic) {
        epic.setId(++counterId);
        epics.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public Subtask addSubtask(Subtask subtask) {
        if (subtask.getStartTime() != null && !checkForIntersections(subtask)) {
            System.out.println("Задача пересекается по времени с уже существующей, " +
                    "попробуйте изменить временные рамки");
            System.exit(0);
        }
        changeTableAfterAddTask(subtask);

        subtask.setId(++counterId);
        checkStatusOfEpic(subtask);

        subtasks.put(subtask.getId(), subtask);
        prioritizedTasks.add(subtask);

        epics.get(subtask.getEpicId()).getSubtasksOfEpic().add(counterId);
        epics.get(subtask.getEpicId()).getSortedByStartTimeSubtasks().add(subtask);
        epics.get(subtask.getEpicId()).getSortedByEndTimeSubtasks().add(subtask);

        calculateTimeOfEpic(subtask);

        return subtask;
    }

    public void calculateTimeOfEpic(Subtask subtask) {
        int duration = subtask.getDuration();
        LocalDateTime startTime = subtask.getStartTime();
        Epic epic = epics.get(subtask.getEpicId());

        if (epic.getSortedByStartTimeSubtasks().isEmpty()) {
            epic.setDuration(duration);
            epic.setStartTime(startTime);
        } else {
            epic.setStartTime(epic.getSortedByStartTimeSubtasks().first().getStartTime());

        }
    }

    public boolean checkForIntersections(Task task) {
        int[] bounds = calculateBounds(task);

        for (int i = bounds[0]; i <= bounds[1]; i++) {
            if (!table[i]) {
                return false;
            }
        }
        return true;
    }


    public void changeTableAfterAddTask(Task task) {
        int[] bounds = calculateBounds(task);

        for (int i = bounds[0]; i <= bounds[1]; i++) {
            table[i] = false;
        }
    }

    public int[] calculateBounds(Task task) {
        int[] bounds = new int[2];

        long startTimeToLong = task.getStartTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long endTimeToLong = task.getEndTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        long diffStart = startTimeToLong - START_POINT_LONG;
        long diffEnd = endTimeToLong - START_POINT_LONG;

        bounds[0] = (int) (diffStart / INTERVAL);
        bounds[1] = (int) (diffEnd / INTERVAL);

        return bounds;
    }

    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    public Boolean[] createTableWithIntervals() {
        table = new Boolean[365 * 24 * 60 / 15];
        Arrays.fill(table, true);
        return table;
    }

    @Override
    public void checkStatusOfEpic(Subtask subtask) {
        if (epics.get(subtask.getEpicId()).getSubtasksOfEpic().isEmpty()) {
            Status statusOfSubtask = subtask.getStatus();
            epics.get(subtask.getEpicId()).setStatus(statusOfSubtask);
        } else {
            if ((subtask.getStatus() == Status.NEW && epics.get(subtask.getEpicId()).getStatus() == Status.DONE) ||
                    (subtask.getStatus() == Status.IN_PROGRESS && epics.get(subtask.getEpicId()).getStatus() == Status.NEW) ||
                    (subtask.getStatus() == Status.IN_PROGRESS && epics.get(subtask.getEpicId()).getStatus() == Status.DONE) ||
                    (subtask.getStatus() == Status.DONE && epics.get(subtask.getEpicId()).getStatus() == Status.NEW)) {
                epics.get(subtask.getEpicId()).setStatus(Status.IN_PROGRESS);
            }
        }
    }

    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public HistoryManager getHistoryManager() {
        return historyManager;
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
        } else {
            getEpicById(epicId).setStatus(Status.NEW);
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
        return getEpicById(epicId).getSubtasksOfEpic();
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
