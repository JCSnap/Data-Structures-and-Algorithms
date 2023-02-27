/**
 * The Optimization class contains a static routine to find the maximum in an array that changes direction at most once.
 */
public class Optimization {
    /**
     * A set of test cases.
     */
    static int[][] testCases = {
            {1, 3, 5, 7, 9, 11, 10, 8, 6, 4},
            {67, 65, 43, 42, 23, 17, 9, 100},
            {4, -100, -80, 15, 20, 25, 30},
            {2, 3, 4, 5, 6, 7, 8, 100, 99, 98, 97, 96, 95, 94, 93, 92, 91, 90, 89, 88, 87, 86, 85, 84, 83}
    };

    /**
     * Returns the maximum item in the specified array of integers which changes direction at most once.
     *
     * @param dataArray an array of integers which changes direction at most once.
     * @return the maximum item in data Array
     */
    public static int searchMax(int[] dataArray) {
        int len = dataArray.length;
        int mid = 0;
        int begin = 0;
        int end = len - 1;
        if (len == 1) {
            return dataArray[0]; // by definition, is largest
        } else if (len == 0) {
            return 0;
        } else if (dataArray[1] < dataArray[0]) {
            return Math.max(dataArray[0], dataArray[len - 1]); // if array decrease then increase || decreases, take highest of both ends
        } else if (dataArray[1] > dataArray[0]) { // increases || increases then decreases,
            while (begin <= end) {
                mid = (begin + end) / 2;
                //System.out.println(dataArray[mid]); //debug
                if (isMax(mid, len, dataArray)) {
                    break;
                } else if (isIncreasing(mid, dataArray)) {
                    begin = mid + 1; // search right
                } else {
                    end = mid; // search left
                }
            }
            //System.out.println("Final answer: " + dataArray[mid]); //  debug
            return dataArray[mid];
        } else {
            return 0;
        }
    }

    // if end of array and larger than left value, it is max || max in middle
    // did not include max at left end of array (increasing array is a pre-condition)
    private static boolean isMax(int i, int len, int[] arr) {
        if (i - 1 >= 0 && i + 1 < len) {
            return arr[i - 1] < arr[i] && arr[i + 1] < arr[i];
        } else if (i - 1 < 0) {
            return arr[i + 1] < arr[i];
        } else {
            return arr[i - 1] < arr[i];
        }
    } // if end of array and larger than left value, it is max || max in middle
    // did not include max at left end of array (increasing array is a pre-condition)

    private static boolean isIncreasing(int i, int[] arr) {
        return arr[i + 1] > arr[i];
    } // did not include the case where it at the right end of array because begin =/= end is a pre-condition


    /**
     * A routine to test the searchMax routine.
     */
    public static void main(String[] args) {
        for (int[] testCase : testCases) {
            System.out.println(searchMax(testCase));
        }
    }
}
