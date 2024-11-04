package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    Comparator<T> DequeComparator;

    public MaxArrayDeque(Comparator<T> c) {
        DequeComparator = c;
    }

    private T max(Comparator<T> c){
        if(size() == 0) return null;
        int maxIndex = 0;
        for(int i = 0; i < size(); i++){
            if(c.compare(get(i), get(i+1)) > 0) maxIndex = i;
        }
        return get(maxIndex);
    }

    public T max(){
        return max(DequeComparator);
    }
}
