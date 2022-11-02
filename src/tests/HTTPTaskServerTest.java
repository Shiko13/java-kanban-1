package tests;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import http.HTTPTaskServer;
import http.KVServer;
import http.LocalDateTimeAdapter;
import managers.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HTTPTaskServerTest {
    private final Gson gson = new GsonBuilder()
            .serializeNulls()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();
    private HttpClient client;
    private KVServer kvServer;
    private HTTPTaskServer httpTaskServer;


    @BeforeEach
    public void start() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        httpTaskServer = new HTTPTaskServer();
        httpTaskServer.start();
        client = HttpClient.newHttpClient();
        TaskManager manager = httpTaskServer.getManager();
        Task task1 = new Task("Task1",
                "Description of Task1", null, Status.NEW,
                200, LocalDateTime.of(2023, 1, 4, 18, 29));
        manager.addTask(task1);
        manager.getTaskById(task1.getId());
        Task task2 = new Task("Task2",
                "Description of Task2", null, Status.NEW,
                200, LocalDateTime.of(2023, 2, 4, 18, 29));
        manager.addTask(task2);
        manager.getTaskById(task2.getId());
        Epic epic = new Epic("Epic",
                "Description of Epic", null, Status.NEW, null, null);
        manager.addEpic(epic);
        manager.getEpicById(epic.getId());
        Subtask subtask1 = new Subtask("Subtask1",
                "Description of Subtask1", epic.getId(), 3, Status.NEW,
                200, LocalDateTime.of(2023, 3, 5, 18, 29));
        manager.addSubtask(subtask1);
        manager.getSubtaskById(subtask1.getId());
        Subtask subtask2 = new Subtask("Subtask2",
                "Description of Subtask2", epic.getId(), 3, Status.NEW,
                200, LocalDateTime.of(2023, 5, 5, 18, 29));
        manager.addSubtask(subtask2);
        manager.getSubtaskById(subtask2.getId());
    }

    @AfterEach
    public void stop() {
        kvServer.stop();
        httpTaskServer.stop();
    }

    @Test
    public void getTasks() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @Test
    public void getEpics() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @Test
    public void getSubtasks() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/subtask/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @Test
    public void getPrioritizedTasks() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @Test
    public void getHistory() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/history/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @Test
    public void getTaskById() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/task/?id=1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        url = URI.create("http://localhost:8080/tasks/task/?id=9");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    public void getEpicById() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/epic/?id=3");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        url = URI.create("http://localhost:8080/tasks/task/?id=10");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    public void getSubtaskById() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/subtask/?id=4");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        url = URI.create("http://localhost:8080/tasks/task/?id=11");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    public void addTask() throws IOException, InterruptedException {
        Task task3 = new Task("Task3",
                "Description of Task3", null, Status.NEW,
                200, LocalDateTime.of(2023, 2, 10, 18, 29));
        URI url = URI.create("http://localhost:8080/tasks/task/");
        String json = gson.toJson(task3);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
    }

    @Test
    public void addEpic() throws IOException, InterruptedException {
        Epic epic2 = new Epic("Epic2",
                "Description of Epic2", null, Status.NEW, null, null);
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        String json = gson.toJson(epic2);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String>response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
    }

    @Test
    public void addSubtask() throws IOException, InterruptedException {
        Subtask subtask3 = new Subtask("Subtask3",
                "Description of Subtask3", 3, null, Status.NEW,
                200, LocalDateTime.of(2023, 2, 17, 18, 29));
        URI url = URI.create("http://localhost:8080/tasks/subtask/");
        String json = gson.toJson(subtask3);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
    }

    @Test
    public void deleteTaskById() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/task/?id=1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        url = URI.create("http://localhost:8080/tasks/task/?id=15");
        request = HttpRequest.newBuilder().uri(url).DELETE().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
        url = URI.create("http://localhost:8080/tasks/task/?=1");
        request = HttpRequest.newBuilder().uri(url).DELETE().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    public void deleteEpicById() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/epic/?id=3");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        url = URI.create("http://localhost:8080/tasks/epic/?id=16");
        request = HttpRequest.newBuilder().uri(url).DELETE().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
        url = URI.create("http://localhost:8080/tasks/epic/?=3");
        request = HttpRequest.newBuilder().uri(url).DELETE().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    public void deleteSubtaskById() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/subtask/?id=4");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        url = URI.create("http://localhost:8080/tasks/subtask/?id=17");
        request = HttpRequest.newBuilder().uri(url).DELETE().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
        url = URI.create("http://localhost:8080/tasks/subtask/?=4");
        request = HttpRequest.newBuilder().uri(url).DELETE().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    public void deleteAllTasks() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @Test
    public void deleteAllEpics() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @Test
    public void deleteAllSubtasks() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/subtask/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }
}