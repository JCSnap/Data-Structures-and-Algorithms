import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;

public class Solution {
    public static void main(String[] args) {
        int[] arr1 = {1,2,20,4,3,2,7,9};
        int[] arr2 = {4,5,4,5,4,5};
    }
    public static int solve(int[] arr) {
        int maxLength = -1;
        int tempMax = 0;
        HashSet<Integer> container = new HashSet<>();
        for (int num : arr) {
            if (!container.contains(num)) {
                container.add(num);
                tempMax++;
            } else {
                maxLength = Integer.max(maxLength, tempMax);
                container = new HashSet<>();
                tempMax = 0;
            }
        }
        return maxLength;
    }
}