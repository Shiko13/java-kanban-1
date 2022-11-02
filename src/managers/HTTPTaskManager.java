package managers;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import http.KVTaskClient;
import http.LocalDateTimeAdapter;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class HTTPTaskManager extends FileBackedTasksManager {
    private final KVTaskClient client;
    private final Gson gson = new GsonBuilder().
            registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    public HTTPTaskManager(String port, boolean isLoad) {
        super(port);
        client = new KVTaskClient(port);
        if (isLoad) {
            load();
        }
    }

    public HTTPTaskManager(String port) {
        this(port, false);
    }

    @Override
    void save() {
        client.put("tasks", gson.toJson(tasks));
        client.put("epics", gson.toJson(epics));
        client.put("subtasks", gson.toJson(subtasks));
        client.put("history", gson.toJson(historyManager.getHistory().stream().map(Task::getId).collect(Collectors.toList())));
    }

    private void load() {
        String jsonTask = client.load("tasks");
        if (!jsonTask.isEmpty()) {
            tasks = gson.fromJson(jsonTask,
                    new TypeToken<Map<Integer, Task>>() {
                    }.getType());
        }

        String jsonEpic = client.load("epics");
        if (!jsonEpic.isEmpty()) {
            epics = gson.fromJson(jsonEpic,
                    new TypeToken<Map<Integer, Epic>>() {
                    }.getType());
        }

        String jsonSubtask = client.load("subtasks");
        if (!jsonSubtask.isEmpty()) {
            subtasks = gson.fromJson(jsonSubtask,
                    new TypeToken<Map<Integer, Subtask>>() {
                    }.getType());
        }

        String jsonHistory = client.load("history");
        List<Integer> history = gson.fromJson(jsonHistory, new TypeToken<List<Integer>>() {
        }.getType());

        if (history != null) {
            for (Integer id : history) {
                if (epics.containsKey(id)) {
                    Epic epic = epics.get(id);
                    historyManager.add(epic);
                }
                if (tasks.containsKey(id)) {
                    Task task1 = tasks.get(id);
                    historyManager.add(task1);
                }
                if (subtasks.containsKey(id)) {
                    Subtask subtask = subtasks.get(id);
                    historyManager.add(subtask);
                }
            }
        }
        prioritizedTasks.addAll(tasks.values());
        prioritizedTasks.addAll(subtasks.values());

        if (!tasks.isEmpty()) {
            Task maxTask = getTasks().stream().max(Comparator.comparing(Task::getId)).orElse(null);
            counterId = maxTask.getId();
        }
        if (!epics.isEmpty()) {
            Epic maxEpic = getEpics().stream().max(Comparator.comparing(Epic::getId)).orElse(null);
            if (maxEpic.getId() > counterId) {
                counterId = maxEpic.getId();
            }
        }
        if (!subtasks.isEmpty()) {
            Subtask maxSubtask = getSubtasks().stream().max(Comparator.comparing(Subtask::getId)).orElse(null);
            if (maxSubtask.getId() > counterId) {
                counterId = maxSubtask.getId();
            }
        }
    }
}
