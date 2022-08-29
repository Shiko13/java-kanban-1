package managers;

import exceptions.ManagerSaveException;
import exceptions.TestFailedException;
import tasks.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager {

    private final File file;

    private static List<Integer> history = new ArrayList<>();

    public FileBackedTasksManager(File file) {
        this.file = file;
    }

    public static void main(String[] args) throws TestFailedException {
        File file = new File("src/resources/log.csv");
        TaskManager withoutFileBackedTasksManager = new FileBackedTasksManager(file);

        createTasks(withoutFileBackedTasksManager);
        withoutFileBackedTasksManager.getTaskById(2);
        withoutFileBackedTasksManager.getTaskById(1);
        withoutFileBackedTasksManager.getEpicById(7);
        withoutFileBackedTasksManager.getSubtaskById(5);

        TaskManager fileBackedTasksManager = loadFromFile(file);

        tasksShouldBeEquals(withoutFileBackedTasksManager, fileBackedTasksManager);
        subtasksShouldBeEquals(withoutFileBackedTasksManager, fileBackedTasksManager);
        epicsShouldBeEquals(withoutFileBackedTasksManager, fileBackedTasksManager);
        viewHistoryShouldBeEquals(withoutFileBackedTasksManager, fileBackedTasksManager);
    }

    private static void createTasks(TaskManager withoutFileBackedTasksManager) {
        int idTask1 = withoutFileBackedTasksManager.
                addTask(new Task("Task1", "Description of Task1", Status.NEW));
        int idTask2 = withoutFileBackedTasksManager.
                addTask(new Task("Task2", "Description of Task2", Status.DONE));
        Epic epic1 = withoutFileBackedTasksManager.
                addEpic(new Epic("Epic1", "Description of epic1", Status.NEW));
        Subtask subtask1 = withoutFileBackedTasksManager.
                addSubtask(new Subtask("Subtask1", "Description of subtask1",
                3, Status.NEW));
        Subtask subtask2 = withoutFileBackedTasksManager.
                addSubtask(new Subtask("Subtask2", "Description of subtask2",
                3, Status.NEW));
        Subtask subtask3 = withoutFileBackedTasksManager.
                addSubtask(new Subtask("Subtask3", "Description of subtask3",
                3, Status.NEW));
        Epic epic2 = withoutFileBackedTasksManager.
                addEpic(new Epic("Epic2", "Description of epic2", Status.NEW));
    }

    private static void viewHistoryShouldBeEquals(TaskManager taskManagerWithoutFileBacked, TaskManager fileBackedTasksManager) throws TestFailedException {
        List<Task> tasksWithoutFileBacked = taskManagerWithoutFileBacked.getHistoryManager().getHistory();
        List<Task> tasksWithFileBacked = fileBackedTasksManager.getHistoryManager().getHistory();
        for (int i = 0; i < tasksWithoutFileBacked.size(); i++) {
            if (!tasksWithoutFileBacked.get(i).equals(tasksWithFileBacked.get(i))) {
                throw new TestFailedException("Не сходится история просмотра");
            }
        }
    }

    private static void epicsShouldBeEquals(TaskManager taskManagerWithoutFileBacked, TaskManager fileBackedTasksManager) throws TestFailedException {
        List<Epic> epicsWithoutFileBacked = taskManagerWithoutFileBacked.getEpics();
        List<Epic> epicsWithFileBacked = fileBackedTasksManager.getEpics();
        for (int i = 0; i < epicsWithoutFileBacked.size(); i++) {
            if (!epicsWithoutFileBacked.get(i).equals(epicsWithFileBacked.get(i))) {
                throw new TestFailedException("Не сходятся эпики");
            }
        }
    }

    private static void subtasksShouldBeEquals(TaskManager taskManagerWithoutFileBacked, TaskManager fileBackedTasksManager) throws TestFailedException {
        List<Subtask> subtasksWithoutFileBacked = taskManagerWithoutFileBacked.getSubtasks();
        List<Subtask> subtasksWithFileBacked = fileBackedTasksManager.getSubtasks();
        for (int i = 0; i < subtasksWithoutFileBacked.size(); i++) {
            if (!subtasksWithoutFileBacked.get(i).equals(subtasksWithFileBacked.get(i))) {
                throw new TestFailedException("Не сходятся сабтаски");
            }
        }
    }

    private static void tasksShouldBeEquals(TaskManager taskManagerWithoutFileBacked, TaskManager fileBackedTasksManager) throws TestFailedException {
        List<Task> tasksWithoutFileBacked = taskManagerWithoutFileBacked.getTasks();
        List<Task> tasksWithFileBacked = fileBackedTasksManager.getTasks();
        for (int i = 0; i < tasksWithoutFileBacked.size(); i++) {
            if (!tasksWithoutFileBacked.get(i).equals(tasksWithFileBacked.get(i))) {
                throw new TestFailedException("Не сходятся таски");
            }
        }
    }

    public static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(file);

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            bufferedReader.readLine();
            String str;
            while ((str = bufferedReader.readLine()) != null) {
                if (!str.isEmpty()) {
                    Task task = fromString(str);
                    switch (task.getTypeOfTask()) {
                        case TASK -> fileBackedTasksManager.tasks.put(task.getId(), task);
                        case EPIC -> fileBackedTasksManager.epics.put(task.getId(), (Epic) task);
                        case SUBTASK -> {
                                fileBackedTasksManager.subtasks.put(task.getId(), (Subtask) task);
                                fileBackedTasksManager.epics.get(((Subtask) task).getEpicId())
                                        .getSubtasksOfEpic().add(((Subtask) task).getId());
                        }
                    }
                } else break;
            }
            historyLoadFromFile(fileBackedTasksManager, bufferedReader);
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при считывании из файла");
        }
        return fileBackedTasksManager;
    }

    private static void historyLoadFromFile(FileBackedTasksManager fileBackedTasksManager, BufferedReader bufferedReader) throws IOException {
        String str;
        if ((str = bufferedReader.readLine()) != null) {
            history = historyFromString(str);
            for (Integer id : history) {
                if (fileBackedTasksManager.tasks.containsKey(id)) {
                    Task task = fileBackedTasksManager.tasks.get(id);
                    fileBackedTasksManager.getHistoryManager().add(task);
                }
                if (fileBackedTasksManager.epics.containsKey(id)) {
                    Epic epic = fileBackedTasksManager.epics.get(id);
                    fileBackedTasksManager.getHistoryManager().add(epic);
                }
                if (fileBackedTasksManager.subtasks.containsKey(id)) {
                    Subtask subtask = fileBackedTasksManager.subtasks.get(id);
                    fileBackedTasksManager.getHistoryManager().add(subtask);
                }
            }
        }
    }

    private void save() throws ManagerSaveException {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            String header = "id,type,name,status,description,epic";
            bufferedWriter.append(header);
            bufferedWriter.newLine();

            for (Task task : getTasks()) {
                bufferedWriter.write(task.toString());
                bufferedWriter.newLine();
            }
            for (Epic epic : getEpics()) {
                bufferedWriter.write(epic.toString());
                bufferedWriter.newLine();
            }
            for (Subtask subtask : getSubtasks()) {
                bufferedWriter.write(subtask.toString());
                bufferedWriter.newLine();
            }
            String history = historyToString(getHistoryManager());
            bufferedWriter.newLine();
            bufferedWriter.append(history);
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении в файл");
        }
    }

    private static Task fromString(String value) {
        String[] splitTask = value.split(",");
        TypeOfTask typeOfTask = TypeOfTask.valueOf(splitTask[1]);
        if (typeOfTask == TypeOfTask.TASK) {
            return new Task(splitTask[2], splitTask[3],
                    Integer.valueOf(splitTask[0]), Status.valueOf(splitTask[4]));
        } else if (typeOfTask == TypeOfTask.EPIC) {
            return new Epic(splitTask[2], splitTask[3],
                    Integer.valueOf(splitTask[0]), Status.valueOf(splitTask[4]));
        } else if (typeOfTask == TypeOfTask.SUBTASK) {
            return new Subtask(splitTask[2], splitTask[3], Integer.parseInt(splitTask[5]),
                    Integer.parseInt(splitTask[0]), Status.valueOf(splitTask[4]));
        }
        throw new ManagerSaveException("Элемент отсутствует");
    }

    private static String historyToString(HistoryManager manager) {
        List<Task> tasks = manager.getHistory();
        String[] tasksArray = new String[tasks.size()];
        for (int i = 0; i < tasks.size(); i++) {
            tasksArray[i] = String.valueOf(tasks.get(i).getId());
        }
        return String.join(",", tasksArray);
    }

    private static List<Integer> historyFromString(String value) {
        String[] split = value.split(",");
        List<Integer> id = new ArrayList<>();
        for (String taskId : split) {
            id.add(Integer.valueOf(taskId));
        }
        return id;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public int addTask(Task task) {
        int i = super.addTask(task);
        save();
        return i;
    }

    @Override
    public Subtask addSubtask(Subtask subtask) {
        Subtask newSubtask = super.addSubtask(subtask);
        save();
        return newSubtask;
    }

    @Override
    public Epic addEpic(Epic epic) {
        Epic newEpic = super.addEpic(epic);
        save();
        return newEpic;
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public Task getTaskById(Integer id) {
        Task newTask = super.getTaskById(id);
        save();
        return newTask;
    }

    @Override
    public Subtask getSubtaskById(Integer id) {
        Subtask newSubtask = super.getSubtaskById(id);
        save();
        return newSubtask;
    }

    @Override
    public Epic getEpicById(Integer id) {
        Epic newEpic = super.getEpicById(id);
        save();
        return newEpic;
    }

    @Override
    public void removeTaskById(Integer id) {
        super.removeTaskById(id);
        save();
    }

    @Override
    public void removeSubtaskById(Integer id) {
        super.removeSubtaskById(id);
        save();
    }

    @Override
    public void removeEpicById(Integer id) {
        super.removeEpicById(id);
        save();
    }

    public static List<Integer> getHistory() {
        return history;
    }
}
