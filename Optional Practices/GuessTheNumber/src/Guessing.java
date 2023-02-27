public class Guessing {

    // Your local variables here
    private int low = 0;
    private int high = 1000;
    private int mid = 0;
    /**
     * Implement how your algorithm should make a guess here
     */
    public int guess() {
        mid = (low + high) / 2;
        System.out.println(mid);
        return  mid;
    }

    /**
     * Implement how your algorithm should update its guess here
     */
    public void update(int answer) {
        if (answer == -1) { // search right
            low = mid + 1;
            guess();
        } else if (answer == 1) {
            high = mid;
            guess();
        } else {
            //
        }
    }
}
