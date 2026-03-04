package core.basesyntax;

import java.util.Objects;

public class MyHashMap<K, V> implements MyMap<K, V> {
    private static final double LOAD_FACTOR = 0.75;
    private static final int DEFAULT_SIZE = 16;
    
    private int threshold = (int)(LOAD_FACTOR * DEFAULT_SIZE);
    private int filledSize = 0;
    
    @SuppressWarnings("unchecked")
    private Node<K, V>[] table = (Node<K, V>[]) new Node[DEFAULT_SIZE];

    @Override
    public void put(K key, V value) {
        if (filledSize >= threshold) {
            resize();
        }

        int hash = (key == null) ? 0 : key.hashCode();

        Node<K, V> newNode = new Node<>(hash, key, value, null);
        int position = calculatePosition(key);

        if (table[position] == null) {
            table[position] = newNode;
            filledSize++;
            return;
        }

        Node<K, V> current = table[position];

        while (current.next != null) {
            if (Objects.equals(current.key, key)) {
                current.value = value;
                return;
            }
            current = current.next;
        }
        if (Objects.equals(current.key, key)) {
            current.value = value;
        } else {
            current.next = newNode;
            filledSize++;
        }
    }

    @Override
    public V getValue(K key) {
        int position = calculatePosition(key);
        Node<K, V> current = table[position];

        while (current != null) {
            if (Objects.equals(current.key, key)) {
                return current.value;
            }
            current = current.next;
        }
        return null;
    }

    @Override
    public int getSize() {
        return filledSize;
    }

    private static class Node<K, V> {
        private final int hash;
        private final K key;
        private V value;
        private Node<K, V> next;

        Node(int hash, K key, V value, Node<K, V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        Node<K, V>[] oldTable = table;
        int newCapacity = oldTable.length << 1;
        Node<K, V>[] newTable = (Node<K, V>[]) new Node[newCapacity];

        for (Node<K, V> current : oldTable) {
            Node<K, V> node = current;

            while (node != null) {
                Node<K, V> next = node.next; // сохраняем ссылку

                int newIndex = node.hash & (newCapacity - 1);

                node.next = newTable[newIndex];
                newTable[newIndex] = node;
                node = next;
            }
        }
        table = newTable;
        threshold = (int)(newCapacity * LOAD_FACTOR);
    }

    private int calculatePosition(K key) {
        return key == null ? 0 : key.hashCode() & (table.length - 1);
    }
}
