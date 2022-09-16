package http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import managers.Managers;
import managers.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TypeOfTask;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.util.*;

public class HTTPTaskServer {
    public static final int PORT = 8080;
    private final TaskManager manager = Managers.getDefault();
    private final Gson gson = new GsonBuilder()
            .serializeNulls()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();
    private final HttpServer server;

    public HTTPTaskServer() throws IOException {
        this.server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/tasks/", new TasksHandler());
        server.createContext("/tasks/task", new TaskHandler());
        server.createContext("/tasks/epic", new EpicHandler());
        server.createContext("/tasks/subtask", new SubtaskHandler());
        server.createContext("/tasks/history", new HistoryHandler());
    }


    class TasksHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange h) throws IOException {
            if ("GET".equals(h.getRequestMethod())) {
                String body = gson.toJson(manager.getPrioritizedTasks());
                h.getResponseHeaders().add("Content-Type", "application/json");
                h.sendResponseHeaders(200, 0);
                try (OutputStream os = h.getResponseBody()) {
                    os.write(body.getBytes());
                }
            } else {
                h.sendResponseHeaders(405, 0);
                try (OutputStream os = h.getResponseBody()) {
                    os.write("Ожидается запрос другого типа".getBytes());
                }
            }
        }
    }

    class TaskHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange h) throws IOException {
            String query = h.getRequestURI().getQuery();
            switch (h.getRequestMethod()) {
                case "GET":
                    if (query == null) {
                        String body = gson.toJson(manager.getTasks());
                        h.getResponseHeaders().add("Content-Type", "application/json");
                        h.sendResponseHeaders(200, 0);
                        try (OutputStream os = h.getResponseBody()) {
                            os.write(body.getBytes());
                        }
                    } else {
                        String[] parameters = query.split("=");
                        int id = Integer.parseInt(parameters[1]);
                        if (manager.getTasks().stream()
                                .anyMatch(t -> t.getId() == id)) {
                            String body = gson.toJson(manager.getTaskById(id));
                            h.getResponseHeaders().add("Content-Type", "application/json");
                            h.sendResponseHeaders(200, 0);
                            try (OutputStream os = h.getResponseBody()) {
                                os.write(body.getBytes());
                            }
                        } else {
                            h.sendResponseHeaders(404, 0);
                            try (OutputStream os = h.getResponseBody()) {
                                os.write("Задачи с таким id нет".getBytes());
                            }
                        }
                    }
                    break;
                case "POST":
                    InputStream is = h.getRequestBody();
                    String body = new String(is.readAllBytes(), UTF_8);
                    JsonElement jsonElement = JsonParser.parseString(body);
                    if (!jsonElement.isJsonObject()) {
                        System.out.println("Тело запроса клиента не соответствует формату JSON");
                        h.sendResponseHeaders(405, 0);
                        return;
                    }
                    Task task = gson.fromJson(body, Task.class);
                    task.setTypeOfTask(TypeOfTask.TASK);
                    task.setEndTime(Optional.ofNullable(task.getStartTime()).isPresent() ? task.getStartTime().plusMinutes(task.getDuration()) : null);
                    manager.addTask(task);
                    h.sendResponseHeaders(201, 0);
                    try (OutputStream os = h.getResponseBody()) {
                        os.write("Задача добавлена".getBytes());
                    }
                    break;
                case "DELETE":
                    if (query == null) {
                        manager.removeAllTasks();
                        h.sendResponseHeaders(201, 0);
                        try (OutputStream os = h.getResponseBody()) {
                            os.write("Задачи удалены".getBytes());
                        }
                    } else {
                        String[] params = query.split("=");
                        int taskId = Integer.parseInt(params[1]);
                        if (manager.getTasks().stream()
                                .anyMatch(t -> t.getId() == taskId)) {
                            manager.removeTaskById(taskId);
                            h.sendResponseHeaders(201, 0);
                            try (OutputStream os = h.getResponseBody()) {
                                os.write("Задача удалена".getBytes());
                            }
                        } else {
                            h.sendResponseHeaders(404, 0);
                            try (OutputStream os = h.getResponseBody()) {
                                os.write("Задачи с таким id нет".getBytes());
                            }
                        }
                    }
                    break;
                default:
                    h.sendResponseHeaders(405, 0);
                    try (OutputStream os = h.getResponseBody()) {
                        os.write("Метод неверен".getBytes());
                    }
            }
        }
    }

    class EpicHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange h) throws IOException {
            String query = h.getRequestURI().getQuery();
            switch (h.getRequestMethod()) {
                case "GET":
                    if (query == null) {
                        String body = gson.toJson(manager.getEpics());
                        h.getResponseHeaders().add("Content-Type", "application/json");
                        h.sendResponseHeaders(200, 0);
                        try (OutputStream os = h.getResponseBody()) {
                            os.write(body.getBytes());
                        }
                    } else {
                        String[] parameters = query.split("=");
                        int epicId = Integer.parseInt(parameters[1]);
                        if (manager.getEpics().stream()
                                .anyMatch(e -> e.getId() == epicId)) {
                            String body = gson.toJson(manager.getEpicById(epicId));
                            h.getResponseHeaders().add("Content-Type", "application/json");
                            h.sendResponseHeaders(200, 0);
                            try (OutputStream os = h.getResponseBody()) {
                                os.write(body.getBytes());
                            }
                        } else {
                            h.sendResponseHeaders(404, 0);
                            try (OutputStream os = h.getResponseBody()) {
                                os.write("Эпика с таким id нет".getBytes());
                            }
                        }
                    }
                    break;
                case "POST":
                    InputStream is = h.getRequestBody();
                    String body = new String(is.readAllBytes(), UTF_8);
                    JsonElement jsonElement = JsonParser.parseString(body);
                    if (!jsonElement.isJsonObject()) {
                        System.out.println("Тело запроса клиента не соответствует формату JSON");
                        h.sendResponseHeaders(405, 0);
                        return;
                    }
                    Epic epic = gson.fromJson(body, Epic.class);
                    epic.setTypeOfTask(TypeOfTask.EPIC);
                    epic.setSubtasksOfEpic(new ArrayList<>());
                    epic.setSortedByEndTimeSubtasks(new TreeSet<>(Comparator.comparing(Task::getEndTime).reversed()));
                    epic.setSortedByStartTimeSubtasks(new TreeSet<>(Comparator.comparing(Task::getStartTime)));
                    manager.addEpic(epic);
                    h.sendResponseHeaders(201, 0);
                    try (OutputStream os = h.getResponseBody()) {
                        os.write("Эпик добавлен".getBytes());
                    }
                    break;
                case "DELETE":
                    if (query == null) {
                        manager.removeAllEpics();
                        h.sendResponseHeaders(201, 0);
                        try (OutputStream os = h.getResponseBody()) {
                            os.write("Эпики удалены".getBytes());
                        }
                    } else {
                        String[] parameters = query.split("=");
                        int epicId = Integer.parseInt(parameters[1]);
                        if (manager.getEpics().stream()
                                .anyMatch(e -> e.getId() == epicId)) {
                            manager.removeEpicById(epicId);
                            h.sendResponseHeaders(201, 0);
                            try (OutputStream os = h.getResponseBody()) {
                                os.write("Эпик удалён".getBytes());
                            }
                        } else {
                            h.sendResponseHeaders(404, 0);
                            try (OutputStream os = h.getResponseBody()) {
                                os.write("Эпика с таким id нет".getBytes());
                            }
                        }
                    }
                    break;
                default:
                    h.sendResponseHeaders(405, 0);
                    try (OutputStream os = h.getResponseBody()) {
                        os.write("Метод неверен".getBytes());
                    }
            }
        }
    }

    class SubtaskHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange h) throws IOException {
            String query = h.getRequestURI().getQuery();
            switch (h.getRequestMethod()) {
                case "GET":
                    if (query == null) {
                        String body = gson.toJson(manager.getSubtasks());
                        h.getResponseHeaders().add("Content-Type", "application/json");
                        h.sendResponseHeaders(200, 0);
                        try (OutputStream os = h.getResponseBody()) {
                            os.write(body.getBytes());
                        }
                    } else {
                        String[] parameters = query.split("=");
                        int subtaskId = Integer.parseInt(parameters[1]);
                        if (manager.getSubtasks().stream()
                                .anyMatch(s -> s.getId() == subtaskId)) {
                            String body = gson.toJson(manager.getSubtaskById(subtaskId));
                            h.getResponseHeaders().add("Content-Type", "application/json");
                            h.sendResponseHeaders(200, 0);
                            try (OutputStream os = h.getResponseBody()) {
                                os.write(body.getBytes());
                            }
                        } else {
                            h.sendResponseHeaders(404, 0);
                            try (OutputStream os = h.getResponseBody()) {
                                os.write("Подзадачи с таким id нет".getBytes());
                            }
                        }
                    }
                    break;
                case "POST":
                    InputStream inputStream = h.getRequestBody();
                    String body = new String(inputStream.readAllBytes(), UTF_8);
                    Subtask subtask = gson.fromJson(body, Subtask.class);
                    subtask.setTypeOfTask(TypeOfTask.SUBTASK);
                    subtask.setEndTime(Optional.ofNullable(subtask.getStartTime()).isPresent() ? subtask.getStartTime().plusMinutes(subtask.getDuration()) : null);
                    manager.addSubtask(subtask);
                    h.sendResponseHeaders(201, 0);
                    try (OutputStream os = h.getResponseBody()) {
                        os.write("Подзадача добавлена".getBytes());
                    }
                    break;
                case "DELETE":
                    String queryId = h.getRequestURI().getQuery();
                    if (queryId == null) {
                        manager.removeAllSubtasks();
                        h.sendResponseHeaders(201, 0);
                        try (OutputStream os = h.getResponseBody()) {
                            os.write("Подзадачи удалены".getBytes());
                        }
                    } else {
                        String[] parameters = queryId.split("=");
                        int id = Integer.parseInt(parameters[1]);
                        if (manager.getSubtasks().stream()
                                .anyMatch(s -> s.getId() == id)) {
                            manager.removeSubtaskById(id);
                            h.sendResponseHeaders(201, 0);
                            try (OutputStream os = h.getResponseBody()) {
                                os.write("Подзадача удалена".getBytes());
                            }
                        } else {
                            h.sendResponseHeaders(404, 0);
                            try (OutputStream os = h.getResponseBody()) {
                                os.write("Подзадачи с таким id нет".getBytes());
                            }
                        }
                    }
                    break;
                default:
                    h.sendResponseHeaders(405, 0);
                    try (OutputStream os = h.getResponseBody()) {
                        os.write("Метод неверен".getBytes());
                    }
            }
        }
    }

    class HistoryHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange h) throws IOException {
            if ("GET".equals(h.getRequestMethod())) {
                String body = gson.toJson(manager.getHistoryManager());
                h.getResponseHeaders().add("Content-Type", "application/json");
                h.sendResponseHeaders(200, 0);
                try (OutputStream os = h.getResponseBody()) {
                    os.write(body.getBytes());
                }
            } else {
                h.sendResponseHeaders(405, 0);
                try (OutputStream os = h.getResponseBody()) {
                    os.write("Метод неверен".getBytes());
                }
            }
        }
    }

    public void start() {
        server.start();
    }
}
