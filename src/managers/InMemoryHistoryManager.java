package managers;

import tasks.Task;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final CustomLinkedList<Task> viewHistory = new CustomLinkedList<>();
    private final HashMap<Integer, Node<Task>> viewHistoryMap = new HashMap<>();

    @Override
    public void add(Task task) {
        if (viewHistoryMap.containsKey(task.getId())) {
            viewHistory.removeNode(viewHistoryMap.get(task.getId()));
        }
        Node<Task> node = viewHistory.linkLast(task);
        viewHistoryMap.put(task.getId(), node);
    }

    @Override
    public void remove(int id) {
        viewHistory.removeNode(viewHistoryMap.get(id));
    }

    @Override
    public List<Task> getHistory() {
        return viewHistory.getTasks();
    }

    static class CustomLinkedList<Task> {
        private Node<Task> head;
        private Node<Task> tail;
        private int size = 0;

        public Node<Task> linkLast(Task task) {
            final Node<Task> oldTail = tail;
            final Node<Task> newTail = new Node<>(oldTail, task, null);
            tail = newTail;
            if (oldTail == null) {
                head = newTail;
            } else {
                oldTail.next = newTail;
            }
            size++;
            return tail;
        }

        public List<Task> getTasks() {
            List<Task> tasks = new ArrayList<>();
            Node<Task> node = head;
            for (int i = 0; i < size; i++) {
                tasks.add(node.data);
                node = node.next;
            }
            return tasks;
        }

        void removeNode(Node<Task> node) {
            if (head == null) {
                return;
            }
            if (node != head && node != tail) {
                node.prev.next = node.next;
                node.next.prev = node.prev;
            } else if (node == head && head.next != null) {
                Node<Task> temp = head;
                head = temp.next;
            } else if (node == tail) {
                Node<Task> temp = tail;
                tail = temp.prev;
            }
            size--;
        }
    }
}
