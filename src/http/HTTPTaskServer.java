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

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;

public class HTTPTaskServer {
    public static final int PORT = 8080;

    public TaskManager getManager() {
        return manager;
    }

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


    private class TasksHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange h) throws IOException {
            if ("GET".equals(h.getRequestMethod())) {
                String body = gson.toJson(manager.getPrioritizedTasks());
                h.getResponseHeaders().add("Content-Type", "application/json");
                h.sendResponseHeaders(200, 0);
                writeText(h, body);
            } else {
                h.sendResponseHeaders(405, 0);
                writeText(h, "Метод неверен");
            }
        }
    }

    private class TaskHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange h) throws IOException {
            String query = h.getRequestURI().getQuery();
            switch (h.getRequestMethod()) {
                case "GET":
                    if (query == null) {
                        String body = gson.toJson(manager.getTasks());
                        h.getResponseHeaders().add("Content-Type", "application/json");
                        h.sendResponseHeaders(200, 0);
                        writeText(h, body);
                    } else {
                        String[] parameters = query.split("=");
                        int id = Integer.parseInt(parameters[1]);
                        if (manager.getTasks().stream()
                                .anyMatch(t -> t.getId() == id)) {
                            String body = gson.toJson(manager.getTaskById(id));
                            h.getResponseHeaders().add("Content-Type", "application/json");
                            h.sendResponseHeaders(200, 0);
                            writeText(h, body);
                        } else {
                            h.sendResponseHeaders(404, 0);
                            writeText(h, "Задачи с таким id нет");
                        }
                    }
                    break;
                case "POST":
                    String body = readText(h);
                    if (body.isEmpty()) {
                        h.sendResponseHeaders(400, 0);
                        writeText(h, "Пустое тело");
                        return;
                    }
                    JsonElement jsonElement = JsonParser.parseString(body);
                    if (!jsonElement.isJsonObject()) {
                        System.out.println("Не формат JSON");
                        h.sendResponseHeaders(405, 0);
                        return;
                    }
                    Task task = gson.fromJson(body, Task.class);
                    if (task.getId() == null) {
                        manager.addTask(task);
                    } else {
                        manager.updateTask(task);
                    }
                    h.sendResponseHeaders(201, 0);
                    writeText(h, "Задача добавлена");
                    break;
                case "DELETE":
                    if (query == null) {
                        manager.removeAllTasks();
                        h.sendResponseHeaders(200, 0);
                        writeText(h, "Задачи удалены");
                    } else {
                        String[] params = query.split("=");
                        int taskId = Integer.parseInt(params[1]);
                        if (manager.getTasks().stream()
                                .anyMatch(t -> t.getId() == taskId)) {
                            manager.removeTaskById(taskId);
                            h.sendResponseHeaders(200, 0);
                            writeText(h, "Задача удалена");
                        } else {
                            h.sendResponseHeaders(404, 0);
                            writeText(h, "Задачи с таким id нет");
                        }
                    }
                    break;
                default:
                    h.sendResponseHeaders(405, 0);
                    writeText(h, "Метод неверен");
            }
        }
    }

    private static void writeText(HttpExchange h, String x) throws IOException {
        try (OutputStream os = h.getResponseBody()) {
            os.write(x.getBytes());
        }
    }

    private class EpicHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange h) throws IOException {
            String query = h.getRequestURI().getQuery();
            switch (h.getRequestMethod()) {
                case "GET":
                    if (query == null) {
                        String body = gson.toJson(manager.getEpics());
                        h.getResponseHeaders().add("Content-Type", "application/json");
                        h.sendResponseHeaders(200, 0);
                        writeText(h, body);
                    } else {
                        String[] parameters = query.split("=");
                        int epicId = Integer.parseInt(parameters[1]);
                        if (manager.getEpics().stream()
                                .anyMatch(e -> e.getId() == epicId)) {
                            String body = gson.toJson(manager.getEpicById(epicId));
                            h.getResponseHeaders().add("Content-Type", "application/json");
                            h.sendResponseHeaders(200, 0);
                            writeText(h, body);
                        } else {
                            h.sendResponseHeaders(404, 0);
                            writeText(h, "Эпика с таким id нет");
                        }
                    }
                    break;
                case "POST":
                    String body = readText(h);
                    if (body.isEmpty()) {
                        h.sendResponseHeaders(400, 0);
                        writeText(h, "Пустое тело");
                        return;
                    }
                    Epic epic = gson.fromJson(body, Epic.class);
                    if (epic.getId() == null) {
                        manager.addEpic(epic);
                    } else {
                        manager.updateTask(epic);
                    }
                    h.sendResponseHeaders(201, 0);
                    writeText(h, "Эпик добавлен");
                    break;
                case "DELETE":
                    if (query == null) {
                        manager.removeAllEpics();
                        h.sendResponseHeaders(200, 0);
                        writeText(h, "Эпики удалены");
                    } else {
                        String[] parameters = query.split("=");
                        int epicId = Integer.parseInt(parameters[1]);
                        if (manager.getEpics().stream()
                                .anyMatch(e -> e.getId() == epicId)) {
                            manager.removeEpicById(epicId);
                            h.sendResponseHeaders(200, 0);
                            writeText(h, "Эпик удалён");
                        } else {
                            h.sendResponseHeaders(404, 0);
                            writeText(h, "Эпика с таким id нет");
                        }
                    }
                    break;
                default:
                    h.sendResponseHeaders(405, 0);
                    writeText(h, "Метод неверен");
            }
        }
    }

    private class SubtaskHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange h) throws IOException {
            String query = h.getRequestURI().getQuery();
            switch (h.getRequestMethod()) {
                case "GET":
                    if (query == null) {
                        String body = gson.toJson(manager.getSubtasks());
                        h.getResponseHeaders().add("Content-Type", "application/json");
                        h.sendResponseHeaders(200, 0);
                        writeText(h, body);
                    } else {
                        String[] parameters = query.split("=");
                        int subtaskId = Integer.parseInt(parameters[1]);
                        if (manager.getSubtasks().stream()
                                .anyMatch(s -> s.getId() == subtaskId)) {
                            String body = gson.toJson(manager.getSubtaskById(subtaskId));
                            h.getResponseHeaders().add("Content-Type", "application/json");
                            h.sendResponseHeaders(200, 0);
                            writeText(h, body);
                        } else {
                            h.sendResponseHeaders(404, 0);
                            writeText(h, "Подзадачи с таким id нет");
                        }
                    }
                    break;
                case "POST":
                    String body = readText(h);
                    if (body.isEmpty()) {
                        h.sendResponseHeaders(400, 0);
                        writeText(h, "Пустое тело");
                        return;
                    }
                    Subtask subtask = gson.fromJson(body, Subtask.class);
                    if (subtask.getId() == null) {
                        manager.addSubtask(subtask);
                    } else {
                        manager.updateSubtask(subtask);
                    }
                    h.sendResponseHeaders(201, 0);
                    writeText(h, "Подзадача добавлена");
                    break;
                case "DELETE":
                    String queryId = h.getRequestURI().getQuery();
                    if (queryId == null) {
                        manager.removeAllSubtasks();
                        h.sendResponseHeaders(200, 0);
                        writeText(h, "Подзадачи удалены");
                    } else {
                        String[] parameters = queryId.split("=");
                        int id = Integer.parseInt(parameters[1]);
                        if (manager.getSubtasks().stream()
                                .anyMatch(s -> s.getId() == id)) {
                            manager.removeSubtaskById(id);
                            h.sendResponseHeaders(200, 0);
                            writeText(h, "Подзадача удалена");
                        } else {
                            h.sendResponseHeaders(404, 0);
                            writeText(h, "Подзадачи с таким id нет");
                        }
                    }
                    break;
                default:
                    h.sendResponseHeaders(405, 0);
                    writeText(h, "Метод неверен");
            }
        }
    }

    private class HistoryHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange h) throws IOException {
            if ("GET".equals(h.getRequestMethod())) {
                String body = gson.toJson(manager.getHistoryManager().getHistory());
                h.getResponseHeaders().add("Content-Type", "application/json");
                h.sendResponseHeaders(200, 0);
                writeText(h, body);
            } else {
                h.sendResponseHeaders(405, 0);
                writeText(h, "Метод неверен");
            }
        }
    }

    private String readText(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes(), UTF_8);
    }

    public void start() {
        server.start();
    }

    public void stop() {
        System.out.println("Останавливаем сервер");
        server.stop(1);
    }
}
