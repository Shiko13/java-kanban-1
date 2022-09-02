package managers;

import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final CustomLinkedList<Task> viewHistory = new CustomLinkedList<>();

    private final HashMap<Integer, CustomLinkedList.Node<Task>> viewHistoryMap = new HashMap<>();

    @Override
    public void add(Task task) {
        if (viewHistoryMap.get(task.getId()) != null) {
            viewHistory.removeNode(viewHistoryMap.get(task.getId()));
        }
        CustomLinkedList.Node<Task> node = viewHistory.linkLast(task);
        viewHistoryMap.put(task.getId(), node);
    }

    @Override
    public void remove(int id) {
        viewHistory.removeNode(viewHistoryMap.get(id));
        viewHistoryMap.remove(id);
    }

    @Override
    public List<Task> getHistory() {
        return viewHistory.getTasks();
    }

    private static class CustomLinkedList<T> {
        private Node<T> head;
        private Node<T> tail;
        private int size = 0;

        static class Node<T> {
            public T data;
            public Node<T> next;
            public Node<T> prev;

            public Node(Node<T> prev, T data, Node<T> next) {
                this.data = data;
                this.next = next;
                this.prev = prev;
            }
        }

        public Node<T> linkLast(T t) {
            final Node<T> oldTail = tail;
            final Node<T> newTail = new Node<>(oldTail, t, null);
            tail = newTail;
            if (oldTail == null) {
                head = newTail;
            } else {
                oldTail.next = newTail;
            }
            size++;
            return tail;
        }

        public List<T> getTasks() {
            List<T> tasks = new ArrayList<>();
            Node<T> node = head;
            while (node != null) {
                tasks.add(node.data);
                node = node.next;
            }
            return tasks;
        }

        void removeNode(Node<T> node) {
            if (head == null || node == null) {
                return;
            }
            if (node != head && node != tail) {
                node.prev.next = node.next;
                node.next.prev = node.prev;
            } else if (node == head && head.next != null) {
                head.next.prev = null;
                head = head.next;
            } else if (node == tail && node.prev != null) {
                tail.prev.next = null;
                tail = tail.prev;
            }
            size--;
        }
    }

    public HashMap<Integer, CustomLinkedList.Node<Task>> getViewHistoryMap() {
        return viewHistoryMap;
    }

    public CustomLinkedList<Task> getViewHistory() {
        return viewHistory;
    }
}
