package tests;

import http.KVServer;
import managers.HTTPTaskManager;
import managers.Managers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HTTPTaskManagerTest extends InMemoryTaskManagerTest {
    private KVServer server;
    private HTTPTaskManager manager;
    private HTTPTaskManager httpTaskManager;


    @BeforeEach
    public void start() {
        try {
            server = new KVServer();
        } catch (IOException e) {
            System.out.println("Не удалось соединиться с сервером");
            e.printStackTrace();
        }
        server.start();
        manager = (HTTPTaskManager) Managers.getDefault();
    }

    @AfterEach
    public void stop() {
        server.stop();
    }

    @Test
    public void createHTTPTaskManagerWithOneTask() {
        Task task = new Task("Task",
                "Task for test createHTTPTaskManagerWithOneTask", null, Status.NEW,
                200, LocalDateTime.of(2023, 1, 4, 18, 29));
        manager.addTask(task);
        httpTaskManager = new HTTPTaskManager("8078", true);
        assertTrue(httpTaskManager.getEpics().isEmpty(), "Список эпиков не пуст");
        assertTrue(httpTaskManager.getSubtasks().isEmpty(), "Список подзадач не пуст");
        assertTrue(httpTaskManager.getHistoryManager().getHistory().isEmpty(), "История не пуста");
        assertEquals(task, httpTaskManager.getTasks().get(0), "Задачи не совпадают");
    }

    @Test
    public void createHTTPTaskManagerWithEpicWithoutSubtasks() {
        Epic epic = new Epic("Epic",
                "Epic for test createHTTPTaskManagerWithEpicWithoutSubtasks",
                1, Status.DONE, null, null);
        manager.addEpic(epic);
        httpTaskManager = new HTTPTaskManager("8078", true);
        assertTrue(httpTaskManager.getHistoryManager().getHistory().isEmpty(), "История не пуста.");
        assertTrue(httpTaskManager.getTasks().isEmpty(), "Список задач не пуст");
        assertTrue(httpTaskManager.getSubtasks().isEmpty(), "Список подзадач не пуст");
        assertEquals(epic, httpTaskManager.getEpics().get(0), "Эпики не совпадают");
    }

    @Test
    public void createHTTPTaskManagerWithEpicWithSubtask() {
        Epic epic = new Epic("Epic",
                "Epic for test",
                1, Status.DONE, null, null);
        manager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask",
                "Subtask for test createHTTPTaskManagerWithEpicWithSubtask",
                1, 2, Status.IN_PROGRESS,
                200, LocalDateTime.of(2023, 1, 4, 18, 29));
        manager.addSubtask(subtask);
        httpTaskManager = new HTTPTaskManager("8078", true);
        assertTrue(httpTaskManager.getTasks().isEmpty(), "Список задач не пуст");
        assertEquals(epic, httpTaskManager.getEpics().get(0), "Эпики не совпадают");
        assertEquals(subtask, httpTaskManager.getSubtasks().get(0), "Подзадачи не совпадают");
    }

    @Test
    public void createHTTPTaskManagerWithHistory() {
        Task task = new Task("Task",
                "Task for test createHTTPTaskManagerWithHistory", null, Status.NEW,
                200, LocalDateTime.of(2023, 2, 4, 11, 29));
        manager.addTask(task);
        Epic epic = new Epic("Epic",
                "Epic for test createHTTPTaskManagerWithHistory",
                2, Status.DONE, null, null);
        manager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask",
                "Subtask for test createHTTPTaskManagerWithHistory",
                2, 3, Status.IN_PROGRESS,
                200, LocalDateTime.of(2023, 1, 4, 18, 29));
        manager.addSubtask(subtask);
        manager.getTaskById(task.getId());
        manager.getSubtaskById(subtask.getId());
        manager.getEpicById(epic.getId());
        httpTaskManager = new HTTPTaskManager("8078", true);
        assertEquals(3, httpTaskManager.getHistoryManager().getHistory().size(), "Неверное количество задач");
        assertTrue(httpTaskManager.getHistoryManager().getHistory().contains(task), "Задача не была добавлена в историю");
        assertTrue(httpTaskManager.getHistoryManager().getHistory().contains(epic), "Эпик не был добавлен в историю");
        assertTrue(httpTaskManager.getHistoryManager().getHistory().contains(subtask), "Подзадача не была добавлена в историю");
        assertEquals(task, httpTaskManager.getHistoryManager().getHistory().get(0), "Неверный порядок истории");
        assertEquals(subtask, httpTaskManager.getHistoryManager().getHistory().get(1), "Неверный порядок истории");
        assertEquals(epic, httpTaskManager.getHistoryManager().getHistory().get(2), "Неверный порядок истории");
    }


    @Test
    public void createHTTPTaskManagerWithPrioritizedTasks() {
        Task task1 = new Task("Task1",
                "Task1 for test createHTTPTaskManagerWithPrioritizedTasks", null, Status.NEW,
                20, LocalDateTime.of(2023, 2, 26, 10, 0));
        manager.addTask(task1);

        Task task2 = new Task("Task2",
                "Task2 for test createHTTPTaskManagerWithPrioritizedTasks", null, Status.NEW,
                20, LocalDateTime.of(2023, 4, 30, 10, 25));
        manager.addTask(task2);

        Task task3 = new Task("Task3",
                "Task3 for test createHTTPTaskManagerWithPrioritizedTasks", null, Status.NEW,
                20, LocalDateTime.of(2023, 1, 31, 11, 0));
        manager.addTask(task3);

        httpTaskManager = new HTTPTaskManager("8078", true);

        final List<Task> expected = List.of(
                task3,
                task1,
                task2
        );
        final List<Task> actual = httpTaskManager.getPrioritizedTasks();
        assertEquals(expected, actual, "Неверно расставлены приоритеты");
    }

    @Test
    public void tasksOfHTTPTaskManagerShouldBeEqualsTasksOfManager() {
        Task task1 = new Task("Task1",
                "Task1 for test tasksOfHTTPTaskManagerShouldBeEqualsTasksOfManager", null, Status.NEW,
                20, LocalDateTime.of(2023, 2, 26, 10, 0));
        manager.addTask(task1);

        Task task2 = new Task("Task2",
                "Task2 for test tasksOfHTTPTaskManagerShouldBeEqualsTasksOfManager", null, Status.NEW,
                20, LocalDateTime.of(2023, 4, 30, 10, 25));
        manager.addTask(task2);

        Task task3 = new Task("Task3",
                "Task3 for test tasksOfHTTPTaskManagerShouldBeEqualsTasksOfManager", null, Status.NEW,
                20, LocalDateTime.of(2023, 1, 31, 11, 0));
        manager.addTask(task3);

        httpTaskManager = new HTTPTaskManager("8078", true);
        assertEquals(manager.getTasks(), httpTaskManager.getTasks(),
                "Список задач после выгрузки не совпадает");
    }

    @Test
    public void subtasksOfHTTPTaskManagerShouldBeEqualsSubtasksOfManager() {
        Epic epic1 = new Epic("Epic1",
                "Epic1 for test subtasksOfHTTPTaskManagerShouldBeEqualsSubtasksOfManager", null, Status.NEW, null, null);
        manager.addEpic(epic1);

        Epic epic2 = new Epic("Epic2",
                "Epic2 for test subtasksOfHTTPTaskManagerShouldBeEqualsSubtasksOfManager", null, Status.DONE, null, null);
        manager.addEpic(epic2);

        Subtask subtask1 = new Subtask("Subtask1",
                "Subtask1 for test subtasksOfHTTPTaskManagerShouldBeEqualsSubtasksOfManager", 1, 3, Status.NEW,
                200, LocalDateTime.of(2023, 1, 4, 18, 29));
        manager.addSubtask(subtask1);

        Subtask subtask2 = new Subtask("Subtask2",
                "Subtask2 for test subtasksOfHTTPTaskManagerShouldBeEqualsSubtasksOfManager", 2, 4, Status.DONE,
                200, LocalDateTime.of(2023, 2, 4, 18, 29));
        manager.addSubtask(subtask2);

        Subtask subtask3 = new Subtask("Subtask3",
                "Subtask3 for test subtasksOfHTTPTaskManagerShouldBeEqualsSubtasksOfManager", 2, 5, Status.IN_PROGRESS,
                200, LocalDateTime.of(2023, 3, 4, 18, 29));
        manager.addSubtask(subtask3);

        httpTaskManager = new HTTPTaskManager("8078", true);
        assertEquals(manager.getSubtasks(), httpTaskManager.getSubtasks(),
                "Список подзадач после выгрузки не совпадает");
    }

    @Test
    public void epicsOfHTTPTaskManagerShouldBeEqualsEpicsOfManager() {
        Epic epic1 = new Epic("Epic1",
                "Epic1 for test epicsOfHTTPTaskManagerShouldBeEqualsEpicsOfManager", null, Status.NEW, null, null);
        manager.addEpic(epic1);

        Epic epic2 = new Epic("Epic2",
                "Epic2 for test epicsOfHTTPTaskManagerShouldBeEqualsEpicsOfManager", null, Status.DONE, null, null);
        manager.addEpic(epic2);

        Subtask subtask1 = new Subtask("Subtask1",
                "Subtask1 for test epicsOfHTTPTaskManagerShouldBeEqualsEpicsOfManager", 1, 3, Status.NEW,
                200, LocalDateTime.of(2023, 1, 4, 18, 29));
        manager.addSubtask(subtask1);

        Subtask subtask2 = new Subtask("Subtask2",
                "Subtask2 for test epicsOfHTTPTaskManagerShouldBeEqualsEpicsOfManager", 2, 4, Status.DONE,
                200, LocalDateTime.of(2023, 2, 4, 18, 29));
        manager.addSubtask(subtask2);

        Subtask subtask3 = new Subtask("Subtask3",
                "Subtask3 for test epicsOfHTTPTaskManagerShouldBeEqualsEpicsOfManager", 2, 5, Status.IN_PROGRESS,
                200, LocalDateTime.of(2023, 3, 4, 18, 29));
        manager.addSubtask(subtask3);

        Epic epic3 = new Epic("Epic3",
                "Epic3 for test epicsOfHTTPTaskManagerShouldBeEqualsEpicsOfManager", null, Status.DONE, null, null);
        manager.addEpic(epic3);

        httpTaskManager = new HTTPTaskManager("8078", true);
        assertEquals(manager.getEpics(), httpTaskManager.getEpics(),
                "Список эпиков после выгрузки не совпадает");
    }

    @Test
    public void historyOfHTTPTaskManagerShouldBeEqualsHistoryOfManager() {
        Task task1 = new Task("Task1",
                "Task1 for test historyOfHTTPTaskManagerShouldBeEqualsHistoryOfManager", null, Status.NEW,
                20, LocalDateTime.of(2023, 2, 26, 10, 0));
        manager.addTask(task1);
        manager.getTaskById(1);

        Task task2 = new Task("Task2",
                "Task2 for test historyOfHTTPTaskManagerShouldBeEqualsHistoryOfManager", null, Status.NEW,
                20, LocalDateTime.of(2023, 4, 30, 10, 25));
        manager.addTask(task2);
        manager.getTaskById(2);

        Task task3 = new Task("Task3",
                "Task3 for test historyOfHTTPTaskManagerShouldBeEqualsHistoryOfManager", null, Status.NEW,
                20, LocalDateTime.of(2023, 1, 31, 11, 0));
        manager.addTask(task3);
        manager.getTaskById(3);

        httpTaskManager = new HTTPTaskManager("8078", true);
        assertEquals(manager.getHistoryManager().getHistory(), httpTaskManager.getHistoryManager().getHistory(),
                "История после выгрузки не совпадает");
    }

    @Test
    public void prioritizedTasksOfHTTPTaskManagerShouldBeEqualsPrioritizedTasksOfManager() {
        Task task1 = new Task("Task1",
                "Task1 for test prioritizedTasksOfHTTPTaskManagerShouldBeEqualsPrioritizedTasksOfManager", null, Status.NEW,
                20, LocalDateTime.of(2023, 2, 26, 10, 0));
        manager.addTask(task1);

        Task task2 = new Task("Task2",
                "Task2 for test prioritizedTasksOfHTTPTaskManagerShouldBeEqualsPrioritizedTasksOfManager", null, Status.NEW,
                20, LocalDateTime.of(2023, 4, 30, 10, 25));
        manager.addTask(task2);

        Task task3 = new Task("Task3",
                "Task3 for test prioritizedTasksOfHTTPTaskManagerShouldBeEqualsPrioritizedTasksOfManager", null, Status.NEW,
                20, LocalDateTime.of(2023, 1, 31, 11, 0));
        manager.addTask(task3);

        httpTaskManager = new HTTPTaskManager("8078", true);
        assertEquals(manager.getPrioritizedTasks(), httpTaskManager.getPrioritizedTasks(),
                "Очередь задач после выгрузки не совпадает");
    }

    @Test
    public void idOfTasksShouldBeCorrect() {
        Task task1 = new Task("Task1",
                "Task1 for test idOfTasksShouldBeCorrect", null, Status.NEW,
                20, LocalDateTime.of(2023, 2, 26, 10, 0));
        manager.addTask(task1);

        Task task2 = new Task("Task2",
                "Task2 for test idOfTasksShouldBeCorrect", null, Status.NEW,
                20, LocalDateTime.of(2023, 4, 30, 10, 25));
        manager.addTask(task2);

        Task task3 = new Task("Task3",
                "Task3 for test idOfTasksShouldBeCorrect", null, Status.NEW,
                20, LocalDateTime.of(2023, 1, 31, 11, 0));
        manager.addTask(task3);

        httpTaskManager = new HTTPTaskManager("8078", true);
        assertEquals(task1, httpTaskManager.getTasks().get(0),
                "id задач неверный");
        assertEquals(task2, httpTaskManager.getTasks().get(1),
                "id задач неверный");
        assertEquals(task3, httpTaskManager.getTasks().get(2),
                "id задач неверный");
    }

    @Test
    public void counterIdShouldBeCorrect() {
        Task task1 = new Task("Task1",
                "Task1 for test counterIdShouldBeCorrect", null, Status.NEW,
                20, LocalDateTime.of(2023, 2, 26, 10, 0));
        manager.addTask(task1);

        Task task2 = new Task("Task2",
                "Task2 for test counterIdShouldBeCorrect", null, Status.NEW,
                20, LocalDateTime.of(2023, 4, 30, 10, 25));
        manager.addTask(task2);

        Task task3 = new Task("Task3",
                "Task3 for test counterIdShouldBeCorrect", null, Status.NEW,
                20, LocalDateTime.of(2023, 1, 31, 11, 0));
        manager.addTask(task3);

        Task task4 = new Task("Task4",
                "Task4 for test counterIdShouldBeCorrect", null, Status.NEW,
                25, LocalDateTime.of(2023, 3, 26, 11, 0));
        manager.addTask(task4);

        Epic epic = new Epic("Epic",
                "Epic for test counterIdShouldBeCorrect", null, Status.DONE, null, null);
        manager.addEpic(epic);

        Subtask subtask = new Subtask("Subtask",
                "Subtask for test counterIdShouldBeCorrect", epic.getId(), null, Status.NEW,
                200, LocalDateTime.of(2023, 1, 4, 18, 29));
        manager.addSubtask(subtask);

        httpTaskManager = new HTTPTaskManager("8078", true);
        assertEquals(manager.getCounterId(), httpTaskManager.getCounterId(),
                "counterId неверный");
    }
}