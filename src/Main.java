import managers.*;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;
import exceptions.TestFailedException;

import java.util.List;

public class Main {
    public static void main(String[] args) throws TestFailedException {
        checkHistory_ShouldBeRightOrderAndNoDuplicates();
        checkHistory_ShouldNotContainDeletedTask();
        checkHistory_ShouldNotContainDeletedEpicAndHisSubtasks();
        check_CorrectCreationTasks();
        check_CorrectCreationEpics();
        check_CorrectCreationSubtasks();
        check_CorrectDeletionTasks();
        check_CorrectDeletionEpics();
        check_CorrectDeletionSubtasksById();
        check_CorrectDeletionAllTasks();
        check_CorrectDeletionAllEpics();
        check_CorrectDeletionAllSubtasks();
        check_CorrectUpdateTasks();
        check_CorrectUpdateEpics();
        check_CorrectUpdateSubtasks();
        check_StatusOfEpicAfterChangingOfSubtask();
        check_StatusOfEpicAfterDeletionOfSubtask();
    }

    public static void createTasks(TaskManager taskManager) {
        int idTask1 = taskManager.addTask(new Task("Task1", "Description of task1", Status.NEW));
        int idTask2 = taskManager.addTask(new Task("Task2", "Description of task2", Status.NEW));
        Epic epic1 = taskManager.addEpic(new Epic("Epic1", "Description of epic1", Status.NEW));
        Subtask subtask1 = taskManager.addSubtask(new Subtask("Subtask1", "Description of subtask1",
                3, Status.NEW));
        Subtask subtask2 = taskManager.addSubtask(new Subtask("Subtask2", "Description of subtask2",
                3, Status.NEW));
        Subtask subtask3 = taskManager.addSubtask(new Subtask("Subtask3", "Description of subtask3",
                3, Status.NEW));
        Epic epic2 = taskManager.addEpic(new Epic("Epic2", "Description of epic2", Status.NEW));
    }

    public static void checkHistory_ShouldBeRightOrderAndNoDuplicates() throws TestFailedException {
        TaskManager taskManager = Managers.getDefault();
        createTasks(taskManager);

        taskManager.getSubtaskById(5);
        taskManager.getTaskById(2);
        taskManager.getSubtaskById(6);
        taskManager.getTaskById(1);
        taskManager.getEpicById(3);
        taskManager.getEpicById(7);
        taskManager.getSubtaskById(4);
        taskManager.getEpicById(3);
        taskManager.getSubtaskById(5);
        taskManager.getTaskById(1);
        taskManager.getEpicById(7);

        List<Task> actual = taskManager.getHistoryManager().getHistory();
        List<Task> expected = List.of(
                taskManager.getTaskById(2),
                taskManager.getSubtaskById(6),
                taskManager.getSubtaskById(4),
                taskManager.getEpicById(3),
                taskManager.getSubtaskById(5),
                taskManager.getTaskById(1),
                taskManager.getEpicById(7)
        );

        if (!actual.equals(expected)) {
            throw new TestFailedException("Actual history does not equals expected");
        }
    }

    public static void checkHistory_ShouldNotContainDeletedTask() throws TestFailedException {
        TaskManager taskManager = Managers.getDefault();
        createTasks(taskManager);

        taskManager.getTaskById(1);
        taskManager.getTaskById(2);
        taskManager.getEpicById(3);
        taskManager.getSubtaskById(4);
        taskManager.getSubtaskById(5);
        taskManager.getSubtaskById(6);
        taskManager.getEpicById(7);
        taskManager.removeTaskById(1);

        List<Task> actual = taskManager.getHistoryManager().getHistory();
        List<Task> expected = List.of(
                taskManager.getTaskById(2),
                taskManager.getEpicById(3),
                taskManager.getSubtaskById(4),
                taskManager.getSubtaskById(5),
                taskManager.getSubtaskById(6),
                taskManager.getEpicById(7)
        );

        if (!actual.equals(expected)) {
            throw new TestFailedException("Actual history does not equals expected");
        }
    }

