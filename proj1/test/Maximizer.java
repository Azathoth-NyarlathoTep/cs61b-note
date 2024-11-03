package test;

public class Maximizer {
//    public static Object max(Object[] items){
//        int maxDog = 0;
//        for(int i=0;i<items.length;i++){
//            if(items[i] > items[maxDog]) maxDog = i;
//        }
//        return items[maxDog];
//    }

    public static Comparable max(Comparable[] items){
        int maxDog = 0;
        for(int i=0;i<items.length;i++){
            int cmp = items[i].compareTo(items[maxDog]);
            if(cmp>0) maxDog = i;
        }
        return items[maxDog];
    }

    public static void main(String[] args){
        Dog[] dogs = {new Dog("Elyse",3),new Dog("Sture",9),new Dog("Artemesios",15)};
        Dog maxDog = (Dog)max(dogs);
        maxDog.bark();
    }
}
