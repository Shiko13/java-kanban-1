package managers;

import http.KVServer;
import http.KVTaskClient;

public final class Managers {

    private Managers() {
    }

    public static TaskManager getDefault() {
        return new HTTPTaskManager("8078");
    }

    public static TaskManager getInMemoryTaskManager() {
        return new InMemoryTaskManager();
    }

    public static TaskManager getFileBackedTasksManager(String path) {
        return new FileBackedTasksManager(path);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
