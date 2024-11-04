package deque;
import org.junit.Test;

import java.util.Comparator;

import static org.junit.Assert.*;

public class MaxArrayDequeTest {
    @Test
    public void bigLLDequeTest() {
        class OneComparator<T> implements Comparator<T> {
            public int compare(T a, T b) {
                return (int)a - (int)b;
            }
        }

    }
}
