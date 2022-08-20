package managers;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TypeOfTask;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileBackedManager extends InMemoryTaskManager {

    File file = new File("/resources/log.txt");

    public FileBackedManager(File file) {

    }

    void save() throws IOException {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            bufferedWriter.write(toString());
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения");
        }
    }

    private void addTaskToFile(BufferedWriter bufferedWriter, ArrayList<Task> tasks) throws IOException {
        for (Task task : tasks) {
            bufferedWriter.write(task.getId());
            bufferedWriter.write(task.getName());
            bufferedWriter.write(task.getDescription());
            bufferedWriter.newLine();
        }
    }

    String toString(Task task) {
        return task.getId() + "," + TypeOfTask.TASK + "," +
    }

    Task fromString(String value) {

    }

    static String historyToString(HistoryManager manager) {

    }

    static List<Integer> historyFromString(String value) {

    }

    @Override
    public Subtask addSubtask(Subtask subtask) {
        Subtask newSubtask = super.addSubtask(subtask);
        save();
        return newSubtask;
    }
}
