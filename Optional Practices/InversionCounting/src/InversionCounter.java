class InversionCounter {

    public static long countSwaps(int[] arr) {
        long swapCount = 0;
        long n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (arr[j] > arr[j + 1]) {
                    // swap arr[j+1] and arr[j]
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                    swapCount++;
                }
            }
        }
        return swapCount;
    }
    public static long countSwaps2(int[] arr, int left, int right) {
        long swapCount = 0;
        long n = arr.length;
        int count = 0;
        for (int i = left; i < right; i++) {
            for (int j = left; j < right - count; j++) {
                if (arr[j] > arr[j + 1]) {
                    // swap arr[j+1] and arr[j]
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                    swapCount++;
                }
            }
            count++;
        }
        return swapCount;
    }
    /**
     * Given an input array so that arr[left1] to arr[right1] is sorted and arr[left2] to arr[right2] is sorted
     * (also left2 = right1 + 1), merges the two so that arr[left1] to arr[right2] is sorted, and returns the
     * minimum amount of adjacent swaps needed to do so.
     */
    public static long mergeAndCount(int[] arr, int left1, int right1, int left2, int right2) {
        if (arr.length == 0 || arr.length == 1) {
            return 0;
        }
        long a = countSwaps2(arr, left1, right2);
        return a;
    }
}