    public static void checkHistory_ShouldNotContainDeletedEpicAndHisSubtasks() throws TestFailedException {
        TaskManager taskManager = Managers.getDefault();
        createTasks(taskManager);

        taskManager.getTaskById(1);
        taskManager.getTaskById(2);
        taskManager.getEpicById(3);
        taskManager.getSubtaskById(4);
        taskManager.getSubtaskById(5);
        taskManager.getSubtaskById(6);
        taskManager.getEpicById(7);
        taskManager.removeEpicById(3);

        List<Task> actual = taskManager.getHistoryManager().getHistory();
        List<Task> expected = List.of(
                taskManager.getTaskById(1),
                taskManager.getTaskById(2),
                taskManager.getEpicById(7)
        );

        if (!actual.equals(expected)) {
            throw new TestFailedException("Actual history does not equals expected");
        }
    }

    public static void check_CorrectCreationTasks() throws TestFailedException {
        TaskManager taskManager = Managers.getDefault();

        int idTask1 = taskManager.addTask(new Task("Task1", "Description of task1", Status.NEW));
        int idTask2 = taskManager.addTask(new Task("Task2", "Description of task2", Status.NEW));
        int idTask3 = taskManager.addTask(new Task("Task3", "Description of task3", Status.NEW));
        int idTask4 = taskManager.addTask(new Task("Task4", "Description of task4", Status.NEW));
        int idTask5 = taskManager.addTask(new Task("Task5", "Description of task5", Status.NEW));
        int idTask6 = taskManager.addTask(new Task("Task6", "Description of task6", Status.NEW));

        List<Task> actual = taskManager.getTasks();

        List<String> expectedNamesOfTasks = List.of(
                "Task1",
                "Task2",
                "Task3",
                "Task4",
                "Task5",
                "Task6"
        );

        for (Task task : actual) {
            if (!expectedNamesOfTasks.contains(task.getName())) {
                throw new TestFailedException("Actual list of tasks does not equals expected");
            }
        }
    }

    public static void check_CorrectCreationEpics() throws TestFailedException {
        TaskManager taskManager = Managers.getDefault();

        Epic epic1 = taskManager.addEpic(new Epic("Epic1", "Description of epic1", Status.NEW));
        Subtask subtask1 = taskManager.addSubtask(new Subtask("Subtask1", "Description of subtask1",
                1, Status.NEW));
        Subtask subtask2 = taskManager.addSubtask(new Subtask("Subtask2", "Description of subtask2",
                1, Status.NEW));
        Subtask subtask3 = taskManager.addSubtask(new Subtask("Subtask3", "Description of subtask3",
                1, Status.NEW));
        Epic epic2 = taskManager.addEpic(new Epic("Epic2", "Description of epic2", Status.NEW));

        List<Epic> actual = taskManager.getEpics();

        List<String> expectedNamesOfEpics = List.of(
                "Epic1",
                "Epic2"
        );

        for (Epic epic : actual) {
            if (!expectedNamesOfEpics.contains(epic.getName())) {
                throw new TestFailedException("Actual list of epics does not equals expected");
            }
        }
    }

    public static void check_CorrectCreationSubtasks() throws TestFailedException {
        TaskManager taskManager = Managers.getDefault();

        Epic epic1 = taskManager.addEpic(new Epic("Epic1", "Description of epic1", Status.NEW));
        Subtask subtask1 = taskManager.addSubtask(new Subtask("Subtask1", "Description of subtask1",
                1, Status.NEW));
        Subtask subtask2 = taskManager.addSubtask(new Subtask("Subtask2", "Description of subtask2",
                1, Status.NEW));
        Subtask subtask3 = taskManager.addSubtask(new Subtask("Subtask3", "Description of subtask3",
                1, Status.NEW));

        List<Subtask> actual = taskManager.getSubtasks();

        List<String> expectedNamesOfSubtasks = List.of(
                "Subtask1",
                "Subtask2",
                "Subtask3"
        );

        for (Subtask subtask : actual) {
            if (!expectedNamesOfSubtasks.contains(subtask.getName())) {
                throw new TestFailedException("Actual list of subtasks does not equals expected");
            }
        }
    }

