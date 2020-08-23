package TreeWalker.coding.FallThrough;

public class Test {
    public void test(int i) {
        switch (i) {
            case 0:
                i++;
                break;
            case 1:
                i++;
            case 2:  // violation without filter
            case 3:
            case 4: {
                i++;
            }
            case 5:  // violation without filter
                i++;
            case 6:  // violation without filter
                i++;
            case 7:  // violation without filter
                i++;
        }
    }
}
