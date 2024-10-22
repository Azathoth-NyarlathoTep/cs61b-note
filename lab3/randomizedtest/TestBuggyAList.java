package randomizedtest;

import edu.princeton.cs.algs4.Alphabet;
import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import timingtest.AList;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
  // YOUR TESTS HERE
    @Test
    public void testThreeAddThreeRemove(){
      AListNoResizing<Integer> l = new AListNoResizing<Integer>();
      BuggyAList<Integer> r = new BuggyAList<>();

      l.addLast(5);
      l.addLast(10);
      l.addLast(15);
      r.addLast(5);
      r.addLast(10);
      r.addLast(15);

      assertEquals(l.size(), r.size());

      assertEquals(l.removeLast(), r.removeLast());
      assertEquals(l.removeLast(), r.removeLast());
      assertEquals(l.removeLast(), r.removeLast());
    }

    @Test
    public void randomizedTest(){
      AListNoResizing<Integer> L = new AListNoResizing<>();
      BuggyAList<Integer> R = new BuggyAList<>();

      int N = 1000;
      for (int i = 0; i < N; i += 1) {
        int operationNumber = StdRandom.uniform(0, 4);
        if (operationNumber == 0) {
          // addLast
          int randVal = StdRandom.uniform(0, 5000);
          L.addLast(randVal);
          R.addLast(randVal);
//          System.out.println("addLast(" + randVal + ")");
//          assertEquals(L, R);
        } else if (operationNumber == 1) {
          // size
          int size_1 = L.size();
          int size_2 = R.size();
          assertEquals(size_1, size_2);
        }
        else if (operationNumber == 2) {
          //getLast
          int val_1 = -1;
          int val_2 = -1;
          if(L.size()>0) val_1 = L.getLast();
          if(R.size()>0) val_2 = R.getLast();
          assertEquals(val_1, val_2);
        }else if (operationNumber == 3) {
          int val_1 = -1;
          int val_2 = -1;
          if(L.size()>0) val_1 = L.removeLast();
          if(R.size()>0) val_2 = R.removeLast();
          assertEquals(val_1, val_2);
        }
      }
    }
}



