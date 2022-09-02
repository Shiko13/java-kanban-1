package tests;

import managers.*;
import tasks.*;

import static managers.FileBackedTasksManager.*;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {

    private final File file = new File("src/resources/log.csv");

    @BeforeEach
    public void createManager() {
        taskManager = (FileBackedTasksManager) Managers.getFileBackedTasksManager(file);
    }

    @Test
    void ifTasksEmpty() {
        Task task = new Task("Task",
                "Task for test ifTasksEmpty", null, Status.NEW,
                200, LocalDateTime.of(2023, 1, 4, 18, 29));
        taskManager.addTask(task);

        taskManager.getTaskById(1);
        taskManager.removeAllTasks();

        FileBackedTasksManager fileBackedTasksManager = loadFromFile(file);

        List<Task> savedTasks = fileBackedTasksManager.getHistoryManager().getHistory();
        List<Task> empty = new ArrayList<>();

        List<Integer> expected = List.of(1);
        List<Integer> actual = FileBackedTasksManager.getHistory();

        assertEquals(expected, actual, "Неверная история");
        assertEquals(empty, savedTasks, "Список задач не пустой");
    }

    @Test
    void ifEpicHaveNotSubtasks() {
        Epic epic = new Epic("Epic",
                "Epic for test ifEpicHaveNotSubtasks", null, Status.NEW, null, null);
        taskManager.addEpic(epic);

        FileBackedTasksManager fileBackedTasksManager = loadFromFile(file);

        List<Epic> actual = fileBackedTasksManager.getEpics();
        List<Epic> expected = List.of(epic);

        assertEquals(expected, actual, "Проблема с эпиками без подзадач");
    }

    @Test
    void ifHistoryEmpty() {
        Task task = new Task("Task",
                "Task for test ifHistoryEmpty", null, Status.NEW,
                200, LocalDateTime.of(2023, 1, 4, 18, 29));
        taskManager.addTask(task);

        List<Integer> expected = new ArrayList<>();
        List<Integer> actual = FileBackedTasksManager.getHistory();

        assertEquals(expected, actual, "Неверная история");
    }
}