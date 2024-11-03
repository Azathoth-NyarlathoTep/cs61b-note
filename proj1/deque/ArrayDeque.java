package deque;

public class ArrayDeque<T> implements Deque<T> {
    private T[] items;
    private int size;
    private int capacity;
    private int nextFirst;
    private int nextLast;

    public ArrayDeque() {
        items = (T[]) new Object[8];
        capacity = 8;
        nextFirst = 8/2;
        nextLast = nextFirst+1;
        size = 0;
    }

    private void resize(int cap){
        T[] temp = (T[]) new Object[cap];
        for(int i = 0; i < size; i++){
            temp[i] = get(i);
        }
        items = temp;
        nextFirst = cap-1;
        nextLast = size;
        capacity = cap;
    }

    private void shrinkSize() {
        if (size < items.length / 4 && size > 4) {
            resize(size * 2);
        }
    }

    @Override
    public void addFirst(T item) {
        if(size == items.length){
            resize(size * 2);
        }
        items[nextFirst] = item;
        nextFirst = (nextFirst+capacity-1)%capacity;
        size++;
    }

    @Override
    public void addLast(T item) {
        if(size == items.length){
            resize(size * 2);
        }
        items[nextLast] = item;
        nextLast = (nextLast+1)%capacity;
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
            for(int i = 0; i < size; i++)
            {
                T item = get(i);
                System.out.print(item + " ");
            }
        }
        System.out.println();
    }

    @Override
    public T removeFirst() {
        if (size > 0) {
//            int i = (nextFirst + 1) % capacity;
//            T v = items[i];
//            nextFirst = i;
//            size -= 1;
//            shrinkSize();
//            return v;
            nextFirst = (nextFirst+1)%capacity;
            T item = items[nextFirst];
            size--;
            shrinkSize();
            return item;
        }
        return null;
    }

    @Override
    public T removeLast() {
        if(size > 0){
            nextLast = (nextLast+capacity-1)%capacity;
            T item = items[nextLast];
            size--;
            shrinkSize();
            return item;
        }
        return null;
    }

    @Override
    public T get(int index) {
        if(index >= 0 && index < size){
            return items[(nextFirst+index+1)%capacity];
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (!(o instanceof Deque)) {
            return false;
        }
        Deque<?> ao = (Deque<?>) o;
        if (ao.size() == size) {
            for (int i = 0; i < size; i++) {
                if (!(get(i).equals(ao.get(i)))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
