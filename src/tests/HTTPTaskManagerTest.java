package tests;

import http.KVServer;
import managers.HTTPTaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class HTTPTaskManagerTest extends InMemoryTaskManagerTest {

    private KVServer server;
    HTTPTaskManager manager;

    @BeforeEach
    public void init() {
        try {
            server = new KVServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
        server.start();
        manager = new HTTPTaskManager("8078");
    }

    @AfterEach
    public void destroy() {
        server.stop();
    }

    @Test
    public void test1_shouldCreateAnEmptyHTTPManagerFromTheEmptyServer() {
        HTTPTaskManager loadedManager = manager.load("8078");
        assertTrue(loadedManager.getTasks().isEmpty(), "Коллекция не пуста.");
        assertTrue(loadedManager.getEpics().isEmpty(), "Коллекция не пуста.");
        assertTrue(loadedManager.getSubtasks().isEmpty(), "Коллекция не пуста.");
        assertTrue(loadedManager.getHistoryManager().getHistory().isEmpty(), "История не пуста.");
    }

    @Test
    public void test2_shouldCreateAnHTTPManagerFromTheServerWithOneEpic() {
        // Given
        Epic epic = new Epic("Epic",
                "Epic for test",
                1, Status.DONE, null, null);
        manager.addEpic(epic);
        // When
        HTTPTaskManager loadedManager = manager.load("8078");
        // Then
        assertTrue(loadedManager.getHistoryManager().getHistory().isEmpty(), "История не пуста.");
        assertTrue(loadedManager.getTasks().isEmpty(), "Коллекция не пуста.");
        assertTrue(loadedManager.getSubtasks().isEmpty(), "Коллекция не пуста.");
        assertFalse(loadedManager.getEpics().isEmpty(), "Задача не была добавлена.");
        assertEquals(epic, loadedManager.getEpics().get(0), "Задачи не совпадают.");
    }

    @Test
    public void test3_shouldCreateAnHTTPManagerFromTheServerWithEpicSubtaskHistory()
            throws IOException, InterruptedException {
        // Given
        Epic epic = new Epic("Epic",
                "Epic for test",
                1, Status.DONE, null, null);
        manager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask",
                "Subtask for test calculateEpicStatusIfSubtasksOfEpicIsEmptyAfterRemoveSubtask",
                1, 2, Status.IN_PROGRESS,
                200, LocalDateTime.of(2023, 1, 4, 18, 29));
        manager.addSubtask(subtask);
        manager.getSubtaskById(subtask.getId());
        manager.getEpicById(epic.getId());
        System.out.println(epic);
        System.out.println(subtask);
        // When
        HTTPTaskManager loadedManager = manager.load("8078");
        // Then
        assertTrue(loadedManager.getTasks().isEmpty(), "Коллекция не пуста.");

        assertFalse(loadedManager.getEpics().isEmpty(), "Задача не была добавлена.");
        assertEquals(epic, loadedManager.getEpics().get(0), "Задачи не совпадают.");
        assertFalse(loadedManager.getSubtasks().isEmpty(), "Задача не была добавлена.");
        assertEquals(subtask, loadedManager.getSubtasks().get(0), "Задачи не совпадают.");

        assertEquals(2, loadedManager.getHistoryManager().getHistory().size(), "Неверное количество задач.");
        assertTrue(loadedManager.getHistoryManager().getHistory().contains(subtask), "Задача на была добавлена в историю.");
        assertEquals(subtask, loadedManager.getHistoryManager().getHistory().get(0), "Неверный порядок отображения задач.");
        assertTrue(loadedManager.getHistoryManager().getHistory().contains(epic), "Задача не была добавлена в историю.");
        assertEquals(epic, loadedManager.getHistoryManager().getHistory().get(1), "Неверный порядок отображения задач.");
    }
}