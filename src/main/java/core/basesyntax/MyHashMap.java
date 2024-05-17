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

    private static final int DEFAULT_CAPACITY = 16;
    private static final float LOAD_FACTOR = 0.75f;
    private static final int ARRAY_RESIZE_INCREMENT = 2;
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
        Node<K, V> newNode = new Node(Objects.hash(key), key, value, null);

        // If bucket is empty we put a new Node in it
        if (table[index]==null){
            table[index] = newNode;
        }
        // If bucket isn't empty we check all linked list and
        // select the appropriate bucket
        else{
            Node<K, V> temporaryNode = table[index];
            while (temporaryNode.next != null){
                if (Objects.equals(temporaryNode.key, key)) {
                    temporaryNode.value = value;
                    return;
                }
                temporaryNode = temporaryNode.next;
            }
            if (Objects.equals(temporaryNode.key, key)) {
                temporaryNode.value = value;
                return;
            }
            temporaryNode.next = newNode;
        }

        currentSize++;

    }
    @Override
    public V getValue(K key) {

        V result = null;

        int index = calculateIndex(key);
        Node<K, V> temporaryNode = table[index];

        while (temporaryNode!=null){
            if (Objects.equals(temporaryNode.key, key)) result = temporaryNode.value;
            temporaryNode = temporaryNode.next;
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

                Node<K, V> temporaryNode = node.next;

                while (temporaryNode != null) {
                    put(temporaryNode.key, temporaryNode.value);
                    currentSize -= 1;
                    temporaryNode = temporaryNode.next;
                }
            }
        }
    }
}
