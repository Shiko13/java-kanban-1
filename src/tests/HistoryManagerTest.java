package tests;

import managers.*;
import tasks.*;

import java.time.LocalDateTime;
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
        taskManager = Managers.getFileBackedTasksManager("src/resources/log.csv");
    }

    @Test
    public void getHistory() {
        Task task1 = new Task("Task1",
                "Task1 for test getHistory", null, Status.NEW,
                200, LocalDateTime.of(2023, 01, 04, 18, 29));
        taskManager.addTask(task1);
        historyManager.add(task1);

        Task task2 = new Task("Task2 ",
                "Task2 for test getHistory", null, Status.DONE,
                200, LocalDateTime.of(2023, 02, 04, 18, 29));
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
                "Task for test add", null, Status.NEW,
                200, LocalDateTime.of(2023, 01, 04, 18, 29));
        taskManager.addTask(task);
        historyManager.add(task);

        final List<Task> history = historyManager.getHistory();

        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
    }

    @Test
    public void removeFromBegin() {
        Task task1 = new Task("Task1",
                "Task1 for test removeFromBegin", null, Status.NEW,
                200, LocalDateTime.of(2023, 01, 04, 18, 29));
        taskManager.addTask(task1);
        historyManager.add(task1);

        Task task2 = new Task("Task2",
                "Task2 for test removeFromBegin", null, Status.DONE,
                200, LocalDateTime.of(2023, 01, 05, 18, 29));
        taskManager.addTask(task2);
        historyManager.add(task2);

        Task task3 = new Task("Task3",
                "Task3 for test removeFromBegin", null, Status.IN_PROGRESS,
                200, LocalDateTime.of(2023, 01, 06, 18, 29));
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
                "Task1 for test removeFromMiddle", null, Status.NEW,
                200, LocalDateTime.of(2023, 01, 04, 18, 29));
        taskManager.addTask(task1);
        historyManager.add(task1);

        Task task2 = new Task("Task2",
                "Task2 for test removeFromMiddle", null, Status.DONE,
                200, LocalDateTime.of(2023, 02, 04, 18, 29));
        taskManager.addTask(task2);
        historyManager.add(task2);

        Task task3 = new Task("Task3",
                "Task3 for test removeFromMiddle", null, Status.IN_PROGRESS,
                200, LocalDateTime.of(2023, 03, 04, 18, 29));
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
                "Task1 for test removeFromEnd", null, Status.NEW,
                200, LocalDateTime.of(2023, 01, 04, 18, 29));
        taskManager.addTask(task1);
        historyManager.add(task1);

        Task task2 = new Task("Task2",
                "Task2 for test removeFromEnd", null, Status.DONE,
                200, LocalDateTime.of(2023, 02, 04, 18, 29));
        taskManager.addTask(task2);
        historyManager.add(task2);

        Task task3 = new Task("Task3",
                "Task3 for test removeFromEnd", null, Status.IN_PROGRESS,
                200, LocalDateTime.of(2023, 03, 04, 18, 29));
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
                "Task1 for test addDuplicate", null, Status.NEW,
                200, LocalDateTime.of(2023, 01, 04, 18, 29));
        taskManager.addTask(task1);
        historyManager.add(task1);

        Task task2 = new Task("Task2",
                "Task2 for test addDuplicate", null, Status.DONE,
                200, LocalDateTime.of(2023, 02, 04, 18, 29));
        taskManager.addTask(task2);
        historyManager.add(task2);

        Task task3 = new Task("Task3",
                "Task3 for test addDuplicate", null, Status.IN_PROGRESS,
                200, LocalDateTime.of(2023, 03, 04, 18, 29));
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