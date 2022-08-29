package tests;

import managers.*;
import tasks.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {

    private HistoryManager historyManager;
    private TaskManager taskManager;

    @BeforeEach
    public void createManagers() {
        historyManager = Managers.getDefaultHistory();
        taskManager = Managers.getDefault();
    }

    @Test
    public void getHistory() {
        Task task1 = new Task("Task1",
                "Task1 for test getHistory", Status.NEW);
        taskManager.addTask(task1);
        historyManager.add(task1);

        Task task2 = new Task("Task2 ",
                "Task2 for test getHistory", Status.DONE);
        taskManager.addTask(task2);
        historyManager.add(task2);

        List<Task> expected = List.of(
                task1,
                task2
        );

        List<Task> actual = historyManager.getHistory();
        assertEquals(expected, actual, "Неправильная история");
    }

    @Test
    public void add() {
        Task task = new Task("Task",
                "Task for test add", Status.NEW);
        taskManager.addTask(task);
        historyManager.add(task);

        final List<Task> history = historyManager.getHistory();

        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
    }

    @Test
    public void removeFromBegin() {
        Task task1 = new Task("Task1",
                "Task1 for test removeFromBegin", Status.NEW);
        taskManager.addTask(task1);
        historyManager.add(task1);

        Task task2 = new Task("Task2",
                "Task2 for test removeFromBegin", Status.DONE);
        taskManager.addTask(task2);
        historyManager.add(task2);

        Task task3 = new Task("Task3",
                "Task3 for test removeFromBegin", Status.IN_PROGRESS);
        taskManager.addTask(task3);
        historyManager.add(task3);

        historyManager.remove(1);

        List<Task> expected = List.of(
                task2,
                task3
        );

        final List<Task> actual = historyManager.getHistory();

        assertEquals(expected, actual, "Метод неверно удаляет из начала");
    }

    @Test
    public void removeFromMiddle() {
        Task task1 = new Task("Task1 ",
                "Task1 for test removeFromMiddle", Status.NEW);
        taskManager.addTask(task1);
        historyManager.add(task1);

        Task task2 = new Task("Task2",
                "Task2 for test removeFromMiddle", Status.DONE);
        taskManager.addTask(task2);
        historyManager.add(task2);

        Task task3 = new Task("Task3",
                "Task3 for test removeFromMiddle", Status.IN_PROGRESS);
        taskManager.addTask(task3);
        historyManager.add(task3);

        historyManager.remove(3);

        List<Task> expected = List.of(
                task1,
                task2
        );

        final List<Task> actual = historyManager.getHistory();

        assertEquals(expected, actual, "Метод неверно удаляет из середины");
    }

    @Test
    public void removeFromEnd() {
        Task task1 = new Task("Task1",
                "Task1 for test removeFromEnd", Status.NEW);
        taskManager.addTask(task1);
        historyManager.add(task1);

        Task task2 = new Task("Task2",
                "Task2 for test removeFromEnd", Status.DONE);
        taskManager.addTask(task2);
        historyManager.add(task2);

        Task task3 = new Task("Task3",
                "Task3 for test removeFromEnd", Status.IN_PROGRESS);
        taskManager.addTask(task3);
        historyManager.add(task3);

        historyManager.remove(2);

        List<Task> expected = List.of(
                task1,
                task3
        );

        final List<Task> actual = historyManager.getHistory();

        assertEquals(expected, actual, "Метод неверно удаляет с конца");
    }

    @Test
    public void addDuplicate() {
        Task task1 = new Task("Task1",
                "Task1 for test addDuplicate", Status.NEW);
        taskManager.addTask(task1);
        historyManager.add(task1);

        Task task2 = new Task("Task2",
                "Task2 for test addDuplicate", Status.DONE);
        taskManager.addTask(task2);
        historyManager.add(task2);

        Task task3 = new Task("Task3",
                "Task3 for test addDuplicate", Status.IN_PROGRESS);
        taskManager.addTask(task3);
        historyManager.add(task3);

        historyManager.add(task2);

        List<Task> expected = List.of(
                task1,
                task3,
                task2
        );

        final List<Task> actual = historyManager.getHistory();

        assertEquals(expected, actual, "Неверно обрабатываются дубликаты");
    }

    @Test
    public void shouldHistoryBeEmpty() {
        final List<Task> actual = historyManager.getHistory();
        List<Task> expected = new ArrayList<>();

        assertEquals(expected, actual, "История не пуста");
    }
}