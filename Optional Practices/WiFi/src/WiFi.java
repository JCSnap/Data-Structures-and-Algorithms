import java.util.Arrays;

class WiFi {

    /**
     * Implement your solution here
     */
    public static double computeDistance(int[] houses, int numOfAccessPoints) {
        Arrays.sort(houses);
        int max = houses[houses.length - 1];
        double start = 0;
        double end = max / (2 * numOfAccessPoints);
        double mid = 0;
        while (start < end) {
            mid = (start + end) / 2;
            if (coverable(houses, numOfAccessPoints, mid)) {
                end = mid;
            } else {
                start = mid + 0.5;
            }
        }
        System.out.println(start);
        System.out.println(mid);
        System.out.println(end);
        return start;
    }

    /**
     * Implement your solution here
     */
    public static boolean coverable(int[] houses, int numOfAccessPoints, double distance) {
        if (houses.length == 0) {
            return true;
        } else if (numOfAccessPoints <= 0 || distance <= 0) {
            return false;
        } else {
            Arrays.sort(houses);
            int routersCount = 0;
            int index = 0; // always start from left
            double range = 0;
            while (routersCount < numOfAccessPoints) { // the moment more routers are needed to continue, terminate
                routersCount++;
                range = (double) houses[index] + 2 * distance; // to maximize range, first house will be extreme left end
                while (houses[index] <= range) { // skips pass any houses that are under the range of said router
                    index++;
                    if (index >= houses.length) {
                        return true; // Since routerCount < numOfAccessPoints is a pre-condition, the moment all houses are
                                     // accounted for, return true
                    }
                } // if house falls outside of range, exit loop. Go through another iteration of increasing router
            }
            return false;
        }
    }
}
