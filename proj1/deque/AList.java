package deque;

public class AList {
    private int size;
    private int[] items;

    public AList(){
        items = new int[100];
        size = 0;
    }

    private void resize(int capacity){
        int[] a = new int[capacity];
        System.arraycopy(items, 0, a, 0, size);
        items = a;
    }

    public void addLast(int item){
        if(size == items.length){
            resize(size *2);
        }

        items[size] = item;
        size++;
    }

    public int getLast(){
        return items[size-1];
    }

    public int get(int index){
        return items[index];
    }

    public int size(){
        return size;
    }

    public int removeLast(){
        int x = getLast();
        size--;
        return x;
    }
}
