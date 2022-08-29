package tests;

import managers.*;
import tasks.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class TaskManagerTest<T extends TaskManager> {

    private TaskManager taskManager;

    @BeforeEach
    public void createTaskManager() {
        taskManager = Managers.getDefault();
    }

    @Test
    public void updateTask() {
        Task task = new Task("Task",
                "Task for test updateTask", Status.NEW);
        final int taskId = taskManager.addTask(task);

        Task updateTask = new Task("UpdateTask",
                "UpdateTask", taskId, Status.DONE);
        taskManager.updateTask(updateTask);

        final Task savedTask = taskManager.getTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertNotEquals(task, savedTask, "Задачи совпадают.");

        final List<Task> tasks = taskManager.getTasks();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(savedTask, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    public void updateEpic() {
        Epic epic = new Epic("Epic",
                "Epic for test updateEpic", 1, Status.NEW);
        taskManager.addEpic(epic);

        Epic updateEpic = new Epic("UpdateEpic",
                "UpdateEpic", 1, Status.DONE);
        taskManager.updateEpic(updateEpic);

        final Epic savedEpic = taskManager.getEpicById(1);

        assertNotNull(savedEpic, "Эпик не найден.");
        assertNotEquals(epic, savedEpic, "Эпики совпадают.");

        final List<Epic> epics = taskManager.getEpics();

        assertNotNull(epics, "Эпики на возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(savedEpic, epics.get(0), "Эпики совпадают.");
    }

    @Test
    public void updateSubtask() {
        Epic epic = new Epic("Epic",
                "Epic for test updateSubtask", Status.NEW);
        taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Subtask",
                "Subtask for test updateSubtask", 1, 2, Status.NEW);
        taskManager.addSubtask(subtask);

        Subtask updateSubtask = new Subtask("UpdateSubtask",
                "UpdateSubtask", 1, 2, Status.DONE);
        taskManager.updateSubtask(updateSubtask);

        final Subtask savedSubtask = taskManager.getSubtaskById(2);

        assertNotNull(savedSubtask, "Подзадача не найдена.");
        assertNotEquals(subtask, savedSubtask, "Подзадачи совпадают.");

        final List<Subtask> subtasks = taskManager.getSubtasks();

        assertNotNull(subtasks, "Подзадачи на возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(savedSubtask, subtasks.get(0), "Подзадачи совпадают.");
    }

    @Test
    public void addTask() {
        Task task = new Task("Task",
                "Task for test addTask", Status.NEW);
        final int taskId = taskManager.addTask(task);

        final Task savedTask = taskManager.getTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getTasks();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    public void addEpic() {
        Epic epic = new Epic("Epic",
                "Epic for test addEpic", Status.NEW);
        taskManager.addEpic(epic);

        final Epic savedEpic = taskManager.getEpicById(1);

        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");

        final List<Epic> epics = taskManager.getEpics();

        assertNotNull(epics, "Эпики на возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic, epics.get(0), "Эпики не совпадают.");
    }

    @Test
    public void addSubtask() {
        Epic epic = new Epic("Epic",
                "Epic for test addSubtask", Status.NEW);
        taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Subtask",
                "Subtask for test addSubtask", 1, 2, Status.NEW);
        taskManager.addSubtask(subtask);

        final Subtask savedSubtask = taskManager.getSubtaskById(2);

        assertNotNull(savedSubtask, "Подзадача не найдена.");
        assertEquals(subtask, savedSubtask, "Подзадачи не совпадают.");

        final List<Subtask> subtasks = taskManager.getSubtasks();

        assertNotNull(subtasks, "Подзадачи на возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(subtask, subtasks.get(0), "Подзадачи не совпадают.");
    }

    @Test
    public void getTasks() {
        Task task1 = new Task("Task1",
                "Task1 for test getTasks", Status.NEW);
        taskManager.addTask(task1);

        Task task2 = new Task("Task2",
                "Task2 for test getTasks", Status.DONE);
        taskManager.addTask(task2);

        Task task3 = new Task("Task3 for test getTasks",
                "Task3 for test getTasks", Status.IN_PROGRESS);
        taskManager.addTask(task3);

        List<Task> expected = List.of(
                task1,
                task2,
                task3
        );

        List<Task> actual = taskManager.getTasks();

        assertEquals(expected, actual, "Неправильный список задач");
    }

    @Test
    public void getEpics() {
        Epic epic1 = new Epic("Epic1",
                "Epic1 for test getEpics", Status.NEW);
        taskManager.addEpic(epic1);

        Epic epic2 = new Epic("Epic2",
                "Epic2 for test getEpics", Status.DONE);
        taskManager.addEpic(epic2);

        Epic epic3 = new Epic("Epic3",
                "Epic3 for test getEpics", Status.IN_PROGRESS);
        taskManager.addEpic(epic3);

        List<Epic> expected = List.of(
                epic1,
                epic2,
                epic3
        );

        List<Epic> actual = taskManager.getEpics();

        assertEquals(expected, actual, "Неправильный список эпиков");
    }

    @Test
    public void getSubtasks() {
        Epic epic1 = new Epic("Epic1",
                "Epic1 for test getSubtasks", Status.NEW);
        taskManager.addEpic(epic1);

        Epic epic2 = new Epic("Epic2",
                "Epic2 for test getSubtasks", Status.DONE);
        taskManager.addEpic(epic2);

        Subtask subtask1 = new Subtask("Subtask1",
                "Subtask1 for test getSubtasks", 1, 3, Status.NEW);
        taskManager.addSubtask(subtask1);

        Subtask subtask2 = new Subtask("Subtask2",
                "Subtask2 for test getSubtasks", 2, 4, Status.DONE);
        taskManager.addSubtask(subtask2);

        Subtask subtask3 = new Subtask("Subtask3",
                "Subtask3 for test getSubtasks", 2, 5, Status.IN_PROGRESS);
        taskManager.addSubtask(subtask3);

        List<Subtask> expected = List.of(
                subtask1,
                subtask2,
                subtask3
        );

        List<Subtask> actual = taskManager.getSubtasks();

        assertEquals(expected, actual, "Неправильный список подзадач");
    }

    @Test
    public void getHistoryManager() {
        Task task = new Task("Task",
                "Task for test getHistoryManager", Status.NEW);
        taskManager.addTask(task);

        Epic epic = new Epic("Epic",
                "Epic for test getHistoryManager", Status.NEW);
        taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Subtask",
                "Subtask for test getHistoryManager", 2, 3, Status.NEW);
        taskManager.addSubtask(subtask);

        taskManager.getTaskById(1);
        taskManager.getSubtaskById(3);
        taskManager.getEpicById(2);

        List<Task> expected = List.of(
                task,
                subtask,
                epic
        );

        List<Task> actual = taskManager.getHistoryManager().getHistory();

        assertEquals(expected, actual, "Неправильный список просмотра");
    }

    @Test
    public void removeAllTasks() {
        Task task1 = new Task("Task1",
                "Task1 for test removeAllTasks", Status.NEW);
        taskManager.addTask(task1);

        Task task2 = new Task("Task2",
                "Task2 for test removeAllTasks", Status.DONE);
        taskManager.addTask(task2);

        taskManager.removeAllTasks();
        List<Task> emptyTasks = new ArrayList<>();
        List<Task> tasks = taskManager.getTasks();

        assertEquals(emptyTasks, tasks, "Список задач не очистился");
    }

    @Test
    public void removeAllEpics() {
        Epic epic1 = new Epic("Epic1",
                "Epic1 for test removeAllEpics", Status.NEW);
        taskManager.addEpic(epic1);

        Epic epic2 = new Epic("Epic2",
                "Epic2 for test removeAllEpics", Status.DONE);
        taskManager.addEpic(epic2);

        taskManager.removeAllEpics();

        List<Epic> emptyEpics = new ArrayList<>();
        List<Epic> epics = taskManager.getEpics();

        assertEquals(emptyEpics, epics, "Список эпиков не очистился");
    }

    @Test
    public void removeAllSubtasks() {
        Epic epic1 = new Epic("Epic1",
                "Epic1 for test removeAllSubtasks", Status.NEW);
        taskManager.addEpic(epic1);

        Epic epic2 = new Epic("Epic2",
                "Epic2 for test removeAllSubtasks", Status.DONE);
        taskManager.addEpic(epic2);

        Subtask subtask1 = new Subtask("Subtask1",
                "Subtask1 for test removeAllSubtasks description", 1, 3, Status.NEW);
        taskManager.addSubtask(subtask1);

        Subtask subtask2 = new Subtask("Subtask2",
                "Subtask2 for test removeAllSubtasks", 2, 4, Status.DONE);
        taskManager.addSubtask(subtask2);

        Subtask subtask3 = new Subtask("Subtask3",
                "Subtask3 for test removeAllSubtasks", 2, 5, Status.IN_PROGRESS);
        taskManager.addSubtask(subtask3);

        taskManager.removeAllSubtasks();

        List<Subtask> emptySubtasks = new ArrayList<>();
        List<Subtask> subtasks = taskManager.getSubtasks();

        assertEquals(emptySubtasks, subtasks, "Список подзадач не очистился");
    }

    @Test
    public void getTaskById() {
        Task task = new Task("Task",
                "Task for test getTaskById", Status.NEW);
        taskManager.addTask(task);

        assertEquals(task, taskManager.getTaskById(1), "Возвращается неверная задача");
    }

    @Test
    public void getEpicById() {
        Epic epic = new Epic("Epic",
                "Epic for test getEpicById", Status.NEW);
        taskManager.addEpic(epic);

        assertEquals(epic, taskManager.getEpicById(1), "Возвращается неверный эпик");
    }

    @Test
    public void getSubtaskById() {
        Epic epic = new Epic("Epic",
                "Epic for test getSubtaskById", Status.NEW);
        taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Subtask",
                "Subtask for test getSubtaskById", 1, 2, Status.NEW);
        taskManager.addSubtask(subtask);

        assertEquals(subtask, taskManager.getSubtaskById(2), "Возвращается неверная подзадача");
    }

    @Test
    public void removeTaskById() {
        Task task1 = new Task("Task1",
                "Task1 for test removeTaskById", Status.NEW);
        taskManager.addTask(task1);

        Task task2 = new Task("Task2",
                "Task2 for test removeTaskById", Status.DONE);
        taskManager.addTask(task2);

        Task task3 = new Task("Task3",
                "Task3 for test removeTaskById", Status.IN_PROGRESS);
        taskManager.addTask(task3);

        taskManager.removeTaskById(2);

        List<Task> actual = taskManager.getTasks();
        List<Task> expected = List.of(
                task1,
                task3
        );

        assertEquals(expected, actual, "Неверно удалена задача");
    }

    @Test
    public void removeEpicById() {
        Epic epic1 = new Epic("Epic1",
                "Epic1 for test removeEpicById", Status.NEW);
        taskManager.addEpic(epic1);
        Epic epic2 = new Epic("Epic2",
                "Epic2 for test removeEpicById", Status.DONE);
        taskManager.addEpic(epic2);

        Epic epic3 = new Epic("Epic3",
                "Epic3 for test removeEpicById", Status.IN_PROGRESS);
        taskManager.addEpic(epic3);

        taskManager.removeEpicById(2);

        List<Epic> actual = taskManager.getEpics();
        List<Epic> expected = List.of(
                epic1,
                epic3
        );

        assertEquals(expected, actual, "Неверно удалён эпик");
    }

    @Test
    public void removeSubtaskById() {
        Epic epic1 = new Epic("Epic1",
                "Epic1 for test removeSubtaskById", Status.NEW);
        taskManager.addEpic(epic1);

        Epic epic2 = new Epic("Epic2",
                "Epic2 for test removeSubtaskById", Status.DONE);
        taskManager.addEpic(epic2);

        Subtask subtask1 = new Subtask("Subtask1",
                "Subtask1 for test removeSubtaskById", 1, 3, Status.NEW);
        taskManager.addSubtask(subtask1);

        Subtask subtask2 = new Subtask("Subtask2",
                "Subtask2 for test removeSubtaskById", 2, 4, Status.DONE);
        taskManager.addSubtask(subtask2);

        Subtask subtask3 = new Subtask("Subtask3",
                "Subtask3 for test removeSubtaskById", 2, 5, Status.IN_PROGRESS);
        taskManager.addSubtask(subtask3);

        taskManager.removeSubtaskById(4);

        List<Subtask> actual = taskManager.getSubtasks();
        List<Subtask> expected = List.of(
                subtask1,
                subtask3
        );

        assertEquals(expected, actual, "Неверно удалена подзадача");
    }

    @Test
    public void getIdSubtasksOfEpic() {
        Epic epic = new Epic("Epic",
                "Epic for test getIdSubtasksOfEpic", Status.NEW);
        taskManager.addEpic(epic);

        Subtask subtask1 = new Subtask("Subtask1",
                "Subtask1 for test getIdSubtasksOfEpic", 1, 2, Status.NEW);
        taskManager.addSubtask(subtask1);

        Subtask subtask2 = new Subtask("Subtask2",
                "Subtask2 for test getIdSubtasksOfEpic", 1, 3, Status.DONE);
        taskManager.addSubtask(subtask2);

        Subtask subtask3 = new Subtask("Subtask3",
                "Subtask3 for test getIdSubtasksOfEpic", 1, 4, Status.IN_PROGRESS);
        taskManager.addSubtask(subtask3);

        List<Integer> actual = taskManager.getIdSubtasksOfEpic(1);
        List<Integer> expected = List.of(
                2,
                3,
                4
        );

        assertEquals(expected, actual, "Неверный список id подзадач эпика");
    }
}