import managers.InMemoryTaskManager;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();

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

        taskManager.getTaskById(1);
        taskManager.getTaskById(2);
        taskManager.getEpicById(3);
        taskManager.getSubtaskById(4);
        taskManager.getSubtaskById(5);
        taskManager.getSubtaskById(6);
        taskManager.getEpicById(7);
        taskManager.removeTaskById(1);
        taskManager.removeEpicById(3);
        taskManager.getTaskById(2);
        List<Task> tasks = taskManager.getHistoryManager().getHistory();
        for (Task task : tasks) {
            System.out.println(task);
        }
    }
}
