package ru.tbank;

import java.util.Collection;

public class CustomLinkedList<T> {
    private Node<T> head;
    private Node<T> tail;
    private int size;

    public CustomLinkedList() {
        head = null;
        tail = null;
        size = 0;
    }

    public void add(T element) {
        Node<T> newNode = new Node<>(element);
        if (head == null) {
            head = newNode;
            tail = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
        }
        size++;
    }

    public T get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        Node<T> current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current.element;
    }

    public void remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        if (index == 0) {
            head = head.next;
            if (head!= null) {
                head.prev = null;
            } else {
                tail = null;
            }
        } else if (index == size - 1) {
            tail = tail.prev;
            if (tail!= null) {
                tail.next = null;
            } else {
                head = null;
            }
        } else {
            Node<T> current = head;
            for (int i = 0; i < index - 1; i++) {
                current = current.next;
            }
            current.next = current.next.next;
            if (current.next!= null) {
                current.next.prev = current;
            } else {
                tail = current;
            }
        }
        size--;
    }

    public boolean contains(T element) {
        Node<T> current = head;
        while (current!= null) {
            if (current.element.equals(element)) {
                return true;
            }
            current = current.next;
        }
        return false;
    }

    public void addAll(Collection<? extends T> collection) {
        for (T element : collection) {
            add(element);
        }
    }

    private static class Node<T> {
        T element;
        Node<T> next;
        Node<T> prev;

        public Node(T element) {
            this.element = element;
            this.next = null;
            this.prev = null;
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        Node<T> current = head;
        while (current!= null) {
            sb.append(current.element).append(" ");
            current = current.next;
        }
        return sb.toString();
    }
}
