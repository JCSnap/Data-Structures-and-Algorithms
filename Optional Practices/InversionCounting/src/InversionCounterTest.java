import org.junit.Test;

import static org.junit.Assert.*;

public class InversionCounterTest {

    @Test
    public void countSwapsTest1() {
        int[] arr = {3, 1, 2};
        assertEquals(2L, InversionCounter.countSwaps(arr));
    }

    @Test
    public void countSwapsTest2() {
        int[] arr = {2, 3, 4, 1};
        assertEquals(3L, InversionCounter.countSwaps(arr));
    }

    @Test
    public void mergeAndCountTest1() {
        int[] arr = {3, 1, 2};
        assertEquals(2L, InversionCounter.mergeAndCount(arr, 0, 0, 1, 2));
    }

    @Test
    public void mergeAndCountTest2() {
        int[] arr = {2, 3, 4, 1};
        assertEquals(3L, InversionCounter.mergeAndCount(arr, 0, 2, 3, 3));
    }

    @Test
    public void mergeAndCountTest3() {
        int[] arr = {1, 5, 7, 2, 3, 4, 1, 2, 8, 3};
        assertEquals(5L, InversionCounter.mergeAndCount(arr, 3, 5, 6, 7));
    }
    @Test
    public void mergeAndCountTest4() {
        int[] arr = {5, 2, 3, 2, 1};
        assertEquals(0L, InversionCounter.mergeAndCount(arr, 1, 1, 2, 2));
    }
}