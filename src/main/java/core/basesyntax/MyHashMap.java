package core.basesyntax;

import java.util.Objects;
import java.lang.reflect.Field;

public class MyHashMap<K, V> implements MyMap<K, V>, Cloneable {

    static class Node<V, K>{
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

    private int totalSize = 16;
    final static float loadFactor = 0.75f;
    Node[] table = new Node[getTotalSize()];


    public void setTotalSize(int totalSize) {
        this.totalSize = totalSize;
    }

    public int getTotalSize() {
        return totalSize;
    }

    public final int hash(K key, V value) {
        int res = Objects.hash(key, value);
        if(res < 0) res*=-1;
        return res;
    }

    public static boolean compareObjects(Object obj1, Object obj2) {
        if (obj1 == obj2)
            return true;
        if (obj1 == null || obj2 == null || obj1.getClass() != obj2.getClass())
            return false;

        Field[] fields = obj1.getClass().getDeclaredFields();

        for (Field field : fields) {
            try {
                field.setAccessible(true);
                Object value1 = field.get(obj1);
                Object value2 = field.get(obj2);

                if (!Objects.equals(value1, value2))
                    return false;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return true;
    }

    @Override
    public void put(K key, V value) {

        // Resizing
        int threshold = (int) (loadFactor * getTotalSize());
        int currentSize = this.getSize();
        if (currentSize + 1 > threshold){
            this.resize();
        }

        // Getting of element index
        int h = this.hash(key, value);
        int index = h % getTotalSize();

        // If the key exists, we change the value
        for (int i = 0; i < table.length; i++){
            if (table[i] instanceof Node) {
                if(table[i].key instanceof Object && compareObjects(table[i].key, key)){
                    table[i].value = value;
                    return;
                }
                else if(table[i].key == key){
                    table[i].value = value;
                    return;
                }
            }
        }

        // If the key doesn't exist, we create a new Node
        if (this.table[index] == null) {
            Node node = new Node(h, key, value, null);
            this.table[index] = node;
        }
        else{
            Node oldNode = table[index];
            if (oldNode.key!=key && oldNode.value!=value){
                Node temp1 = oldNode;
                Node temp2 = oldNode.next;
                if (temp2 == null) temp1.next = new Node(h, key, value, null);
                else{
                    while (temp2!=null){
                        temp1 = temp1.next;
                        temp2 = temp2.next;
                    }
                    temp1.next = new Node(h, key, value, null);
                }
            }
        }
    }
    @Override
    public V getValue(K key) {

        V result = null;

        for (int i = 0; i < table.length; i++) {

            Node node = table[i];

            // If in this bucket there is only one Node we check it
            if (node instanceof Node) {
                if (node.next == null) {
                    if (node.key instanceof Object && compareObjects(node.key, key)) {
                        result = (V) node.value;
                    } else if (node.key == key) {
                        result = (V) node.value;
                    }
                }

                // If in this bucket there is more than one Node we check all of them
                else {
                    while (node != null) {
                        if (node.key instanceof Object && compareObjects(node.key, key)) {
                            result = (V) node.value;
                        } else if (node.key == key) {
                            result = (V) node.value;
                        }
                        node = node.next;
                    }
                }

            }

        }
        return result;
    }

    @Override
    public int getSize() {

        int size = 0;

        for (int i = 0; i < table.length; i++){

            Node node = table[i];

            if (node instanceof Node) {
                size++;
            }

            if (node instanceof Node && node.next != null) {

                Node temp = node.next;

                while (temp != null) {
                    size++;
                    temp = temp.next;
                }
            }

        }
        return size;
    }

    private void resize(){

        // Double the size
        setTotalSize(getTotalSize()*2);

        // Re-create the array "table"
        Node[] oldTable = table;
        Node[] newTable = new Node[getTotalSize()];
        table = newTable;

        // Re-locate all Nodes by new indices
        for (int i = 0; i < oldTable.length; i++){

            Node node = oldTable[i];

            if (node!=null) this.put((K)node.key, (V)node.value);

            if (node!=null && node.next != null) {

                Node temp = node.next;

                while (temp != null) {
                    this.put((K)temp.key, (V)temp.value);
                    temp = temp.next;
                }
            }
        }
    }
}
