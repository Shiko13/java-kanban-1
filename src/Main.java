import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import http.HTTPTaskServer;
import http.KVServer;
import http.KVTaskClient;
import http.LocalDateTimeAdapter;
import managers.HTTPTaskManager;
import managers.Managers;
import managers.TaskManager;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {
        new KVServer().start();
//        kvTaskClient.load("test");
        new HTTPTaskServer().start();
    }
}
