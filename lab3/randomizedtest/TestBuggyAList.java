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

      int N = 500;
      for (int i = 0; i < N; i += 1) {
        int operationNumber = StdRandom.uniform(0, 3);
        if (operationNumber == 0) {
          // addLast
          int randVal = StdRandom.uniform(0, 5000);
          L.addLast(randVal);
          System.out.println("addLast(" + randVal + ")");
        } else if (operationNumber == 1) {
          // size
          int size = L.size();
          System.out.println("size: " + size);
        }
      }
    }
}
