package test;
import java.util.Comparator;


public class Dog implements Comparable<Dog>{//把OurComparable改成Java内置的Comparable
    private String name;
    private int size;

    public Dog(String n,int s)
    {
        name = n;
        size = s;
    }

    public void bark(){
        System.out.println(name + " says:bark");
    }

    @Override
    public int compareTo(Dog uddaDog) {
//        Dog uddaDog = (Dog) o;

        return this.size - uddaDog.size;//这是在比较中常用的技巧，通过这样就不必有下面的冗长的比较语句

//        if(this.size < uddaDog.size) return -1;
//        else if(this.size == uddaDog.size) return 0;
//        else return 1;
    }

    public static class NameComparator implements Comparator<Dog> {
        public int compare(Dog d1, Dog d2) {
            return d1.name.compareTo(d2.name);
        }
    }
}
