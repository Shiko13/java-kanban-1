package managers;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import http.KVTaskClient;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TypeOfTask;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class HTTPTaskManager extends FileBackedTasksManager {
    private final KVTaskClient client;
    private final Gson gson = new GsonBuilder().
            registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    public HTTPTaskManager(String port) {
        super(port);
        client = new KVTaskClient(port);
    }

    public KVTaskClient getClient() {
        return client;
    }

    @Override
    void save() {
        client.put("tasks", gson.toJson(tasks));
        client.put("epics", gson.toJson(epics));
        client.put("subtasks", gson.toJson(subtasks));
        client.put("history", gson.toJson(historyManager.getHistory()));
    }

    public HTTPTaskManager load(String port) {
        HTTPTaskManager manager = new HTTPTaskManager(port);

        String jsonTask = client.load("tasks");
        if (!jsonTask.isEmpty()) {
            manager.tasks = gson.fromJson(manager.getClient().load("tasks"),
                    new TypeToken<Map<Integer, Task>>() {
                    }.getType());
        }

        String jsonEpic = client.load("epics");
        System.out.println(jsonEpic);
        if (!jsonEpic.isEmpty()) {
            manager.epics = gson.fromJson(manager.getClient().load("epics"),
                    new TypeToken<Map<Integer, Epic>>() {
                    }.getType());
        }

        String jsonSubtask = client.load("subtasks");
        if(!jsonSubtask.isEmpty()) {
            manager.subtasks = gson.fromJson(manager.getClient().load("subtasks"),
                    new TypeToken<Map<Integer, Subtask>>() {
                    }.getType());
        }

        String jsonHistory = client.load("history");
        List<Task> history = gson.fromJson(jsonHistory, new TypeToken<List<Task>>() {
        }.getType());

        if (history != null) {
            for (Task task : history) {
                if (epics.containsKey(task.getId())) {
                    Epic epic = epics.get(task.getId());
                    historyManager.add(epic);
                }
                if (tasks.containsKey(task.getId())) {
                    Task task1 = tasks.get(task.getId());
                    historyManager.add(task1);
                }
                if (subtasks.containsKey(task.getId())) {
                    Subtask subTask = subtasks.get(task.getId());
                    historyManager.add(subTask);
                }
            }
        }
        return manager;
    }

    class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {

        private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");

        @Override
        public void write(final JsonWriter jsonWriter, final LocalDateTime localDateTime) throws IOException {
            if (localDateTime == null) {
                jsonWriter.nullValue();
            } else {
                jsonWriter.value(localDateTime.format(formatter));
            }
        }

        @Override
        public LocalDateTime read(final JsonReader jsonReader) throws IOException {
            if (jsonReader.peek() == JsonToken.NULL) {
                jsonReader.nextNull();
                return null;
            } else {
                return LocalDateTime.parse(jsonReader.nextString(), formatter);
            }
        }
    }
}
