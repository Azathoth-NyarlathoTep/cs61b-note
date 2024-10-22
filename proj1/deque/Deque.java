package deque;

public interface Deque <T>{
    void addFirst(T item);
    void addLast(T item);
    default boolean isEmpty(){
        if(size() == 0) return true;
        else return false;
    }
    int size();
    void Print();
    T removeFirst();
    T removeLast();
    T get(int index);
}
