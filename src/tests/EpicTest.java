package tests;

import managers.*;
import tasks.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    private TaskManager taskManager;

    @BeforeEach
    public void createTaskManager() {
        taskManager = Managers.getDefault();
    }

    @Test
    public void calculateEpicStatusIfSubtasksOfEpicIsEmptyAfterRemoveSubtask() {
        Epic epic = new Epic("Epic",
                "Epic for test calculateEpicStatusIfSubtasksOfEpicIsEmptyAfterRemoveSubtask",
                1, Status.DONE);
        taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Subtask",
                "Subtask for test calculateEpicStatusIfSubtasksOfEpicIsEmptyAfterRemoveSubtask",
                1, 2, Status.IN_PROGRESS);
        taskManager.addSubtask(subtask);

        assertEquals(epic.getStatus(), Status.IN_PROGRESS);

        taskManager.removeSubtaskById(2);

        assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    public void calculateEpicStatusIfAllSubtasksHaveStatusNew() {
        Epic epic = new Epic("Epic",
                "Epic for test calculateEpicStatusIfAllSubtasksHaveStatusNew",
                1, Status.DONE);
        taskManager.addEpic(epic);

        Subtask subtask1 = new Subtask("Subtask1",
                "Subtask1 for test calculateEpicStatusIfAllSubtasksHaveStatusNew",
                1, 2, Status.IN_PROGRESS);
        taskManager.addSubtask(subtask1);

        Subtask subtask2 = new Subtask("Subtask2",
                "Subtask2 for test calculateEpicStatusIfAllSubtasksHaveStatusNew",
                1, 3, Status.IN_PROGRESS);
        taskManager.addSubtask(subtask2);

        taskManager.updateSubtask(new Subtask("Subtask1",
                "New description of subtask1", 1, 2, Status.NEW));
        taskManager.updateSubtask(new Subtask("Subtask2",
                "New description of subtask2", 1, 3, Status.NEW));
        assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    public void calculateEpicStatusIfAllSubtasksHaveStatusDone() {
        Epic epic = new Epic("Epic",
                "Epic for test calculateEpicStatusIfAllSubtasksHaveStatusDone",
                1, Status.NEW);
        taskManager.addEpic(epic);

        Subtask subtask1 = new Subtask("Subtask1",
                "Subtask1 for test calculateEpicStatusIfAllSubtasksHaveStatusDone",
                1, 2, Status.DONE);
        taskManager.addSubtask(subtask1);

        Subtask subtask2 = new Subtask("Subtask2",
                "Subtask2 for test calculateEpicStatusIfAllSubtasksHaveStatusDone",
                1, 3, Status.DONE);
        taskManager.addSubtask(subtask2);

        assertEquals(Status.DONE, epic.getStatus());
    }

    @Test
    public void calculateEpicStatusIfAllSubtasksHaveStatusNewOrDone() {
        Epic epic = new Epic("Epic",
                "Epic for test calculateEpicStatusIfAllSubtasksHaveStatusNewOrDone",
                1, Status.NEW);
        taskManager.addEpic(epic);

        Subtask subtask1 = new Subtask("Subtask1",
                "Subtask1 for test calculateEpicStatusIfAllSubtasksHaveStatusNewOrDone",
                1, 2, Status.NEW);
        taskManager.addSubtask(subtask1);

        Subtask subtask2 = new Subtask("Subtask2",
                "Subtask2 for test calculateEpicStatusIfAllSubtasksHaveStatusNewOrDone",
                1, 3, Status.DONE);
        taskManager.addSubtask(subtask2);

        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    public void calculateEpicStatusIfAllSubtasksHaveStatusInProgress() {
        Epic epic = new Epic("Epic",
                "Epic for test calculateEpicStatusIfAllSubtasksHaveStatusInProgress",
                1, Status.NEW);
        taskManager.addEpic(epic);

        Subtask subtask1 = new Subtask("Subtask1",
                "Subtask1 for test calculateEpicStatusIfAllSubtasksHaveStatusInProgress",
                1, 2, Status.IN_PROGRESS);
        taskManager.addSubtask(subtask1);

        Subtask subtask2 = new Subtask("Subtask2",
                "Subtask1 for test calculateEpicStatusIfAllSubtasksHaveStatusInProgress",
                1, 3, Status.IN_PROGRESS);
        taskManager.addSubtask(subtask2);

        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }
}