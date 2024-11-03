package test;

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.HashSet;
import java.util.Set;

public class Launcher {
    public static void main(String[] args){
        Set<Integer> javaset = new HashSet<Integer>();
        javaset.add(5);
        javaset.add(6);
        javaset.add(7);
        javaset.add(8);

        ArraySet<Integer> aset = new ArraySet<>();
        aset.add(1);
        aset.add(2);
        aset.add(11);
        aset.add(12);

        for(int i:javaset){
            System.out.println(i);
        }

        Iterator<Integer> seer = javaset.iterator();

        while(seer.hasNext()){
            int i = seer.next();
            System.out.println(i);
        }

        Iterator<Integer> aseer = aset.iterator();
        while(aseer.hasNext()){
            int i = aseer.next();
            System.out.println(i);
        }
        for(int i:aset){
            System.out.println(i);
        }

        System.out.println(javaset);
        System.out.println(aset);
    }
}
