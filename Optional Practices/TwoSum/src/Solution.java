import java.util.HashMap;
public class Solution {

    public static void main(String[] args) {
        int[] arr1 = {1, 1, 3, 10};
        int[] arr2 = {4, 5, 4, 5, 4, 5, 4};
        int[] arr3 = {3, 3, 3, 3, 3};

        //System.out.println(solve(arr1, 4)); // Output: 1
        //System.out.println(solve(arr2, 9)); // Output: 12
        System.out.println(solve(arr3, 6));
    }

    public static int solve(int[] arr, int target) {
        HashMap<Integer, Integer> count = new HashMap<>();
        int disjointPairs = 0;

        for (int i : arr) {
            if (!count.containsKey(i)) {
                count.put(i, 1);
            } else {
                //System.out.println("key: " + i + " old count: " + count.get(i) + " new count: " + (count.get(i) + 1));
                count.replace(i, count.get(i), count.get(i) + 1);
            }
        }

        for (int num : arr) {
            //System.out.println("num: " + num);
            //System.out.println("count: " + count.get(num));
            int difference = target - num;
            if (count.containsKey(difference)) {
                if (difference == num && count.get(num) > 1) {
                    disjointPairs++;
                    count.replace(num, count.get(num), count.get(num) - 2);
                }
                if (count.get(difference) > 0 && count.get(num) > 0 && difference != num) {
                    disjointPairs++;
                    count.replace(difference, count.get(difference), count.get(difference) - 1);
                    count.replace(num, count.get(num), count.get(num) - 1);
                }
            }
        }
        return disjointPairs;
    }
}
