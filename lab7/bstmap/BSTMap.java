package bstmap;

import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>,V> implements Map61B<K, V> { //在泛型中继承接口的时候不能用implements只能用extends
    public class Node{                                                  //而在这里extends是一种约束，它是用于确保K可以用于比较操作(即可以用compareTo()方法)
        private K key;                                                  //这种方法本来是用于指定上界的，比如extends了number就不能用String相关方法等
        private V value;
        private Node left;
        private Node right;

        public Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    private Node root;
    private int size;

    public BSTMap() {
        root = null;
        size = 0;
    }

    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    @Override
    public boolean containsKey(K key) {
        return find(root, key) != null;
    }

    private Node find(Node node, K key) { //本来想写成boolean型函数，但思考一下发现如果返回值为node节点，那么在get函数中也可以直接使用
        if (node == null) return null;
        if (key.compareTo(node.key)<0) return find(node.left,key);
        if (key.compareTo(node.key)>0) return find(node.right,key);
        return node;
    }

    @Override
    public V get(K key) {
        Node node = find(root, key);
        if(node == null) return null;
        else return node.value;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(K key, V value) {
        if(root == null) {
            root = new Node(key,value);
        }
        else build(root,key,value);
        size++;
    }

    private void build(Node node,K key,V value){
        if (key.compareTo(node.key)<0) {
            if(node.left == null) {
                node.left = new Node(key,value);
            }
            else {
                build(node.left,key,value);
            }
        }
        else if (key.compareTo(node.key)>0) {
            if(node.right == null) {
                node.right = new Node(key,value);
            }
            else {
                build(node.right,key,value);
            }
        }
    }

//    @Override
//    public void put(K key, V value) {
//        root = putHelper(root, key, value);
//    }
//    private Node putHelper(Node node, K key, V value) {
//        if (node == null) {
//            size += 1;
//            return new Node(key, value);
//        }
//        if (key.compareTo(node.key) < 0) {
//            node.left = putHelper(node.left, key, value);
//        }
//        if (key.compareTo(node.key) > 0) {
//            node.right = putHelper(node.right, key, value);
//        }
//        return node;
//    }

    @Override
    public Set keySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator iterator() {
        throw new UnsupportedOperationException();
    }

    public void printInOrder() {}
}