    public static void check_CorrectDeletionTasks() throws TestFailedException {
        TaskManager taskManager = Managers.getDefault();

        int idTask1 = taskManager.addTask(new Task("Task1", "Description of task1", Status.NEW));
        int idTask2 = taskManager.addTask(new Task("Task2", "Description of task2", Status.NEW));
        int idTask3 = taskManager.addTask(new Task("Task3", "Description of task3", Status.NEW));
        int idTask4 = taskManager.addTask(new Task("Task4", "Description of task4", Status.NEW));
        int idTask5 = taskManager.addTask(new Task("Task5", "Description of task5", Status.NEW));
        int idTask6 = taskManager.addTask(new Task("Task6", "Description of task6", Status.NEW));
        taskManager.removeTaskById(2);
        taskManager.removeTaskById(4);
        taskManager.removeTaskById(6);

        List<Task> actual = taskManager.getTasks();

        List<String> expectedNamesOfTasks = List.of(
                "Task1",
                "Task3",
                "Task5"
        );

        for (Task task : actual) {
            if (!expectedNamesOfTasks.contains(task.getName())) {
                throw new TestFailedException("Actual list of tasks does not equals expected");
            }
        }
    }

    public static void check_CorrectDeletionEpics() throws TestFailedException {
        TaskManager taskManager = Managers.getDefault();

        Epic epic1 = taskManager.addEpic(new Epic("Epic1", "Description of epic1", Status.NEW));
        Subtask subtask1 = taskManager.addSubtask(new Subtask("Subtask1", "Description of subtask1",
                1, Status.NEW));
        Subtask subtask2 = taskManager.addSubtask(new Subtask("Subtask2", "Description of subtask2",
                1, Status.NEW));
        Subtask subtask3 = taskManager.addSubtask(new Subtask("Subtask3", "Description of subtask3",
                1, Status.NEW));
        Epic epic2 = taskManager.addEpic(new Epic("Epic2", "Description of epic2", Status.NEW));
        Epic epic3 = taskManager.addEpic(new Epic("Epic3", "Description of epic3", Status.NEW));
        taskManager.removeEpicById(1);
        taskManager.removeEpicById(5);

        List<Epic> actual = taskManager.getEpics();

        List<String> expectedNamesOfEpics = List.of(
                "Epic3"
        );

        for (Epic epic : actual) {
            if (!expectedNamesOfEpics.contains(epic.getName())) {
                throw new TestFailedException("Actual list of epics does not equals expected");
            }
        }
    }

    public static void check_CorrectDeletionSubtasksById() throws TestFailedException {
        TaskManager taskManager = Managers.getDefault();

        Epic epic1 = taskManager.addEpic(new Epic("Epic1", "Description of epic1", Status.NEW));
        Subtask subtask1 = taskManager.addSubtask(new Subtask("Subtask1", "Description of subtask1",
                1, Status.NEW));
        Subtask subtask2 = taskManager.addSubtask(new Subtask("Subtask2", "Description of subtask2",
                1, Status.NEW));
        Subtask subtask3 = taskManager.addSubtask(new Subtask("Subtask3", "Description of subtask3",
                1, Status.NEW));
        taskManager.removeSubtaskById(3);
        taskManager.removeSubtaskById(4);

        List<Subtask> actual = taskManager.getSubtasks();

        List<String> expectedNamesOfSubtasks = List.of(
                "Subtask1"
        );

        for (Subtask subtask : actual) {
            if (!expectedNamesOfSubtasks.contains(subtask.getName())) {
                throw new TestFailedException("Actual list of subtasks does not equals expected");
            }
        }
    }

