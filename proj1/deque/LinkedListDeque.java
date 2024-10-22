package deque;

public class LinkedListDeque<T> implements Deque<T> {

    private static class Node<T>{
        private T value;
        private Node<T> next,prev;

        Node(T value, Node<T> prev, Node<T> next){
            this.value = value;
            this.next = next;
            this.prev = prev;
        }
    }

    public Node<T> sentinel;
    private int size;

    public LinkedListDeque(){
        sentinel = new Node<T>(null, null, null);
        sentinel.next = sentinel;
        sentinel.prev = sentinel;
        size = 0;
    }

    @Override
    public void addFirst(T item) {
        sentinel.next = new Node<T>(item, sentinel, sentinel.next);
        sentinel.next.next.prev = sentinel.next;
        size++;
    }

    @Override
    public void addLast(T item) {
        sentinel.prev = new Node<T>(item, sentinel.prev, sentinel);
        sentinel.prev.prev.next = sentinel.prev;
        size++;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void Print() {
        if(size>0)
        {
            Node<T> p = sentinel;
            for(int i =0;i<size;i++)
            {
                p = p.next;
                System.out.print(p.value + " ");
            }
            System.out.println();
        }
    }

    @Override
    public T removeFirst() {
        if(size>0)
        {
            T tmp = sentinel.next.value;
            sentinel.next = sentinel.next.next;
            size--;
            sentinel.next.prev = sentinel;
            return tmp;
        }
        return null;
    }

    @Override
    public T removeLast() {
        if(size>0)
        {
            T tmp = sentinel.prev.value;
            sentinel.prev = sentinel.prev.prev;
            sentinel.prev.next = sentinel;
            size--;
            return tmp;
        }
        return null;
    }

    @Override
    public T get(int index) {
        if(index>=0 && index<size)
        {
            Node<T> p = sentinel;
            for(int i =0;i<index;i++)
                p =  p.next;
            return p.value;
        }
        return null;
    }
}
