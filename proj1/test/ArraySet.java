package test;

import org.hamcrest.internal.ArrayIterator;

import java.util.Iterator;
import java.util.Set;

public class ArraySet<T> implements Iterable<T> {
    private T[] items;
    private int size;

    public ArraySet() {
        items = (T[]) new Object[100];
    }

    public boolean contains(T x){
        for(int i = 0; i < size; i++){
            if(items[i].equals(x)){     //这将直接查看二者在语义上是否相等而不会出现检查字符串而错误地去比较地址的错误
                return true;
            }
        }
        return false;
    }

    public void add(T x){
        if(contains(x)){
            return ;
        }
        items[size] = x;
        size++;
    }

    public int size(){
        return size;
    }

    public Iterator<T> iterator(){
        return new ArraySetIterator();
    }

    private class ArraySetIterator implements Iterator<T>{//Java是强类型语言，而Iterator本质上是一个接口，若不继承会在上面iterator方法的时候因为静态类型不符合报错
        private int wizPos;
        public ArraySetIterator(){
            wizPos = 0;
        }

        @Override
        public boolean hasNext(){
            return wizPos < size;
        }
        @Override
        public T next(){
            T returnItem = items[wizPos];
            wizPos++;
            return returnItem;
        }
    }

//    @Override
//    public String toString(){
//        String returnString = "{";
//        for(T item:this)
//        {
//            returnString += item + ",";
//        }
//        returnString += "}";
//        return returnString;
//    }

    @Override
    public String toString() {
        StringBuilder returnSB = new StringBuilder("{");
        for (int i = 0; i < size - 1; i += 1) {
            returnSB.append(items[i].toString());
            returnSB.append(", ");
        }
        returnSB.append(items[size - 1]);
        returnSB.append("}");
        return returnSB.toString();
    }

    public static void main(String[] args){
        ArraySet<String> st = new ArraySet<>();
        st.add("horse");
        st.add("cat");
        st.add("dog");
        st.add("pig");
        st.add("cow");
        st.add("pig");
        st.add("fish");
    }
}
