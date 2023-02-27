///////////////////////////////////
// This is the main shift register class.
// Notice that it implements the ILFShiftRegister interface.
// You will need to fill in the functionality.
///////////////////////////////////

/**
 * class ShiftRegister
 * @author
 * Description: implements the ILFShiftRegister interface.
 */
public class ShiftRegister implements ILFShiftRegister {
    ///////////////////////////////////
    // Create your class variables here
    ///////////////////////////////////
    // TODO:
    int[] cur_seed;
    int cur_size;
    int cur_tap;
    ///////////////////////////////////
    // Create your constructor here:
    ///////////////////////////////////
    ShiftRegister(int size, int tap) {
        if (size > 0 && size > tap && tap >= 0) { // size and tap cannot be negative and since tap starts from 0, must be smaller
            cur_size = size;
            cur_tap = tap;
        } else {
            System.out.print("Invalid input. ");
        }
    }

    ///////////////////////////////////
    // Create your class methods here:
    ///////////////////////////////////
    /**
     * setSeed
     * @param seed
     * Description:
     */
    @Override
    public void setSeed(int[] seed) {
        cur_seed = seed;
        if (seed.length != cur_size) { // edge case
            System.out.println("Invalid input. ");
        } else {
            for (int i = 0; i < cur_size; i++) {
                if (cur_seed[i] != 0 && cur_seed[i] != 1) {
                    System.out.print("Error, seed contains invalid value");
                    break; /* terminate as long as one value is invalid */
                }
            }
        }
    }

    /**
     * shift
     * @return
     * Description:
     */
    @Override
    public int shift() {
        int xor_tap = cur_seed[cur_tap]; // the value of the tap
        int feedback;
        int most_sig = cur_seed[cur_size - 1]; // the value of the most significant bit
        if (xor_tap == most_sig) { // xor implementation, I only found out later than java has inbuilt xor (a ^ b)
            feedback = 0;
        } else {
            feedback = 1;
        }
        if (cur_size == 1) { // edge case
            return feedback;
        } else {
            for (int i = cur_size - 1; i > 0; i--) { // shift to the right since array is a reversed version of seed
                cur_seed[i] = cur_seed[i - 1];
            }
            cur_seed[0] = feedback;
            System.out.println(cur_seed[0]);
            System.out.print(cur_seed[1]);
            System.out.print(cur_seed[2]);
            System.out.print(cur_seed[3]);
            System.out.print(cur_seed[4]);
            System.out.print(cur_seed[5]);
            System.out.print(cur_seed[6]);
            System.out.print(cur_seed[7]);
            System.out.print(cur_seed[8]);
            return cur_seed[0];
        }
    }

    /**
     * generate
     * @param 
     * @return
     * Description:
     */
    @Override
    public int generate(int k) {
        int[] binary_val = new int[k]; // to store the binaries
        double val = 0; /* double required to use Math.pow */
        for (int i = 0; i < k; i++) {
            binary_val[i] = shift();
        }
        for (int i = 0; i < k; i++) {
            if (i == k - 1 && binary_val[i] == 1) { // if least significant is a 1
                val = val + 1;
            } else if (binary_val[i] == 0) {
                continue; // do nothing
            } else {
                val = val + Math.pow(2, (k - i - 1));
            }
        }
        return (int) val; // change from double back to int
    }

    /**
     * Returns the integer representation for a binary int array.
     * @param array
     * @return
     */



    private int toDecimal(int[] array) {
        // TODO:
        return 0;
    }

}

