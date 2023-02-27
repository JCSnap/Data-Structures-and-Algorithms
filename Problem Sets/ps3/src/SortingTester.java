import java.security.Key;
import java.util.Random;
public class SortingTester {
    public static boolean checkSort(ISort sorter, int size) {
        KeyValuePair[] testArray = initRandomArray(size);
        sorter.sort(testArray);
        if (size < 2) {
            return true;
        }
        for (int i = 1; i < size; i++) {
            int val = testArray[i].compareTo(testArray[i-1]);
            if (val == -1) {
                return false;
            } else {
                continue;
            }
        }
        return true;
    }


    public static boolean isStable(ISort sorter, int size) {
        KeyValuePair[] testArrayRandom = initRandomArray(size);
        sorter.sort(testArrayRandom);
        return checkStable(testArrayRandom);
    }

    private static KeyValuePair[] initRandomArray(int size) {
        KeyValuePair[] testArray = new KeyValuePair[size];
        Random rand = new Random();
        for (int i = 0; i < size; i++) {
            int randomInt = rand.nextInt(2);
            testArray[i] = new KeyValuePair(randomInt, i);
        }
        return testArray;
    }

    private static boolean checkStable(KeyValuePair[] arr) {
        for (int i = 1; i < arr.length; i++) {
            int val = arr[i].compareTo(arr[i-1]);
            if (val == 0) {
                if (arr[i].getValue() < arr[i - 1].getValue()) {
                    System.out.println("FALSE");
                    return false;
                } else {}
            } else {
                continue;
            }
        }
        System.out.println("TRUE");
        return true;
    }

    private static KeyValuePair[] initHomogenousArray(int size) {
        KeyValuePair[] testArray = new KeyValuePair[size];
        for (int i = 0; i < size; i++) {
            testArray[i] = new KeyValuePair(5, i);
        }
        return testArray;
    }

    private static KeyValuePair[] initCornerArray(int size) {
        KeyValuePair[] testArray = new KeyValuePair[size];
        if (size <= 2) {
            for (int i = 0; i < size; i++) {
                testArray[i] = new KeyValuePair(5, i);
            }
            return testArray;
        }
        for (int i = 0; i < size; i++) {
            if (i < 2) {
                testArray[i] = new KeyValuePair(5, i);
            } else if (i == 2) {
                testArray[i] = new KeyValuePair(1, i);
            } else {
                testArray[i] = new KeyValuePair(5, i);
            }
        }
        return testArray;
    }

    private static KeyValuePair[] initAscendingArray(int size) {
        KeyValuePair[] testArray = new KeyValuePair[size];
        for (int i = 0; i < size; i++) {
            testArray[i] = new KeyValuePair(i, i);
        }
        return testArray;
    }

    private static KeyValuePair[] initDescendingArray(int size) {
        KeyValuePair[] testArray = new KeyValuePair[size];
        for (int i = 0; i < size; i++) {
            testArray[i] = new KeyValuePair(size - i - 1, i);
        }
        return testArray;
    }

    private static KeyValuePair[] initLastOneSmallestArray(int size) {
        KeyValuePair[] testArray = new KeyValuePair[size];
        for (int i = 0; i < size - 1; i++) {
            testArray[i] = new KeyValuePair(i + 1, i);
        }
        testArray[size - 1] = new KeyValuePair(0, size - 1);
        return testArray;
    }
    private static void AscendDescendRatio() {

        ISort sorterA = new SorterA();
        ISort sorterC = new SorterC();
        ISort sorterD = new SorterD();
        ISort sorterE = new SorterE();
        ISort sorterF = new SorterF();

        ISort[] sorterArray = {sorterA, sorterC, sorterD, sorterE, sorterF};
        for (int i = 0; i < sorterArray.length; i++) {
            KeyValuePair[] ascendingArray = initAscendingArray(1000);
            KeyValuePair[] descendingArray = initDescendingArray(1000);
            KeyValuePair[] lastOneSmallestArray = initLastOneSmallestArray(1000);
            KeyValuePair[] ascendingArrayDummy = ascendingArray;
            KeyValuePair[] descendingArrayDummy = descendingArray;
            KeyValuePair[] lastOneSmallArrayDummy = lastOneSmallestArray;
            double costAscend = sorterArray[i].sort(ascendingArrayDummy);
            double costDescend = sorterArray[i].sort((descendingArrayDummy));
            double costLastSmall = sorterArray[i].sort(lastOneSmallArrayDummy);
            double ratio = costDescend / costLastSmall;
            System.out.println(costAscend);
            System.out.println(costDescend);
            System.out.println(sorterArray[i] + ": " + ratio);
        }
    }

    private static void BestWorstCase() {
        ISort sorterD2 = new SorterD();
        ISort sorterE2 = new SorterE();
        long bestD = Long.MAX_VALUE;
        long worstD = 0;
        long bestE = Long.MAX_VALUE;
        long worstE = 0;
        for (int i = 0; i < 100; i++) {
            KeyValuePair[] randomArray = initRandomArray(1000);
            KeyValuePair[] randomArray1 = initRandomArray(1000);
            long costD = sorterD2.sort(randomArray);
            long costE = sorterE2.sort(randomArray1);
            if (costD < bestD) {bestD = costD;}
            else if (costD > worstD) {worstD = costD;}
            else {}
            if (costE < bestE) {bestE = costE;}
            else if (costE > worstE) {worstE = costE;}
            else {}
        }
        System.out.println("D: Best: " + bestD + ", Worst: " + worstD);
        System.out.println("E: Best: " + bestE + ", Worst: " + worstE);
    }
    public static void main(String[] args) {
        //AscendDescendRatio();
        BestWorstCase();
    }
}
