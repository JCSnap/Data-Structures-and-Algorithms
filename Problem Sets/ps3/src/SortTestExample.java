/**
 * This class is a simple example for how to use the sorting classes.
 * It sorts three numbers, and measures how long it takes.
 */

public class SortTestExample {
    public static void main(String[] args) {
        // Create three key value pairs
        KeyValuePair[] testArray = new KeyValuePair[3];
        testArray[0] = new KeyValuePair(3, 20);
        testArray[1] = new KeyValuePair(2, 20);
        testArray[2] = new KeyValuePair(1, 20);
        boolean A = true;
        //Create a Sorter
        ISort sortingObjectA = new SorterE();
        /* for (int i = 0; i < 30; i++) {
            A = A && SortingTester.checkSort(sortingObjectA, 5000);
        }
        System.out.println(A); */

        SortingTester.isStable(sortingObjectA, 3);

        // Do the sorting
        long sortCost = sortingObjectA.sort(testArray);

        System.out.println(testArray[0]);
        System.out.println(testArray[1]);
        System.out.println(testArray[2]);
        System.out.println("Sort Cost: " + sortCost);
    }
}