    public static void check_CorrectDeletionAllTasks() throws TestFailedException {
        TaskManager taskManager = Managers.getDefault();

        int idTask1 = taskManager.addTask(new Task("Task1", "Description of task1", Status.NEW));
        int idTask2 = taskManager.addTask(new Task("Task2", "Description of task2", Status.NEW));
        int idTask3 = taskManager.addTask(new Task("Task3", "Description of task3", Status.NEW));
        int idTask4 = taskManager.addTask(new Task("Task4", "Description of task4", Status.NEW));
        int idTask5 = taskManager.addTask(new Task("Task5", "Description of task5", Status.NEW));
        int idTask6 = taskManager.addTask(new Task("Task6", "Description of task6", Status.NEW));
        taskManager.removeAllTasks();

        List<Task> actual = taskManager.getTasks();

        if (actual.size() != 0) {
            throw new TestFailedException("Actual list of tasks does not equals expected");
        }
    }

    public static void check_CorrectDeletionAllEpics() throws TestFailedException {
        TaskManager taskManager = Managers.getDefault();

        Epic epic1 = taskManager.addEpic(new Epic("Epic1", "Description of epic1", Status.NEW));
        Subtask subtask1 = taskManager.addSubtask(new Subtask("Subtask1", "Description of subtask1",
                1, Status.NEW));
        Subtask subtask2 = taskManager.addSubtask(new Subtask("Subtask2", "Description of subtask2",
                1, Status.NEW));
        Subtask subtask3 = taskManager.addSubtask(new Subtask("Subtask3", "Description of subtask3",
                1, Status.NEW));
        Epic epic2 = taskManager.addEpic(new Epic("Epic2", "Description of epic2", Status.NEW));
        Epic epic3 = taskManager.addEpic(new Epic("Epic3", "Description of epic3", Status.NEW));
        taskManager.removeAllEpics();

        List<Epic> actualEpics = taskManager.getEpics();
        List<Subtask> actualSubtasks = taskManager.getSubtasks();

        if (actualEpics.size() != 0 || actualSubtasks.size() != 0) {
            throw new TestFailedException("Actual list of epics or list of subtasks does not equals expected");
        }
    }

    public static void check_CorrectDeletionAllSubtasks() throws TestFailedException {
        TaskManager taskManager = Managers.getDefault();

        Epic epic1 = taskManager.addEpic(new Epic("Epic1", "Description of epic1", Status.NEW));
        Subtask subtask1 = taskManager.addSubtask(new Subtask("Subtask1", "Description of subtask1",
                1, Status.NEW));
        Subtask subtask2 = taskManager.addSubtask(new Subtask("Subtask2", "Description of subtask2",
                1, Status.NEW));
        Subtask subtask3 = taskManager.addSubtask(new Subtask("Subtask3", "Description of subtask3",
                1, Status.NEW));
        taskManager.removeAllSubtasks();

        List<Subtask> actual = taskManager.getSubtasks();

        if (actual.size() != 0) {
            throw new TestFailedException("Actual list of subtasks does not equals expected");
        }
    }

    public static void check_CorrectUpdateTasks() throws TestFailedException {
        TaskManager taskManager = Managers.getDefault();

        int idTask1 = taskManager.addTask(new Task("Task1", "Description of task1", Status.NEW));
        int idTask2 = taskManager.addTask(new Task("Task2", "Description of task2", Status.NEW));

        taskManager.updateTask(new Task("Task1", "New description of task1", 1, Status.NEW));
        taskManager.updateTask(new Task("Task2", "Description of task2", 2, Status.DONE));

        if (!taskManager.getTaskById(1).getDescription().equals("New description of task1") ||
                !taskManager.getTaskById(2).getStatus().equals(Status.DONE)) {
            throw new TestFailedException("Update tasks does not work correct");
        }
    }

