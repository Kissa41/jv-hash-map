package core.basesyntax;

import java.util.Objects;

public class MyHashMap<K, V> implements MyMap<K, V> {

    static class Node<K, V>{
        final int hash;
        final K key;
        V value;
        MyHashMap.Node<K,V> next;

        public Node(int hash, K key, V value, Node<K, V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }

    }

    final private static int DEFAULT_CAPACITY = 16;
    final private static float LOAD_FACTOR = 0.75f;
    final private static int ARRAY_RESIZE_INCREMENT = 2;
    private int currentSize = 0;
    Node<K, V>[] table = new Node[DEFAULT_CAPACITY];

    public final int calculateIndex(K key) {
        int res = Objects.hash(key);
        if(res < 0) res*=-1;
        return res % table.length;
    }

    @Override
    public void put(K key, V value) {

        // Resizing
        int threshold = (int) (LOAD_FACTOR * table.length);
        if (currentSize + 1 > threshold){
            resize();
        }

        int index = calculateIndex(key);
        Node<K, V> node = new Node(Objects.hash(key), key, value, null);

        // If bucket is empty we put a new Node in it
        if (table[index]==null){
            table[index] = node;
        }
        // If bucket isn't empty we check all linked list and
        // select the appropriate bucket
        else{
            Node<K, V> temp = table[index];
            while (temp.next != null){
                if (Objects.equals(temp.key, key)) {
                    temp.value = value;
                    return;
                }
                temp = temp.next;
            }
            if (Objects.equals(temp.key, key)) {
                temp.value = value;
                return;
            }
            temp.next = node;
        }

        currentSize+=1;

    }
    @Override
    public V getValue(K key) {

        V result = null;

        int index = calculateIndex(key);
        Node<K, V> temp = table[index];

        while (temp!=null){
            if (Objects.equals(temp.key, key)) result = temp.value;
            temp = temp.next;
        }

        return result;
    }

    @Override
    public int getSize() {
        return currentSize;
    }

    private void resize(){

        // Re-create the array "table" with doubled capacity
        Node<K, V>[] oldTable = table;
        table = new Node[table.length * ARRAY_RESIZE_INCREMENT];

        // Re-locate all Nodes by new indices
        for (Node<K, V> node : oldTable) {

            if (node != null) {
                put(node.key, node.value);
                currentSize -= 1;
            }

            if (node != null && node.next != null) {

                Node<K, V> temp = node.next;

                while (temp != null) {
                    put(temp.key, temp.value);
                    currentSize -= 1;
                    temp = temp.next;
                }
            }
        }
    }
}