    public static void check_CorrectUpdateEpics() throws TestFailedException {
        TaskManager taskManager = Managers.getDefault();

        Epic epic1 = taskManager.addEpic(new Epic("Epic1", "Description of epic1", Status.NEW));
        Subtask subtask1 = taskManager.addSubtask(new Subtask("Subtask1", "Description of subtask1",
                1, Status.NEW));
        Subtask subtask2 = taskManager.addSubtask(new Subtask("Subtask2", "Description of subtask2",
                1, Status.NEW));
        Subtask subtask3 = taskManager.addSubtask(new Subtask("Subtask3", "Description of subtask3",
                1, Status.NEW));
        Epic epic2 = taskManager.addEpic(new Epic("Epic2", "Description of epic2", Status.NEW));

        taskManager.updateEpic(new Epic("Epic1", "New description of epic1", 1, Status.NEW));
        taskManager.updateEpic(new Epic("Epic2", "Description of epic2", 2, Status.DONE));

        if (!taskManager.getEpicById(1).getDescription().equals("New description of epic1") ||
                !taskManager.getEpicById(2).getStatus().equals(Status.DONE)) {
            throw new TestFailedException("Update epics does not work correct");
        }
    }

    public static void check_CorrectUpdateSubtasks() throws TestFailedException {
        TaskManager taskManager = Managers.getDefault();

        Epic epic1 = taskManager.addEpic(new Epic("Epic1", "Description of epic1", Status.NEW));
        Subtask subtask1 = taskManager.addSubtask(new Subtask("Subtask1", "Description of subtask1",
                1, Status.NEW));
        Subtask subtask2 = taskManager.addSubtask(new Subtask("Subtask2", "Description of subtask2",
                1, Status.NEW));
        Subtask subtask3 = taskManager.addSubtask(new Subtask("Subtask3", "Description of subtask3",
                1, Status.NEW));

        taskManager.updateSubtask(new Subtask("New name of subtask1", "Description of subtask1",
                1, 1, Status.NEW));
        taskManager.updateSubtask(new Subtask("Subtask2", "New description of subtask2",
                1, 2, Status.NEW));
        taskManager.updateSubtask(new Subtask("Subtask3", "Description of subtask3",
                1, 3, Status.DONE));

        if (!taskManager.getSubtaskById(1).getName().equals("New name of subtask1") ||
                !taskManager.getSubtaskById(2).getDescription().equals("New description of subtask2") ||
                !taskManager.getSubtaskById(3).getStatus().equals(Status.DONE)) {
            throw new TestFailedException("Update epics does not work correct");
        }
    }

    public static void check_StatusOfEpicAfterChangingOfSubtask() throws TestFailedException {
        TaskManager taskManager = Managers.getDefault();

        Epic epic1 = taskManager.addEpic(new Epic("Epic1", "Description of epic1", Status.NEW));
        Subtask subtask1 = taskManager.addSubtask(new Subtask("Subtask1", "Description of subtask1",
                1, Status.NEW));
        Subtask subtask2 = taskManager.addSubtask(new Subtask("Subtask2", "Description of subtask2",
                1, Status.NEW));
        Epic epic2 = taskManager.addEpic(new Epic("Epic2", "Description of epic2", Status.NEW));
        Subtask subtask3 = taskManager.addSubtask(new Subtask("Subtask3", "Description of subtask3",
                4, Status.NEW));
        Subtask subtask4 = taskManager.addSubtask(new Subtask("Subtask4", "Description of subtask4",
                4, Status.NEW));
        Epic epic3 = taskManager.addEpic(new Epic("Epic3", "Description of epic3", Status.NEW));
        Subtask subtask5 = taskManager.addSubtask(new Subtask("Subtask5", "Description of subtask5",
                7, Status.NEW));
        Subtask subtask6 = taskManager.addSubtask(new Subtask("Subtask6", "Description of subtask6",
                7, Status.NEW));
        Epic epic4 = taskManager.addEpic(new Epic("Epic4", "Description of epic4", Status.IN_PROGRESS));
        Subtask subtask7 = taskManager.addSubtask(new Subtask("Subtask7", "Description of subtask75",
                10, Status.IN_PROGRESS));
        Subtask subtask8 = taskManager.addSubtask(new Subtask("Subtask8", "Description of subtask8",
                10, Status.IN_PROGRESS));

        taskManager.updateSubtask(new Subtask("Subtask1", "Description of subtask1",
                1, 2, Status.DONE));
        taskManager.updateSubtask(new Subtask("Subtask2", "Description of subtask2",
                1, 3, Status.DONE));
        taskManager.updateSubtask(new Subtask("Subtask3", "Description of subtask3",
                4, 5, Status.IN_PROGRESS));
        taskManager.updateSubtask(new Subtask("Subtask4", "Description of subtask4",
                4, 6, Status.IN_PROGRESS));
        taskManager.updateSubtask(new Subtask("Subtask5", "Description of subtask5",
                7, 8, Status.DONE));
        taskManager.updateSubtask(new Subtask("Subtask6", "Description of subtask6",
                7, 9, Status.IN_PROGRESS));
        taskManager.updateSubtask(new Subtask("Subtask7", "Description of subtask7",
                10, 11, Status.NEW));
        taskManager.updateSubtask(new Subtask("Subtask8", "Description of subtask8",
                10, 12, Status.NEW));

        if (!taskManager.getEpicById(1).getStatus().equals(Status.DONE) ||
                !taskManager.getEpicById(4).getStatus().equals(Status.IN_PROGRESS) ||
                !taskManager.getEpicById(7).getStatus().equals(Status.IN_PROGRESS) ||
                !taskManager.getEpicById(10).getStatus().equals(Status.NEW)) {
            throw new TestFailedException("Update status of epic after changing his subtask does not work correct");
        }
    }

    public static void check_StatusOfEpicAfterDeletionOfSubtask() throws TestFailedException {
        TaskManager taskManager = Managers.getDefault();

        Epic epic1 = taskManager.addEpic(new Epic("Epic1", "Description of epic1", Status.NEW));
        Subtask subtask1 = taskManager.addSubtask(new Subtask("Subtask1", "Description of subtask1",
                1, Status.DONE));
        Subtask subtask2 = taskManager.addSubtask(new Subtask("Subtask2", "Description of subtask2",
                1, Status.IN_PROGRESS));
        Epic epic2 = taskManager.addEpic(new Epic("Epic2", "Description of epic2", Status.NEW));
        Subtask subtask3 = taskManager.addSubtask(new Subtask("Subtask3", "Description of subtask3",
                4, Status.NEW));
        Subtask subtask4 = taskManager.addSubtask(new Subtask("Subtask4", "Description of subtask4",
                4, Status.IN_PROGRESS
        ));
        Epic epic3 = taskManager.addEpic(new Epic("Epic3", "Description of epic3", Status.NEW));
        Subtask subtask5 = taskManager.addSubtask(new Subtask("Subtask5", "Description of subtask5",
                7, Status.DONE));
        Subtask subtask6 = taskManager.addSubtask(new Subtask("Subtask6", "Description of subtask6",
                7, Status.IN_PROGRESS));

        taskManager.removeSubtaskById(3);
        taskManager.removeSubtaskById(6);
        taskManager.removeSubtaskById(8);

        if (!taskManager.getEpicById(1).getStatus().equals(Status.DONE) ||
                !taskManager.getEpicById(4).getStatus().equals(Status.NEW) ||
                !taskManager.getEpicById(7).getStatus().equals(Status.IN_PROGRESS)) {
            throw new TestFailedException("Update status of epic after delete his subtask does not work correct");
        }
    }
}
